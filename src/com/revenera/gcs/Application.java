package com.revenera.gcs;

import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;
import com.revenera.gcs.implementor.GeneratorBase;
import com.revenera.gcs.implementor.GeneratorImplementor;
import com.revenera.gcs.implementor.TechnologyProperties;
import com.revenera.gcs.logging.Level;
import com.revenera.gcs.logging.LoggingFactory;
import com.revenera.gcs.transaction.ExecutionRecord;
import com.revenera.gcs.utils.Frame;
import com.revenera.gcs.utils.Serializer;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The root of the service, registered as a listener will set stuff up when the context is initialized
 */
@WebListener
public class Application implements ServletContextListener {

  private static final LoggingFactory logger = LoggingFactory.create(Application.class);

  static {

    try (final AppContext ctx = new AppContext(Frame.TWO)) {
      final Level level = Level.valueOf(
          AppContext.getApplicationProperties().getLoggingLevel().toUpperCase());

      LoggingFactory.setLoggingLevel(level);

      logger.info().log("set logging level to", level);
    }
    catch (final Throwable t) {
      logger.exception(t);
    }
  }

  private final Housekeeper housekeeper = new Housekeeper();

  public Application() {
    logger.me(this);
    try (final AppContext ctx = Beans.makeContext(this)) {
      logger.info().log("version", AppContext.getApplicationProperties().getVersionDetails());
    }
    catch (final Throwable t) {
      logger.exception(t);
    }
  }

  private void serializeToLogPath(final String filename, final List<String> content) {
    try {
      //TODO: log into log directory in web app
      final String root = Beans.getLogPath().toRealPath().toString();

      if (Files.exists(Paths.get(root))) {

        FileUtils.writeLines(
            Paths.get(root, filename).toAbsolutePath().toFile().getAbsoluteFile(),
            content,
            true);
      }
    }
    catch (final Throwable t) {
      logger.exception(t);
    }
  }

  private void polling() {
    try (final AppContext ctx = Beans.makeContext(this)) {

      while (AppContext.getTransactionManager().hasTransactions()) {
        //TODO:need to depopulate the queue even if not serializing
        final Map.Entry<Object, Object> content = AppContext.getTransactionManager().pollTransactions();

        if (content != null) {
          serializeToLogPath(content.getKey() + ".yaml",
              Collections.singletonList(
                  Serializer.safeSerializeYaml(content.getValue())));
        }
      }
    }
    catch (final Throwable t) {
      logger.exception(t);
    }
  }

  private void logging() {
    try (final AppContext ctx = Beans.makeContext(this)) {

      final List<String> messages = new ArrayList<>();
      while (LoggingFactory.hasMessages()) {
        final String content = LoggingFactory.pollMessageQueue();
        if (content != null) {
          messages.add(content);
        }
      }

      if (!messages.isEmpty()) {
        serializeToLogPath(LocalDate.now() + ".revenera.log", messages);
      }
    }
    catch (final Throwable t) {
      logger.exception(t);
    }
  }

  private void housekeeping() {
    try (final AppContext ctx = Beans.makeContext(this)) {
      //TODO:what is this supposed to do?
      logger.yaml(Level.DEBUG,
          AppContext.getExecutionManager().getRecords().stream()
              .sorted(Comparator.comparing(ExecutionRecord::getUpdated).reversed())
              .collect(Collectors.toList()));
    }
    catch (final Throwable t) {
      logger.exception(t);
    }
  }

  /*
   * ServletContextListener
   */
  @Override
  public void contextInitialized(final ServletContextEvent event) {
    logger.in();

    try (final AppContext ctx = Beans.makeContext(this)) {
      Beans.setResourcesRoot(event.getServletContext().getRealPath("/WEB-INF"));

      try (final AnnotationManager manager = new AnnotationManager()) {

        final List<String> files = manager.findClassFilesInPackage(Paths.get("com/revenera"));

        for (final String typename : files) {

          final Class<?> type = Class.forName(typename);

          if (type.isAnnotationPresent(GeneratorImplementor.class)) {

            final GeneratorImplementor annotation = type.getAnnotation(GeneratorImplementor.class);

            logger.info().log("found implementor",
                annotation.technologyId(),
                annotation.technologyName(),
                annotation.isDefault(),
                type.getSimpleName());

            if (TechnologyProperties.class.isAssignableFrom(type)) {

              final TechnologyProperties imp = (TechnologyProperties) type.newInstance();

              imp.configureTechnologyProperties(annotation.technologyId(), annotation.technologyName());

              AppContext.getImplementorFactory().addImplementor(imp, annotation.isDefault());
            }
            else {
              logger.error().log("invalid implementor",
                  annotation.technologyId(),
                  annotation.technologyName(),
                  type.getSimpleName());
            }
          }
        }
      } // close annotation manager

      final LicenseGeneratorServiceInterface implementor = AppContext.getImplementorFactory().getDefaultImplementor();
      if (implementor != null) {
        logger.info().log("default implementor", implementor.getClass().getName());
      }
      else {
        throw new RuntimeException("No default implementor found");
      }

      this.housekeeper.initialize();

      this.housekeeper.start(Timers.housekeeping, this::housekeeping, 1, AppContext.getApplicationProperties().getHousekeepingFrequency(), TimeUnit.MINUTES);
      this.housekeeper.start(Timers.logging, this::logging, 1, 1, TimeUnit.SECONDS);
      this.housekeeper.start(Timers.polling, this::polling, 500, 500, TimeUnit.MILLISECONDS);

      logger.yaml(Level.DEBUG, AppContext.getApplicationData());
    }
    catch (final Throwable t) {
      logger.exception(t);
    }
    logger.out();
  }

  private enum Timers {
    housekeeping, logging, polling
  }

  @Override
  public void contextDestroyed(final ServletContextEvent event) {
    logger.in();
    try {
      this.housekeeper.cancel(Timers.polling);
      this.housekeeper.cancel(Timers.logging);
      this.housekeeper.cancel(Timers.housekeeping);

      this.housekeeper.shutdown();
    }
    finally {
      logger.out();
    }
  }
}

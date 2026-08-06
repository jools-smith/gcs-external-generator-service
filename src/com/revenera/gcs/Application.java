package com.revenera.gcs;

import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;
import com.revenera.gcs.implementor.GeneratorImplementor;
import com.revenera.gcs.implementor.TechnologyProperties;
import com.revenera.gcs.logging.Level;
import com.revenera.gcs.logging.LoggingFactory;
import com.revenera.gcs.transaction.ExecutionRecord;
import com.revenera.gcs.utils.Serializer;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
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
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {

      final Level level = Level.valueOf(
          ExecutionContext.getApplicationProperties().getLoggingLevel().toUpperCase());

      ExecutionContext.getLoggingManager().setLevel(level);

      logger.get().info("set logging level to {0}", level);
    }
    catch (final Throwable t) {
      logger.exception(t);
    }
  }

  private final Housekeeper housekeeper = new Housekeeper();

  public Application() {
    logger.me(this);
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      logger.get().info("version", ExecutionContext.getApplicationProperties().getVersionDetails());
    }
    catch (final Throwable t) {
      logger.exception(t);
    }
  }

  private void serializeToLogPath(final String filename, final List<String> content) throws IOException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      final String root = ExecutionContext.getLogPath().toRealPath().toString();

      if (Files.exists(Paths.get(root))) {
        FileUtils.writeLines(
            Paths.get(root, filename).toAbsolutePath().toFile().getAbsoluteFile(),
            content,
            true);
      }
    }
  }

  private void polling() {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {

      while (ExecutionContext.getTransactionManager().hasTransactions()) {
        //TODO:need to depopulate the queue even if not serializing
        final Map.Entry<Object, Object> content = ExecutionContext.getTransactionManager().pollTransactions();

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
    logger.in();
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      logger.in();

      final List<String> messages = new ArrayList<>();

      while (!ExecutionContext.getLoggingManager().isEmpty()) {
        messages.add(ExecutionContext.getLoggingManager().popMessage());
      }

      if (!messages.isEmpty()) {
        serializeToLogPath(LocalDate.now() + ".revenera.log", messages);
      }
    }
    catch (final Throwable t) {
      logger.exception(t);
    }
    finally {
      logger.out();
    }
  }

  private void housekeeping() {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      //TODO:what is this supposed to do?
      logger.yaml(Level.DEBUG,
          ExecutionContext.getExecutionManager().getRecords().stream()
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
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      Beans.setResourcesRoot(event.getServletContext().getRealPath("/WEB-INF"));

      try (final AnnotationManager manager = new AnnotationManager()) {

        final List<String> files = manager.findClassFilesInPackage(Paths.get("com/revenera"));

        for (final String typename : files) {

          final Class<?> type = Class.forName(typename);

          if (type.isAnnotationPresent(GeneratorImplementor.class)) {

            final GeneratorImplementor annotation = type.getAnnotation(GeneratorImplementor.class);

            logger.get().info("found",
                annotation.technologyId(),
                annotation.technologyName(),
                annotation.isDefault(),
                type.getSimpleName());

            if (TechnologyProperties.class.isAssignableFrom(type)) {

              final TechnologyProperties imp = (TechnologyProperties) type.newInstance();

              imp.configureTechnologyProperties(annotation.technologyId(), annotation.technologyName());

              ExecutionContext.getImplementorFactory().addImplementor(imp, annotation.isDefault());
            }
            else {
              logger.get().warn("invalid implementor",
                  annotation.technologyId(),
                  annotation.technologyName(),
                  type.getSimpleName());
            }
          }
        }
      } // close annotation manager

      final LicenseGeneratorServiceInterface implementor = ExecutionContext.getImplementorFactory().getDefaultImplementor();
      if (implementor != null) {
        logger.get().info("default implementor", implementor.getClass().getName());
      }
      else {
        throw new RuntimeException("No default implementor found");
      }

      this.housekeeper.initialize();

      this.housekeeper.start(Timers.housekeeping, this::housekeeping, 1, ExecutionContext.getApplicationProperties().getHousekeepingFrequency(), TimeUnit.MINUTES);
      this.housekeeper.start(Timers.logging, this::logging, 1, 1, TimeUnit.SECONDS);
      this.housekeeper.start(Timers.polling, this::polling, 500, 500, TimeUnit.MILLISECONDS);
    }
    catch (final Throwable t) {
      logger.exception(t);
    }
    finally {
      logger.yaml(Level.DEBUG, ExecutionContext.getApplicationData());
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

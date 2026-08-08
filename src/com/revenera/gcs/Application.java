package com.revenera.gcs;

import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;
import com.revenera.gcs.implementor.GeneratorImplementor;
import com.revenera.gcs.implementor.TechnologyProperties;
import com.revenera.gcs.logging.Level;
import com.revenera.gcs.logging.LoggingFactory;
import com.revenera.gcs.utils.Serializer;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {

      final List<String> messages = new ArrayList<>();

      while (!ExecutionContext.getLoggingManager().isEmpty()) {
        messages.add(ExecutionContext.getLoggingManager().popMessage());
      }

      if (!messages.isEmpty()) {
        serializeToLogPath(LocalDate.now() + ".revenera.log", messages);
      }
    }
    catch (final Throwable t) {
      System.out.printf("EXCEPTION %s %s%n", t.getClass().getName(), t.getMessage());
      logger.exception(t);
    }
  }

  private void housekeeping() {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      logger.yaml(Level.DEBUG, ExecutionContext.getRecordsBag());
    }
    catch (final Throwable t) {
      logger.exception(t);
    }
  }

  Object servletContext;

  private void listAttributes(final ServletContextEvent event) {
    final Enumeration<String> itt = event.getServletContext().getAttributeNames();
    while (itt.hasMoreElements()) {
      final String name = itt.nextElement();

      final Object value = event.getServletContext().getAttribute(name);

      logger.get().debug("{0} {1}", name, value != null ? value.toString() : "null");
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

      listAttributes(event);

      try (final AnnotationManager manager = new AnnotationManager()) {

        final List<String> files = manager.findClassFilesInPackage(Paths.get("com/revenera"));

        for (final String typename : files) {

          final Class<?> type = Class.forName(typename);

          if (type.isAnnotationPresent(GeneratorImplementor.class)) {

            final GeneratorImplementor annotation = type.getAnnotation(GeneratorImplementor.class);

            logger.get().info("found id:{0} name:{1} default:{2} {3}",
                annotation.technologyId(),
                annotation.technologyName(),
                annotation.isDefault(),
                type.getSimpleName());

            if (TechnologyProperties.class.isAssignableFrom(type)) {

              final TechnologyProperties technologyImplementor = (TechnologyProperties) type.newInstance();

              // set up properties from the annotations
              technologyImplementor.configureTechnologyProperties(annotation.technologyId(), annotation.technologyName());

              ExecutionContext.getImplementorFactory().addImplementor(technologyImplementor, annotation.isDefault());

              // let the implementor know it's been registered
              technologyImplementor.registerConfirmation();
            }
            else {
              logger.get().warn("invalid id:{0} name:{1} {2}",
                  annotation.technologyId(),
                  annotation.technologyName(),
                  type.getSimpleName());
            }
          }
        }
      } // close annotation manager

      final LicenseGeneratorServiceInterface implementor = ExecutionContext.getImplementorFactory().getDefaultImplementor();
      if (implementor != null) {
        logger.get().info("default implementor {0}", implementor.getClass().getName());
      }
      else {
        logger.get().warn("no default implementor found");
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

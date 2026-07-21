package com.revenera.gcs;

import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;
import com.revenera.gcs.implementor.GeneratorBase;
import com.revenera.gcs.transaction.DiagnosticsFactory;
import com.revenera.gcs.utils.AnnotationManager;
import com.revenera.gcs.utils.GeneratorImplementor;
import com.revenera.gcs.utils.Utils;
import com.revenera.gcs.logging.Level;
import com.revenera.gcs.logging.LoggingFactory;
import org.apache.commons.io.FileUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * The root of the service, registered as a listener will set stuff up when the context is initialized
 */
@WebListener
public class Application implements
    ServletContextListener,
    ServletContextAttributeListener,
    ServletRequestListener,
    ServletRequestAttributeListener,
    HttpSessionListener,
    HttpSessionAttributeListener,
    HttpSessionIdListener {

  private static final LoggingFactory logger = LoggingFactory.create(Application.class);

  static {
    try {
      final Level level = Level.valueOf(
          Beans.applicationProperties.getLoggingLevel().toUpperCase());

      LoggingFactory.setLoggingLevel(level);

      logger.verbose().log("set logging level to", level.toString());
    }
    catch (final Throwable t) {
      logger.exception(t);
    }
  }

  public Application() {
    logger.in();
    try {
      logger.me(this);

      logger.info().log("version", Beans.applicationProperties.getVersionDetails());
    }
    catch (final Throwable t) {
      logger.exception(t);
    }
    finally {
      logger.out();
    }
  }


  private final Housekeeper housekeeper = new Housekeeper();

  private void serializeToLogPath(final String filename, final List<String> content) {
    try {
//      final String root = Beans.applicationProperties.getLoggingRoot();
      //TODO: log into log directory in web app
      final String root = Beans.getLogPath().toRealPath().toString();

      if (Files.exists(Paths.get(root))) {

        FileUtils.writeLines(
            Paths.get(root, filename).toAbsolutePath().toFile().getAbsoluteFile(),
            content,
            true);
      }
    }
    catch (final Exception e) {
      //TODO:?
    }
  }

  private void polling() {
    try (final DiagnosticsFactory.ExecutionContext context = Beans.diagnosticsFactory.makeExecutionContext()) {
      while (Beans.diagnosticsFactory.hasTransactions()) {
        //TODO:need to depopulate the queue even if not serializing
        final DiagnosticsFactory.TransactionRecord content = Beans.diagnosticsFactory.pollTransactions();
        if (content != null) {
          serializeToLogPath(content.key + ".json",
              Collections.singletonList(
                  Utils.safeSerializeJsonIndented(content)));
        }
      }
    }
    catch (final Throwable t) {
      logger.exception(t);
    }
  }

  private void logging() {
    try (final DiagnosticsFactory.ExecutionContext context = Beans.diagnosticsFactory.makeExecutionContext()) {

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
  }

  private void housekeeping() {
    try (final DiagnosticsFactory.ExecutionContext context = Beans.diagnosticsFactory.makeExecutionContext()) {
      //TODO:what is this supposed to do?
      logger.yaml(Level.DEBUG, Beans.diagnosticsFactory.getRecords());
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

    try {
      Beans.setResourcesRoot(event.getServletContext().getRealPath("/WEB-INF"));

      final AnnotationManager manager = new AnnotationManager();

      final List<String> files = manager.findClassFilesInPackage(GeneratorBase.class);

      for (final String typename : files) {

        final Class<?> type = Class.forName(typename);

        if (type.isAnnotationPresent(GeneratorImplementor.class)) {

          final GeneratorImplementor annotation = type.getAnnotation(GeneratorImplementor.class);

          logger.info().log("found annotation",
              annotation.technologyId(),
              annotation.technologyName(),
              annotation.isDefault(),
              type.getName());

          if (GeneratorBase.class.isAssignableFrom(type)) {

            final GeneratorBase imp = (GeneratorBase) type.newInstance();

            imp.configureTechnologyProperties(annotation.technologyId(), annotation.technologyName());

            Beans.implementorFactory.addImplementor(imp, annotation.isDefault());
          }
        }
      }

      final LicenseGeneratorServiceInterface implementor = Beans.implementorFactory.getDefaultImplementor();
      if (implementor != null) {
        logger.info().log("default implementor", implementor.getClass().getName());
      }
      else {
        throw new RuntimeException("No default implementor found");
      }

      this.housekeeper.initialize();

      this.housekeeper.start(Timers.housekeeping, this::housekeeping, 1, Beans.applicationProperties.getHousekeepingFrequency(), TimeUnit.MINUTES);
      this.housekeeper.start(Timers.logging, this::logging, 1, 1, TimeUnit.SECONDS);
      this.housekeeper.start(Timers.polling, this::polling, 500, 500, TimeUnit.MILLISECONDS);
    }
    catch (final Throwable t) {
      logger.exception(t);
    }
    finally {
      logger.yaml(Level.DEBUG, Beans.getApplicationData());
    }
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

  /*
   * ServletContextAttributeListener
   */
  @Override
  public void attributeAdded(ServletContextAttributeEvent servletContextAttributeEvent) {
    logger.in();
  }

  @Override
  public void attributeRemoved(ServletContextAttributeEvent servletContextAttributeEvent) {
    logger.in();
  }

  @Override
  public void attributeReplaced(ServletContextAttributeEvent servletContextAttributeEvent) {
    logger.in();
  }

  /*
   * ServletContextAttributeListener
   */
  @Override
  public void requestDestroyed(ServletRequestEvent request) {
    logger.in();
  }

  @Override
  public void requestInitialized(ServletRequestEvent request) {
    logger.in();
  }

  /*
   * ServletRequestAttributeListener
   */
  @Override
  public void attributeAdded(ServletRequestAttributeEvent servletRequestAttributeEvent) {
    logger.in();
  }

  @Override
  public void attributeRemoved(ServletRequestAttributeEvent servletRequestAttributeEvent) {
    logger.in();
  }

  @Override
  public void attributeReplaced(ServletRequestAttributeEvent servletRequestAttributeEvent) {
    logger.in();
  }

  /*
   * HttpSessionListener
   */
  @Override
  public void sessionCreated(HttpSessionEvent httpSessionEvent) {
    logger.in();
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
    logger.in();
  }

  /*
   * HttpSessionAttributeListener
   */
  @Override
  public void attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent) {
    logger.in();
  }

  @Override
  public void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent) {
    logger.in();
  }

  @Override
  public void attributeReplaced(HttpSessionBindingEvent httpSessionBindingEvent) {
    logger.in();
  }

  /*
   * HttpSessionIdListener
   */
  @Override
  public void sessionIdChanged(HttpSessionEvent httpSessionEvent, String s) {
    logger.in();
  }
}

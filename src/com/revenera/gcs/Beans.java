package com.revenera.gcs;

import com.revenera.gcs.implementor.ImplementorFactory;
import com.revenera.gcs.logging.Loggable;
import com.revenera.gcs.logging.LoggingFactory;
import com.revenera.gcs.logging.LoggingManager;
import com.revenera.gcs.transaction.DiagnosticsFactory;
import org.apache.commons.lang3.time.StopWatch;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Beans {
  private static final LoggingFactory logger = LoggingFactory.create(Beans.class);

  final static StopWatch stopwatch = new StopWatch();
  final static ApplicationProperties applicationProperties;
  final static ImplementorFactory implementorFactory;
  final static DiagnosticsFactory diagnosticsFactory;
  final static LoggingManager loggingManager;

  static String web_inf;

  static {
    try {
      loggingManager = new LoggingManager();
      applicationProperties = new ApplicationProperties();
      implementorFactory = new ImplementorFactory();
      diagnosticsFactory = new DiagnosticsFactory();
      stopwatch.start();
    }
    finally {
      logger.get().info("Beans initialized...");
    }
  }

  public static void setResourcesRoot(final String value) {
    web_inf = value;
    logger.get().info("resources root set ", getResourcePath().toAbsolutePath().toString());
  }

  public static Path getResourcePath(final String... parts) {
    return Paths.get(web_inf, parts);
  }

  public static Path getLogPath() {
    return getResourcePath("logs");
  }

}

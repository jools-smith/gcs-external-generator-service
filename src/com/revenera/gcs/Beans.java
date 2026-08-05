package com.revenera.gcs;

import com.revenera.gcs.implementor.ImplementorFactory;
import com.revenera.gcs.logging.LoggingContextFactory;
import com.revenera.gcs.logging.LoggingFactory;
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

  // TODO: debug for now...
  final static LoggingContextFactory loggingContextFactory;

  static String web_inf;

  static {
    logger.in();
    stopwatch.start();
    applicationProperties = new ApplicationProperties();
    implementorFactory = new ImplementorFactory();
    diagnosticsFactory = new DiagnosticsFactory();
    loggingContextFactory = new LoggingContextFactory();
  }

  public static void setResourcesRoot(final String value) {
    web_inf = value;
    logger.info().log("resources root set ", getResourcePath().toAbsolutePath().toString());
  }

  public static Path getResourcePath(final String... parts) {
    return Paths.get(web_inf, parts);
  }

  public static Path getLogPath() {
    return getResourcePath("logs");
  }

//  public static AppContext makeContext(final Object self) {
//    return new AppContext(self);
//  }
}

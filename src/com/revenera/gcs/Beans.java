package com.revenera.gcs;

import com.revenera.gcs.implementor.ImplementorFactory;
import com.revenera.gcs.implementor.ImplementorManagement;
import com.revenera.gcs.logging.LoggingFactory;
import com.revenera.gcs.transaction.DiagnosticsFactory;
import com.revenera.gcs.transaction.ExecutionManagement;
import com.revenera.gcs.transaction.TransactionManagement;
import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class Beans {
  private static final LoggingFactory logger = LoggingFactory.create(Beans.class);

  private final static StopWatch stopwatch = new StopWatch();
  private final static ApplicationProperties applicationProperties;
  private final static ImplementorFactory implementorFactory;
  private final static DiagnosticsFactory diagnosticsFactory;

  private static String web_inf;

  static {
    stopwatch.start();
    applicationProperties = new ApplicationProperties();
    implementorFactory = new ImplementorFactory();
    diagnosticsFactory = new DiagnosticsFactory();
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

  public static AppContext makeContext() {
    return new AppContext(
        diagnosticsFactory,
        diagnosticsFactory,
        implementorFactory,
        applicationProperties,
        stopwatch);
  }

  public static Duration getApplicationDuration() {
    return stopwatch.getDuration();
  }


}

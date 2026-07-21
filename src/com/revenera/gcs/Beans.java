package com.revenera.gcs;

import com.revenera.gcs.implementor.ImplementorFactory;
import com.revenera.gcs.transaction.DiagnosticsFactory;
import com.revenera.gcs.logging.LoggingFactory;
import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class Beans {
  private static final LoggingFactory logger = LoggingFactory.create(Beans.class);

  private final static Instant started = Instant.now();

  private static String web_inf;

  public static void setResourcesRoot(final String value) {
    Beans.web_inf = value;

    logger.info().log("resources root set ", getResourcePath());
  }

  public static Path getResourcePath(final String... parts) {
    return Paths.get(web_inf, parts);
  }

  public static final ApplicationProiperties applicationProperties = new ApplicationProiperties();

  public final static ImplementorFactory implementorFactory = new ImplementorFactory();

  public final static DiagnosticsFactory diagnosticsFactory = new DiagnosticsFactory();

  public static Path getLogPath() {
    return Paths.get(web_inf, "logs");
  }

  public static Duration getApplicationDuration() {
    return Duration.between(started, Instant.now());
  }

  public static Object getApplicationData() {

    return new LinkedHashMap<String, Object>() {
      {
        put("build", new LinkedHashMap<String, Object>() {
          {
            final ApplicationProiperties bv = Beans.applicationProperties;

            put("version", bv.getVersion());
            put("timestamp", bv.getTimeStamp());
            put("date", bv.getDate());
            put("time", bv.getTime());
            put("release", bv.getRelease());
            put("user", bv.getUser());
            put("logging_level", bv.getLoggingLevel());
            put("housekeeping", bv.getHousekeepingFrequency());
          }
        });

        put("system", new LinkedHashMap<String, Object>() {
          {
            put("timestamp", Instant.now().toString());
            put("up_time", getApplicationDuration().toString());
            put("user_name", SystemProperties.getUserName("unknown"));
            put("host_name", SystemUtils.getHostName());
            put("resource_path", Beans.getResourcePath().toAbsolutePath().toString());
          }
        });

        final Runtime runtime = Runtime.getRuntime();
        put("environment", new LinkedHashMap<String, Object>() {
          {
            Function<Long, String> tomb = v -> (v / (1024 * 1024)) + "MB";

            put("processors", runtime.availableProcessors());
            put("free_memory", tomb.apply(runtime.freeMemory()));
            put("total_memory", tomb.apply(runtime.totalMemory()));
            put("max_memory", tomb.apply(runtime.maxMemory()));
          }
        });

        put("operating-system", new LinkedHashMap<String, Object>() {
          {
            put("os_name", SystemUtils.OS_NAME);
            put("os_version", SystemUtils.OS_VERSION);
            put("os_arch", SystemUtils.OS_ARCH);
          }
        });

        put("java", new LinkedHashMap<String, Object>() {
          {
            put("java_version", SystemUtils.JAVA_VERSION);
            put("java_vendor", SystemUtils.JAVA_VENDOR);
            put("java_class_version", SystemUtils.JAVA_CLASS_VERSION);
            put("java_vm_name", SystemUtils.JAVA_VM_NAME);
            put("java_vm_info", SystemUtils.JAVA_VM_INFO);
          }
        });

        put("diagnostics", Beans.diagnosticsFactory.getRecords());
      }
    };
  }
}

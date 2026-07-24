package com.revenera.gcs;

import com.revenera.gcs.implementor.ImplementorManagement;
import com.revenera.gcs.transaction.ExecutionManagement;
import com.revenera.gcs.transaction.TransactionManagement;
import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.SystemUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class AppContext implements AutoCloseable {

  final Instant timestamp;
  final StackTraceElement frame;

  AppContext(final int depth) {
    this.timestamp = Instant.now();
    try {
      throw new Exception();
    }
    catch (final Throwable t) {
      frame = t.getStackTrace()[depth];
    }
  }

  @Override
  public void close() {
    Beans.diagnosticsFactory.submitExecutionDetails(timestamp, frame);
  }

  public static Duration getApplicationDuration() {
    return Beans.stopwatch.getDuration();
  }

  public static ApplicationProperties getApplicationProperties() {
    return Beans.applicationProperties;
  }

  public static ImplementorManagement getImplementorFactory() {
    return Beans.implementorFactory;
  }

  public static ExecutionManagement getExecutionManager() {
    return Beans.diagnosticsFactory;
  }

  public static TransactionManagement getTransactionManager() {
    return Beans.diagnosticsFactory;
  }

  public static Map<String, Object> getApplicationData() {

    return new LinkedHashMap<String, Object>() {
      {
        put("build", new LinkedHashMap<String, Object>() {
          {
            final ApplicationProperties bv = getApplicationProperties();

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
            put("up_time", Beans.stopwatch.getDuration().toString());
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

//public class AppContext implements AutoCloseable {
//  public static ThreadLocal<ExecutionManagement> e_manager = new ThreadLocal<>();
//  public static ThreadLocal<TransactionManagement> t_manager = new ThreadLocal<>();
//  public static ThreadLocal<ImplementorManagement> i_manager = new ThreadLocal<>();
//  public static ThreadLocal<ApplicationProperties> properties = new ThreadLocal<>();
//  public static ThreadLocal<StopWatch> stopwatch = new ThreadLocal<>();
//
//  private static ThreadLocal<Instant> timestamp = new ThreadLocal<>();
//  private static ThreadLocal<StackTraceElement> frame = new ThreadLocal<>();
//
//
//  public AppContext(
//      final ExecutionManagement e,
//      final TransactionManagement t,
//      final ImplementorManagement i,
//      final ApplicationProperties ap,
//      final StopWatch sw,
//      final int depth) {
//
//    if (e_manager.get() != null) {
//      throw new RuntimeException("Context has already been initialized");
//    }
//
//    try {
//      e_manager.set(e);
//      t_manager.set(t);
//      i_manager.set(i);
//      properties.set(ap);
//      stopwatch.set(sw);
//      timestamp.set(Instant.now());
//
//      throw new Exception();
//    }
//    catch (final Exception ex) {
//      frame.set(ex.getStackTrace()[depth]);
//    }
//  }
//
//  @Override
//  public void close()  {
//    e_manager.get().submitExecutionDetails(timestamp.get(), frame.get());
//
//    e_manager.remove();
//    t_manager.remove();
//    i_manager.remove();
//    properties.remove();
//    stopwatch.remove();
//    timestamp.remove();
//    frame.remove();
//
//  }
//
//  public static Duration getApplicationDuration() {
//    return stopwatch.get().getDuration();
//  }
//
//  public static ApplicationProperties getApplicationProperties() {
//    return properties.get();
//  }
//
//  public static ImplementorManagement getImplementorFactory() {
//    return i_manager.get();
//  }
//
//  public static ExecutionManagement getExecutionManager() {
//    return e_manager.get();
//  }
//
//  public static TransactionManagement getTransactionManager() {
//    return t_manager.get();
//  }
//
//  public static Map<String, Object> getApplicationData() {
//
//    return new LinkedHashMap<String, Object>() {
//      {
//        put("build", new LinkedHashMap<String, Object>() {
//          {
//            final ApplicationProperties bv = getApplicationProperties();
//
//            put("version", bv.getVersion());
//            put("timestamp", bv.getTimeStamp());
//            put("date", bv.getDate());
//            put("time", bv.getTime());
//            put("release", bv.getRelease());
//            put("user", bv.getUser());
//            put("logging_level", bv.getLoggingLevel());
//            put("housekeeping", bv.getHousekeepingFrequency());
//          }
//        });
//
//        put("system", new LinkedHashMap<String, Object>() {
//          {
//            put("timestamp", Instant.now().toString());
//            put("up_time", stopwatch.get().getDuration().toString());
//            put("user_name", SystemProperties.getUserName("unknown"));
//            put("host_name", SystemUtils.getHostName());
//            put("resource_path", Beans.getResourcePath().toAbsolutePath().toString());
//          }
//        });
//
//        final Runtime runtime = Runtime.getRuntime();
//        put("environment", new LinkedHashMap<String, Object>() {
//          {
//            Function<Long, String> tomb = v -> (v / (1024 * 1024)) + "MB";
//
//            put("processors", runtime.availableProcessors());
//            put("free_memory", tomb.apply(runtime.freeMemory()));
//            put("total_memory", tomb.apply(runtime.totalMemory()));
//            put("max_memory", tomb.apply(runtime.maxMemory()));
//          }
//        });
//
//        put("operating-system", new LinkedHashMap<String, Object>() {
//          {
//            put("os_name", SystemUtils.OS_NAME);
//            put("os_version", SystemUtils.OS_VERSION);
//            put("os_arch", SystemUtils.OS_ARCH);
//          }
//        });
//
//        put("java", new LinkedHashMap<String, Object>() {
//          {
//            put("java_version", SystemUtils.JAVA_VERSION);
//            put("java_vendor", SystemUtils.JAVA_VENDOR);
//            put("java_class_version", SystemUtils.JAVA_CLASS_VERSION);
//            put("java_vm_name", SystemUtils.JAVA_VM_NAME);
//            put("java_vm_info", SystemUtils.JAVA_VM_INFO);
//          }
//        });
//
//        put("diagnostics", e_manager.get().getRecords());
//      }
//    };
//  }
//}

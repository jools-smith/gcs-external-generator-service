package com.revenera.gcs;

import com.revenera.gcs.implementor.ImplementorManagement;
import com.revenera.gcs.logging.LoggingFactory;
import com.revenera.gcs.transaction.ExecutionManagement;
import com.revenera.gcs.transaction.TransactionManagement;
import com.revenera.gcs.utils.Frame;
import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.SystemUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;


public class AppContext implements AutoCloseable {
  private static final LoggingFactory logger = LoggingFactory.create(AppContext.class);

  static class Data {
    Map.Entry<Class<?>, Object> request;
    Map.Entry<Class<?>, Object> response;
    final List<Map.Entry<Class<?>, Object>> payload = new LinkedList<>();
    final Instant timestamp = Instant.now();
    final Frame frame;

    Data(final Frame frame) {
      this.frame = frame;
    }

    boolean hasData() {
      return this.request != null || this.response != null || !this.payload.isEmpty();
    }
  }

  static final ThreadLocal<Data> context = new ThreadLocal<>();

  AppContext(final int depth) {
    context.set(new Data(new Frame(depth)));
  }

  @Override
  public void close() {
    final Data data = context.get();

    Beans.diagnosticsFactory.submitExecutionDetails(data.timestamp, data.frame);

    if (data.hasData()) {
      Beans.diagnosticsFactory.submitTransaction(data.frame, data.timestamp, data.request, data.response, data.payload);
    }

    // clear down thread data
    context.remove();
  }

  public static void injectRequest(final Object data) {
    context.get().request = new AbstractMap.SimpleImmutableEntry<>(data.getClass(), data);
  }

  public static void injectPayload(final Object data) {
    context.get().payload.add(new AbstractMap.SimpleImmutableEntry<>(data.getClass(), data));
  }

  public static <T> T injectResponse(final Class<T> type, final T data) {

    final Class<?> clazz = type.equals(data.getClass().getSuperclass()) ? type : data.getClass();

    context.get().response = new AbstractMap.SimpleImmutableEntry<>(clazz, data);

    return data;
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
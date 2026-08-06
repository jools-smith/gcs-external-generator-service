package com.revenera.gcs;

import com.revenera.gcs.implementor.ImplementorManagement;
import com.revenera.gcs.logging.LoggingManager;
import com.revenera.gcs.transaction.ExecutionManagement;
import com.revenera.gcs.transaction.ExecutionRecord;
import com.revenera.gcs.transaction.TransactionManagement;
import com.revenera.gcs.utils.Frame;
import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class DataObject {
  public final Frame frame;
  public final Class<?> type;
  public final Object data;

  DataObject(final Frame frame, final Object data, final Class<?> type) {
    this.frame = frame;
    this.data = data;
    this.type = type;
  }

  DataObject(final Frame frame, final Object data) {
    this(frame, data, data.getClass());
  }
}

class Transaction {
  final Frame frame;
  final Instant timestamp = Instant.now();

  DataObject request;
  DataObject response;
  List<DataObject> payload;

  Transaction(final Frame frame) {
    this.frame = frame;
  }

  boolean hasData() {
    return this.request != null || this.response != null || this.payload != null;
  }
}

public class ExecutionContext implements AutoCloseable {

  static final ThreadLocal<LinkedList<Transaction>> context = new ThreadLocal<>();

  private final boolean serialize;

  public ExecutionContext() {
    this(true);
  }

  public ExecutionContext(final boolean serialize) {

    this.serialize = serialize;

    if (context.get() == null) {
      context.set(new LinkedList<>());
    }

    context.get().add(new Transaction(new Frame(Frame.Depth.ONE)));
  }

  @Override
  public void close() {
    final Transaction transaction = context.get().removeLast();

    if (this.serialize) {
      Beans.diagnosticsFactory.submitExecutionDetails(transaction.timestamp, transaction.frame);

      if (transaction.hasData()) {
        Beans.diagnosticsFactory.submitTransaction(
            transaction.frame,
            transaction.timestamp,
            transaction.request,
            transaction.response,
            transaction.payload);
      }
    }

    if (context.get().isEmpty()) {
      // clear down thread data
      context.remove();
    }
  }

  @SuppressWarnings("UnusedReturnValue")
  public static <T> T injectRequest(final T data) {
    context.get().getLast().request = new DataObject(new Frame(Frame.Depth.ONE), data);

    return data;
  }

  @SuppressWarnings("UnusedReturnValue")
  public static <T> T injectPayload(final T data) {
    final Transaction ctx = context.get().getLast();

    if (ctx.payload == null) {
      ctx.payload = new LinkedList<>();
    }

    ctx.payload.add(new DataObject(new Frame(Frame.Depth.ONE), data));

    return data;
  }

  public static <T> T injectResponse(final T data) {

    context.get().getLast().response = new DataObject(new Frame(Frame.Depth.ONE), data);

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

  public static LoggingManager getLoggingManager() {
    return  Beans.loggingManager;
  }

  public static Path getLogPath() {
    return Beans.getLogPath();
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

        put("diagnostics", Beans.diagnosticsFactory.getRecords().stream()
            .sorted(Comparator.comparing(ExecutionRecord::getUpdated).reversed())
            .collect(Collectors.toList()));

      }
    };
  }
}
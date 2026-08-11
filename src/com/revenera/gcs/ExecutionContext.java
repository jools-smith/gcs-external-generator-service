package com.revenera.gcs;

import com.revenera.gcs.implementor.ImplementorManagement;
import com.revenera.gcs.logging.LoggingManager;
import com.revenera.gcs.transaction.ExecutionManagement;
import com.revenera.gcs.transaction.ExecutionRecord;
import com.revenera.gcs.transaction.TransactionManagement;
import com.revenera.gcs.utils.Frame;
import com.revenera.gcs.utils.HandyBag;
import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

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

  private ExecutionContext(final Frame frame, final boolean serialize) {
    this.serialize = serialize;

    if (context.get() == null) {
      context.set(new LinkedList<>());
    }
    context.get().add(new Transaction(frame));
  }

  public ExecutionContext() {
    this(new Frame(Frame.Depth.ONE), true);
  }

//  private ExecutionContext(final boolean serialize) {
//    this(new Frame(Frame.Depth.ONE), serialize);
//  }

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

  @SuppressWarnings("unused")
  public static ExecutionManagement getExecutionManager() {
    return Beans.diagnosticsFactory;
  }

  public static TransactionManagement getTransactionManager() {
    return Beans.diagnosticsFactory;
  }

  public static LoggingManager getLoggingManager() {
    return Beans.loggingManager;
  }

  public static Path getLogPath() {
    return Beans.getLogPath();
  }

  public static HandyBag getRecordsBag() {
    final HandyBag bag = new HandyBag();
    Beans.diagnosticsFactory.getRecords().stream()
        .sorted(Comparator.comparing(ExecutionRecord::getUpdated).reversed())
        .forEach(x ->
            bag.beginSection(x.getMethod())
                .with("visits", x.getCount())
                .with("mean latency", x.getMeanLatency())
                .with("total sojourn", x.getTotalDuration()));

    return bag;
  }

  public static HandyBag getApplicationData() {
    final ApplicationProperties bv = getApplicationProperties();

    final Runtime runtime = Runtime.getRuntime();

    final Function<Long, String> tomb = v -> (v / (1024 * 1024)) + "MB";

    return new HandyBag()
        .beginSection("build")
        .with("version", bv.getVersion())
        .with("timestamp", bv.getTimestamp())
        .with("date", bv.getDate())
        .with("time", bv.getTime())
        .with("release", bv.getRelease())
        .with("user", bv.getUser())
        .with("logging_level", bv.getLoggingLevel())
        .with("housekeeping-frequency", bv.getHousekeepingFrequency())
        .with("echo-log", bv.getLoggingEcho())
        .endSection()

        .beginSection("system")
        .with("timestamp", Instant.now().toString())
        .with("up_time", Beans.stopwatch.getDuration().toString())
        .with("user_name", SystemProperties.getUserName("unknown"))
        .with("host_name", SystemUtils.getHostName())
        .with("resource_path", Beans.getResourcePath().toAbsolutePath().toString())
        .endSection()

        .beginSection("environment")
        .with("processors", runtime.availableProcessors())
        .with("free_memory", tomb.apply(runtime.freeMemory()))
        .with("total_memory", tomb.apply(runtime.totalMemory()))
        .with("max_memory", tomb.apply(runtime.maxMemory()))
        .endSection()

        .beginSection("environment")
        .with("os_name", SystemUtils.OS_NAME)
        .with("os_version", SystemUtils.OS_VERSION)
        .with("os_arch", SystemUtils.OS_ARCH)
        .endSection()

        .beginSection("environment")
        .with("java_version", SystemUtils.JAVA_VERSION)
        .with("java_vendor", SystemUtils.JAVA_VENDOR)
        .with("java_class_version", SystemUtils.JAVA_CLASS_VERSION)
        .with("java_vm_name", SystemUtils.JAVA_VM_NAME)
        .with("java_vm_info", SystemUtils.JAVA_VM_INFO)
        .endSection()

        .with("diagnostics", getRecordsBag());
  }
}

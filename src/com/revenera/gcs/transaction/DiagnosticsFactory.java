package com.revenera.gcs.transaction;

import com.revenera.gcs.logging.LoggingFactory;
import com.revenera.gcs.utils.Frame;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DiagnosticsFactory implements TransactionManagement, ExecutionManagement {
  private static final LoggingFactory logger = LoggingFactory.create(DiagnosticsFactory.class);

  private final List<ExecutionRecord> records = new LinkedList<>();

  private final Queue<Map.Entry<Object, Object>> transactions = new ConcurrentLinkedQueue<>();

  @Override
  public List<ExecutionRecord> getRecords() {
    return this.records;
  }

  @Override
  public boolean hasRecords() {
    return !this.records.isEmpty();
  }

  @Override
  public void submitExecutionDetails(final Instant timestamp, final Frame frame) {
    final Duration dur =  Duration.between(timestamp, Instant.now());

    final Optional<ExecutionRecord> item = records.stream()
        .filter(e -> e.matches(frame))
        .findFirst();

    if (item.isPresent()) {
      item.get().touch(dur);
    }
    else {
      records.add(new ExecutionRecord(frame).touch(dur));
    }
  }


  @Override
  public Map.Entry<Object, Object> pollTransactions() {
    return this.transactions.poll();
  }

  @Override
  public boolean hasTransactions() {
    return !this.transactions.isEmpty();
  }

  private static String getSimpleClassName(final StackTraceElement frame) {
    String classname = frame.getClassName();
    try {
      classname = Class.forName(frame.getClassName()).getSimpleName();
    }
    catch (final ClassNotFoundException e) {
      logger.exception(e);
    }
    return classname;
  }

  enum TransTypes {
    frame, start, duration, request, response, payload
  }
  @Override
  public void submitTransaction(final Frame frame, final Instant start, final Object request, final Object response,final List<Map.Entry<Class<?>, Object>> payload ) {

    final String classname = frame.getSimpleClassName();
    final String method = frame.getMethodName();

    final String key = String.join(".",
        start.toString()
            .replace('T', '.')
            .replace(':', '.')
            .replace('-', '.')
            .replace("Z", ""),
        classname,
        method);

    transactions.offer(new AbstractMap.SimpleEntry<>(key, new LinkedHashMap<Object, Object>() {
      {
        put(TransTypes.frame, frame);
        put(TransTypes.start, start.toString());
        put(TransTypes.duration, Duration.between(Instant.now(), start).toNanos() / 1_000_000_000.0);

        Optional.ofNullable(request).ifPresent(e -> put(TransTypes.request, request));

        Optional.ofNullable(response).ifPresent(e -> put(TransTypes.response, response));

        Optional.ofNullable(payload).ifPresent(e -> {
          if (!payload.isEmpty()) {
            put(TransTypes.payload, request);
          }
        });
      }
    }));
  }
}
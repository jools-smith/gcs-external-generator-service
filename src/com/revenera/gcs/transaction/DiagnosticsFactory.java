package com.revenera.gcs.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revenera.gcs.logging.LoggingFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class DiagnosticsFactory {
  private static final LoggingFactory logger = LoggingFactory.create(DiagnosticsFactory.class);

  public static class ExecutionRecord {
    private final String key;
    private Duration duration;
    private long count;

    private static String makeKey(final StackTraceElement element) {
      return element.getClassName() + "." + element.getMethodName() + ":" + element.getLineNumber();
    }

    private ExecutionRecord(final StackTraceElement element) {
      this.key = makeKey(element);
      this.duration = Duration.ZERO;
      this.count = 0;
    }

    boolean matches(final StackTraceElement element) {
      return this.key.equals(makeKey(element));
    }

    @JsonIgnore
    ExecutionRecord touch(final Duration duration) {
      this.duration = this.duration.plus(duration);
      this.count++;
      return this;
    }

    public String getKey() {
      return this.key;
    }

    public double getDuration() {
      return this.duration.toNanos() / 1_000_000_000.0;
    }

    public long getCount() {
      return this.count;
    }

    public double getMean() {
      return getDuration() / this.count;
    }
  }

  public class ExecutionContext implements AutoCloseable {
    private final Instant timestamp;
    private final StackTraceElement frame;

    private ExecutionContext(final int depth) {
      this.timestamp = Instant.now();

      try {
        throw new Exception();
      }
      catch (final Exception e) {
        this.frame = e.getStackTrace()[depth];
      }
    }

    @Override
    public void close() {
      final Duration dur =  Duration.between(timestamp, Instant.now());

      final Optional<ExecutionRecord> item = records.stream()
          .filter(e -> e.matches(this.frame))
          .findFirst();

      if (item.isPresent()) {
        item.get().touch(dur);
      }
      else {
        records.add(new ExecutionRecord(this.frame).touch(dur));
      }
    }
  }

  public static class TransactionRecord {
    public String key;
    public String started;
    public double duration;
    public Map<String, Object> payload;
  }

  public class TransactionContext implements AutoCloseable {
    private final Instant timestamp = Instant.now();;
    private final StackTraceElement frame;
    private final Map<Class<?>, Object> payload = new LinkedHashMap<>();

    private TransactionContext(final int depth) {
      try {
        throw new Exception();
      }
      catch (final Exception e) {
        this.frame = e.getStackTrace()[depth];
      }
    }

    public final String getKey() {
      String classname = frame.getClassName();
      try {
        classname = Class.forName(this.frame.getClassName()).getSimpleName();
      }
      catch (final ClassNotFoundException ignored) {

      }
      return String.join(".",
          this.timestamp.toString()
              .replace('T', '.')
              .replace(':', '.')
              .replace('-', '.')
              .replace("Z", ""),
          classname,
          this.frame.getMethodName());
    }

    public Object add(final Object obj) {
      this.payload.put(obj.getClass(), obj);
      return obj;
    }

    public <T> T add(Class<T> type, final T t) {
      this.payload.put(t.getClass(), t);
      return t;
    }

    @JsonIgnore
    TransactionRecord asTransactionRecord() {
      return new TransactionRecord() {
        {
          this.key = TransactionContext.this.getKey();
          this.started = TransactionContext.this.timestamp.toString();
          this.duration = Duration.between(Instant.now(), TransactionContext.this.timestamp).toNanos() / 1_000_000_000.0;
          this.payload = TransactionContext.this.payload.entrySet().stream()
              .collect(Collectors.toMap(
                  x -> x.getKey().getTypeName(),
                  Map.Entry::getValue,
                  (v1, v2) -> v1,
                  LinkedHashMap::new));
        }
      };
    }

    @Override
    public void close() {
      transactions.offer(asTransactionRecord());
    }
  }

  private final List<ExecutionRecord> records = new LinkedList<>();

  private final Queue<TransactionRecord> transactions = new ConcurrentLinkedQueue<>();

  public List<ExecutionRecord> getRecords() {
    return this.records;
  }

  /** for calls at arbitrary depth */
  public ExecutionContext makeExecutionContext(final int depth) {
    return new ExecutionContext(depth + 3);
  }

  /** for direct calls */
  public ExecutionContext makeExecutionContext() {
    return new ExecutionContext(3);
  }

  public TransactionContext makeTransactionContext() {
    return new TransactionContext(3);
  }

  public TransactionContext makeTransactionContext(final int depth) {
    return new TransactionContext(depth + 3);
  }

  public TransactionRecord pollTransactions() {
    return this.transactions.poll();
  }

  public boolean hasTransactions() {
    return !this.transactions.isEmpty();
  }
}
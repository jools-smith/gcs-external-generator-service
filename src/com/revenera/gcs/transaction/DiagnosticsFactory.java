package com.revenera.gcs.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revenera.gcs.logging.LoggingFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

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

  public void submitExecutionContext(final ExecutionScope context) {
    final Duration dur =  Duration.between(context.getTimestamp(), Instant.now());

    final Optional<DiagnosticsFactory.ExecutionRecord> item = records.stream()
        .filter(e -> e.matches(context.getFrame()))
        .findFirst();

    if (item.isPresent()) {
      item.get().touch(dur);
    }
    else {
      records.add(new DiagnosticsFactory.ExecutionRecord(context.getFrame()).touch(dur));
    }
  }

  public void submitTransactionContext(final TransactionRecord record) {
    transactions.offer(record);
  }

  public static class TransactionRecord {
    public String key;
    public String started;
    public double duration;
    public Map<String, Object> payload;
  }



  private final List<ExecutionRecord> records = new LinkedList<>();

  private final Queue<TransactionRecord> transactions = new ConcurrentLinkedQueue<>();

  public List<ExecutionRecord> getRecords() {
    return this.records;
  }

  /** for calls at arbitrary depth */
  public ExecutionScope makeExecutionContext(final int depth) {
    return new ExecutionScope(this,depth + 2);
  }

  /** for direct calls */
  public ExecutionScope makeExecutionContext() {
    return new ExecutionScope(this,2);
  }

  public TransactionScope makeTransactionContext() {
    return new TransactionScope(this,2);
  }

  public TransactionScope makeTransactionContext(final int depth) {
    return new TransactionScope(this,depth + 2);
  }

  public TransactionRecord pollTransactions() {
    return this.transactions.poll();
  }

  public boolean hasTransactions() {
    return !this.transactions.isEmpty();
  }
}
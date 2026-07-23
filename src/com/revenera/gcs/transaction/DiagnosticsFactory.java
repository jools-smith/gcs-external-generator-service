package com.revenera.gcs.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revenera.gcs.logging.LoggingFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DiagnosticsFactory implements TransactionManagement, ExecutionManagement {
  private static final LoggingFactory logger = LoggingFactory.create(DiagnosticsFactory.class);

  private final List<ExecutionRecord> records = new LinkedList<>();

  private final Queue<TransactionRecord> transactions = new ConcurrentLinkedQueue<>();

  @Override
  public List<ExecutionRecord> getRecords() {
    return this.records;
  }

  @Override
  public void submitExecutionScope(final ExecutionScope context) {
    final Duration dur =  Duration.between(context.getTimestamp(), Instant.now());

    final Optional<ExecutionRecord> item = records.stream()
        .filter(e -> e.matches(context.getFrame()))
        .findFirst();

    if (item.isPresent()) {
      item.get().touch(dur);
    }
    else {
      records.add(new ExecutionRecord(context.getFrame()).touch(dur));
    }
  }

  @Override
  public ExecutionScope makeExecutionScope() {
    return new ExecutionScope(this,2);
  }

  @Override
  public TransactionScope makeTransactionScope() {
    return new TransactionScope(this,2);
  }

  @Override
  public TransactionRecord pollTransactions() {
    return this.transactions.poll();
  }

  @Override
  public boolean hasTransactions() {
    return !this.transactions.isEmpty();
  }
  @Override
  public void submitTransactionContext(final TransactionRecord record) {
    transactions.offer(record);
  }
}
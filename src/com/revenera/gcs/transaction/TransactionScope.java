package com.revenera.gcs.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionScope implements AutoCloseable {
  private final Instant timestamp = Instant.now();;
  private final StackTraceElement frame;

  private final DiagnosticsFactory factory;

  public TransactionScope(final DiagnosticsFactory factory, final int depth) {
    this.factory = factory;
    try {
      throw new Exception();
    }
    catch (final Exception e) {
      this.frame = e.getStackTrace()[depth];
    }
    //TODO:
    TransactionContext.set(new TransactionData());
  }

  @JsonIgnore
  private final String getKey() {
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

  @JsonIgnore
  private TransactionRecord asTransactionRecord() {
    return new TransactionRecord(
        TransactionScope.this.getKey(),
        TransactionScope.this.timestamp.toString(),
        Duration.between(Instant.now(), TransactionScope.this.timestamp).toNanos() / 1_000_000_000.0,
        TransactionContext.get().getPayload().entrySet().stream()
            .collect(Collectors.toMap(
                x -> x.getKey().getTypeName(),
                Map.Entry::getValue,
                (v1, v2) -> v1,
                LinkedHashMap::new)));
  }

  @Override
  public void close() {
    this.factory.submitTransactionContext(asTransactionRecord());
  }
}

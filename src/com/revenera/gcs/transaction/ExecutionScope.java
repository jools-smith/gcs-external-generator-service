package com.revenera.gcs.transaction;

import java.time.Instant;

public class ExecutionScope implements AutoCloseable {

  private final Instant timestamp;
  private final StackTraceElement frame;
  private final DiagnosticsFactory factory;

  public ExecutionScope(final DiagnosticsFactory factory, final int depth) {
    this.timestamp = Instant.now();
    this.factory = factory;
    try {
      throw new Exception();
    }
    catch (final Exception e) {
      this.frame = e.getStackTrace()[depth];
    }
  }

  public Instant getTimestamp() {
    return this.timestamp;
  }

  public StackTraceElement getFrame() {
    return this.frame;
  }

  @Override
  public void close() {
    this.factory.submitExecutionContext(this);
  }
}
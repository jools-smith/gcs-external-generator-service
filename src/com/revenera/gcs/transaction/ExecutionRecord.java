package com.revenera.gcs.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revenera.gcs.utils.Frame;

import java.time.Duration;

public class ExecutionRecord {
  private final String key;
  private Duration duration;
  private long count;

  private static String makeKey(final Frame element) {
    return element.getClassName() + "." + element.getMethodName() + ":" + element.getLineNumber();
  }

  ExecutionRecord(final Frame element) {
    this.key = makeKey(element);
    this.duration = Duration.ZERO;
    this.count = 0;
  }

  @JsonIgnore
  public boolean matches(final Frame element) {
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

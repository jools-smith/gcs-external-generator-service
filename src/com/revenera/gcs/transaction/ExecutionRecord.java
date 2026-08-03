package com.revenera.gcs.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.revenera.gcs.utils.Frame;

import java.time.Duration;
import java.time.Instant;

@JsonPropertyOrder({
    "method",
    "totalDuration",
    "count",
    "meanLatency",
    "timeSinceUpdate"
})
public class ExecutionRecord {
  private final String key;
  private Duration duration;
  private long count;
  private Instant updated;

  private static String makeKey(final Frame element) {
    return String.format("%s.%s(%d)",
        element.getSimpleClassName(),
        element.getMethodName(),
        element.getLineNumber());
  }

  ExecutionRecord(final Frame element) {
    this.key = makeKey(element);
    this.duration = Duration.ZERO;
    this.count = 0;
    this.updated = Instant.now();
  }

  @JsonIgnore
  public boolean matches(final Frame element) {
    return this.key.equals(makeKey(element));
  }

  @JsonIgnore
  ExecutionRecord touch(final Duration duration) {
    this.duration = this.duration.plus(duration);
    this.count++;
    this.updated = Instant.now();
    return this;
  }

  @JsonIgnore
  public Instant getUpdated() {
    return this.updated;
  }

  public String getMethod() {
    return this.key;
  }

  public double getTotalDuration() {
    return this.duration.toNanos() / 1_000_000_000.0;
  }

  @SuppressWarnings("unused")
  public long getCount() {
    return this.count;
  }
  @SuppressWarnings("unused")
  public double getMeanLatency() {
    return getTotalDuration() / this.count;
  }

  @SuppressWarnings("unused")
  public String getTimeSinceUpdate() {
    return Duration.between(this.updated, Instant.now()).toString();
  }
}

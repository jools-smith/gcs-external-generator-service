package com.revenera.gcs.transaction;

import java.util.Map;

public class TransactionRecord {
  public final String key;
  public final String started;
  public final double duration;
  public final Map<String, Object> payload;

  TransactionRecord(final String key, final String started, final double duration, Map<String, Object> payload) {
    this.key = key;
    this.started = started;
    this.duration = duration;
    this.payload = payload;
  }
}

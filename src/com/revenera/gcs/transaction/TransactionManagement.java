package com.revenera.gcs.transaction;

import com.revenera.gcs.utils.Frame;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface TransactionManagement {
  boolean hasTransactions();
  Map.Entry<String, Object> pollTransactions();
  void submitTransaction(final Frame frame, final Instant start, final List<Map.Entry<Class<?>, Object>> payload);
}

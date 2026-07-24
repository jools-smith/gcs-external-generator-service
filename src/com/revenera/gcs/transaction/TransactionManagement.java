package com.revenera.gcs.transaction;

import com.revenera.gcs.utils.Frame;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface TransactionManagement {
  boolean hasTransactions();
  Map.Entry<Object, Object> pollTransactions();
  void submitTransaction(
      final Frame frame,
      final Instant start,
      final Object request,
      final Object response,
      final List<Map.Entry<Class<?>, Object>> payload);
}

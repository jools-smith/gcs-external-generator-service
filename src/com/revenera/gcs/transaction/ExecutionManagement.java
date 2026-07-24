package com.revenera.gcs.transaction;

import java.time.Instant;
import java.util.List;

public interface ExecutionManagement {
  List<ExecutionRecord> getRecords();

  void submitExecutionDetails(final Instant timestamp, final StackTraceElement frame);
}

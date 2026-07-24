package com.revenera.gcs.transaction;

import com.revenera.gcs.utils.Frame;

import java.time.Instant;
import java.util.List;

public interface ExecutionManagement {
  List<ExecutionRecord> getRecords();
  boolean hasRecords();
  void submitExecutionDetails(final Instant timestamp, final Frame frame);
}

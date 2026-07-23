package com.revenera.gcs.transaction;

import java.util.List;

public interface ExecutionManagement {
  void submitExecutionScope(final ExecutionScope context);
  ExecutionScope makeExecutionScope();
  List<ExecutionRecord> getRecords();
}

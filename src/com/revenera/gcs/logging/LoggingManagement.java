package com.revenera.gcs.logging;

public interface LoggingManagement {
  boolean willLog(Level level);

  void setLevel(Level level);

  Level getLevel();
}

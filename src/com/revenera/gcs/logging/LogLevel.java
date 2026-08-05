package com.revenera.gcs.logging;

public enum LogLevel {
  ALL(0), ERROR(1), WARN(2), INFO(4), FULL(8), TRACE(16), DEBUG(32);

  final int level;
  LogLevel(final int level) {
    this.level = level;
  }
}

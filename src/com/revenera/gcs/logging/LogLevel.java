package com.revenera.gcs.logging;

public enum LogLevel {
  ALL(0), ERROR(1), WARN(2), INFO(4), DEBUG(8), VERBOSE(16), TRACE(32);

  final int level;
  LogLevel(final int level) {
    this.level = level;
  }
}

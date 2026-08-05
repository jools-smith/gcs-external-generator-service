package com.revenera.gcs.logging;

public interface Logging {
  void log(LogLevel level, String message, Object... params);

  void exception(Throwable t);
}

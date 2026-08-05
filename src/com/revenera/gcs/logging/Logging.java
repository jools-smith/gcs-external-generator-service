package com.revenera.gcs.logging;

public interface Logging {
  void log(LogLevel level, String message, Object... params);

  void error(String message, Object... params);
  void warn(String message, Object... params);
  void info(String message, Object... params);
  void full(String message, Object... params);
  void debug(String message, Object... params);
  void trace(String message, Object... params);


  void exception(Throwable t);
}

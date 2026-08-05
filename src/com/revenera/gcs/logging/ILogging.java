package com.revenera.gcs.logging;

public interface ILogging {
  void log(String message);

  void log(Object... params);

  void log(Throwable t);

  /* TODO: introduce later
  void log(Level level, String format, Object...params);

  default void error(String format, Object... params) {
    log(Level.ERROR, format, params);
  }

  default void warn(String format, Object... params) {
    log(Level.WARN, format, params);
  }

  default void info(String format, Object... params) {
    log(Level.INFO, format, params);
  }

  default void full(String format, Object... params) {
    log(Level.FULL, format, params);
  }

  default void debug(String format, Object... params) {
    log(Level.DEBUG, format, params);
  }
  **/
}


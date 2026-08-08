package com.revenera.gcs.logging;

public interface Logging {
  @SuppressWarnings("unused")
  default void all(String message) {
    log(Level.ALL, "{0}", message);
  }
  @SuppressWarnings("unused")
  default void error(String message) {
    log(Level.ERROR, "{0}", message);
  }
  @SuppressWarnings("unused")
  default void warn(String message) {
    log(Level.WARN, "{0}", message);
  }
  @SuppressWarnings("unused")
  default void info(String message) {
    log(Level.INFO, "{0}", message);
  }
  @SuppressWarnings("unused")
  default void full(String message) {
    log(Level.FULL, "{0}", message);
  }
  @SuppressWarnings("unused")
  default void trace(String message) {
    log(Level.TRACE, "{0}", message);
  }

  default void debug(String message) {
    log(Level.DEBUG, "{0}", message);
  }

  void exception(Throwable t);

  void array(Level level, String caption, Object...params);

  void log(Level level, String format, Object...params);
  @SuppressWarnings("unused")
  default void all(String format, Object... params) {
    log(Level.ALL, format, params);
  }

  default void error(String format, Object... params) {
    log(Level.ERROR, format, params);
  }

  default void warn(String format, Object... params) {
    log(Level.WARN, format, params);
  }

  default void info(String format, Object... params) {
    log(Level.INFO, format, params);
  }
  @SuppressWarnings("unused")
  default void full(String format, Object... params) {
    log(Level.FULL, format, params);
  }
  @SuppressWarnings("unused")
  default void trace(String format, Object... params) {
    log(Level.TRACE, format, params);
  }

  default void debug(String format, Object... params) {
    log(Level.DEBUG, format, params);
  }
}


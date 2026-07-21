package com.revenera.gcs.logging;

public interface ILogging {
  void log(String message);

  void log(Object... params);

  void log(Throwable t);
}


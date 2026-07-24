package com.revenera.gcs.utils;

import com.revenera.gcs.logging.LoggingFactory;

public class Frame {
  private static final LoggingFactory logger = LoggingFactory.create(Frame.class);

  final StackTraceElement frame;

  public Frame(final int depth) {
    this.frame = Thread.currentThread().getStackTrace()[depth + 2];
  }

  public String getClassName() {
    return frame.getClassName();
  }

  public String getMethodName() {
    return frame.getMethodName();
  }

  public String getFileName() {
    return frame.getFileName();
  }

  public int getLineNumber() {
    return frame.getLineNumber();
  }

  public String getSimpleClassName() {
    String classname = frame.getClassName();
    try {
      classname = Class.forName(frame.getClassName()).getSimpleName();
    }
    catch (final ClassNotFoundException e) {
      logger.exception(e);
    }
    return classname;
  }
}

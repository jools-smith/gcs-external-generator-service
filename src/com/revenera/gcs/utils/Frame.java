package com.revenera.gcs.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.revenera.gcs.logging.LoggingFactory;

@JsonPropertyOrder({
    "className",
    "methodName",
    "lineNumber"
})
public class Frame {
  private static final LoggingFactory logger = LoggingFactory.create(Frame.class);

  private final StackTraceElement frame;

  public Frame(final int depth) {
    this.frame = Thread.currentThread().getStackTrace()[depth + 2];
  }

  @JsonIgnore
  public String getClassName() {
    return frame.getClassName();
  }

  public String getMethodName() {
    return frame.getMethodName();
  }

  @JsonIgnore
  public String getFileName() {
    return frame.getFileName();
  }

  public int getLineNumber() {
    return frame.getLineNumber();
  }

  @JsonProperty("className")
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

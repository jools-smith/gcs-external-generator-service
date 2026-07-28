package com.revenera.gcs.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.revenera.gcs.logging.LoggingFactory;

import java.util.Arrays;
import java.util.stream.Stream;

@JsonPropertyOrder({
    "className",
    "methodName",
    "lineNumber"
})
public class Frame {
  public static final short ONE = 1;
  public static final short TWO = 2;
  public static final short THREE = 3;
  public static final short FOUR = 4;
  public static final short FIVE = 5;


  private static final LoggingFactory logger = LoggingFactory.create(Frame.class);

  private final StackTraceElement frame;

  public Frame(final short depth) {
    this.frame = Thread.currentThread().getStackTrace()[depth];
  }

  public Frame(final String fqcn) {
    this.frame = getFrame(fqcn);
  }

  private StackTraceElement getFrame(final String fqcn) {
    final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
    for (short i = 2; i < stack.length; i++) {
      if (stack[i].getClassName().equals(fqcn)) {
        return stack[i];
      }
    }
    throw new IllegalArgumentException("No frame found for " + fqcn);
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

package com.revenera.gcs.logging;

public enum Level {
  ERROR(0, "ERROR"),
  WARNING(1, "WARN"),
  INFO(2, "INFO"),
  DEBUG(4, "DEBUG"),
  TRACE(8, "TRACE"),
  // catchall
  ALL(Integer.MAX_VALUE, "ALL");

  private final int value;
  private final String text;

  Level(final int value, final String text) {
    this.value = value;
    this.text = text;
  }

  public int getValue() {
    return value;
  }

  @SuppressWarnings("unused")
  public String getText() {
    return text;
  }

  public int compare(final Level level) {
    return this.value - level.value;
  }
}

package com.revenera.gcs.logging;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public final class LoggingManager implements LoggingManagement, LoggingQueue {

  final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();

  final AtomicReference<Level> loggingLevel = new AtomicReference<>(Level.TRACE);

  // LoggingManagement
  @Override
  public boolean willLog(final Level level) {
    return loggingLevel.get().compare(level) >= 0;
  }

  @Override
  public void setLevel(Level level) {
    loggingLevel.getAndSet(level);
  }

  @Override
  public Level getLevel() {
    return loggingLevel.get();
  }

  // LoggingQueue
  @Override
  public boolean isEmpty() {
    return messageQueue.isEmpty();
  }

  @Override
  public String peekMessage() {
    return messageQueue.peek();
  }

  @Override
  public String popMessage() {
    return messageQueue.poll();
  }

  @Override
  public String postMessage(String message) {
    messageQueue.offer(message);
    return message;
  }
}

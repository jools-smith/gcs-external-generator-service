package com.revenera.gcs.logging;

import com.revenera.gcs.utils.Frame;
import com.revenera.gcs.utils.Serializer;
import org.apache.commons.lang3.ClassUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import java.util.Formatter;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class LoggingFactory {
  static final DateTimeFormatter tomcat_formatter = DateTimeFormatter
      .ofPattern("dd-MMM-yyyy HH:mm:ss.SSS", Locale.ENGLISH)
      .withZone(ZoneId.systemDefault());

  @SuppressWarnings("unused")
  static final DateTimeFormatter zulu_formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
          .withZone(ZoneId.systemDefault());

  static final Object lock = new Object();

  static final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();

  static final AtomicReference<Level> loggingLevel = new AtomicReference<>(Level.TRACE);

  // genuinely is an inner class, cannot be static
  private class InnerLogging implements ILogging {
    final Instant time = Instant.now();
    final Level level;
    final Frame frame;

    InnerLogging(final Level level) {
      this.level = level;
      this.frame = new Frame(LoggingFactory.this.type);
    }

    @SuppressWarnings("SameParameterValue")
    private String makeLogMessage(final DateTimeFormatter timeFormatter, final String message) {

      final Appendable appendable = new StringBuilder();

      try (final Formatter formatter = new Formatter(appendable)) {

        final String className = LoggingFactory.this.type.getCanonicalName().equals(this.frame.getClassName()) ?
            LoggingFactory.this.type.getSimpleName() :
            ClassUtils.getAbbreviatedName(this.frame.getClassName(), 32);

        formatter.format("%s %5s [%s] %s %s (%d) %s",
            timeFormatter.format(this.time),
            this.level,
            Thread.currentThread().getName(),
            className,
            this.frame.getMethodName(),
            this.frame.getLineNumber(),
            message);

        formatter.flush();

        return appendable.toString();
      }
    }

    private void post(final String message) {
      synchronized (LoggingFactory.lock) {

        final String content = makeLogMessage(tomcat_formatter, message);
        
        System.out.println(content);

        if (LoggingFactory.willLog(level)) {
          LoggingFactory.messageQueue.add(content);
        }
      }
    }

    @Override
    public void log(final String message) {
      post(message);
    }

    @Override
    public void log(final Object... params) {
      post(Arrays
          .stream(params)
          .map(x -> x == null ? "null" : x.toString())
          .collect(Collectors.joining(" | ")));
    }

    @Override
    public void log(final Throwable t) {
      final StackTraceElement frame = t.getStackTrace()[0];

      log(t.getClass().getName(),
          t.getLocalizedMessage(),
          frame.getFileName(),
          frame.getClassName(),
          frame.getMethodName(),
          frame.getLineNumber());
    }
  }

  public static boolean willLog(final Level level) {
    return loggingLevel.get().compare(level) >= 0;
  }

  public static void setLoggingLevel(final Level level) {
    loggingLevel.getAndSet(level);
  }

  @SuppressWarnings("unused")
  public static Level getLoggingLevel() {
    return loggingLevel.get();
  }

  public static boolean hasMessages() {
    return !messageQueue.isEmpty();
  }

  public static String pollMessageQueue() {
    return messageQueue.poll();
  }

  final Class<?> type;

  LoggingFactory(Class<?> type) {
    this.type = type;
  }

  public ILogging error() {
    return new InnerLogging(Level.ERROR);
  }

  @SuppressWarnings("unused")
  public ILogging warning() {
    return new InnerLogging(Level.WARNING);
  }

  public ILogging info() {
    return new InnerLogging(Level.INFO);
  }

  public ILogging debug() {
    return new InnerLogging(Level.DEBUG);
  }

  @SuppressWarnings("unused")
  public ILogging verbose() {
    return new InnerLogging(Level.TRACE);
  }

  @SuppressWarnings("unused")
  public ILogging trace() {
    return new InnerLogging(Level.TRACE);
  }

  public ILogging get(final Level level) {
    return new InnerLogging(level);
  }

  public void in() {
    new InnerLogging(Level.TRACE).log("-->");
  }

  public void out() {
    new InnerLogging(Level.TRACE).log("<--");
  }

  @SuppressWarnings("unused")
  public void json(final Level level, final Object obj) {
    new InnerLogging(level)
        .log(obj.getClass().getName(), Serializer.safeSerializeJsonIndented(obj));
  }

  public void yaml(final Level level, final Object obj) {
    new InnerLogging(level)
        .log(obj.getClass().getName(), Serializer.safeSerializeYaml(obj));
  }

  public void me(final Object obj) {
    new InnerLogging(Level.TRACE)
        .log(String.format("%08X", obj.hashCode()));
  }

  public void exception(final Throwable t) {
    new InnerLogging(Level.ERROR).log(t);
  }

  public Class<?> getType() {
    return this.type;
  }

  public static LoggingFactory create(final Class<?> type) {
    return new LoggingFactory(type);
  }
}

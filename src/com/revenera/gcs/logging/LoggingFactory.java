package com.revenera.gcs.logging;

import com.revenera.gcs.utils.Serializer;
import org.apache.commons.lang3.ClassUtils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class LoggingFactory {
  final static DateTimeFormatter formatter = DateTimeFormatter
      .ofPattern("dd-MMM-yyyy HH:mm:ss.SSS", Locale.ENGLISH)
      .withZone(ZoneId.systemDefault());

  final static DateTimeFormatter formatterZulu =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
          .withZone(ZoneId.systemDefault());

  // genuinely is an inner class, cannot be static
  private class InnerLogging extends LoggingContext implements ILogging {
//    final Instant time = Instant.now();
//    final LoggingContext context;

    InnerLogging(final Level level) {
      super(level, LoggingFactory.this.type.getCanonicalName());
    }

    private void post(final String message) {
      synchronized (LoggingFactory.lock) {
        final String content = String.format("%s %5s [%s] %s.%s(%d) {%s} %s",
            formatter.format(getTime()),
            getLevel().getText(),
            Thread.currentThread().getName(),
            ClassUtils.getAbbreviatedName(getClassName(), 32),
            getMethodName(),
            getLineNumber(),
            LoggingFactory.this.type.getSimpleName(),
            message);

        // rely on stdout redirection in Tomcat...
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
          .map(Object::toString)
          .collect(Collectors.joining(" | ")));
    }

    @Override
    public void log(Throwable t) {
      final StackTraceElement frame = t.getStackTrace()[0];

      log(t.getClass().getName(),
          t.getLocalizedMessage(),
          frame.getFileName(),
          frame.getClassName(),
          frame.getMethodName(),
          frame.getLineNumber());
    }
  }

  static final Object lock = new Object();

  final static Queue<String> messageQueue = new ConcurrentLinkedQueue<>();

  final static AtomicReference<Level> loggingLevel = new AtomicReference<>(Level.TRACE);

  public static boolean willLog(final Level level) {
    return loggingLevel.get().compare(level) >= 0;
  }

  public static void setLoggingLevel(final Level level) {
    loggingLevel.getAndSet(level);
  }

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

  public ILogging warning() {
    return new InnerLogging(Level.WARNING);
  }

  public ILogging info() {
    return new InnerLogging(Level.INFO);
  }

  public ILogging debug() {
    return new InnerLogging(Level.DEBUG);
  }

  public ILogging verbose() {
    return new InnerLogging(Level.TRACE);
  }

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
    ///
    return this.type;
  }

  public static LoggingFactory create(final Class<?> type) {
    //
    return new LoggingFactory(type);
  }
}

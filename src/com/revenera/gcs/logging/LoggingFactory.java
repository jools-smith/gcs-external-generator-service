package com.revenera.gcs.logging;

import com.revenera.gcs.utils.Frame;
import com.revenera.gcs.utils.Serializer;

import java.time.Instant;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class LoggingFactory {
  static final Object lock = new Object();

  final static Queue<String> messageQueue = new ConcurrentLinkedQueue<>();

  final static AtomicReference<Level> loggingLevel = new AtomicReference<>(Level.TRACE);

//  final static AtomicReference<String> loggingRoot = new AtomicReference<>("c:\\revenera");

  public static boolean willLog(final Level level) {
    return loggingLevel.get().compare(level) >= 0;
  }

  public static Level setLoggingLevel(final Level level) {
    return loggingLevel.getAndSet(level);
  }

  public static boolean hasMessages() {
    return !messageQueue.isEmpty();
  }

  public static String pollMessageQueue() {
    return messageQueue.poll();
  }

  final Class<?> type;
  final String fqcn;

  // implementor
  class SimpleLoggingImplementor implements ILogging {
    final Instant time = Instant.now();
    final Context context;
    
    private SimpleLoggingImplementor(final Context context) {
      this.context = context;
    }

    private void post(final String message) {
      synchronized (lock) {
        final String content = String.format("%s %5s [%s] {%s} %s.%s(%d) %s",
            this.context.getTimeUtc(),
            this.context.getLevel().getText(),
            Thread.currentThread().getName(),
            LoggingFactory.this.type.getSimpleName(),
            abbreviatePackageName(this.context.getClassName(), 48),
            this.context.getMethodName(),
            this.context.getLineNumber(),
            message);

        // rely on stdout redirection in Tomcat...
        System.out.println(content);

        if (willLog(this.context.level)) {
          messageQueue.add(content);
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

  public static String abbreviatePackageName(final String name, final int limit) {

    String str = name;

    if (str.length() > limit) {
      final String[] parts = name.split("\\.");

      for (int i = 0; i < parts.length; i++) {
        parts[i] = parts[i].substring(0, 1);

        str = String.join(".", parts);
        if (str.length() <= limit) {
          break;
        }
      }
    }

    return str;
  }


  LoggingFactory(Class<?> type) {
    this.type = type;
    this.fqcn = type.getCanonicalName();
  }

  public ILogging error() {
    return new SimpleLoggingImplementor(
        new Context(Level.ERROR, this.fqcn));
  }

  public ILogging warning() {
    return new SimpleLoggingImplementor(
        new Context(Level.WARNING, this.fqcn));
  }

  public ILogging info() {
    return new SimpleLoggingImplementor(
        new Context(Level.INFO, this.fqcn));
  }

  public ILogging debug() {
    return new SimpleLoggingImplementor(
        new Context(Level.DEBUG, this.fqcn));
  }

  public ILogging verbose() {
    return new SimpleLoggingImplementor(
        new Context(Level.TRACE, this.fqcn));
  }

  public ILogging trace() {
    return new SimpleLoggingImplementor(
        new Context(Level.TRACE, this.fqcn));
  }

  public ILogging get(final Level level) {
    return new SimpleLoggingImplementor(
        new Context(level, this.fqcn));
  }

  public void in() {
    new SimpleLoggingImplementor(
        new Context(Level.TRACE, this.fqcn)).log("-->");
  }

  public void out() {
    new SimpleLoggingImplementor(
        new Context(Level.TRACE, this.fqcn)).log("<--");
  }

  public void json(final Level level, final Object obj) {
    new SimpleLoggingImplementor(
        new Context(level, this.fqcn)).log(
            obj.getClass().getName(), Serializer.safeSerializeJsonIndented(obj));
  }

  public void yaml(final Level level, final Object obj) {
    new SimpleLoggingImplementor(
        new Context(level, this.fqcn)).log(
            obj.getClass().getName(), Serializer.safeSerializeYaml(obj));
  }

  public void me(final Object obj) {
    new SimpleLoggingImplementor(
        new Context(Level.TRACE, this.fqcn)).log(String.format("%08X", obj.hashCode()));
  }

  public void exception(final Throwable t) {
    new SimpleLoggingImplementor(
        new Context(Level.ERROR, this.fqcn)).log(t);
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

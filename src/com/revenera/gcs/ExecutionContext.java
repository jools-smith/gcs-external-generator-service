package com.revenera.gcs;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

public class ExecutionContext implements AutoCloseable {

  static final ThreadLocal<Deque<Context>> contexts = new ThreadLocal<>();


  public interface Logging {
    void log(String message);
    void log(Throwable t);
  }

  private static class Logger implements Logging {
    final Context context;

    private static String timestamp() {
      return Instant.now().toString()
          .replace("T", " ")
          .replace("Z", "");
    }
    Logger(Context context) {
      this.context = context;
    }

    @Override
    public void log(String message) {
      System.out.printf("%s [%s] %s.%s %s%n",
          timestamp(),
          Thread.currentThread().getName(),
          context.frame.getClassName(),
          context.frame.getMethodName(),
          message);
    }

    @Override
    public void log(Throwable t) {
      System.out.printf("%s [%s] %s.%s %s %s%n",
          timestamp(),
          Thread.currentThread().getName(),
          context.frame.getClassName(),
          context.frame.getMethodName(),
          t.getClass().getSimpleName(),
          t.getLocalizedMessage());
    }
  }

  public static class Context {
    final StackTraceElement frame;
    final String name;
    Context(StackTraceElement frame, String name) {
      this.frame = frame;
      this.name = name;
    }

    @Override
    public String toString() {
      return String.format("[%s.%s (%s)]", frame.getClassName(), frame.getMethodName(), frame.getLineNumber());
    }
  }

  public ExecutionContext(final String name) {

    if (contexts.get() == null) {
      contexts.set(new ArrayDeque<>());
    }

    final StackTraceElement frame = Thread.currentThread().getStackTrace()[2];

    contexts.get().offer(new Context(frame, name));
  }

  public Context getCurrentContext() {
    return contexts.get().getLast();
  }

  public Context getRootContext() {
    return contexts.get().getFirst();
  }

  @Override
  public void close() {

    final Context ctx = contexts.get().poll();
    if (ctx != null) {
//      System.out.println("removed context " +  ctx.toString());
    }

    if (contexts.get().isEmpty()) {
      contexts.remove();
//      System.out.println("removed thread local");
    }
  }

  public static Logging getLogger() {
    return new Logger(contexts.get().getLast());
  }
}

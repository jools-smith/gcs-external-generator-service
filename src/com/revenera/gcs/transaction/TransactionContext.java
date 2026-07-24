package com.revenera.gcs.transaction;

import com.revenera.gcs.logging.LoggingFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public final class TransactionContext {
  private static final LoggingFactory logger = LoggingFactory.create(TransactionContext.class);

  public interface IData {
    Object add(final Object obj);
    <T> T add(Class<T> type, final T t);
    Map<Class<?>, Object> getPayload();
  }

  public static class Data implements IData {
    private final Map<Class<?>, Object> payload = new LinkedHashMap<>();

    @Override
    public Object add(final Object obj) {
      this.payload.put(obj.getClass(), obj);
      return obj;
    }

    @Override
    public <T> T add(Class<T> type, final T t) {
      this.payload.put(t.getClass(), t);
      return t;
    }

    @Override
    public Map<Class<?>, Object> getPayload() {
      return this.payload;
    }
  }

  private static final ThreadLocal<Data> CURRENT = new ThreadLocal<>();

  private TransactionContext() {
  }

  static void initialize() {
    logger.in();
    if (CURRENT.get() != null) {
      throw new IllegalStateException("A TransactionContext is already registered for this thread");
    }

    CURRENT.set(new Data());
  }
  public static IData get() {
    return CURRENT.get();
  }

  static void clear() {
    logger.in();
    CURRENT.remove();
  }
}

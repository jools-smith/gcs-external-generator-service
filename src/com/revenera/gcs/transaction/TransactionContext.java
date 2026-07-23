package com.revenera.gcs.transaction;

public final class TransactionContext {

  private static final ThreadLocal<TransactionData> CURRENT = new ThreadLocal<>();

  private TransactionContext() {
  }

  public static TransactionData get() {
    return CURRENT.get();
  }

  static void set(final TransactionData context) {
    if (context == null) {
      throw new IllegalArgumentException("TransactionContext must not be null");
    }

    if (CURRENT.get() != null) {
      throw new IllegalStateException("A TransactionContext is already registered for this thread");
    }

    CURRENT.set(context);
  }

  static void clear() {
    CURRENT.remove();
  }
}

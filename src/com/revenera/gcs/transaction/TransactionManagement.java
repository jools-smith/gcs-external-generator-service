package com.revenera.gcs.transaction;

public interface TransactionManagement {
  boolean hasTransactions();
  TransactionRecord pollTransactions();
  TransactionScope makeTransactionScope();
  void submitTransactionContext(final TransactionRecord record);
}

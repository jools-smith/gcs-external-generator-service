package com.revenera.gcs.transaction;

import com.revenera.gcs.ApplicationProiperties;
import com.revenera.gcs.Beans;
import com.revenera.gcs.implementor.ImplementorFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class TransactionData {
  public final ImplementorFactory implementorFactory = Beans.getImplementorFactory();
  public final DiagnosticsFactory diagnosticsFactory = Beans.getDiagnosticsFactory();
  public final ApplicationProiperties applicationProperties = Beans.getApplicationProperties();

  private final Map<Class<?>, Object> payload = new LinkedHashMap<>();

  public Object add(final Object obj) {
    this.payload.put(obj.getClass(), obj);
    return obj;
  }

  public <T> T add(Class<T> type, final T t) {
    this.payload.put(t.getClass(), t);
    return t;
  }

  public Map<Class<?>, Object> getPayload() {
    return this.payload;
  }

}

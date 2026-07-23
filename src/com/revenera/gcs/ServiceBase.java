package com.revenera.gcs;

import com.flexnet.external.type.*;
import com.revenera.gcs.transaction.DiagnosticsFactory;
import com.revenera.gcs.utils.Utils;
import com.revenera.gcs.logging.LoggingFactory;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;


public abstract class ServiceBase {

  protected final LoggingFactory logger = LoggingFactory.create(this.getClass());

  protected ServiceBase() {
    this.logger.in();
  }

  /**
   *
   * @param obj request payload from which to assess the FNO license technology name
   * @return FNO license technology name
   */
  protected String getLicenseTechnology(final Object obj) {
    final AtomicReference<LicenseTechnology> tech = new AtomicReference<>();

    if (obj instanceof ProductRequest) {
      tech.set(((ProductRequest) obj).getLicenseTechnology());
    }
    else if (obj instanceof LicenseModelRequest) {
      tech.set(((LicenseModelRequest) obj).getLicenseTechnology());
    }
    else if (obj instanceof GeneratorRequest) {
      tech.set(((GeneratorRequest) obj).getLicenseTechnology());
    }
    else if (obj instanceof ConsolidatedLicenseResquest) {
      ((ConsolidatedLicenseResquest) obj).getFulfillments().stream().findFirst().ifPresent(x -> {
        tech.set(x.getLicenseTechnology());
      });
    }
    else if (obj instanceof FulfillmentRecordSet) {
      ((FulfillmentRecordSet) obj).getFulfillments().stream().findFirst().ifPresent(x -> {
        tech.set(x.getLicenseTechnology());
      });
    }
    else if (obj instanceof RenewableEntitlementLineItems) {
      ((RenewableEntitlementLineItems) obj).getRenewableEntitlementLineItems().stream().findFirst().ifPresent(x -> {
        tech.set(x.getLicenseTechnology());
      });
    }
    else if (obj instanceof EntitlementLineItem) {
      tech.set(((EntitlementLineItem) obj).getLicenseTechnology());
    }
    else if (obj instanceof FulfillmentRecord) {
      tech.set(((FulfillmentRecord) obj).getLicenseTechnology());
    }
    else if (obj instanceof ConsolidatedLicenseRecord) {
      tech.set(((ConsolidatedLicenseRecord) obj).getLicenseTechnology());
    }
    else if (obj instanceof PingRequest) {
      //todo: KLUDGE
      return ((PingRequest) obj).getStr();
    }

    if (tech.get() == null) {
      throw new RuntimeException(obj.getClass().getName() + " | cannot retrieve license technology");
    }
    else {
//      this.logger.log(Log.Level.info, "license tech:" + tech.get().getName());
      return tech.get().getName();
    }
  }

  String frameDetails(final StackTraceElement frame) {
    return  String.join("|",
            frame.getFileName(),
            frame.getClassName(),
            frame.getMethodName(),
            java.lang.String.valueOf(frame.getLineNumber()));
  }

  public Function<Throwable, SvcException> serviceException = (throwable) -> new SvcException() {
    {
      this.setMessage(frameDetails(Thread.currentThread().getStackTrace()[3]));
      
      this.setName(throwable.getClass().getName());
    }
  };
}
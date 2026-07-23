package com.flexnet.external.webservice.keygenerator;

import com.flexnet.external.type.*;

import java.util.concurrent.atomic.AtomicReference;

public class ServiceHelper {
  public static String getStackTraceElement(final StackTraceElement frame) {
    return String.join("|",
        frame.getFileName(),
        frame.getClassName(),
        frame.getMethodName(),
        String.valueOf(frame.getLineNumber()));
  }
  
  public static SvcException makeServiceException(final Throwable throwable) {
    final StackTraceElement frame = Thread.currentThread().getStackTrace()[3];

    final SvcException sex = new SvcException();
    sex.setMessage(getStackTraceElement(frame));
    sex.setName(throwable.getClass().getName());

    return sex;
  }

  public static String getLicenseTechnology(final Object obj) {
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
}

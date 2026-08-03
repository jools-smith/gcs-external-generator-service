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
    final StackTraceElement frame = Thread.currentThread().getStackTrace()[2];

    final SvcException sex = new SvcException();
    sex.setMessage(getStackTraceElement(frame));
    sex.setName(throwable.getClass().getSimpleName());

    return sex;
  }

  public static String getLicenseTechnology(final Object obj) {
    final AtomicReference<LicenseTechnology> tech = new AtomicReference<>();

    if (obj instanceof ProductRequest) {
      final ProductRequest data = (ProductRequest) obj;

      tech.set(data.getLicenseTechnology());
    }
    else if (obj instanceof LicenseModelRequest) {
      final LicenseModelRequest data = (LicenseModelRequest) obj;

      tech.set(data.getLicenseTechnology());
    }
    else if (obj instanceof GeneratorRequest) {
      final GeneratorRequest data = (GeneratorRequest) obj;

      tech.set(data.getLicenseTechnology());
    }
    else if (obj instanceof ConsolidatedLicenseResquest) {
      final ConsolidatedLicenseResquest data = (ConsolidatedLicenseResquest) obj;
      //noinspection CodeBlock2Expr
      data.getFulfillments().stream().findFirst().ifPresent(x -> {
        tech.set(x.getLicenseTechnology());
      });
    }
    else if (obj instanceof FulfillmentRecordSet) {
      final FulfillmentRecordSet data = (FulfillmentRecordSet) obj;
      //noinspection CodeBlock2Expr
      data.getFulfillments().stream().findFirst().ifPresent(x -> {
        tech.set(x.getLicenseTechnology());
      });
    }
    else if (obj instanceof RenewableEntitlementLineItems) {
      final RenewableEntitlementLineItems data = (RenewableEntitlementLineItems) obj;
      //noinspection CodeBlock2Expr
      data.getRenewableEntitlementLineItems().stream().findFirst().ifPresent(x -> {
        tech.set(x.getLicenseTechnology());
      });
    }
    else if (obj instanceof EntitlementLineItem) {
      final EntitlementLineItem data = (EntitlementLineItem) obj;

      tech.set(data.getLicenseTechnology());
    }
    else if (obj instanceof FulfillmentRecord) {
      final FulfillmentRecord data = (FulfillmentRecord) obj;

      tech.set(data.getLicenseTechnology());
    }
    else if (obj instanceof ConsolidatedLicenseRecord) {
      final ConsolidatedLicenseRecord data = (ConsolidatedLicenseRecord) obj;

      tech.set(data.getLicenseTechnology());
    }
    else if (obj instanceof PingRequest) {
      final PingRequest data = (PingRequest) obj;
      //todo: KLUDGE
      tech.set(new LicenseTechnology() {
        {
          this.setName(data.getStr());
        }
      });
    }

    if (tech.get() == null) {
      throw new RuntimeException(obj.getClass().getName() + " | cannot retrieve license technology");
    }
    else {
      return tech.get().getName();
    }
  }
}

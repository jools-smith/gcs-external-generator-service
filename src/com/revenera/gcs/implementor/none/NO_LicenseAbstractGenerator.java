package com.revenera.gcs.implementor.none;

import com.flexnet.external.type.*;
import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;
import com.revenera.gcs.ExecutionContext;
import com.revenera.gcs.implementor.AbstractGenerator;
import com.revenera.gcs.implementor.GeneratorImplementor;

import java.util.Collections;

@SuppressWarnings("unused")
@GeneratorImplementor(technologyId = "NONE", technologyName = "No Enforcement License Technology", isDefault = false)
public class NO_LicenseAbstractGenerator extends AbstractGenerator implements LicenseGeneratorServiceInterface {

  @Override
  public LicenseGeneratorServiceInterface generator() {
    return this;
  }

  @Override
  public PingResponse ping(final PingRequest request) {
    ExecutionContext.injectRequest(request);

    return ExecutionContext.injectResponse(super.doPing());
  }

  @Override
  public GeneratorResponse generateLicense(final GeneratorRequest request) {
    logger.in();

    ExecutionContext.injectRequest(request);

    return ExecutionContext.injectResponse(new GeneratorResponse() {
      {
        this.licenseFileName = "License";
        this.licenseText = "No license available";

        this.licenseFiles = NO_LicenseAbstractGenerator.super.makeLicenseFiles(request.getLicenseFileDefinitions(), "No license available", null);
      }
    });
  }

  @Override
  public ConsolidatedLicense consolidateFulfillments(final FulfillmentRecordSet request) {
    logger.in();

    ExecutionContext.injectRequest(request);

    return ExecutionContext.injectResponse(new ConsolidatedLicense() {
      {
        this.fulfillments = request.getFulfillments();

        this.licFiles = Collections.singletonList(new LicenseFileMapItem() {
          {
            this.name = "License";
            this.value = "No license available";
          }
        });
      }
    });
  }

  @Override
  public Status validateProduct(final ProductRequest request) {
    ExecutionContext.injectRequest(request);

    return ExecutionContext.injectResponse(super.doValidateProduct(request));
  }

  @Override
  public Status validateLicenseModel(final LicenseModelRequest request) {
    ExecutionContext.injectRequest(request);

    return ExecutionContext.injectResponse(super.doValidateLicenseModel(request));
  }

  @Override
  public LicenseFileDefinitionMap generateLicenseFilenames(final GeneratorRequest request) {
    ExecutionContext.injectRequest(request);

    return throwNotImplementedException();
  }

  @Override
  public LicenseFileDefinitionMap generateConsolidatedLicenseFilenames(final ConsolidatedLicenseResquest request) {
    ExecutionContext.injectRequest(request);

    return throwNotImplementedException();
  }

  @Override
  public String generateCustomHostIdentifier(final HostIdRequest request) {
    ExecutionContext.injectRequest(request);

    return throwNotImplementedException();
  }

  @Override
  public void registerConfirmation() {
    logger.get().info("{0} {1} {2}", this.getClass().getSimpleName(), this.id, this.name);
  }
}

package com.revenera.gcs.implementor.none;

import com.flexnet.external.type.*;
import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;
import com.revenera.gcs.ExecutionContext;
import com.revenera.gcs.implementor.GeneratorBase;
import com.revenera.gcs.implementor.GeneratorImplementor;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Collections;

@SuppressWarnings("unused")
@GeneratorImplementor(technologyId = "NONE", technologyName = "No Enforcement License Technology", isDefault = false)
public class NO_LicenseGenerator extends GeneratorBase implements LicenseGeneratorServiceInterface {

  @Override
  public LicenseGeneratorServiceInterface generator() {
    return this;
  }

  @Override
  public PingResponse ping(final PingRequest request) {
    ExecutionContext.injectRequest(this, request);

    return ExecutionContext.injectResponse(this, PingResponse.class, super.doPing());
  }

  @Override
  public GeneratorResponse generateLicense(final GeneratorRequest request) {
    logger.in();

    ExecutionContext.injectRequest(this, request);

    return ExecutionContext.injectResponse(this, GeneratorResponse.class, new GeneratorResponse() {
      {
        this.licenseFileName = "License";
        this.licenseText = "No license available";

        this.licenseFiles = NO_LicenseGenerator.super.makeLicenseFiles(request.getLicenseFileDefinitions(), "No license available", null);
      }
    });
  }

  @Override
  public ConsolidatedLicense consolidateFulfillments(final FulfillmentRecordSet request) {
    logger.in();

    ExecutionContext.injectRequest(this, request);

    return ExecutionContext.injectResponse(this, ConsolidatedLicense.class, new ConsolidatedLicense() {
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
    ExecutionContext.injectRequest(this, request);

    return ExecutionContext.injectResponse(this, Status.class, super.doValidateProduct(request));
  }

  @Override
  public Status validateLicenseModel(final LicenseModelRequest request) {
    ExecutionContext.injectRequest(this, request);

    return ExecutionContext.injectResponse(this, Status.class, super.doValidateLicenseModel(request));
  }

  @Override
  public LicenseFileDefinitionMap generateLicenseFilenames(final GeneratorRequest request) {
    ExecutionContext.injectRequest(this, request);

    throw new NotImplementedException("generateLicenseFilenames");
  }

  @Override
  public LicenseFileDefinitionMap generateConsolidatedLicenseFilenames(final ConsolidatedLicenseResquest request) {
    ExecutionContext.injectRequest(this, request);

    throw new NotImplementedException("generateConsolidatedLicenseFilenames");
  }

  @Override
  public String generateCustomHostIdentifier(final HostIdRequest request) {
    ExecutionContext.injectRequest(this, request);

    throw new NotImplementedException("generateCustomHostIdentifier");
  }
}

package com.revenera.gcs.implementor.diag;

import com.flexnet.external.type.*;
import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;
import com.revenera.gcs.ExecutionContext;
import com.revenera.gcs.implementor.GeneratorBase;
import com.revenera.gcs.implementor.GeneratorImplementor;
import org.apache.commons.lang3.NotImplementedException;

@SuppressWarnings("unused")
@GeneratorImplementor(technologyId = "DEF", technologyName = "Unimplemented License Technology", isDefault = true)
public final class DiagnosticLicenseGenerator extends GeneratorBase implements LicenseGeneratorServiceInterface  {

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
  public GeneratorResponse generateLicense(GeneratorRequest request) {
    ExecutionContext.injectRequest(this, request);

    throw new NotImplementedException("generateLicense");
  }

  @Override
  public ConsolidatedLicense consolidateFulfillments(final FulfillmentRecordSet request) {
    ExecutionContext.injectRequest(this, request);

    throw new NotImplementedException("consolidateFulfillments");
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


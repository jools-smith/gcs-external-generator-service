package com.revenera.gcs.implementor.diag;

import com.flexnet.external.type.*;
import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;
import com.revenera.gcs.ExecutionContext;
import com.revenera.gcs.implementor.AbstractGenerator;
import com.revenera.gcs.implementor.GeneratorImplementor;

@SuppressWarnings("unused")
@GeneratorImplementor(technologyId = "DEF", technologyName = "Unimplemented License Technology", isDefault = true)
public final class DiagnosticLicenseAbstractGenerator extends AbstractGenerator implements LicenseGeneratorServiceInterface  {

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
  public GeneratorResponse generateLicense(GeneratorRequest request) {
    ExecutionContext.injectRequest(request);

    return throwNotImplementedException();
  }

  @Override
  public ConsolidatedLicense consolidateFulfillments(final FulfillmentRecordSet request) {
    ExecutionContext.injectRequest(request);

    return throwNotImplementedException();
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


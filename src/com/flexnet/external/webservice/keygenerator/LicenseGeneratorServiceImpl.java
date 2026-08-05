package com.flexnet.external.webservice.keygenerator;

import com.flexnet.external.type.*;
import com.revenera.gcs.ExecutionContext;

import javax.jws.WebService;

@WebService(
    serviceName = "revenera-gcs",
    endpointInterface = "com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface",
    wsdlLocation = "WEB-INF/wsdl/schema/LicenseGeneratorService.wsdl"
)
public class LicenseGeneratorServiceImpl extends ServiceBase implements LicenseGeneratorServiceInterface {

  @Override
  public PingResponse ping(final PingRequest payload) throws LicGeneratorException {
    //noinspection unused
    try (final ExecutionContext ignored = new ExecutionContext()) {
      // invoke implementor
      return ExecutionContext
          .getImplementorFactory()
          .getImplementor(ServiceHelper.getLicenseTechnology(payload))
          .ping(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), ServiceHelper.makeServiceException(t));
    }
  }

  @Override
  public Status validateProduct(final ProductRequest payload) throws LicGeneratorException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      // invoke implementor
      return ExecutionContext
          .getImplementorFactory()
          .getImplementor(ServiceHelper.getLicenseTechnology(payload))
          .validateProduct(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), ServiceHelper.makeServiceException(t));
    }
  }

  @Override
  public Status validateLicenseModel(final LicenseModelRequest payload) throws LicGeneratorException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {

      return ExecutionContext
          .getImplementorFactory()
          .getImplementor(ServiceHelper.getLicenseTechnology(payload))
          .validateLicenseModel(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), ServiceHelper.makeServiceException(t));
    }
  }

  @Override
  public GeneratorResponse generateLicense(final GeneratorRequest payload) throws LicGeneratorException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {

      return ExecutionContext
          .getImplementorFactory()
          .getImplementor(ServiceHelper.getLicenseTechnology(payload))
          .generateLicense(payload);

    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), ServiceHelper.makeServiceException(t));
    }
  }

  @Override
  public ConsolidatedLicense consolidateFulfillments(final FulfillmentRecordSet payload) throws LicGeneratorException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {

      return ExecutionContext
          .getImplementorFactory()
          .getImplementor(ServiceHelper.getLicenseTechnology(payload))
          .consolidateFulfillments(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), ServiceHelper.makeServiceException(t));
    }
  }

  @Override
  public LicenseFileDefinitionMap generateLicenseFilenames(final GeneratorRequest payload) throws LicGeneratorException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {

      return ExecutionContext
          .getImplementorFactory()
          .getImplementor(ServiceHelper.getLicenseTechnology(payload))
          .generateLicenseFilenames(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), ServiceHelper.makeServiceException(t));
    }
  }


  @Override
  public LicenseFileDefinitionMap generateConsolidatedLicenseFilenames(final ConsolidatedLicenseResquest payload)
      throws LicGeneratorException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {

      return ExecutionContext
          .getImplementorFactory()
          .getImplementor(ServiceHelper.getLicenseTechnology(payload))
          .generateConsolidatedLicenseFilenames(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), ServiceHelper.makeServiceException(t));
    }
  }

  @Override
  public String generateCustomHostIdentifier(final HostIdRequest payload) throws LicGeneratorException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      return ExecutionContext
          .getImplementorFactory()
          .getDefaultImplementor()
          .generateCustomHostIdentifier(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), ServiceHelper.makeServiceException(t));
    }
  }
}

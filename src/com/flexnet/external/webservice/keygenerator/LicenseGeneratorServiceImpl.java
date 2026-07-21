package com.flexnet.external.webservice.keygenerator;

import com.flexnet.external.type.*;
import com.revenera.gcs.Beans;
import com.revenera.gcs.ServiceBase;
import com.revenera.gcs.transaction.DiagnosticsFactory;
import com.revenera.gcs.logging.Level;

import javax.jws.WebService;

@WebService(
        endpointInterface = "com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface",
        wsdlLocation = "WEB-INF/wsdl/schema/LicenseGeneratorService.wsdl"
)
public class LicenseGeneratorServiceImpl extends ServiceBase implements LicenseGeneratorServiceInterface {

  public PingResponse ping1(final PingRequest payload) throws LicGeneratorException {
//    logger.in(payload);
    try (final DiagnosticsFactory.ExecutionContext context = Beans.diagnosticsFactory.makeExecutionContext()){
      logger.json(Level.DEBUG, payload);
      final String tech = super.getLicenseTechnology(payload);

      return Beans.implementorFactory
          .getImplementor(tech)
          .ping(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), this.serviceException.apply(t));
    }
  }

  @Override
  public PingResponse ping(final PingRequest payload) throws LicGeneratorException {
//    logger.in(payload);
    try (final DiagnosticsFactory.ExecutionContext context = Beans.diagnosticsFactory.makeExecutionContext()){
      logger.json(Level.DEBUG, payload);
      final String tech = super.getLicenseTechnology(payload);

      return Beans.implementorFactory
          .getImplementor(tech)
          .ping(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), this.serviceException.apply(t));
    }
  }

  @Override
  public Status validateProduct(final ProductRequest payload) throws LicGeneratorException {
//    logger.in(payload);
    try (final DiagnosticsFactory.ExecutionContext context = Beans.diagnosticsFactory.makeExecutionContext()){

      final String tech = super.getLicenseTechnology(payload);

      return Beans.implementorFactory
          .getImplementor(tech)
          .validateProduct(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), this.serviceException.apply(t));
    }

  }

  @Override
  public Status validateLicenseModel(final LicenseModelRequest payload) throws LicGeneratorException {
//    logger.in(payload);
    try (final DiagnosticsFactory.ExecutionContext context = Beans.diagnosticsFactory.makeExecutionContext()){

      final String tech = super.getLicenseTechnology(payload);

      return Beans.implementorFactory
          .getImplementor(tech)
          .validateLicenseModel(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), this.serviceException.apply(t));
    }
  }

  @Override
  public GeneratorResponse generateLicense(final GeneratorRequest payload) throws LicGeneratorException {
//    logger.in(payload);
    try (final DiagnosticsFactory.ExecutionContext context = Beans.diagnosticsFactory.makeExecutionContext()){

      final String tech = super.getLicenseTechnology(payload);

      return Beans.implementorFactory
          .getImplementor(tech)
          .generateLicense(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), this.serviceException.apply(t));
    }
  }

  @Override
  public ConsolidatedLicense consolidateFulfillments(final FulfillmentRecordSet payload) throws LicGeneratorException {
//    logger.in(payload);
    try (final DiagnosticsFactory.ExecutionContext context = Beans.diagnosticsFactory.makeExecutionContext()){

      final String tech = super.getLicenseTechnology(payload);

      return Beans.implementorFactory
          .getImplementor(tech)
          .consolidateFulfillments(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), this.serviceException.apply(t));
    }
  }

  @Override
  public LicenseFileDefinitionMap generateLicenseFilenames(final GeneratorRequest payload) throws LicGeneratorException {
//    logger.in(payload);
    try (final DiagnosticsFactory.ExecutionContext context = Beans.diagnosticsFactory.makeExecutionContext()){

      final String tech = super.getLicenseTechnology(payload);

      return Beans.implementorFactory
          .getImplementor(tech)
          .generateLicenseFilenames(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), this.serviceException.apply(t));
    }
  }


  @Override
  public LicenseFileDefinitionMap generateConsolidatedLicenseFilenames(final ConsolidatedLicenseResquest payload)
      throws LicGeneratorException {
//    logger.in(payload);
    try (final DiagnosticsFactory.ExecutionContext context = Beans.diagnosticsFactory.makeExecutionContext()){

      final String tech = super.getLicenseTechnology(payload);

      return Beans.implementorFactory
          .getImplementor(tech)
          .generateConsolidatedLicenseFilenames(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), this.serviceException.apply(t));
    }
  }

  @Override
  public String generateCustomHostIdentifier(final HostIdRequest payload) throws LicGeneratorException {
//    logger.in(payload);
    try (final DiagnosticsFactory.ExecutionContext context = Beans.diagnosticsFactory.makeExecutionContext()){

      return Beans.implementorFactory
          .getDefaultImplementor()
          .generateCustomHostIdentifier(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), this.serviceException.apply(t));
    }
  }
}

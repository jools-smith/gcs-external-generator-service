package com.flexnet.external.webservice.keygenerator;

import com.flexnet.external.type.*;
import com.revenera.gcs.AppContext;
import com.revenera.gcs.Beans;

import javax.jws.WebService;

@WebService(
    endpointInterface = "com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface",
    wsdlLocation = "WEB-INF/wsdl/schema/LicenseGeneratorService.wsdl"
)
public class LicenseGeneratorServiceImpl extends ServiceBase implements LicenseGeneratorServiceInterface {

//  public PingResponse ping1(final PingRequest payload) throws LicGeneratorException {

  /// /    logger.in(payload);
//    try (final AppContext ctx = Beans.makeContext()) {
//      try (final ExecutionScope context = AppContext.getExecutionManager().makeExecutionScope()) {
//        logger.json(Level.DEBUG, payload);
//        final String tech = ServiceHelper.getLicenseTechnology(payload);
//
//        return AppContext
//            .getImplementorFactory()
//            .getImplementor(tech)
//            .ping(payload);
//      }
//    }
//    catch (final Throwable t) {
//      throw new LicGeneratorException(t.getMessage(), ServiceHelper.makeServiceException(t));
//    }
//  }
  @Override
  public PingResponse ping(final PingRequest payload) throws LicGeneratorException {
//    logger.in(payload);
    try (final AppContext ctx = Beans.makeContext()) {
      // invoke implementor
      return AppContext
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
//    logger.in(payload);
    try (final AppContext ctx = Beans.makeContext()) {
      // invoke implementor
      return AppContext
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
//    logger.in(payload);
    try (final AppContext ctx = Beans.makeContext()) {


      return AppContext
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
//    logger.in(payload);
    try (final AppContext ctx = Beans.makeContext()) {


      return AppContext
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
//    logger.in(payload);
    try (final AppContext ctx = Beans.makeContext()) {

      return AppContext
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
//    logger.in(payload);
    try (final AppContext ctx = Beans.makeContext()) {

      return AppContext
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
//    logger.in(payload);
    try (final AppContext ctx = Beans.makeContext()) {

      return AppContext
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
//    logger.in(payload);
    try (final AppContext ctx = Beans.makeContext()) {
      return AppContext
          .getImplementorFactory()
          .getDefaultImplementor()
          .generateCustomHostIdentifier(payload);
    }
    catch (final Throwable t) {
      throw new LicGeneratorException(t.getMessage(), ServiceHelper.makeServiceException(t));
    }
  }
}

package com.revenera.gcs.webservices;

import com.flexnet.external.type.*;
import com.flexnet.external.webservice.keygenerator.LicGeneratorException;
import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;
import com.revenera.gcs.ExecutionContext;
import com.revenera.gcs.implementor.ServiceImplementor;
import com.revenera.gcs.logging.Loggable;

import javax.jws.WebService;


@WebService(
    serviceName = "revenera-gcs",
    endpointInterface = "com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface",
    wsdlLocation = "WEB-INF/wsdl/schema/LicenseGeneratorService.wsdl"
)
@ServiceImplementor(serviceName = "LicenseGeneratorService")
public class LicenseGeneratorServiceImpl extends Loggable implements LicenseGeneratorServiceInterface {

  @Override
  public PingResponse ping(final PingRequest payload) throws LicGeneratorException {
    //noinspection unused
    try (final ExecutionContext ignored = new ExecutionContext()) {
      try {
        // invoke implementor
        return ExecutionContext
            .getImplementorFactory()
            .getImplementor(ServiceHelper.getLicenseTechnology(payload))
            .ping(payload);
      }
      catch (final Throwable t) {
        logger.exception(t);
        return ServiceHelper.raiseLicGeneratorException(t);
      }
    }
  }

  @Override
  public Status validateProduct(final ProductRequest payload) throws LicGeneratorException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      try {
        // invoke implementor
        return ExecutionContext
            .getImplementorFactory()
            .getImplementor(ServiceHelper.getLicenseTechnology(payload))
            .validateProduct(payload);
      }
      catch (final Throwable t) {
        logger.exception(t);
        return ServiceHelper.raiseLicGeneratorException(t);
      }
    }
  }

  @Override
  public Status validateLicenseModel(final LicenseModelRequest payload) throws LicGeneratorException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      try {
        return ExecutionContext
            .getImplementorFactory()
            .getImplementor(ServiceHelper.getLicenseTechnology(payload))
            .validateLicenseModel(payload);
      }
      catch (final Throwable t) {
        logger.exception(t);
        return ServiceHelper.raiseLicGeneratorException(t);
      }
    }
  }

  @Override
  public GeneratorResponse generateLicense(final GeneratorRequest payload) throws LicGeneratorException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      try {
        return ExecutionContext
            .getImplementorFactory()
            .getImplementor(ServiceHelper.getLicenseTechnology(payload))
            .generateLicense(payload);

      }
      catch (final Throwable t) {
        logger.exception(t);
        return ServiceHelper.raiseLicGeneratorException(t);
      }
    }
  }

  @Override
  public ConsolidatedLicense consolidateFulfillments(final FulfillmentRecordSet payload) throws LicGeneratorException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      try {
        return ExecutionContext
            .getImplementorFactory()
            .getImplementor(ServiceHelper.getLicenseTechnology(payload))
            .consolidateFulfillments(payload);
      }
      catch (final Throwable t) {
        logger.exception(t);
        return ServiceHelper.raiseLicGeneratorException(t);
      }
    }
  }

  @Override
  public LicenseFileDefinitionMap generateLicenseFilenames(final GeneratorRequest payload) throws LicGeneratorException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      try {
        return ExecutionContext
            .getImplementorFactory()
            .getImplementor(ServiceHelper.getLicenseTechnology(payload))
            .generateLicenseFilenames(payload);
      }
      catch (final Throwable t) {
        logger.exception(t);
        return ServiceHelper.raiseLicGeneratorException(t);
      }
    }
  }


  @Override
  public LicenseFileDefinitionMap generateConsolidatedLicenseFilenames(final ConsolidatedLicenseResquest payload)
      throws LicGeneratorException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      try {
        return ExecutionContext
            .getImplementorFactory()
            .getImplementor(ServiceHelper.getLicenseTechnology(payload))
            .generateConsolidatedLicenseFilenames(payload);
      }
      catch (final Throwable t) {
        logger.exception(t);
        return ServiceHelper.raiseLicGeneratorException(t);
      }
    }
  }

  @Override
  public String generateCustomHostIdentifier(final HostIdRequest payload) throws LicGeneratorException {
    //noinspection unused
    try (final ExecutionContext ctx = new ExecutionContext()) {
      try {
        return ExecutionContext
            .getImplementorFactory()
            .getDefaultImplementor()
            .generateCustomHostIdentifier(payload);
      }
      catch (final Throwable t) {
        logger.exception(t);
        return ServiceHelper.raiseLicGeneratorException(t);
      }
    }
  }
}

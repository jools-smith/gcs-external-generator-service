package com.revenera.gcs.implementor.none;

import com.flexnet.external.type.*;
import com.flexnet.external.webservice.keygenerator.LicGeneratorException;
import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;
import com.revenera.gcs.AppContext;
import com.revenera.gcs.implementor.GeneratorBase;
import com.revenera.gcs.implementor.GeneratorImplementor;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;

@SuppressWarnings("unused")
@GeneratorImplementor(technologyId = "NONE", technologyName = "No Enforcement License Technology", isDefault = false)
public class NO_LicenseGenerator extends GeneratorBase {

  @Override
  public PingResponse ping(final PingRequest request) {
    return super.ping(request);
  }



  @Override
  public GeneratorResponse generateLicense(final GeneratorRequest request) throws LicGeneratorException {
    logger.in();

    AppContext.injectRequest(this, request);

    return AppContext.injectResponse(this, GeneratorResponse.class, new GeneratorResponse() {
      {
        this.licenseFileName = "License";
        this.licenseText = "No license available";
      }
    });
  }

  @Override
  public ConsolidatedLicense consolidateFulfillments(final FulfillmentRecordSet request) throws LicGeneratorException {
    logger.in();

    AppContext.injectRequest(this, request);

    return AppContext.injectResponse(this, ConsolidatedLicense.class, new ConsolidatedLicense() {
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
  public Status validateProduct(ProductRequest request) throws LicGeneratorException {
    AppContext.injectRequest(this, request);
    return AppContext.injectResponse(this, Status.class, new Status() {
      {
        this.code = 0;
        this.message = "OK";
      }
    });
  }

  @Override
  public Status validateLicenseModel(LicenseModelRequest request) throws LicGeneratorException {
    AppContext.injectRequest(this, request);
    return AppContext.injectResponse(this, Status.class, new Status() {
      {
        this.code = 0;
        this.message = "OK";
      }
    });
  }

  @Override
  public LicenseFileDefinitionMap generateLicenseFilenames(GeneratorRequest request) throws LicGeneratorException {
    AppContext.injectRequest(this, request);
    return AppContext.injectResponse(this, LicenseFileDefinitionMap.class, new LicenseFileDefinitionMap());
  }

  @Override
  public LicenseFileDefinitionMap generateConsolidatedLicenseFilenames(ConsolidatedLicenseResquest request) throws LicGeneratorException {
    AppContext.injectRequest(this, request);
    return AppContext.injectResponse(this, LicenseFileDefinitionMap.class, new LicenseFileDefinitionMap());
  }

  @Override
  public String generateCustomHostIdentifier(HostIdRequest request) throws LicGeneratorException {
    AppContext.injectRequest(this, request);
    return AppContext.injectResponse(this, String.class, "");
  }
}

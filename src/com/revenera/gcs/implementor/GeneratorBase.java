package com.revenera.gcs.implementor;

import com.flexnet.external.type.*;
import com.flexnet.external.webservice.keygenerator.LicGeneratorException;
import com.flexnet.external.webservice.keygenerator.LicenseGeneratorServiceInterface;
import com.revenera.gcs.AppContext;
import com.revenera.gcs.Beans;
import com.revenera.gcs.logging.LoggingFactory;
import com.revenera.gcs.utils.Serializer;
import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.SystemUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public abstract class GeneratorBase implements TechnologyProperties, LicenseGeneratorServiceInterface {

  protected final LoggingFactory logger = LoggingFactory.create(this.getClass());

  protected List<LicenseFileMapItem> makeLicenseFiles(final List<LicenseFileDefinition> files, final String text, final byte[] bytes) {
    return new ArrayList<LicenseFileMapItem>() {
      {
        files.forEach(lfd -> {
          switch (lfd.getLicenseStorageType()) {
            case TEXT:
              Optional.ofNullable(text).ifPresent(license -> this.add(new LicenseFileMapItem() {
                {
                  this.name = lfd.getName();
                  this.value = license;
                }
              }));
              break;
            case BINARY:
              Optional.ofNullable(bytes).ifPresent(license -> this.add(new LicenseFileMapItem() {
                {
                  this.name = lfd.getName();
                  this.value = license;
                }
              }));
              break;
            default:
              throw new RuntimeException("invalid license file type");
          }
        });
      }
    };
  }

  private static class Technology {
    protected String name;
    protected String id;
  }

  private final Technology technology = new Technology();


  @Override
  public void configureTechnologyProperties(final String id, final String name) {
    this.technology.id = id;
    this.technology.name = name;
  }

  @Override
  public String technologyId() {
    return this.technology.id;
  }

  @Override
  public String technologyName() {
    return this.technology.name;
  }


  private PingResponse doMiniPingResponse() {

    return new PingResponse() {
      {
        this.str = String.join(" | ",
            "GCS External Generator Service",
            AppContext.getApplicationProperties().getVersion(),
            AppContext.getApplicationProperties().getDate(),
            AppContext.getApplicationProperties().getTime());

        this.processedTime = Instant.now().toString();

        final String implementors = String.join(" | ", AppContext.getImplementorFactory().getImplementors());

        this.info = String.join(" | ",
            SystemProperties.getOsName(),
            SystemProperties.getOsVersion(),
            SystemProperties.getOsArch(),
            SystemUtils.getHostName(),
            SystemProperties.getUserName("unknown"),
            implementors);
      }
    };
  }

  private PingResponse doPingResponse() {
    try {
      return new PingResponse() {
        {
          this.info = Serializer.safeSerializeYaml(AppContext.getApplicationData());

          class Bag {
            final Map<String, Object> elements = new LinkedHashMap<>();

            Bag with(final String key, final Object... values) {

              this.elements.put(key, Arrays
                  .stream(values)
                  .map(Object::toString)
                  .collect(Collectors.joining(" | ")));

              return this;
            }

            String build() {
              return this.elements.entrySet()
                  .stream()
                  .map(e -> e.getKey() + ": " + e.getValue())
                  .collect(Collectors.joining("\n"));
            }
          }

          this.str = new Bag()
              .with("technology", logger.getType().getSimpleName(), technologyId())
              .with("version",
                  AppContext.getApplicationProperties().getVersion(),
                  AppContext.getApplicationProperties().getDate(),
                  AppContext.getApplicationProperties().getTime())
              .with("system",
                  SystemProperties.getOsName(),
                  SystemProperties.getOsVersion(),
                  SystemProperties.getOsArch()
              )
              .with("host",
                  SystemUtils.getHostName(),
                  SystemProperties.getUserName("unknown"))
              .with("path", Beans.getResourcePath())
              .with("up-time", AppContext.getApplicationDuration())
              .build();

          this.processedTime = Instant.now().toString();
        }
      };
    }
    catch (final Throwable t) {
      return new PingResponse() {
        {
          this.info = t.getClass().getTypeName();
          this.str = t.getMessage();
          this.processedTime = Instant.now().toString();
        }
      };
    }
  }

  @Override
  public PingResponse ping(final PingRequest request) {
    logger.in();

    AppContext.injectRequest(request);

    return AppContext.injectResponse(PingResponse.class, request.getStr().equals("BIG") ? doPingResponse() : doMiniPingResponse());
  }

  @Override
  public Status validateProduct(final ProductRequest request) throws LicGeneratorException {

    AppContext.injectRequest(request);
    return AppContext.injectResponse(Status.class, new Status() {
      {
        this.message = "product is validated | " + request.getName() + " | " + request.getVersion();
        this.code = 0;
      }
    });
  }

  @Override
  public Status validateLicenseModel(final LicenseModelRequest request) throws LicGeneratorException {

    AppContext.injectRequest(request);
    return AppContext.injectResponse(Status.class, new Status() {
      {
        this.message = "license request is validated | " + request.getName();
        this.code = 0;
      }
    });
  }

  @Override
  public ConsolidatedLicense consolidateFulfillments(final FulfillmentRecordSet request) throws LicGeneratorException {

    AppContext.injectRequest(request);
    final String license = request.getFulfillments().stream().flatMap(fulfilment -> fulfilment.getLicenseFiles().stream()).filter(lfd -> String.class.isAssignableFrom(lfd.getValue().getClass())).map(lfd -> lfd.getValue().toString()).collect(Collectors.joining("\n"));

    return AppContext.injectResponse(ConsolidatedLicense.class, new ConsolidatedLicense() {
      {
        this.fulfillments = request.getFulfillments();

        request.getFulfillments().stream().findAny().ifPresent(fid -> this.licFiles =
            makeLicenseFiles(fid.getLicenseTechnology().getLicenseFileDefinitions(), license, null));
      }
    });
  }

  private <T> T except(final Class<T> type, final String message) {
    throw new RuntimeException(message + " | " + type.getName());
  }

  @Override
  public LicenseFileDefinitionMap generateLicenseFilenames(final GeneratorRequest request) throws LicGeneratorException {

    AppContext.injectRequest(request);
    return except(LicenseFileDefinitionMap.class, "generateLicenseFilenames not implemented");
  }

  @Override
  public LicenseFileDefinitionMap generateConsolidatedLicenseFilenames(final ConsolidatedLicenseResquest request) throws LicGeneratorException {

    AppContext.injectRequest(request);
    return except(LicenseFileDefinitionMap.class, "generateConsolidatedLicenseFilenames not implemented");
  }

  @Override
  public String generateCustomHostIdentifier(final HostIdRequest request) throws LicGeneratorException {

    AppContext.injectRequest(request);
    return except(String.class, "generateCustomHostIdentifier not implemented");
  }
}

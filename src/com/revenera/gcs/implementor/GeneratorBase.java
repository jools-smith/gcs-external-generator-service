package com.revenera.gcs.implementor;

import com.flexnet.external.type.*;
import com.revenera.gcs.ExecutionContext;
import com.revenera.gcs.Beans;
import com.revenera.gcs.logging.LoggingFactory;
import com.revenera.gcs.utils.Serializer;
import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.SystemUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

abstract class Technology implements TechnologyProperties {
  protected String name;
  protected String id;

  @Override
  public void configureTechnologyProperties(final String id, final String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public String technologyId() {
    return this.id;
  }

  @Override
  public String technologyName() {
    return this.name;
  }
}

public abstract class GeneratorBase extends Technology {

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


  protected PingResponse doPing() {
    return new PingResponse() {
      {
        this.info = Serializer.safeSerializeYaml(ExecutionContext.getApplicationData());

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
                ExecutionContext.getApplicationProperties().getVersion(),
                ExecutionContext.getApplicationProperties().getDate(),
                ExecutionContext.getApplicationProperties().getTime())
            .with("system",
                SystemProperties.getOsName(),
                SystemProperties.getOsVersion(),
                SystemProperties.getOsArch()
            )
            .with("host",
                SystemUtils.getHostName(),
                SystemProperties.getUserName("unknown"))
            .with("path", Beans.getResourcePath())
            .with("up-time", ExecutionContext.getApplicationDuration())
            .build();

        this.processedTime = Instant.now().toString();
      }
    };
  }

  public Status doValidateProduct(final ProductRequest request) {
    return new Status() {
      {
        this.message = "product is validated | " + request.getName() + " | " + request.getVersion();
        this.code = 0;
      }
    };
  }

  public Status doValidateLicenseModel(final LicenseModelRequest request)  {
    return new Status() {
      {
        this.message = "license request is validated | " + request.getName();
        this.code = 0;
      }
    };
  }
}

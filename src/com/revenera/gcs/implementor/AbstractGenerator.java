package com.revenera.gcs.implementor;

import com.flexnet.external.type.*;
import com.revenera.gcs.Beans;
import com.revenera.gcs.ExecutionContext;
import com.revenera.gcs.logging.LoggingFactory;
import com.revenera.gcs.utils.HandyBag;
import com.revenera.gcs.utils.Serializer;
import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.SystemUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractGenerator extends AbstractTechnology {

  protected final LoggingFactory logger = LoggingFactory.create(this.getClass());

  @SuppressWarnings("SameParameterValue")
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
        this.str = Serializer.safeSerializeYaml(new HandyBag()
            .withJoined("technology",
                logger.getType().getSimpleName(),
                technologyId())
            .withJoined("version",
                ExecutionContext.getApplicationProperties().getVersion(),
                ExecutionContext.getApplicationProperties().getDate(),
                ExecutionContext.getApplicationProperties().getTime())
            .withJoined("system",
                SystemProperties.getOsName(),
                SystemProperties.getOsVersion(),
                SystemProperties.getOsArch()
            )
            .withJoined("host",
                SystemUtils.getHostName(),
                SystemProperties.getUserName("unknown"))
            .with("path", Beans.getResourcePath())
            .with("up-time", ExecutionContext.getApplicationDuration().toString()));

        this.info = Serializer.safeSerializeYaml(ExecutionContext.getApplicationData());

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

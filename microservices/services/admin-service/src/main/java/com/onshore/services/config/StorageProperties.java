package com.onshore.services.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "com.onshore.storage")
public class StorageProperties {

  private String bucketName;
  private String endpoint;
  private String region;
  private String publicBaseUrl;
  private String accessKey;
  private String secretKey;
}

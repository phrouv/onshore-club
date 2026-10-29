package com.onshore.services.config;

import java.net.URI;
import java.util.Properties;
import org.gaul.s3proxy.AuthenticationType;
import org.gaul.s3proxy.S3Proxy;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

/** File system backed S3Proxy server, and the S3 client talking to it. */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfiguration {

  @Bean
  public BlobStore blobStore(StorageProperties properties) {
    Properties overrides = new Properties();
    overrides.setProperty("jclouds.filesystem.basedir", properties.getLocalFileBaseDirectory());
    return ContextBuilder.newBuilder("filesystem")
        .overrides(overrides)
        .build(BlobStoreContext.class)
        .getBlobStore();
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  public S3Proxy s3Proxy(BlobStore blobStore, StorageProperties properties) {
    return S3Proxy.builder()
        .awsAuthentication(AuthenticationType.NONE, null, null)
        .blobStore(blobStore)
        .endpoint(URI.create(properties.getProxyEndpoint()))
        .build();
  }

  @Bean
  public S3Client s3Client(StorageProperties properties) {
    return S3Client.builder()
        .region(Region.US_EAST_1)
        .endpointOverride(URI.create(properties.getProxyEndpoint()))
        .credentialsProvider(AnonymousCredentialsProvider.create())
        .serviceConfiguration(
            S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .checksumValidationEnabled(false)
                .build())
        .build();
  }
}

package com.onshore.services.service.impl;

import com.onshore.services.config.StorageProperties;
import com.onshore.services.service.ImageStorageService;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

  private final S3Client s3Client;
  private final String bucket;
  private final String publicBaseUrl;

  public ImageStorageServiceImpl(S3Client s3Client, StorageProperties properties) {
    this.s3Client = s3Client;
    this.bucket = properties.getBucketName();
    this.publicBaseUrl = trimTrailingSlash(properties.getPublicBaseUrl());
  }

  @Override
  public String upload(String key, byte[] content, String contentType) {
    ensureBucket();
    s3Client.putObject(
        PutObjectRequest.builder().bucket(bucket).key(key).contentType(contentType).build(),
        RequestBody.fromBytes(content));
    return publicBaseUrl + "/" + key;
  }

  @Override
  public void deleteByUrl(String url) {
    if (url == null || !url.startsWith(publicBaseUrl + "/")) {
      return;
    }
    String key = url.substring(publicBaseUrl.length() + 1);
    s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
  }

  private void ensureBucket() {
    try {
      s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
    } catch (NoSuchBucketException e) {
      s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
      s3Client.putBucketPolicy(
          PutBucketPolicyRequest.builder().bucket(bucket).policy(publicReadPolicy()).build());
    }
  }

  /** Browsers load the category images directly from the bucket. */
  private String publicReadPolicy() {
    return """
        {"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"AWS":["*"]},\
        "Action":["s3:GetObject"],"Resource":["arn:aws:s3:::%s/*"]}]}"""
        .formatted(bucket);
  }

  private static String trimTrailingSlash(String value) {
    return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
  }
}

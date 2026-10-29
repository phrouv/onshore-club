package com.onshore.services.service.impl;

import com.onshore.services.config.StorageProperties;
import com.onshore.services.service.ImageStorageService;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

  private final S3Client s3Client;
  private final String bucket;

  public ImageStorageServiceImpl(S3Client s3Client, StorageProperties properties) {
    this.s3Client = s3Client;
    this.bucket = properties.getBucketName();
  }

  @Override
  public void upload(String key, byte[] content, String contentType) {
    ensureBucket();
    s3Client.putObject(
        PutObjectRequest.builder().bucket(bucket).key(key).contentType(contentType).build(),
        RequestBody.fromBytes(content));
  }

  @Override
  public byte[] download(String key) {
    return s3Client
        .getObjectAsBytes(GetObjectRequest.builder().bucket(bucket).key(key).build())
        .asByteArray();
  }

  @Override
  public void delete(String key) {
    s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
  }

  private void ensureBucket() {
    try {
      s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
    } catch (NoSuchBucketException e) {
      s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
    }
  }
}

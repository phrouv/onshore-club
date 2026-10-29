package com.onshore.services.service;

/** Stores category images through the S3Proxy API; the object key is saved in Category.image. */
public interface ImageStorageService {

  void upload(String key, byte[] content, String contentType);

  byte[] download(String key);

  void delete(String key);
}

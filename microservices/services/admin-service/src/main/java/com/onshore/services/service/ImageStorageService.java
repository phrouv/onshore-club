package com.onshore.services.service;

/**
 * Stores category images in MinIO through the S3 API; the public URL is saved in Category.image.
 */
public interface ImageStorageService {

  /** Uploads the content and returns the public URL of the stored object. */
  String upload(String key, byte[] content, String contentType);

  /** Deletes the object behind a URL returned by {@link #upload}; other URLs are ignored. */
  void deleteByUrl(String url);
}

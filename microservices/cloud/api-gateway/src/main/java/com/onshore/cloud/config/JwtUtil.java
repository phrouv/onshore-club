package com.onshore.cloud.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final SecretKey key;

  public JwtUtil() {
    this.key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }

  public String extractEmail(String token) {
    return extractAllClaims(token).get("email", String.class);
  }

  public String extractAuthorities(String token) {
    return extractAllClaims(token).get("authorities", String.class);
  }

  public UUID extractUserId(String token) {
    return UUID.fromString(extractAllClaims(token).get("userId", String.class));
  }

  public boolean isTokenValid(String token) {
    try {
      extractAllClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /** Returns how long until the token expires (may be negative if already expired). */
  public Duration getRemainingValidity(String token) {
    Date expiration = extractAllClaims(token).getExpiration();
    return Duration.between(Instant.now(), expiration.toInstant());
  }
}

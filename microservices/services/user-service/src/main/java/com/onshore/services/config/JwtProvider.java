package com.onshore.services.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class JwtProvider {

  private final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

  public String generateToken(Authentication auth, UUID userId) {
    Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
    String roles = populateAuthorities(authorities);

    return Jwts.builder()
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
        .claim("email", auth.getName())
        .claim("authorities", roles)
        .claim("userId", userId.toString())
        .signWith(key)
        .compact();
  }

  public String getEmailFromJwtToken(String jwt) {
    if (jwt.startsWith(JwtConstant.TOKEN_PREFIX)) {
      jwt = jwt.substring(JwtConstant.TOKEN_PREFIX.length());
    }
    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
    return String.valueOf(claims.get("email"));
  }

  private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
    Set<String> auths = new HashSet<>();
    for (GrantedAuthority authority : authorities) {
      auths.add(authority.getAuthority());
    }
    return String.join(",", auths);
  }
}

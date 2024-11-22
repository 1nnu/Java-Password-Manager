package ee.taltech.passman.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  SecretKey key = Jwts.SIG.HS256.key().build();

  public String generateJwtToken(String username) {
    return Jwts.builder()
        .issuer("passman")
        .notBefore(Date.from(Instant.now()))
        .expiration(Date.from(Instant.now().plus(10, ChronoUnit.MINUTES)))
        .subject(username)
        .signWith(key)
        .compact();
  }

  public String generateJwtToken(String username, long expirationMinutes) {
    return Jwts.builder()
        .issuer("passman")
        .notBefore(Date.from(Instant.now()))
        .expiration(Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES)))
        .subject(username)
        .signWith(key)
        .compact();
  }

  public boolean isValidToken(String jwt) {
    try {
      return !Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload().isEmpty();
    } catch (Exception e) {
      return false;
    }
  }

  public String getUsernameFromToken(String validJwt) {
    try {
      return Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(validJwt)
          .getPayload()
          .getSubject();
    } catch (Exception e) {
      throw new JwtException("Could not get username");
    }
  }

  public boolean hasVaultAccess(String jwt) {
    try {
      return Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(jwt)
          .getPayload()
          .get("vaultaccess")
          .equals("true");
    } catch (Exception e) {
      return false;
    }
  }

  public String generateVaultAccessJwtToken(String username) {
    return Jwts.builder()
        .issuer("passman")
        .notBefore(Date.from(Instant.now()))
        .expiration(Date.from(Instant.now().plus(10, ChronoUnit.MINUTES)))
        .subject(username)
        .claim("vaultaccess", "true")
        .signWith(key)
        .compact();
  }
}

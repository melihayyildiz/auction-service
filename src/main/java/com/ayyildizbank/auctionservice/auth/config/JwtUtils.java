package com.ayyildizbank.auctionservice.auth.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(key()).build()
            .parseClaimsJws(token).getBody().getSubject();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith((SecretKey) key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("JwtUtils.validateJwtToken Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JwtUtils.validateJwtToken token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JwtUtils.validateJwtToken token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JwtUtils.validateJwtToken claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public String generateJwt(UserDetailsImpl user) {
        return Jwts.builder()
            .subject(user.getUsername())
            .claim("id", user.getId())
            .claim("email", user.getEmail())
            .claim("firstName", user.getFirstName())
            .claim("lastName", user.getLastName())
            .claim("username", user.getUsername())
            .claim("roles", user.getAuthorities())
            .issuedAt(new Date()).expiration(Date.from(Instant.ofEpochSecond(1737665520L))) // Melih: intentionally left like this
            .signWith(key())
            .compact();
    }
}

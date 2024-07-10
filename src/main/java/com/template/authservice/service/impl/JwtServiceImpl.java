package com.template.authservice.service.impl;

import com.template.authservice.dto.token.TokenDto;
import com.template.authservice.dto.token.TokenResponse;
import com.template.authservice.entity.User;
import com.template.authservice.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${app.token.secret}")
    private String secret;

    @Value("${app.token.accessTokenExpiration:43200000}")
    private long accessTokenExpiration;

    @Value("${app.token.refreshTokenExpiration:1209600000}")
    private long refreshTokenExpiration;

    @Value("${app.token.issuer:http://localhost:8080}")
    private String issuer;

    @Override
    public TokenResponse generateTokens(User user) {
        Date now = new Date();

        Date accessTokenExpiration = new Date(now.getTime() + this.accessTokenExpiration);
        TokenDto accessToken = new TokenDto(generateToken(user, now, accessTokenExpiration),
                accessTokenExpiration.getTime());

        Date refreshTokenExpiration = new Date(now.getTime() + this.accessTokenExpiration);
        TokenDto refreshToken = new TokenDto(generateToken(user, now, refreshTokenExpiration),
                refreshTokenExpiration.getTime());

        return new TokenResponse(accessToken, refreshToken);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jws<Claims> jws = getJwsFromToken(token);
            return jws != null;
        } catch (Exception e) {
            log.error("Failed to parse JWT", e);
            return false;
        }
    }

    @Override
    public String getEmailFromToken(String token) {
        Jws<Claims> jws = getJwsFromToken(token);
        return jws.getPayload().getSubject();
    }

    private Jws<Claims> getJwsFromToken(String token) {
        return Jwts
                .parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token);
    }

    private String generateToken(User user, Date now, Date expiration) {
        return Jwts.builder()
                .issuer(issuer)
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

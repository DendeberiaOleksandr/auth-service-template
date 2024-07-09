package com.template.authservice.service.impl;

import com.template.authservice.dto.token.TokenDto;
import com.template.authservice.dto.token.TokenResponse;
import com.template.authservice.entity.User;
import com.template.authservice.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
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

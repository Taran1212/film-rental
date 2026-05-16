package com.example.backend.security;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "mysecretkeymysecretkeymysecretkey12";

    private final Key key =
            Keys.hmacShaKeyFor(
                    SECRET_KEY.getBytes()
            );

    public String generateToken(
            String username,
            String role
    ) {

        Map<String, Object> claims =
                new HashMap<>();

        claims.put("role", role);

        return Jwts.builder()

                .setClaims(claims)

                .setSubject(username)

                .setIssuedAt(new Date())

                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000 * 60 * 60 * 10
                        )
                )

                .signWith(
                        key,
                        SignatureAlgorithm.HS256
                )

                .compact();
    }

    public String extractUsername(
            String token
    ) {

        return extractAllClaims(token)
                .getSubject();
    }

    public String extractRole(
            String token
    ) {

        return extractAllClaims(token)
                .get("role", String.class);
    }

    public boolean isTokenValid(
            String token
    ) {

        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(
            String token
    ) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims extractAllClaims(
            String token
    ) {

        return Jwts.parserBuilder()

                .setSigningKey(key)

                .build()

                .parseClaimsJws(token)

                .getBody();
    }
}
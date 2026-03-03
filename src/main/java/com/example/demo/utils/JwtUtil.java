package com.example.demo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// 이거를 쓰면 알아서 찾아서 가져와준다. 그러면 변수이름을 맞춰줘야 한다. yml 변수와
// 복잡할 때는 이렇게 쓰고 간단할때는 value로 사용하기
// @ConfigurationProperties(prefix = "jwt")

// Value를 쓰기 위해 이걸 같이 달아준다.
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String key;

    @Value("${jwt.expire}")
    private int expire;

    public SecretKey getEncodedKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }

    public String createToken(Long idx, String email, String role) {
        String jwt = Jwts.builder()
                .claim("idx", idx)
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + expire)).signWith(getEncodedKey()).compact();

        return jwt;
    }

    public Long getUserIdx(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getEncodedKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("idx", Long.class);
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getEncodedKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("email", String.class);
    }

    public String getRole(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getEncodedKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role", String.class);
    }
}
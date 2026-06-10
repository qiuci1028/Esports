package com.esports.bigdata.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类：签发 / 解析 / 校验
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire-hours}")
    private Long expireHours;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("username", username);
        claims.put("role", role);
        Date now = new Date();
        Date exp = new Date(now.getTime() + expireHours * 3600 * 1000);
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.warn("JWT 解析失败：{}", e.getMessage());
            return null;
        }
    }

    public boolean isExpired(String token) {
        Claims c = parseToken(token);
        return c == null || c.getExpiration().before(new Date());
    }

    public Long getUserId(String token) {
        Claims c = parseToken(token);
        return c == null ? null : c.get("uid", Long.class);
    }

    public String getUsername(String token) {
        Claims c = parseToken(token);
        return c == null ? null : c.getSubject();
    }

    public String getRole(String token) {
        Claims c = parseToken(token);
        return c == null ? null : c.get("role", String.class);
    }
}

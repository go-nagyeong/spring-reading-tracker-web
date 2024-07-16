package com.readingtracker.boochive.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration; // 15분
    private final long refreshTokenExpiration; // 24시간

    public JwtTokenProvider(@Value("${security.jwt.secret}") String secretKey,
                            @Value("${security.jwt.access-token-expiration}") long accessTokenExpiration,
                            @Value("${security.jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * Refresh Token을 사용해 새로운 Access Token 재발급
     */
    public String generateAccessTokenFromRefreshToken(String refreshToken) {
        Claims claims = extractAllClaims(refreshToken);
        return generateAccessToken(claims.getSubject());
    }

    /**
     * Refresh Token 발급
     */
    public String generateAccessToken(String username) {
        return generateToken(username, accessTokenExpiration);
    }

    /**
     * Access Token 발급
     */
    public String generateRefreshToken(String username) {
        return generateToken(username, refreshTokenExpiration);
    }

    /**
     * 사용자 이름을 기반으로 JWT 토큰 생성
     */
    public String generateToken(String username, Long expiration) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰이 유효한지 검사
     */
    public boolean isTokenValid(String token) {
        try {
            boolean isTokenExpired = extractExpiration(token).before(new Date()); // 토큰 만료 여부
            return !isTokenExpired;
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우 false를 반환
            log.info("Expired e: {}", e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            // 기타 JWT 예외 처리
            log.info("Etc e: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 토큰에서 모든 클레임 추출
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
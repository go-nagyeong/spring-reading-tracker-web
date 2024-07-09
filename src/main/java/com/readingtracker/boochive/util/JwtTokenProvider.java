package com.readingtracker.boochive.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final long accessTokenExpiration; // 15분
    private final long refreshTokenExpiration; // 24시간

    public JwtTokenProvider(@Value("${security.jwt.secret}") String secretKey,
                            @Value("${security.jwt.access-token-expiration}") long accessTokenExpiration,
                            @Value("${security.jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.secretKey = secretKey;
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
//                .setClaims(claims) // deprecated
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 토큰이 유효한지 검사
     */
    public boolean isTokenValid(String token) {
        boolean isTokenExpired = extractExpiration(token).before(new Date()); // 토큰 만료 여부
        return !isTokenExpired;
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
        return Jwts.parser()
//                .setSigningKey(getSigningKey()) // deprecated
//                .parseClaimsJws(token)
//                .build()
//                .getBody();
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    /**
     * 서명에 사용할 Secret Key를 생성
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
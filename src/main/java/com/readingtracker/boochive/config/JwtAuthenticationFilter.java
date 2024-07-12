package com.readingtracker.boochive.config;

import com.readingtracker.boochive.service.UserDetailServiceImpl;
import com.readingtracker.boochive.util.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailServiceImpl userDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. 요청에서 JWT 토큰 추출
            String token = resolveAccessToken(request);

            // 2. 토큰 유효성 검사 후 권한 부여
            if (token != null && jwtTokenProvider.isTokenValid(token)) {
                String userEmail = jwtTokenProvider.extractUsername(token);

                UserDetails userDetails = userDetailService.loadUserByUsername(userEmail);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            handleJwtException(e, request, response, filterChain);
        }
    }

    /**
     * JWT 토큰 예외 처리
     */
    private void handleJwtException(JwtException e, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        log.info("JWT 필터 처리 중 오류 발생: " + e.getMessage());

        // 토큰 만료 처리 >> 토큰 만료 여부 확인 후 Refresh Token을 통한 재발급 처리 (MPA 형태의 프론트 때문에 서버 측에서 자동 처리)
        if (e instanceof ExpiredJwtException) {
            String refreshToken = resolveRefreshToken(request);

            if (refreshToken != null && jwtTokenProvider.isTokenValid(refreshToken)) {
                try {
                    String newAccessToken = jwtTokenProvider.generateAccessTokenFromRefreshToken(refreshToken);

                    // 새 액세스 토큰 쿠키에 저장
                    Cookie cookie = new Cookie("access_token", newAccessToken);
                    cookie.setHttpOnly(true); // Http Only 속성 설정
//                    cookie.setSecure(true);   // https 연결에서만 전송할 경우 설정 (TODO: 개발 중 임시 주석)
                    cookie.setPath("/");      // 쿠키가 적용될 경로 설정
                    response.addCookie(cookie);

                    // 토큰 재발급 후 클라이언트 요청을 재진행
                    log.info("토큰 재발급 후 새로 발급된 토큰으로 원래 요청을 다시 처리");
                    filterChain.doFilter(request, response);
                } catch (Exception ex) {
                    log.info("JWT 재발급 중 오류 발생: " + ex.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Unauthorized: Failed to refresh access token");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: No refresh token provided");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: " + e.getMessage());
        }
    }

    /**
     * 요청에서 Access Token 추출
     */
    private String resolveAccessToken(HttpServletRequest request) {
        // Request Header에서 토큰 추출
//        String bearerToken = request.getHeader("Authorization");
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }

        // 쿠키에서 토큰 추출 (MPA 형태의 프론트 때문에 페이지 이동의 경우, 쿠키에서 찾기)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for(Cookie cookie: cookies){
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    /**
     * 요청에서 Refresh Token 추출 / TODO: (임시) 추후 Refresh Token은 Redis에 저장
     */
    public String resolveRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for(Cookie cookie: cookies){
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}

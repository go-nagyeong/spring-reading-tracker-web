package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.util.ApiResponse;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.LoginForm;
import com.readingtracker.boochive.dto.RegisterForm;
import com.readingtracker.boochive.service.UserService;
import com.readingtracker.boochive.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, String>>> register(@Valid @RequestBody RegisterForm form, BindingResult bindingResult) {
        // 추가 검증 (이메일 중복 확인, 비밀번호 확인 일치 여부)
        if (!bindingResult.hasFieldErrors("email")) {
            if (userService.findUserByEmail(form.getEmail()).isPresent()) {
                ObjectError newError = new ObjectError("email", "이미 존재하는 이메일입니다.");
                bindingResult.addError(newError);
            }
        }
        if (!bindingResult.hasFieldErrors("confirmPassword")) {
            if (!form.getConfirmPassword().isBlank() && !form.getConfirmPassword().equals(form.getPassword())) {
                ObjectError newError = new ObjectError("confirmPassword", "비밀번호가 일치하지 않습니다.");
                bindingResult.addError(newError);
            }
        }

        // 유효성 검사 결과 전처리
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = handleValidationErrors(bindingResult);
            return ApiResponse.failure("", errorMap);
        }

        // 회원 데이터 생성
        User user = new User();
        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword());
        user.setName(form.getUsername());
        userService.saveUser(user);

        return ApiResponse.success("회원가입에 성공하였습니다.");
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@Valid @RequestBody LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        // 유효성 검사 결과 전처리
        Map<String, String> errorMap = new LinkedHashMap<>();
        if (bindingResult.hasErrors()) {
            errorMap = handleValidationErrors(bindingResult);
            return ApiResponse.failure("", errorMap);
        }

        // AuthenticationManager를 사용하여 로그인 인증 (인증 객체 생성 후 전달)
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(form.getEmail(), form.getPassword());
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            // 잘못된 인증 정보 (이메일 또는 비밀번호)
            errorMap.put("password", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return ApiResponse.failure("", errorMap);
        } catch (LockedException e) {
            // 계정이 잠겼을 경우
            errorMap.put("password", "사용자 계정이 잠겼습니다. 관리자에게 문의하세요.");
            return ApiResponse.failure("", errorMap);
        } catch (AuthenticationException e) {
            // 그 외의 인증 예외 처리
            log.error("인증 객체 생성 에러: {}", e.getMessage(), e);
        }

        // 인증 성공 시 토큰 발급
        String accessToken = jwtTokenProvider.generateAccessToken(form.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(form.getEmail());

        // 토큰 쿠키에 저장
        // (memo) Access Token까지 저장하는 이유는 클라이언트에서 페이지 이동시 헤더에 담아 보내지 못하기 때문에 저장된 쿠키로 검증
        setHttpOnlyCookie("access_token", accessToken, response);
        setHttpOnlyCookie("refresh_token", refreshToken, response); // TODO: (임시) 추후 Refresh Token은 Redis에 저장

        return ApiResponse.success("로그인에 성공하였습니다.");
    }

    /**
     * HttpOnly + Secure 쿠키 저장 메서드 (JWT 토큰 저장)
     */
    private void setHttpOnlyCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true); // Http Only 속성 설정
//        cookie.setSecure(true);   // https 연결에서만 전송할 경우 설정 (TODO: 개발 중 임시 주석)
        cookie.setPath("/");      // 쿠키가 적용될 경로 설정
        response.addCookie(cookie);
    }

    /**
     * 유효성 검사 결과 전처리 메서드
     */
    private Map<String, String> handleValidationErrors(BindingResult bindingResult) {
        // 유효성 검사 결과 순서 정렬 (원래는 정렬 없이 랜덤으로 나옴)
        List<FieldError> fieldErrors = new ArrayList<>(bindingResult.getFieldErrors());
        fieldErrors.sort(Comparator
                .comparing(FieldError::getField)
                .thenComparing(Comparator.comparing(FieldError::getCode).reversed()));

        // <필드(key): 에러메세지(value)> 구조로 저장
        Map<String, String> errorMap = new LinkedHashMap<>(); // 순서 적용을 위해 LinkedHashMap 사용
        fieldErrors.forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        bindingResult.getGlobalErrors().forEach(error -> {
            errorMap.put(error.getObjectName(), error.getDefaultMessage());
        });

        return errorMap;
    }
}

package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.PasswordUpdateRequest;
import com.readingtracker.boochive.dto.UserInfoRequest;
import com.readingtracker.boochive.dto.UserInfoResponse;
import com.readingtracker.boochive.mapper.UserInfoMapper;
import com.readingtracker.boochive.service.UserService;
import com.readingtracker.boochive.util.ApiResponse;
import com.readingtracker.boochive.util.FileStorageUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final FileStorageUtil fileStorageUtil;

    /**
     * GET - 로그인 유저 세부 회원 정보 조회 (이름, 프로필 이미지, 성별, 생년월일 등)
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getLoggedInUserInfoDetail(@AuthenticationPrincipal User user) {
        UserInfoResponse loggedInUser = UserInfoMapper.INSTANCE.toDto(user);

        return ApiResponse.success(null, loggedInUser);
    }

    /**
     * POST - 프로필 이미지 업로드
     */
    @PostMapping("/profile-image")
    public ResponseEntity<ApiResponse<String>> uploadProfileImage(@RequestParam Long userId,
                                                                  @RequestParam MultipartFile file) throws IOException {
        String directory = "images/profile/"+userId;
        fileStorageUtil.clearDirectory(directory); // 업로드 전 기존 이미지 삭제
        String saveUrl = fileStorageUtil.upload(directory, file);

        return ApiResponse.success(null, saveUrl);
    }

    /**
     * PUT - 회원정보 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserInfoResponse>> updateUser(@PathVariable Long id,
                                                                    @Valid @RequestBody UserInfoRequest userInfo,
                                                                    BindingResult bindingResult) {
        // 유효성 검사
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = handleValidationErrors(bindingResult);
            return ApiResponse.failure(null, errorMap);
        }

        UserInfoResponse savedUser = userService.updateUser(id, userInfo);

        return ApiResponse.success("회원정보가 수정되었습니다.", savedUser);
    }

    /**
     * PUT - 비밀번호 변경
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Object>> updatePassword(@PathVariable Long id,
                                                              @Valid @RequestBody PasswordUpdateRequest request,
                                                              BindingResult bindingResult) {
        // 유효성 검사
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = handleValidationErrors(bindingResult);
            return ApiResponse.failure(null, errorMap);
        }

        userService.updatePassword(id, request);

        return ApiResponse.success("비밀번호가 변경되었습니다.");
    }

    /**
     * DELETE - 계정 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteAccount(@PathVariable Long id,
                                                             HttpServletRequest request,
                                                             HttpServletResponse response) {
        userService.deleteUserById(id);
        clearCookies(request, response);

        return ApiResponse.success(null);
    }

    /**
     * 쿠키 삭제 (토큰 만료시 처리)
     */
    private void clearCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setValue("");
                cookie.setMaxAge(0);
                cookie.setPath("/"); // 쿠키의 경로 설정 (루트로 설정)
                response.addCookie(cookie);
            }
        }
    }

    /**
     * (공통 메서드) 유효성 검사 결과 전처리
     */
    private Map<String, String> handleValidationErrors(BindingResult bindingResult) {
        // 유효성 검사 결과 순서 정렬 (원래는 정렬 없이 랜덤으로 나옴)
        List<FieldError> fieldErrors = new ArrayList<>(bindingResult.getFieldErrors());
        fieldErrors.sort(Comparator
                .comparing(FieldError::getField)
                .thenComparing(Comparator.comparing(FieldError::getCode).reversed()));

        // <필드(key): 에러메세지(value)> 구조로 저장
        Map<String, String> errorMap = new LinkedHashMap<>(); // 순서 적용을 위해 LinkedHashMap 사용
        bindingResult.getGlobalErrors().forEach(error -> { // @ConfirmPassword
            if (error.getCode().equals("NewPassword")) {
                errorMap.put("password", error.getDefaultMessage());
            } else { // "ConfirmPassword"
                errorMap.put("confirmPassword", error.getDefaultMessage());
            }
        });
        fieldErrors.forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });

        return errorMap;
    }
}

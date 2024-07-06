package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.common.ApiResponse;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.RegisterForm;
import com.readingtracker.boochive.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<HashMap<String, String>>> register(@Valid @RequestBody RegisterForm form, BindingResult bindingResult) {
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

        // 유효성 검사 응답 데이터 전처리
        if (bindingResult.hasErrors()) {
            // 유효성 검증 결과 순서 정렬 (원래는 정렬 없이 랜덤으로 나옴)
            List<FieldError> fieldErrors = new ArrayList<>(bindingResult.getFieldErrors());
            fieldErrors.sort(Comparator.comparing(FieldError::getCode).reversed());

            HashMap<String, String> errorMap = new HashMap<>();
            fieldErrors.forEach(error -> {
                errorMap.put(error.getField(), error.getDefaultMessage());
            });
            bindingResult.getGlobalErrors().forEach(error -> {
                errorMap.put(error.getObjectName(), error.getDefaultMessage());
            });

            return ApiResponse.failure("", errorMap, HttpStatus.MULTI_STATUS);
        }

        // 회원 데이터 생성
        User user = new User();
        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword());
        user.setName(form.getUsername());
        userService.saveUser(user);

        return ApiResponse.success("회원가입이 성공적으로 완료되었습니다.");
    }
}

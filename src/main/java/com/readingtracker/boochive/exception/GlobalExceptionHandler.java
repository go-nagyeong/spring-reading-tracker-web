package com.readingtracker.boochive.exception;

import com.readingtracker.boochive.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataAccessException(DataAccessException e) {
        log.error("GlobalExceptionHandler :: 데이터베이스 오류", e);
        return ApiResponse.failure("데이터베이스 오류가 발생했습니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("GlobalExceptionHandler :: 알 수 없는 오류", e);
        return ApiResponse.failure("알 수 없는 오류가 발생했습니다.");
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse<Object>> handleIOException(IOException e) {
        log.error("GlobalExceptionHandler :: 파일 처리 오류", e);
        return ApiResponse.failure("파일 처리 중 오류가 발생했습니다. 나중에 다시 시도해 주세요.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("GlobalExceptionHandler :: 데이터 타입 오류", e);
        return ApiResponse.failure("데이터 형식이 올바르지 않습니다.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("GlobalExceptionHandler :: Illegal Argument 오류", e);
        return ApiResponse.failure(e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.error("GlobalExceptionHandler :: 존재하지 않는 레코드 오류", e);
        return ApiResponse.failure(e.getMessage());
    }

    @ExceptionHandler(ResourceAccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceAccessDeniedException(ResourceAccessDeniedException e) {
        log.error("GlobalExceptionHandler :: 접근 권한 오류", e);
        return ApiResponse.failure(e.getMessage());
    }

    /**
     * 커스텀 에러
     */
    @ExceptionHandler(AladinApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleAladinApiException(AladinApiException e) {
        log.error("GlobalExceptionHandler :: 알라딘 API 오류", e);
        return ApiResponse.failure(e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error("GlobalExceptionHandler :: 잘못된 URL 오류", e);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("GlobalExceptionHandler :: 잘못된 URL 오류", e);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleCustomArgumentNotValidException(CustomArgumentNotValidException e) {
        log.error("GlobalExceptionHandler :: 유효성 검사 오류", e);

        // 유효성 검사 결과 정렬 - 필드 기준 정렬(커스텀), 검증 코드 기준 정렬
        Map<String, Integer> sortByField = e.getFieldPriority();
        Map<String, Integer> sortByValidation = e.getValidationPriority();

        List<ObjectError> errors = e.getBindingResult().getAllErrors().stream()
                .sorted(Comparator
                        .comparing(
                                (ObjectError error) -> error instanceof FieldError
                                        ? sortByField.getOrDefault(((FieldError) error).getField(), Integer.MAX_VALUE)
                                        // NOTE: 필드 에러가 아닌 글로벌 에러는 필드명 대신 검증 코드로 처리
                                        : sortByField.getOrDefault(error.getCode(), Integer.MAX_VALUE)
                        )
                        .thenComparing(error -> sortByValidation.getOrDefault(error.getCode(), Integer.MAX_VALUE))
                )
                .toList();

        // 결과 반환 타입에 따라 결과 전처리
        String message = null;
        Map<String, String> data = null;

        if ("single".equals(e.getErrorFormat())) {
            message = errors.get(0).getDefaultMessage();
        } else {
            data = errors.stream()
                    .collect(LinkedHashMap::new, (map, error) -> {
                        String key = error instanceof FieldError ?
                                ((FieldError) error).getField() : error.getCode();
                        map.putIfAbsent(key, error.getDefaultMessage());
                    }, LinkedHashMap::putAll);
        }

        return ApiResponse.failure(message, data);
    }
}

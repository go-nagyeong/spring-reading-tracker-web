package com.readingtracker.boochive.exception;

import com.readingtracker.boochive.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.NoSuchElementException;

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
}

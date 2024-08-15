package com.readingtracker.boochive.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class MyErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            // 직접 상태 코드가 설정되지 않았을 때 기본 상태 코드 설정
            statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        }

        if (statusCode != null) {
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "errors/404";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "errors/403";
            }
        }

        return "errors/500";
    }
}

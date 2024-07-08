package com.readingtracker.boochive.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
//        Object httpStatus = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//        int statusCode = Integer.parseInt(httpStatus.toString());
//        if (statusCode == HttpStatus.NOT_FOUND.value()) {
//        }
        return "errors/404";
    }
}

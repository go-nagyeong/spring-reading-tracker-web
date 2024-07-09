package com.readingtracker.boochive.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class ViewController {

    /**
     * Home
     */
    @GetMapping("/")
    public String home() {
        if (!isAuthenticated()) {
            return "redirect:/login"; // 비인증 상태 > 로그인 페이지로 다이렉트
        }
        return "index";
    }

    /**
     * User
     */
    @GetMapping("/login")
    public String showLoginForm() {
        if (isAuthenticated()) {
            return "redirect:/"; // 인증 상태 > 대시보드 페이지로 리다이렉트
        }
        return "user/login";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        if (isAuthenticated()) {
            return "redirect:/"; // 인증 상태 > 대시보드 페이지로 리다이렉트
        }
        return "user/register";
    }

    @GetMapping("/find-password")
    public String showFindPasswordForm() {
        if (isAuthenticated()) {
            return "redirect:/"; // 인증 상태 > 대시보드 페이지로 리다이렉트
        }
        return "user/find-password";
    }

    // 로그인 여부 검증 메서드
    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    /**
     * TODO: 임시
     */
    @RequestMapping("/{page1}/{page2}")
    public String custom2(@PathVariable("page1") String page1, @PathVariable("page2") String page2) {
        return page1+"/"+page2.replace(".html","");
    }
}

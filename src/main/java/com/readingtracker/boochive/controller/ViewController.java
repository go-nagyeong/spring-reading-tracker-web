package com.readingtracker.boochive.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
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
     * Book
     */
    @GetMapping("/books")
    public String showBookSearchList() {
        return "books/book-list";
    }

    @GetMapping("/books/{isbn}")
    public String showBookDetail() {
        return "books/book-detail";
    }

    @GetMapping("/books/partials/{partial}")
    public String showBookPartial(@PathVariable String partial) {
        return "books/partials/"+partial;
    }

    /**
     * TODO: 임시
     */
    @RequestMapping("/common/{page}")
    public String custom1(@PathVariable("page") String page) {
        return "common/"+page;
    }

    @RequestMapping("/notes/{page}")
    public String custom2(@PathVariable("page") String page) {
        return "notes/"+page;
    }

    @RequestMapping("/dashboard/{page}")
    public String custom3(@PathVariable("page") String page) {
        return "dashboard/"+page;
    }

    @RequestMapping("/mypage/{page}")
    public String custom4(@PathVariable("page") String page) {
        return "mypage/"+page;
    }

    @RequestMapping("/settings/{page}")
    public String custom5(@PathVariable("page") String page) {
        return "settings/"+page;
    }
}

package com.readingtracker.boochive.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ViewController {

    /**
     * Dashboard
     */
    @GetMapping("/")
    public String index() {
        if (!isAuthenticated()) {
            return "redirect:/login"; // 비인증 상태 > 로그인 페이지로 다이렉트
        }
        return "dashboard/index";
    }

    // TODO: 추후 웰컴 페이지 만들어서 대시보드랑 분리 (React로)
//    @GetMapping("/dashboard")
//    public String dashboard() {
//        return "dashboard/index";
//    }


    /**
     * Authentication
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
    @GetMapping("/books/search")
    public String showBookSearchList() {
        return "books/book-list";
    }

    @GetMapping("/books/detail/{isbn}")
    public String showBookDetail() {
        return "books/book-detail";
    }

    @GetMapping("/books/partials/{partial}")
    public String showBookPartial(@PathVariable String partial) {
        return "books/partials/"+partial;
    }


    /**
     * Setting
     */
    @GetMapping("/settings/account")
    public String showAccountSettingForm() {
        return "settings/account-setting";
    }


    /**
     * My Page - Reading Book, Collection
     */
    @GetMapping("/mypage")
    public String myPage() {
        return "mypage/mypage";
    }
    @GetMapping("/my/reading-books")
    public String showMyBookList() {
        return "mypage/my-book-list";
    }

    @GetMapping("/my/collections")
    public String showMyCollectionList() {
        return "mypage/my-collection";
    }

    @GetMapping("/my/partials/{partial}")
    public String showMyPagePartial(@PathVariable String partial) {
        return "mypage/partials/"+partial;
    }


    /**
     * Reading Note - My Notes
     */
    @GetMapping("/my/notes")
    public String showNoteList() {
        return "redirect:/my/notes/pencil";
    }

    @GetMapping("/my/notes/pencil")
    public String showPencilNoteList() {
        return "notes/pencil-note";
    }

    @GetMapping("/my/notes/highlight")
    public String showHighlightNoteList() {
        return "notes/highlight-note";
    }

    @GetMapping("/my/notes/post-it")
    public String showPostItNoteList() {
        return "notes/post-it-note";
    }

    @GetMapping("/notes/partials/{partial}")
    public String showNotePartial(@PathVariable String partial) {
        return "notes/partials/"+partial;
    }


    /**
     * Common
     */
    @GetMapping("/common/reading-timer")
    public String showReadingTimer() {
        return "common/reading-timer";
    }

    @GetMapping("/common/delete-confirm")
    public String showDeleteConfirm() {
        return "common/modal-delete-confirm";
    }
}

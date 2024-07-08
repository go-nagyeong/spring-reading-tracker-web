package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.dto.RegisterForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

    /**
     * Home
     */
    @RequestMapping("/")
    public String home() {
        return "index";
    }

    /**
     * User
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "user/login";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "user/register";
    }

    @GetMapping("/find-password")
    public String showFindPasswordForm() {
        return "user/find-password";
    }

    /**
     * TODO: 임시
     */
    @RequestMapping("/{page}")
    public String custom1(@PathVariable("page") String page) {
        return page.replace(".html","");
    }

    @RequestMapping("/{page1}/{page2}")
    public String custom2(@PathVariable("page1") String page1, @PathVariable("page2") String page2) {
        return page1+"/"+page2.replace(".html","");
    }
}

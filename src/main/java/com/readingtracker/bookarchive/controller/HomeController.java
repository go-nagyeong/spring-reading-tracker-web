package com.readingtracker.bookarchive.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String home() {
        return "index";
    }

    @RequestMapping("/{page}")
    public String custom1(@PathVariable("page") String page) {
        return page.replace(".html","");
    }

    @RequestMapping("/{page1}/{page2}")
    public String custom2(@PathVariable("page1") String page1, @PathVariable("page2") String page2) {
        return page1+"/"+page2.replace(".html","");
    }
}

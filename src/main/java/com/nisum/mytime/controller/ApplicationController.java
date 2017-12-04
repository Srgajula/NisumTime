package com.nisum.mytime.controller;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController("/")
public class ApplicationController {

    public String login() {
        return "index";
    }

    @RequestMapping("/logout")
    public ModelAndView logout(HttpSession session) {
        return new ModelAndView("login");
    }

    @RequestMapping("/emp")
    public ModelAndView emp(HttpSession session) {
        return new ModelAndView("login");
    }
}

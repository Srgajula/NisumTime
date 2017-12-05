package com.nisum.mytime.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class ApplicationController {

    public String login() {
        return "index";
    }
}

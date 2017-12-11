package com.nisum.mytime.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nisum.mytime.model.Employee;

@Controller
public class GoogleController {

    @PostMapping("/empData")
    @ResponseBody
    public String getEmpData(@RequestBody Employee employee,
            HttpSession session) {
        System.out.println(employee.getEmail());
        return "{'message':'success'}";
    }
}

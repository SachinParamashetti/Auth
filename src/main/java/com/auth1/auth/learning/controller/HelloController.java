package com.auth1.auth.learning.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping(value = "/hi", method = RequestMethod.POST)
    public String hello(){
        return "Hello";
    }
}

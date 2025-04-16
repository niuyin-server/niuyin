package com.niuyin.service.ai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestController
 *
 * @AUTHOR: roydon
 * @DATE: 2025/4/10
 **/
@Slf4j
@RequiredArgsConstructor
@CrossOrigin("*")
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}

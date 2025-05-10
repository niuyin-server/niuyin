package com.niuyin.service.ai.controller;

import com.niuyin.common.cache.ratelimiter.core.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * TestController
 *
 * @AUTHOR: roydon
 * @DATE: 2025/4/10
 **/
@Slf4j
@RequiredArgsConstructor
@RestController
public class TestController {

    @RateLimiter(count = 1, time = 5)
    @GetMapping("/test")
    public String test() {
        return "test";
    }

}

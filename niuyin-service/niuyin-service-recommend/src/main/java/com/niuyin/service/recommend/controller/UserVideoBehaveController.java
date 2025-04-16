package com.niuyin.service.recommend.controller;

import com.niuyin.service.recommend.service.IUserVideoBehaveService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * 用户视频行为表(UserVideoBehave)表控制层
 *
 * @author roydon
 * @since 2024-04-27 18:56:46
 */
@RestController
@RequestMapping("/api/v1/userVideoBehave")
public class UserVideoBehaveController {

    @Resource
    private IUserVideoBehaveService userVideoBehaveService;

}


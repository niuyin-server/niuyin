package com.niuyin.service.behave.controller.v1;

import com.niuyin.service.behave.service.IUserVideoBehaveService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户视频行为表(UserVideoBehave)表控制层
 *
 * @author roydon
 * @since 2024-04-19 14:21:12
 */
@RestController
@RequestMapping("/api/v1/userVideoBehave")
public class UserVideoBehaveController {

    @Resource
    private IUserVideoBehaveService userVideoBehaveService;

}


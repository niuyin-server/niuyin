package com.niuyin.service.video.controller.v1;

import com.niuyin.service.video.service.IUserVideoCompilationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户视频合集表(UserVideoCompilation)表控制层
 *
 * @author roydon
 * @since 2023-11-27 18:08:37
 */
@RestController
@RequestMapping("/api/v1/userVideoCompilation")
public class UserVideoCompilationController {

    @Resource
    private IUserVideoCompilationService userVideoCompilationService;

}


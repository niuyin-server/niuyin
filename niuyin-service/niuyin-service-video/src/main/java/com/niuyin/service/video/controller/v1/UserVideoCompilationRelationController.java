package com.niuyin.service.video.controller.v1;

import com.niuyin.service.video.service.IUserVideoCompilationRelationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户视频合集与视频关联表(UserVideoCompilationRelation)表控制层
 *
 * @author roydon
 * @since 2023-12-08 20:21:11
 */
@RestController
@RequestMapping("/api/v1/userVideoCompilationRelation")
public class UserVideoCompilationRelationController {

    @Resource
    private IUserVideoCompilationRelationService userVideoCompilationRelationService;

}


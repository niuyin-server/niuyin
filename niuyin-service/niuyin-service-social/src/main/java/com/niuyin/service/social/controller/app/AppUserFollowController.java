package com.niuyin.service.social.controller.app;

import com.niuyin.common.domain.R;
import com.niuyin.service.social.service.IUserFollowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * AppUserFollowController
 *
 * @AUTHOR: roydon
 * @DATE: 2024/2/1
 **/
@RestController
@RequestMapping("/api/v1/app/follow")
public class AppUserFollowController {

    @Resource
    private IUserFollowService userFollowService;

    /**
     * 推送关注的人视频 拉模式
     *
     * @param lastTime 滚动分页
     * @return
     */
    @GetMapping("/videoFeed")
    public R<?> appFollowFeed(@RequestParam(required = false) Long lastTime){
        return R.ok(userFollowService.followVideoFeed(lastTime));
    }

}

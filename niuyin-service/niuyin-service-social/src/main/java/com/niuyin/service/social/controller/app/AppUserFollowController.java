package com.niuyin.service.social.controller.app;

import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.social.cache.DynamicUser;
import com.niuyin.model.social.vo.Fans;
import com.niuyin.model.social.vo.FollowUser;
import com.niuyin.model.video.vo.VideoVO;
import com.niuyin.service.social.service.IUserFollowService;
import com.niuyin.service.social.service.SocialDynamicsService;
import org.springframework.web.bind.annotation.*;

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

    @Resource
    private SocialDynamicsService socialDynamicsService;

    /**
     * 推送关注的人视频 拉模式
     *
     * @param lastTime 滚动分页
     */
    @GetMapping("/videoFeed")
    public R<?> appFollowFeed(@RequestParam(required = false) Long lastTime) {
        return R.ok(userFollowService.followVideoFeed(lastTime));
    }

    /**
     * 关注动态
     */
    @GetMapping("/dynamic")
    public PageDataInfo<DynamicUser> followDynamicPage() {
        return socialDynamicsService.getSocialDynamicsUser();
    }

    /**
     * 动态视频
     */
    @PostMapping("/dynamicVideoPage")
    public PageDataInfo<VideoVO> dynamicVideoPage(@RequestBody PageDTO pageDTO) {
        return userFollowService.getSocialDynamicVideoPage(pageDTO);
    }

    /**
     * 初始化用户收件箱
     * todo 返回未读视频动态数
     */
    @GetMapping("/initUserInBox")
    public R<?> initUserInBox() {
        socialDynamicsService.initUserFollowInBox(UserContext.getUserId());
        return R.ok(true);
    }

    /**
     * 关注用户
     */
    @GetMapping("/{userId}")
    public R<Boolean> followUser(@PathVariable("userId") Long userId) {
        return R.ok(userFollowService.followUser(userId));
    }

    /**
     * 我的关注分页
     */
    @PostMapping("/followPage")
    public PageDataInfo<FollowUser> followPage(@RequestBody PageDTO pageDTO) {
        return userFollowService.appGetFollowPage(pageDTO);
    }

    /**
     * 我的粉丝分页
     */
    @PostMapping("/fansPage")
    public PageDataInfo<Fans> fansPage(@RequestBody PageDTO pageDTO) {
        return userFollowService.appGetFansPage(pageDTO);
    }
}

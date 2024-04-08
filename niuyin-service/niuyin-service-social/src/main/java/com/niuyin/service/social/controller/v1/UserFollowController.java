package com.niuyin.service.social.controller.v1;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.behave.vo.UserFollowsFansVo;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.social.domain.UserFollow;
import com.niuyin.service.social.service.IUserFollowService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户关注表(UserFollow)表控制层
 *
 * @author roydon
 * @since 2023-10-30 15:54:19
 */
@RestController
@RequestMapping("/api/v1/follow")
public class UserFollowController {

    @Resource
    private IUserFollowService userFollowService;

    /**
     * 关注
     */
    @GetMapping("/{userId}")
    public R<?> follow(@PathVariable("userId") Long userId) {
        return R.ok(userFollowService.followUser(userId));
    }

    /**
     * 取消关注
     */
    @DeleteMapping("/{userId}")
    public R<?> unfollow(@PathVariable("userId") Long userId) {
        return R.ok(userFollowService.unFollowUser(userId));
    }

    /**
     * 分页查询我的关注列表
     */
    @PostMapping("/page")
    public PageDataInfo followPage(@RequestBody PageDTO pageDTO) {
        return userFollowService.getFollowPage(pageDTO);
    }

    /**
     * 是否关注
     *
     * @param userId
     * @return
     */
    @GetMapping("/weatherfollow/{userId}")
    public R<Boolean> weatherfollow(@PathVariable("userId") Long userId) {
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getUserId, UserContext.getUserId());
        queryWrapper.eq(UserFollow::getUserFollowId, userId);
        return R.ok(userFollowService.count(queryWrapper) > 0);
    }

    /**
     * 根据用户id查询该用户的关注和粉丝数量
     *
     * @param userId
     * @return
     */
    @GetMapping("/followFans/{userId}")
    public R<UserFollowsFansVo> followAndFans(@PathVariable("userId") Long userId) {
        UserFollowsFansVo userFollowsFansVo = new UserFollowsFansVo();
        // 查询关注数量
        LambdaQueryWrapper<UserFollow> queryWrapperFollows = new LambdaQueryWrapper<>();
        queryWrapperFollows.eq(UserFollow::getUserId, userId);
        userFollowsFansVo.setFollowedNums(userFollowService.count(queryWrapperFollows));
        // 查询粉丝数
        LambdaQueryWrapper<UserFollow> queryWrapperFans = new LambdaQueryWrapper<>();
        queryWrapperFans.eq(UserFollow::getUserFollowId, userId);
        userFollowsFansVo.setFanNums(userFollowService.count(queryWrapperFans));
        return R.ok(userFollowsFansVo);
    }

    /**
     * 分页查询我的粉丝
     *
     * @param pageDTO
     * @return
     */
    @PostMapping("/fans-page")
    public PageDataInfo getUserFansPage(@RequestBody PageDTO pageDTO) {
        return userFollowService.queryUserFansPage(pageDTO);
    }

    /**
     * 初始化收件箱
     */
    @PostMapping("/initVideoFeed")
    public R<?> initFollowFeed() {
        userFollowService.initFollowVideoFeed();
        return R.ok();
    }

    /**
     * 推送关注的人视频 拉模式
     *
     * @param lastTime 滚动分页
     * @return
     */
    @GetMapping("/videoFeed")
    public R followFeed(@RequestParam(required = false) Long lastTime){
        return R.ok(userFollowService.followVideoFeed(lastTime));
    }

}


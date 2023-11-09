package com.niuyin.service.social.controller.v1;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.feign.member.RemoteMemberService;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.social.UserFollow;
import com.niuyin.model.video.vo.UserFollowsFansVo;
import com.niuyin.service.social.service.IUserFollowService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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

    @Resource
    private RemoteMemberService remoteMemberService;

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
        if (StringUtils.isNull(pageDTO)) {
            LambdaQueryWrapper<UserFollow> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserFollow::getUserId, UserContext.getUserId());
            List<UserFollow> list = userFollowService.list(lambdaQueryWrapper);
            List<Member> res = new ArrayList<>();
            list.forEach(l -> {
                Member user = remoteMemberService.userInfoById(l.getUserFollowId()).getData();
                res.add(user);
            });
            return PageDataInfo.genPageData(res, res.size());
        }
        IPage<UserFollow> userFollowIPage = userFollowService.followPage(pageDTO);
        List<Member> userList = new ArrayList<>();
        userFollowIPage.getRecords().forEach(uf -> {
            Member user = remoteMemberService.userInfoById(uf.getUserFollowId()).getData();
            userList.add(user);
        });
        return PageDataInfo.genPageData(userList, userFollowIPage.getTotal());
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
     * 根据用户id查询该用户的关注和粉丝
     *
     * @param userId
     * @return
     */
    @GetMapping("/followFans/{userId}")
    public R<UserFollowsFansVo> followAndFans(@PathVariable("userId") Long userId) {
        UserFollowsFansVo userFollowsFansVo = new UserFollowsFansVo();
        LambdaQueryWrapper<UserFollow> queryWrapperFans = new LambdaQueryWrapper<>();
        queryWrapperFans.eq(UserFollow::getUserId, userId);
        userFollowsFansVo.setFanNums(userFollowService.count(queryWrapperFans));
        LambdaQueryWrapper<UserFollow> queryWrapperFollows = new LambdaQueryWrapper<>();
        queryWrapperFollows.eq(UserFollow::getUserFollowId, userId);
        userFollowsFansVo.setFollowedNums(userFollowService.count(queryWrapperFollows));
        return R.ok(userFollowsFansVo);
    }


}


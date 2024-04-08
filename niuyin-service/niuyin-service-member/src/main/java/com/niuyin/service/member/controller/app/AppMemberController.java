package com.niuyin.service.member.controller.app;

import com.niuyin.common.domain.R;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.member.domain.MemberInfo;
import com.niuyin.model.member.vo.MemberInfoVO;
import com.niuyin.service.member.constants.UserCacheConstants;
import com.niuyin.service.member.service.IMemberInfoService;
import com.niuyin.service.member.service.IMemberService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * UserController
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Slf4j
@RestController
@RequestMapping("/api/v1/app")
public class AppMemberController {

    @Resource
    private IMemberService memberService;

    @Resource
    private RedisService redisService;

    @Resource
    private IMemberInfoService memberInfoService;

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    @ApiOperation("根据id获取用户信息")
    @GetMapping("/{userId}")
    public R<Member> userInfoById(@PathVariable Long userId) {
        return R.ok(getUserFromCache(userId));
    }

    /**
     * 从缓存获取用户详情
     */
    private MemberInfoVO getUserFromCache(Long userId) {
        Member userCache = redisService.getCacheObject(UserCacheConstants.USER_INFO_PREFIX + userId);
        // 用户详情
        MemberInfo memberInfo = memberInfoService.queryInfoByUserId(userId);
        if (StringUtils.isNotNull(userCache)) {
            MemberInfoVO memberInfoVO = BeanCopyUtils.copyBean(userCache, MemberInfoVO.class);
            memberInfoVO.setMemberInfo(memberInfo);
            return memberInfoVO;
        }
        Member user = memberService.queryById(userId);
        user.setPassword(null);
        user.setSalt(null);
        MemberInfoVO memberInfoVO = BeanCopyUtils.copyBean(user, MemberInfoVO.class);
        memberInfoVO.setMemberInfo(memberInfo);
        // 设置缓存
        redisService.setCacheObject(UserCacheConstants.USER_INFO_PREFIX + userId, user);
        redisService.expire(UserCacheConstants.USER_INFO_PREFIX + userId, UserCacheConstants.USER_INFO_EXPIRE_TIME, TimeUnit.SECONDS);
        return memberInfoVO;
    }

}

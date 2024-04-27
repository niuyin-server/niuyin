package com.niuyin.service.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.service.RedisService;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.dubbo.api.DubboSocialService;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.member.domain.MemberInfo;
import com.niuyin.model.member.vo.MemberInfoVO;
import com.niuyin.model.member.vo.app.AppMemberInfoVO;
import com.niuyin.service.member.constants.UserCacheConstants;
import com.niuyin.service.member.mapper.MemberInfoMapper;
import com.niuyin.service.member.service.IMemberInfoService;
import com.niuyin.service.member.service.IMemberService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 会员详情表(MemberInfo)表服务实现类
 *
 * @author roydon
 * @since 2023-11-12 22:26:26
 */
@Service("memberInfoService")
public class MemberInfoServiceImpl extends ServiceImpl<MemberInfoMapper, MemberInfo> implements IMemberInfoService {
    @Resource
    private MemberInfoMapper memberInfoMapper;

    @Resource
    private RedisService redisService;

    @Resource
    @Lazy
    private IMemberService memberService;

    @DubboReference(mock = "return null")
    private DubboSocialService dubboSocialService;

    /**
     * 通过userId查询用户详情
     */
    @Override
    public MemberInfo queryInfoByUserId(Long userId) {
        return memberInfoMapper.selectInfoByUserId(userId);
    }

    @Override
    public AppMemberInfoVO getUserInfoById(Long userId) {
        MemberInfoVO userFromCache = getMemberInfoVOFromCache(userId);
        AppMemberInfoVO appMemberInfoVO = BeanCopyUtils.copyBean(userFromCache, AppMemberInfoVO.class);
        // 是否关注
        appMemberInfoVO.setWeatherFollow(dubboSocialService.apiWeatherFollow(UserContext.getUserId(), userId));
        // 获赞
        appMemberInfoVO.setLikeCount(100L);
        // 关注数量、粉丝数量
        appMemberInfoVO.setFollowCount(dubboSocialService.apiUserFollowCount(userId));
        appMemberInfoVO.setFansCount(dubboSocialService.apiUserFansCount(userId));
        return appMemberInfoVO;
    }

    /**
     * 从缓存获取用户详情
     */
    private MemberInfoVO getMemberInfoVOFromCache(Long userId) {
        MemberInfoVO userCache = redisService.getCacheObject(UserCacheConstants.MEMBER_INFO_PREFIX + userId);
        if (Objects.isNull(userCache)) {
            Member user = memberService.queryById(userId);
            MemberInfo memberInfo = this.queryInfoByUserId(userId);
            MemberInfoVO memberInfoVO = BeanCopyUtils.copyBean(user, MemberInfoVO.class);
            memberInfoVO.setMemberInfo(memberInfo);
            // 设置缓存
            redisService.setCacheObject(UserCacheConstants.MEMBER_INFO_PREFIX + userId, memberInfoVO);
            redisService.expire(UserCacheConstants.MEMBER_INFO_PREFIX + userId, UserCacheConstants.MEMBER_INFO_EXPIRE_TIME, TimeUnit.DAYS);
            return memberInfoVO;
        }
        return userCache;
    }

}

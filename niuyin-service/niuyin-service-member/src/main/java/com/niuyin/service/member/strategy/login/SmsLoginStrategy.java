package com.niuyin.service.member.strategy.login;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niuyin.common.exception.CustomException;
import com.niuyin.common.utils.IpUtils;
import com.niuyin.common.utils.ServletUtils;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.member.dto.SmsLoginDTO;
import com.niuyin.model.member.enums.LoginTypeEnum;
import com.niuyin.service.member.service.IMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.niuyin.model.common.enums.HttpCodeEnum.TELEPHONE_NOT_EXIST;

/**
 * SmsLoginStrategy
 *
 * @AUTHOR: roydon
 * @DATE: 2024/3/9
 **/
@Slf4j
@Service("smsLoginStrategy")
public class SmsLoginStrategy extends AbstractLoginStrategyService<SmsLoginDTO> {

    @Resource
    private IMemberService memberService;

    /**
     * 策略名称
     */
    @Override
    protected String getStrategyName() {
        return LoginTypeEnum.SMS.getStrategy();
    }

    /**
     * 校验用户
     *
     * @param loginDto
     */
    @Override
    protected Long verifyCredentials(SmsLoginDTO loginDto) {
        log.debug("SmsLoginStrategy verifyCredentials");
        // 查询用户
        Member dbUser = memberService.getOne(Wrappers.<Member>lambdaQuery().eq(Member::getTelephone, loginDto.getTelephone()));
        if (Objects.isNull(dbUser)) {
            throw new CustomException(TELEPHONE_NOT_EXIST);
        }
        // 删除key
//        redisTemplate.delete(verifyKey);
        return dbUser.getUserId();
    }

    @Override
    protected void recordLoginUserInfo(Long userId) {
        Member user = new Member();
        user.setUserId(userId);
        user.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        user.setLoginLocation(IpUtils.getIpLocation(ServletUtils.getRequest()));
        user.setLoginDate(LocalDateTime.now());
        memberService.updateById(user);
    }
}

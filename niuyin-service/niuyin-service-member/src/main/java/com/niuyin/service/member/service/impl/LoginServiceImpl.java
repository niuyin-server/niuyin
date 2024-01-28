package com.niuyin.service.member.service.impl;

import cn.hutool.core.util.PhoneUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niuyin.common.exception.CustomException;
import com.niuyin.common.utils.IpUtils;
import com.niuyin.common.utils.JwtUtil;
import com.niuyin.common.utils.ServletUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.member.dto.SmsLoginDTO;
import com.niuyin.service.member.service.IMemberService;
import com.niuyin.service.member.service.LoginService;
import com.niuyin.starter.sms.constant.AliyunSmsConstants;
import com.niuyin.starter.sms.domain.model.AliyunSmsResponse;
import com.niuyin.starter.sms.domain.model.SmsCode;
import com.niuyin.starter.sms.enums.AliyunSmsTemplateType;
import com.niuyin.starter.sms.service.AliyunSmsService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.niuyin.model.common.enums.HttpCodeEnum.*;
import static com.niuyin.service.member.constants.SmsConstant.*;

/**
 * LoginServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/28
 **/
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private AliyunSmsService aliyunSmsService;

    @Resource
    private IMemberService memberService;

    /**
     * 发送登录验证码 todo 限制每日发送次数
     *
     * @param telephone
     * @return
     */
    @SneakyThrows
    @Override
    public SmsCode sendLoginAuthCode(String telephone) {
        if (!PhoneUtil.isPhone(telephone)) {
            throw new RuntimeException("手机格式有误");
        }
        // 存入redis的验证码key
        String captchaKey = SMS_LOGIN_AUTH_CODE_KEY + telephone;
        // 根据手机号从redis中拿验证码
        String phoneCode = redisTemplate.opsForValue().get(captchaKey);
        if (!Objects.isNull(phoneCode)) {
            throw new RuntimeException("请勿重复发送-" + telephone);
        }
        // 生成随机验证码
        String authCode = RandomStringUtils.randomNumeric(6);
        // 调用服务发送验证码
        AliyunSmsResponse aliSmsResponse = aliyunSmsService.sendAuthCode(telephone, AliyunSmsTemplateType.SMS_460955023, authCode);
        // 调用短信服务失败
        if (!aliSmsResponse.getCode().equals(AliyunSmsConstants.MESSAGE_OK)) {
            log.debug("调用aliyun sms失败：{}", aliSmsResponse);
            throw new RuntimeException("调用aliyun sms失败！");
        }
        // 将验证码存到 redis ，默认一分钟
        redisTemplate.opsForValue().set(captchaKey, authCode, SMS_LOGIN_AUTH_CODE_EXPIRE_TIME, TimeUnit.MINUTES);
        return new SmsCode(authCode, SMS_LOGIN_AUTH_CODE_EXPIRE_TIME);
    }

    /**
     * 手机验证码登录
     *
     * @param smsLoginDTO
     */
    @Override
    public String smsLogin(SmsLoginDTO smsLoginDTO) {
        // 查询用户
        Member dbUser = memberService.getOne(Wrappers.<Member>lambdaQuery().eq(Member::getTelephone, smsLoginDTO.getTelephone()));
        if (Objects.isNull(dbUser)) {
            throw new CustomException(TELEPHONE_NOT_EXIST);
        }
        // 登陆成功
        recordLoginUserInfo(dbUser.getUserId());
        return JwtUtil.getToken(dbUser.getUserId());
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    @Async
    public void recordLoginUserInfo(Long userId) {
        Member user = new Member();
        user.setUserId(userId);
        user.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        user.setLoginDate(LocalDateTime.now());
        memberService.updateById(user);
    }


}

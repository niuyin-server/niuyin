package com.niuyin.service.member.service.impl;

import cn.hutool.core.util.PhoneUtil;
import com.niuyin.model.member.dto.SmsRegisterDTO;
import com.niuyin.service.member.service.IMemberService;
import com.niuyin.service.member.service.RegisterService;
import com.niuyin.starter.sms.constant.AliyunSmsConstants;
import com.niuyin.starter.sms.domain.model.AliyunSmsResponse;
import com.niuyin.starter.sms.domain.model.SmsCode;
import com.niuyin.starter.sms.enums.AliyunSmsTemplateType;
import com.niuyin.starter.sms.service.AliyunSmsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.niuyin.service.member.constants.SmsConstant.SMS_REGISTER_AUTH_CODE_EXPIRE_TIME;
import static com.niuyin.service.member.constants.SmsConstant.SMS_REGISTER_CODE_KEY;

/**
 * RegisterServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/10
 **/
@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private AliyunSmsService aliyunSmsService;

    @Resource
    private IMemberService memberService;

    /**
     * 发送注册验证码
     *
     * @param telephone
     * @return
     */
    @Override
    public SmsCode sendRegisterAuthCode(String telephone) {
        if (!PhoneUtil.isPhone(telephone)) {
            throw new RuntimeException("手机格式有误");
        }
        // 存入redis的验证码key
        String captchaKey = SMS_REGISTER_CODE_KEY + telephone;
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
        // 将验证码存到 redis ，默认5分钟
        redisTemplate.opsForValue().set(captchaKey, authCode, SMS_REGISTER_AUTH_CODE_EXPIRE_TIME, TimeUnit.MINUTES);
        return new SmsCode(authCode, SMS_REGISTER_AUTH_CODE_EXPIRE_TIME * 60);
    }

    /**
     * 手机验证码注册
     *
     * @param smsLoginDTO
     */
    @Override
    public Boolean smsRegister(SmsRegisterDTO smsLoginDTO) {
        // 获取手机验证码
//        String verifyKey = SMS_REGISTER_CODE_KEY + smsLoginDTO.getTelephone();
//        String verifyPhoneCode = redisTemplate.opsForValue().get(verifyKey);
//        if (Objects.isNull(verifyPhoneCode)) {
//            throw new RuntimeException("验证码已失效");
//        }
//        if (!verifyPhoneCode.equals(smsLoginDTO.getSmsCode())) {
//            throw new RuntimeException("验证码错误");
//        }
        // 插入数据
        if (!PhoneUtil.isPhone(smsLoginDTO.getTelephone())) {
            throw new RuntimeException("手机格式有误");
        }
        return memberService.smsRegister(smsLoginDTO);
    }
}

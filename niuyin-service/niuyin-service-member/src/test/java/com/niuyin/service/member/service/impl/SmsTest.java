package com.niuyin.service.member.service.impl;

import com.niuyin.starter.sms.domain.model.AliyunSmsResponse;
import com.niuyin.starter.sms.enums.AliyunSmsTemplateType;
import com.niuyin.starter.sms.service.AliyunSmsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * SmsTest
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/28
 **/
@Slf4j
@SpringBootTest
public class SmsTest {

    @Resource
    private AliyunSmsService aliyunSmsService;

    @Test
    @DisplayName("测试发送短信")
    public void testSendCode() {
        AliyunSmsResponse response = aliyunSmsService.sendAuthCode("18203707837", AliyunSmsTemplateType.SMS_460955023, "123456");
        System.out.println("AliyunSmsResponse = " + response.toString());
    }

}

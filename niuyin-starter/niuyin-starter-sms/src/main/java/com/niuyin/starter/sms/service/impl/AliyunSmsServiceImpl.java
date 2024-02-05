package com.niuyin.starter.sms.service.impl;

import cn.hutool.core.util.PhoneUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.niuyin.starter.sms.config.AliyunSmsConfig;
import com.niuyin.starter.sms.config.AliyunSmsConfigProperties;
import com.niuyin.starter.sms.domain.model.AliyunSmsResponse;
import com.niuyin.starter.sms.service.AliyunSmsService;
import com.niuyin.starter.sms.enums.AliyunSmsTemplateType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * AliyunSmsServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/28
 **/
@Slf4j
@Service
@EnableConfigurationProperties(AliyunSmsConfigProperties.class)
@Import(AliyunSmsConfig.class)
public class AliyunSmsServiceImpl implements AliyunSmsService {

    @Autowired
    private AliyunSmsConfigProperties aliyunSmsConfigProperties;

    @Autowired
    private IAcsClient acsClient;

    /**
     * 发送验证码
     */
    @Override
    public AliyunSmsResponse sendAuthCode(String phone, AliyunSmsTemplateType type, String code) {
        if (!PhoneUtil.isPhone(phone)) {
            log.error("短信发送失败，手机号码不正确！");
            throw new RuntimeException("手机号码不正确！");
        }
        // 阿里云短信服务提供的模板代码，此代码是由自己创建的模板得到的
        if (Objects.isNull(type)) {
            throw new RuntimeException("短信模板不正确！");
        }
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        // 要发送到的手机号
        request.putQueryParameter("PhoneNumbers", phone);
        // 短信签名名称。请在控制台签名管理页面签名名称一列查看。 说明 必须是已添加、并通过审核的短信签名。
        request.putQueryParameter("SignName", aliyunSmsConfigProperties.getSignName());
        // 短信模板ID。请在控制台模板管理页面模板CODE一列查看。 说明 必须是已添加、并通过审核的短信签名；且发送国际/港澳台消息时，请使用国际/港澳台短信模版。
        request.putQueryParameter("TemplateCode", type.toString());
        // 短信模板变量对应的实际值，JSON格式。 说明 如果JSON中需要带换行符，请参照标准的JSON协议处理。
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");

        String data = null;
        try {
            CommonResponse response = acsClient.getCommonResponse(request);
            data = response.getData();
        } catch (Exception e) {
            log.error("短信发送失败：" + e.getMessage(), e);
        }
        // 回调消息转换
        JSONObject jsonObject = JSON.parseObject(data);
        AliyunSmsResponse aliSmsResponse = JSON.toJavaObject(jsonObject, AliyunSmsResponse.class);
        return aliSmsResponse;
    }

    public static void main(String[] args) {
        System.out.println(AliyunSmsTemplateType.SMS_460955023.toString());

    }

}

package com.niuyin.starter.sms.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * aliyun sms 响应体
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/28
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliyunSmsResponse {

    private String Message;
    private String RequestId;
    private String Code;
    private String BizId;

}

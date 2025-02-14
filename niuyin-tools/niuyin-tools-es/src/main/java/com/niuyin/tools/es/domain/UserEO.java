package com.niuyin.tools.es.domain;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

/**
 * 用户es对象
 *
 * @AUTHOR: roydon
 * @DATE: 2024/10/11
 **/
@Data
@Document(indexName = "doc_user", createIndex = true)
public class UserEO {
    // 用户id
    private String userId;
    public static final String USER_ID = "userId";
    // 用户名
    private String username;
    public static final String USERNAME = "username";
    // 昵称
    private String nickName;
    public static final String NICK_NAME = "nickName";
    // 邮件
    private String email;
    public static final String EMAIL = "email";
    // 电话
    private String telephone;
    public static final String TELEPHONE = "telephone";
    // 性别
    private String sex;
    public static final String SEX = "sex";
    // 头像
    private String avatar;
    public static final String AVATAR = "avatar";
    // 创建时间
    private Date createTime;
    public static final String CREATE_TIME = "createTime";
}

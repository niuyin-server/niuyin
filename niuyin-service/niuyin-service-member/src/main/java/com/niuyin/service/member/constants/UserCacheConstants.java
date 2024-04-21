package com.niuyin.service.member.constants;

/**
 * UserCacheConstant
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/29
 **/
public class UserCacheConstants {

    public static final String USER_INFO_PREFIX = "member:member:";

    public static final String MEMBER_INFO_PREFIX = "member:memberinfo:";

    public static final long USER_INFO_EXPIRE_TIME =  3600 * 24 ; //1天
    public static final long MEMBER_INFO_EXPIRE_TIME =  7; //7天
}

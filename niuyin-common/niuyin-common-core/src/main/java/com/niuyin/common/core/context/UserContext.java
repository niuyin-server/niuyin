package com.niuyin.common.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.niuyin.common.core.exception.CustomException;
import com.niuyin.model.common.enums.HttpCodeEnum;
import com.niuyin.model.member.domain.Member;

import java.util.Objects;

/**
 * UserContext
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/30
 **/
public class UserContext {

    private final static ThreadLocal<Member> USER_THREAD_LOCAL = new TransmittableThreadLocal<>();

    //存入线程中
    public static void setUser(Member user) {
        USER_THREAD_LOCAL.set(user);
    }

    //从线程中获取
    public static Member getUser() {
        return USER_THREAD_LOCAL.get();
    }

    public static Member getRequiredUser() {
        Member member = USER_THREAD_LOCAL.get();
        if (Objects.isNull(member)) {
            throw new CustomException(HttpCodeEnum.NEED_LOGIN);
        }
        return member;
    }

    /// 获取用户ID
    public static Long getUserId() {
        return getUser().getUserId();
    }

    public static Long getRequiredUserId() {
        return getUser().getUserId();
    }

    /**
     * 是否登录
     */
    public static boolean hasLogin() {
        if (Objects.isNull(getUser())) {
            return false;
        }
        if (getUserId().equals(0L)) {
            return false;
        }
        return true;
    }

    //清理
    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}

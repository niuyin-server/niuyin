package com.niuyin.common.core.context;

import com.niuyin.model.member.domain.Member;

/**
 * UserContext
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/30
 **/
public class UserContext {

    private final static ThreadLocal<Member> USER_THREAD_LOCAL = new ThreadLocal<>();

    //存入线程中
    public static void setUser(Member user) {
        USER_THREAD_LOCAL.set(user);
    }

    //从线程中获取
    public static Member getUser() {
        return USER_THREAD_LOCAL.get();
    }

    /// 获取用户ID
    public static Long getUserId() {
        return getUser().getUserId();
    }

    /**
     * 是否登录
     */
    public static boolean hasLogin() {
        return !getUserId().equals(0L);
    }

    //清理
    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}

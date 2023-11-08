package com.qiniu.common.context;

import com.qiniu.model.user.domain.User;

/**
 * UserContext
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/30
 **/
public class UserContext {

    private final static ThreadLocal<User> USER_THREAD_LOCAL = new ThreadLocal<>();

    //存入线程中
    public static void setUser(User user) {
        USER_THREAD_LOCAL.set(user);
    }

    //从线程中获取
    public static User getUser() {
        return USER_THREAD_LOCAL.get();
    }

    /// 获取用户ID
    public static Long getUserId() {
        return getUser().getUserId();
    }

    //清理
    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}

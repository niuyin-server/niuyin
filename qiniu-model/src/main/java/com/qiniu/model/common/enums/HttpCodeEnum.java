package com.qiniu.model.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HttpCodeEnum {
    // 成功
    SUCCESS(200, "操作成功"),
    // 登录
    NEED_LOGIN(401, "需要登录后操作"),
    NO_OPERATOR_AUTH(403, "无权限操作"),

    HAS_ERROR(500, "出现异常"),
    SYSTEM_ERROR(502, "系统异常"),
    SENSITIVEWORD_ERROR(503, "禁止出现敏感词"),

    REQUIRE_USERNAME(504, "必需填写用户名"),
    CONTENT_NOT_NULL(506, "评论内容不能为空"),
    FILE_TYPE_ERROR(507, "文件类型错误"),
    FILE_SIZE_ERROR(508, "文件大小超出限制"),
    NICKNAME_EXIST(512, "昵称已存在"),
    LOGIN_ERROR(505, "用户名或密码错误"),
    PASSWORD_ERROR(510, "密码错误"),

    USER_NOT_EXISTS(1000, "用户名不存在"),
    USERNAME_NOT_NULL(1001, "用户名不能为空"),
    NICKNAME_NOT_NULL(1002, "昵称不能为空"),
    PASSWORD_NOT_NULL(1003, "密码不能为空"),
    EMAIL_NOT_NULL(1004, "邮箱不能为空"),

    USERNAME_EXIST(1011, "用户名已存在"),
    PHONENUMBER_EXIST(1012, "手机号已存在"),
    EMAIL_EXIST(1013, "邮箱已存在"),

    TELEPHONE_VALID_ERROR(1020, "手机号码格式错误"),
    EMAIL_VALID_ERROR(1021, "邮箱格式错误"),

    CONFIRM_PASSWORD_NOT_MATCH(1020, "两次密码不一致"),

    NOT_ALLOW_FOLLOW_YOURSELF(1100, "不能关注自己"),
    USER_NOT_EXIST(1101, "用户不存在"),
    ALREADY_FOLLOW(1102, "已关注"),

    IMAGE_TYPE_FOLLOW(1201, "图片类型错误"),
    IMAGE_SIZE_FOLLOW(1202, "图片类型错误"),

    COMMENT_CONTENT_NULL(1301, "请输入评论内容"),

    UPLOAD_FAIL(10001, "上传失败"),
    BIND_FAIL(10002,"绑定失败"),
    BIND_CONTENT_TITLE_FAIL(10003,"视频标题需在30字符以内"),
    BIND_CONTENT_DESC_FAIL(10004,"视频描述需在200字符以内"),
    ;

    int code;
    String msg;

}

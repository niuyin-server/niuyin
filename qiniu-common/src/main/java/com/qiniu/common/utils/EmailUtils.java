package com.qiniu.common.utils;

import java.util.regex.Pattern;

/**
 * @author roydon
 * @date 2023-08-07 22:25【星期一】
 * @description com.roydon.common.utils
 * <p> 邮箱工具类 </p>
 **/
public class EmailUtils {

    public static boolean isValidEmail(String email) {
        if ((email != null) && (!email.isEmpty())) {
            return Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$", email);
        }
        return false;
    }

}

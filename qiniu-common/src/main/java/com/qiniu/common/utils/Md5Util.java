package com.qiniu.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class Md5Util {

    /**
     * Determine encrypt algorithm MD5
     */
    private static final String ALGORITHM_MD5 = "MD5";
    /**
     * UTF-8 Encoding
     */
    private static final String UTF_8 = "UTF-8";

    public static void main(String[] args) {
        System.out.println(md5("abc"));
        System.out.println(MD5_32bit("abc"));

    }

    public static String md5(String string) {
        if (string == null || string.trim().length() < 1) {
            return null;
        }
        try {
            return getMD5(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String getMD5(File file) {
        byte[] fileBytes = null;
        try {
            fileBytes = getFileBytes(file);
            return getMD5(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] getFileBytes(File file) throws IOException {
        int length = (int) file.length();
        byte[] data = new byte[length];
        new FileInputStream(file).read(data);
        return data;
    }

    public static String getMD5(byte[] source) {
        String s = null;
        char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            byte[] tmp = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i]; // 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
                // >>> 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
            }
            s = new String(str); // 换后的结果转换为字符串

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static final String MD5_32bit(String readyEncryptStr) {
        try {
            if (readyEncryptStr != null) {
                //Get MD5 digest algorithm's MessageDigest's instance.
                MessageDigest md = MessageDigest.getInstance(ALGORITHM_MD5);
                //Use specified byte update digest.
                md.update(readyEncryptStr.getBytes());
                //Get cipher text
                byte[] b = md.digest();
                //The cipher text converted to hexadecimal string
                StringBuilder su = new StringBuilder();
                //byte array switch hexadecimal number.
                for (int offset = 0, bLen = b.length; offset < bLen; offset++) {
                    String haxHex = Integer.toHexString(b[offset] & 0xFF);
                    if (haxHex.length() < 2) {
                        su.append("0");
                    }
                    su.append(haxHex);
                }
                return su.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String md5ByCharSet(String string, String charset) {
        if (string == null || string.trim().length() < 1) {
            return null;
        }
        try {
            return getMD5(string.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


}

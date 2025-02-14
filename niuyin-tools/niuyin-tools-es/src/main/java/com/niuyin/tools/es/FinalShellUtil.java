package com.niuyin.tools.es;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Scanner;

/**
 * FinalShell 4.3.10（新版）专业版&高级版激活
 * @author 离山道寺biu
 * @date 2024/03/15
 */
public class FinalShellUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String md5(String msg) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(msg.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String keccak384(String msg) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("Keccak-384");
        byte[] hashBytes = md.digest(msg.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("输入机器码: ");
        String code = scanner.nextLine();
        scanner.close();

        System.out.println("版本号 < 3.9.6 (旧版)");
        try {
            System.out.println("高级版: " + md5("61305" + code + "8552").substring(8, 24));
            System.out.println("专业版: " + md5("2356" + code + "13593").substring(8, 24));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        System.out.println("版本号 >= 3.9.6 (新版)");
        try {
            System.out.println("高级版: " + keccak384(code + "hSf(78cvVlS5E").substring(12, 28));
            System.out.println("专业版: " + keccak384(code + "FF3Go(*Xvbb5s2").substring(12, 28));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}

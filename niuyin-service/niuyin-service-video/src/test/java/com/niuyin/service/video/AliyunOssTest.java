package com.niuyin.service.video;

import com.aliyun.oss.OSSClient;
import com.niuyin.service.video.util.RSAUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static com.niuyin.starter.file.config.aliyun.AliyunOssConfigProperties.BUCKET_NAME;

/**
 * AliyunOssTEst
 *
 * @AUTHOR: roydon
 * @DATE: 2024/5/2
 **/
@Slf4j
@SpringBootTest
public class AliyunOssTest {

    @Resource
    private OSSClient ossClient;

    @Test
    void testGenUrlWithExpireTime() {
        // https://niuyin-server.oss-cn-shenzhen.aliyuncs.com/video/2024/04/27/2f25a3a36b514de188389efc7190c6b3.mp4
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000L);
        String url = ossClient.generatePresignedUrl(BUCKET_NAME, "video/2024/04/27/2f25a3a36b514de188389efc7190c6b3.mp4", expiration).toString();

        log.info("url: {}", url);
    }

    /**
     * DES 对称加密
     */
    @Test
    @SneakyThrows
    void testDesEncypt() {
        String plainText = "https://niuyin-server.oss-cn-shenzhen.aliyuncs.com/video/2024/04/27/2f25a3a36b514de188389efc7190c6b3.mp4";
        String secretKey = "this is password";
        DESKeySpec desKeySpec = new DESKeySpec(secretKey.getBytes(StandardCharsets.UTF_8));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(desKeySpec);

        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
        System.out.println("DES加密后的内容=" + encryptedText);
    }

    /**
     * DES 解密
     */
    @SneakyThrows
    @Test
    public void desDecrypt() {
        String encryptedText = "HAyFHQXRKmihGtxFsrZlAJwla4FE3aqS";
        String secretKey = "this is password";
        DESKeySpec desKeySpec = new DESKeySpec(secretKey.getBytes(StandardCharsets.UTF_8));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(desKeySpec);

        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        String plainText = new String(decryptedBytes, StandardCharsets.UTF_8);
        System.out.println("DES解密的内容=" + plainText);
    }

    @SneakyThrows
    @Test
    void testRSA() {
        String pubKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApIflbUUaiyFuMQVNWjiafO/Z+k/0khTt+hYsZX98pluZvsr98PZE+vyh1TkKOrJOrCRMUavejDRvFeXs6wFyplnHpUyRrvR/6vJHw+5khC6tiR5xOlpqRnRm/L2R2cYJ0Ti4Sh09pgwvnOwp+Jnh2ngJznrmosuoKDJefJmrI+/0hv57dg6Z7W/pMCtmCigr0enonSUZMMnB8kcYQ4Tm/nAE3a45SgfghWC4TBvcozgkPyFzguzQCiaT6ACwtyx7z5eQuzZ17GQfc7z4mXqC+2DVdXdU0iRckpN4FJ/NE0ZwF28Uohyb7F1gf6dFb9CMrLcib1eISToH+4IIf6gO1wIDAQAB";
        String priKeyStr = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCkh+VtRRqLIW4xBU1aOJp879n6T/SSFO36Fixlf3ymW5m+yv3w9kT6/KHVOQo6sk6sJExRq96MNG8V5ezrAXKmWcelTJGu9H/q8kfD7mSELq2JHnE6WmpGdGb8vZHZxgnROLhKHT2mDC+c7Cn4meHaeAnOeuaiy6goMl58masj7/SG/nt2Dpntb+kwK2YKKCvR6eidJRkwycHyRxhDhOb+cATdrjlKB+CFYLhMG9yjOCQ/IXOC7NAKJpPoALC3LHvPl5C7NnXsZB9zvPiZeoL7YNV1d1TSJFySk3gUn80TRnAXbxSiHJvsXWB/p0Vv0IystyJvV4hJOgf7ggh/qA7XAgMBAAECggEARwy8YZR+4ugb2qVsgvKAnEVDf1m5xr+tCNs8btQ/8uMJoJ+uS7k9jpk8FYTdSv2nBaVjI72xjCA0fGNfDRkB9p48ncBUBRZmiwH3RcATvhUeMWFpCgbzb9tigfAnJMDlimkRBEheT+9hPEWr1kQ1iQ9fRMBECrchtcISlYISqqCHDsiUzYpRgyjygCf49hWmWhrRcZn8/4xBJ41Hqk7LsWNVcYGI14Yy/PFwim27b6Mb5TD/RABvriq3b78FVKl3k/k+UCg09ZtEckveu6Apd2KgRFlm9G7of9g/BSOpqcyrC4SnAKkxRFL1U+KAFI/3rYKszC6LlQ4OrFbQyzo2uQKBgQD4zH2V4w8m7kOOaqrHN/TnEMMW8WZRa+lBWfwVjqBMnaOdOEkDadv16k9NjZue8/3iGTxsWN/ncPdiq8D5QOdPMMCKn7DO/+Z/6+daTBHbuID2qHTLXNzKo45xJsUcuptH9wpHOfd97flxjW/cZOGcVLuof+cB45YrdtzkQHO8UwKBgQCpSwKv4No1Op3s5Lk+sVvxWNqotWJK47O/T1YP9lDVdmGM7v2d7Uc9FRbr3xugtmn1zOCw5W7dz3/rQ2isrGaotEno3lLjEd16pYKf49H9HgWVqs+5l2H+L08jl1hHrOOqhsZYeKbHFnoP5pgpnUE70zUjqyatv8PLfZ8LLAuy7QKBgGFYUULWBlWrH8+XOJ6d7DqEOnC9ntT4rdkeBh3BIkMX4q5fGWI2hxOey8yCPYNh39IZIaUa2PBLKN/4Z5aeqGI5pvwWIy26vksK2AhifxJDDGJ14sy21sSKXe8zxifJc3wi0miv63/gHpspRb0r04JBPPNep9n7XARBi4fbF+11AoGBAICxgQ1qhPbJ5JvdE+VQtnA196Mgn+QGTPI1wVrJCJH9OBMG6s1PP0Rz78THCh2WDd7JLFXNVAZSYw/ZYo1BUZkV1MmtmJz9S7ycbTxW157vX5dzDg4rwlaUaDjKKFX0W+2aHtXKgh3+PDQyO6IG5TwxRsgip3iVuqXKjUjU5t/RAoGBALFocURQHsdFNWxAjok5an8hinEiAMBi8nly/BOZyvyXq8x5fyvzghD8ii1BitK6QhvZsaEwhrhW1PxECsU+NPbSNUaRhFtGi1xQF3WGa/vSMH6bzxL+ip8ZKlCMnX5bDegNFK+/2MvTDVXk98ZAnwQ7sBlM578VjhE7BRpgwWyw";
//        String[] keyPairArr = RSAUtil.genKeyPair();
//        System.out.println("公钥: " + keyPairArr[0]);
//        System.out.println();
//
//        //
//        System.out.println("私钥: " + keyPairArr[1]);
//        System.out.println();

        //
        String string = "https://niuyin-server.oss-cn-shenzhen.aliyuncs.com/video/2024/04/27/2f25a3a36b514de188389efc7190c6b3.mp4";

        String msg = RSAUtil.publicEncrypt(string, pubKeyStr);
        System.out.println("加密后内容: " + msg);
        System.out.println();

        //
        String mms = RSAUtil.privateDecrypt(msg, priKeyStr);
        System.out.println("解密后内容: " + mms);
        System.out.println();

    }


}

package com.qiniu.service.search;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.*;
import java.util.Date;

/**
 * EsTest
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
@SpringBootTest
public class EsTest {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Test
    void testCreateIndex(){
//        Date date = new Date();
//        System.out.println("date.getTime() = " + date.getTime());
//        LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
//        Instant.ofEpochSecond()
//        Date date = new Date();
//        Instant instant = date.toInstant();
//        ZoneId zoneId = ZoneId.systemDefault();
//
////        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
//        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), zoneId);
//        System.out.println("Date = " + date);
//        System.out.println("LocalDateTime = " + localDateTime);

        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = Date.from( localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        System.out.println("LocalDateTime = " + localDateTime);
        System.out.println("Date = " + date);
    }

}

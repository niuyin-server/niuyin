//package com.niuyin.service.video;
//
//import com.roydon.starter.ratelimit.annotation.RedissonLock;
//import com.roydon.starter.ratelimit.service.redisson.LockService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import jakarta.annotation.Resource;
//
///**
// * RedissonTest
// *
// * @AUTHOR: roydon
// * @DATE: 2024/2/18
// **/
//@Slf4j
//@SpringBootTest
//public class RedissonTest {
//
//    @Resource
//    private LockService lockService;
//
//    @Test
//    @RedissonLock(prefixKey = "redisson:lock", key = "test")
//    void testRedissonAnnoLock() throws InterruptedException {
//        System.out.println("testRedissonAnnoLock begin");
//        Thread.sleep(100000);
//        System.out.println("testRedissonAnnoLock end");
//        System.out.println();
//    }
////
////    @SneakyThrows
////    @Test
////    void lockTest() {
////        lockService.executeWithLock("redisson:test:lock", 1, TimeUnit.MINUTES, () -> {
////            System.out.println("testRedissonAnnoLock begin");
////            try {
////                Thread.sleep(100000);
////            } catch (InterruptedException e) {
////                throw new RuntimeException(e);
////            }
////            System.out.println("testRedissonAnnoLock end");
////            return null;
////        });
////    }
//
//}

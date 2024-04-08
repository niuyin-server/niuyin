package com.niuyin.service.video;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;

/**
 * FrequencyControlTest
 *
 * @AUTHOR: roydon
 * @DATE: 2024/2/18
 * @despriction: 演示各个限流算法
 * 计数器、漏桶、令牌桶、滑动窗口
 **/
@Slf4j
@SpringBootTest
public class FrequencyControlTest {

    /**
     * 1、计数器算法
     * 计数器采用简单的计数操作，到一段时间节点后自动清零。
     * 优缺点：
     * 控制力度太过于简略，假如1s内限制3次，那么如果3次在前100ms内已经用完，后面的900ms将只能处于阻塞状态，白白浪费掉。
     */
    @SneakyThrows
    @Test
    void testCount() {
        //计数器，这里用信号量实现
        final Semaphore semaphore = new Semaphore(3);
        //定时器，到点清零
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.scheduleAtFixedRate(() -> {
            semaphore.release(3); // 3秒清空一次
        }, 3000, 3000, TimeUnit.MILLISECONDS);

        //模拟无限请求从天而降降临
        while (true) {
            //判断计数器
            semaphore.acquire();
            //如果准许响应，打印一个ok
            log.debug("ok");
        }
    }

    /**
     * 2、漏桶算法
     * 漏桶算法将请求缓存在桶中，服务流程匀速处理。超出桶容量的部分丢弃。
     * 漏桶算法主要用于保护内部的处理业务，保障其稳定有节奏的处理请求，但是无法根据流量的波动弹性调整响应能力。
     * ==现实中，类似容纳人数有限的服务大厅开启了固定的服务窗口。==
     * 优缺点：
     * 有效的挡住了外部的请求，保护了内部的服务不会过载。
     * 内部服务匀速执行，无法应对流量洪峰，无法做到弹性处理突发任务。
     * 任务超时溢出时被丢弃。现实中可能需要缓存队列辅助保持一段时间。
     */
    @SneakyThrows
    @Test
    void testLeaky() {
        //桶，用阻塞队列实现，容量为3
        final LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>(3);

        //定时器，相当于服务的窗口，2s处理一个
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.scheduleAtFixedRate(() -> {
            int v = queue.poll();
            log.debug("请求通过：{}", v);
        }, 2000, 2000, TimeUnit.MILLISECONDS);

        //无数个请求，i 可以理解为请求的编号
        int i = 0;
        while (true) {
            i++;
            log.debug("请求到达：{}", i);
            //如果是put，会一直等待桶中有空闲位置，不会丢弃
//                que.put(i);
            //等待1s如果进不了桶，就溢出丢弃
            queue.offer(i, 1000, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 3、令牌桶算法
     * 令牌桶算法可以认为是漏桶算法的一种升级，它不但可以将流量做一步限制，还可以解决漏桶中无法弹性伸缩处理请求的问题。
     * 体现在现实中，类似服务大厅的门口设置门禁卡发放。发放是匀速的，请求较少时，令牌可以缓存起来，供流量爆发时一次性批量获取使用。而内部服务窗口不设限。
     */
    @SneakyThrows
    @Test
    void testToken() {
        //令牌桶，信号量实现，容量为3
        final Semaphore semaphore = new Semaphore(3);

        //产生令牌定时器，1s一个，匀速颁发令牌
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.scheduleAtFixedRate(() -> {
            if (semaphore.availablePermits() < 3) {
                semaphore.release();
            }
            log.debug("令牌数：{}", semaphore.availablePermits());
        }, 1000, 1000, TimeUnit.MILLISECONDS);


        //等待，等候令牌桶储存
        Thread.sleep(5);
        //模拟洪峰5个请求，前3个迅速响应，后两个排队
        for (int i = 0; i < 5; i++) {
            semaphore.acquire();
            System.out.println("洪峰：" + i);
        }
        //模拟日常请求，2s一个
        for (int i = 0; i < 3; i++) {
            Thread.sleep(1000);
            semaphore.acquire();
            System.out.println("日常：" + i);
            Thread.sleep(1000);
        }
        //再次洪峰
        for (int i = 0; i < 5; i++) {
            semaphore.acquire();
            System.out.println("洪峰：" + i);
        }
        //检查令牌桶的数量
        for (int i = 0; i < 5; i++) {
            Thread.sleep(2000);
            System.out.println("令牌剩余：" + semaphore.availablePermits());
        }
    }

    /**
     * 4、滑动窗口算法
     * 滑动窗口可以理解为细分之后的计数器，计数器粗暴的限定1分钟内的访问次数，而滑动窗口限流将1分钟拆为多个段，不但要求整个1分钟内请求数小于上限，而且要求每个片段请求数也要小于上限。相当于将原来的计数周期做了多个片段拆分。更为精细。
     */
    @Test
    void testSlidingWindow() {
////整个窗口的流量上限，超出会被限流
//        final int totalMax = 5;
//        //每片的流量上限，超出同样会被拒绝，可以设置不同的值
//        final int sliceMax = 5;
//        //分多少片
//        final int slice = 3;
//        //窗口，分3段，每段1s，也就是总长度3s
//        final LinkedList<Long> linkedList = new LinkedList<>();
//        //计数器，每片一个key，可以使用HashMap，这里为了控制台保持有序性和可读性，采用TreeMap
//        Map<Long, AtomicInteger> map = new TreeMap();
//        //心跳，每1s跳动1次，滑动窗口向前滑动一步，实际业务中可能需要手动控制滑动窗口的时机。
//        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
//
//        //获取key值，这里即是时间戳（秒）
//        private Long getKey () {
//            return System.currentTimeMillis() / 1000;
//        }
//
//    public Window() {
//            //初始化窗口，当前时间指向的是最末端，前两片其实是过去的2s
//            Long key = getKey();
//            for (int i = 0; i < slice; i++) {
//                linkedList.addFirst(key - i);
//                map.put(key - i, new AtomicInteger(0));
//            }
//            //启动心跳任务，窗口根据时间，自动向前滑动，每秒1步
//            service.scheduleAtFixedRate(new Runnable() {
//                @Override
//                public void run() {
//                    Long key = getKey();
//                    //队尾添加最新的片
//                    linkedList.addLast(key);
//                    map.put(key, new AtomicInteger());
//
//                    //将最老的片移除
//                    map.remove(linkedList.getFirst());
//                    linkedList.removeFirst();
//
//                    System.out.println("step:" + key + ":" + map);
//                    ;
//                }
//            }, 1000, 1000, TimeUnit.MILLISECONDS);
//        }
//
//        //检查当前时间所在的片是否达到上限
//        public boolean checkCurrentSlice () {
//            long key = getKey();
//            AtomicInteger integer = map.get(key);
//            if (integer != null) {
//                return integer.get() < totalMax;
//            }
//            //默认允许访问
//            return true;
//        }
//
//        //检查整个窗口所有片的计数之和是否达到上限
//        public boolean checkAllCount () {
//            return map.values().stream().mapToInt(value -> value.get()).sum() < sliceMax;
//        }
//
//        //请求来临....
//        public void req () {
//            Long key = getKey();
//            //如果时间窗口未到达当前时间片，稍微等待一下
//            //其实是一个保护措施，放置心跳对滑动窗口的推动滞后于当前请求
//            while (linkedList.getLast() < key) {
//                try {
//                    Thread.sleep(200);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            //开始检查，如果未达到上限，返回ok，计数器增加1
//            //如果任意一项达到上限，拒绝请求，达到限流的目的
//            //这里是直接拒绝。现实中可能会设置缓冲池，将请求放入缓冲队列暂存
//            if (checkCurrentSlice() && checkAllCount()) {
//                map.get(key).incrementAndGet();
//                System.out.println(key + "=ok:" + map);
//            } else {
//                System.out.println(key + "=reject:" + map);
//            }
//        }
//
//
//        public static void main (String[]args) throws InterruptedException {
//            Window window = new Window();
//            //模拟10个离散的请求，相对之间有200ms间隔。会造成总数达到上限而被限流
//            for (int i = 0; i < 10; i++) {
//                Thread.sleep(200);
//                window.req();
//            }
//            //等待一下窗口滑动，让各个片的计数器都置零
//            Thread.sleep(3000);
//            //模拟突发请求，单个片的计数器达到上限而被限流
//            System.out.println("---------------------------");
//            for (int i = 0; i < 10; i++) {
//                window.req();
//            }
//
//        }
    }
}

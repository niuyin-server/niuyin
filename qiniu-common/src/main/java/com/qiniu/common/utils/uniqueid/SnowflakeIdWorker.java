package com.qiniu.common.utils.uniqueid;

public class SnowflakeIdWorker {
    private final long twepoch;
    private final long workerIdBits;
    private final long datacenterIdBits;
    private final long maxWorkerId;
    private final long maxDatacenterId;
    private final long sequenceBits;
    private final long workerIdShift;
    private final long datacenterIdShift;
    private final long timestampLeftShift;
    private final long sequenceMask;
    private long workerId;
    private long datacenterId;
    private long sequence;
    private long lastTimestamp;
    private long intervalTimestamp;

    private SnowflakeIdWorker() {
        this.twepoch = 1420041600000L;
        this.workerIdBits = 5L;
        this.datacenterIdBits = 5L;
        this.maxWorkerId = 31L;
        this.maxDatacenterId = 31L;
        this.sequenceBits = 12L;
        this.workerIdShift = 12L;
        this.datacenterIdShift = 17L;
        this.timestampLeftShift = 22L;
        this.sequenceMask = 4095L;
        this.sequence = 0L;
        this.lastTimestamp = -1L;
        this.intervalTimestamp = 0L;
    }

    public static SnowflakeIdWorker getInstance() {
        return SnowflakeIdWorkerHolder.INSTANCE;
    }

    private Object readResolve() {
        return SnowflakeIdWorkerHolder.INSTANCE;
    }

    public SnowflakeIdWorker(long workerId, long datacenterId) {
        this.twepoch = 1420041600000L;
        this.workerIdBits = 5L;
        this.datacenterIdBits = 5L;
        this.maxWorkerId = 31L;
        this.maxDatacenterId = 31L;
        this.sequenceBits = 12L;
        this.workerIdShift = 12L;
        this.datacenterIdShift = 17L;
        this.timestampLeftShift = 22L;
        this.sequenceMask = 4095L;
        this.sequence = 0L;
        this.lastTimestamp = -1L;
        this.intervalTimestamp = 0L;
        if (workerId <= 31L && workerId >= 0L) {
            if (datacenterId <= 31L && datacenterId >= 0L) {
                this.workerId = workerId;
                this.datacenterId = datacenterId;
            } else {
                throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", 31L));
            }
        } else {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", 31L));
        }
    }

    public synchronized long nextId() {
        long timestamp = this.timeGen();
        if (timestamp < this.lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", this.lastTimestamp - timestamp));
        } else {
            if (this.lastTimestamp == timestamp) {
                this.sequence = this.sequence + 1L & 4095L;
                if (this.sequence == 0L) {
                    timestamp = this.tilNextMillis(this.lastTimestamp);
                }
            } else {
                this.sequence = 0L;
            }

            this.lastTimestamp = timestamp;
            return timestamp - 1420041600000L << 22 | this.datacenterId << 17 | this.workerId << 12 | this.sequence;
        }
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp;
        for(timestamp = this.timeGen(); timestamp <= lastTimestamp; timestamp = this.timeGen()) {
        }

        return timestamp;
    }

    protected long timeGen() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < this.lastTimestamp) {
            this.intervalTimestamp = this.lastTimestamp - timestamp;
        }

        return this.intervalTimestamp + timestamp;
    }

    public static void main(String[] args) throws InterruptedException {
//        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0L, 0L);
//        int threadCount = 100;
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        int testCount = 1000000;
//        CountDownLatch countDownLatch = new CountDownLatch(testCount);
//        CopyOnWriteArraySet<Long> set = new CopyOnWriteArraySet();
//        long beginTime = System.currentTimeMillis();
//
//        for(int i = 0; i < testCount; ++i) {
//            executorService.execute(() -> {
//                try {
//                    long id = idWorker.nextId();
//                    set.add(id);
//                    System.out.println(i + 1);
//                } finally {
//                    countDownLatch.countDown();
//                }
//
//            });
//        }
//
//        countDownLatch.await();
//        System.out.println(set.size() == testCount);
//        System.out.println("cost = " + (System.currentTimeMillis() - beginTime));
    }

    private static class SnowflakeIdWorkerHolder {
        private static final SnowflakeIdWorker INSTANCE = new SnowflakeIdWorker();

        private SnowflakeIdWorkerHolder() {
        }
    }
}

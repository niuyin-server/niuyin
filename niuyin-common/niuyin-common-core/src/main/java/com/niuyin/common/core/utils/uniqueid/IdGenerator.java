package com.niuyin.common.core.utils.uniqueid;

import com.niuyin.common.core.utils.IdUtils;

public class IdGenerator {

    public IdGenerator() {
    }

    public static String generatorId() {
        long id = SnowflakeIdWorker.getInstance().nextId();
        return String.format("%d", id);
    }

    public static String generatorShortId() {
        long id = SnowflakeIdWorker.getInstance().nextId();
        String uuid = IdUtils.shortUUID();
        return String.format("%d%s", id, uuid);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(generatorShortId());
        System.out.println(generatorId());
    }
}

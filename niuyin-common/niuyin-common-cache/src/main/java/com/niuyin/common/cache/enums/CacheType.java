package com.niuyin.common.cache.enums;

public enum CacheType {
    FULL,   //存取，存在就返回，不存在则存入二级缓存再返回，默认值
    PUT,    //只存，强制更新并返回结果
    DELETE  //删除
}

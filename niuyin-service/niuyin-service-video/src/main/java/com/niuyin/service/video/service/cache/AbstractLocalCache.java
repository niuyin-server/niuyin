package com.niuyin.service.video.service.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Iterables;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Description: redis string类型的批量缓存框架
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-10
 */
public abstract class AbstractLocalCache<IN, OUT> implements BatchCache<IN, OUT> {

    private Class<IN> inClass;
    private Class<OUT> outClass;
    private LoadingCache<IN, OUT> cache;

    protected AbstractLocalCache() {
        init(60, 10 * 60, 1024);
    }

    protected AbstractLocalCache(long refreshSeconds, long expireSeconds, int maxSize) {
        init(refreshSeconds, expireSeconds, maxSize);
    }

    private void init(long refreshSeconds, long expireSeconds, int maxSize) {
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.inClass = (Class<IN>) genericSuperclass.getActualTypeArguments()[0];
        this.outClass = (Class<OUT>) genericSuperclass.getActualTypeArguments()[1];
        cache = Caffeine.newBuilder()
                //自动刷新,不会阻塞线程,其他线程返回旧值
                .refreshAfterWrite(refreshSeconds, TimeUnit.SECONDS)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .maximumSize(maxSize)
                .build(new CacheLoader<IN, OUT>() {
                    @Nullable
                    @Override
                    public OUT load(@NonNull IN in) {
                        return AbstractLocalCache.this.load(Collections.singletonList(in)).get(in);
                    }

                    @Override
                    public Map<? extends IN, ? extends OUT> loadAll(Set<? extends IN> keys) throws Exception {
                        IN[] ins = Iterables.toArray(keys, inClass);
                        return AbstractLocalCache.this.load(Arrays.asList(ins));
                    }
                });
    }

    protected abstract Map<IN, OUT> load(List<IN> req);

    @Override
    public OUT get(IN req) {
        return cache.get(req);
    }

    @Override
    public Map<IN, OUT> getBatch(List<IN> req) {
        return cache.getAll(req);
    }

    @Override
    public void delete(IN req) {
        cache.invalidate(req);
    }

    @Override
    public void deleteBatch(List<IN> req) {
        cache.invalidateAll(req);
    }
}

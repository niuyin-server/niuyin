package com.niuyin.service.recommend.service.impl;

import com.niuyin.common.core.service.RedisService;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.service.recommend.service.UserTagModalRecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * UserTagModalServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/27
 **/
@Slf4j
@Service
public class UserTagModalRecommendServiceImpl implements UserTagModalRecommendService {
    public static final String VIDEO_TAG_VIDEOS_CACHE_KEY_PREFIX = "video:tag:videos:";
    public static final String VIDEO_CATEGORY_VIDEOS_CACHE_KEY_PREFIX = "video:category:videos:";
    // 根据分类已推送过的视频 通过userId区分
    public static final String VIDEO_CATEGORY_PUSHED_CACHE_KEY_PREFIX = "video:category:pushed:"; // + userId
    public static final String VIDEO_MEMBER_MODEL_CACHE_KEY_PREFIX = "video:member:model:";
    // 视频观看历史，使用redis保存7天
    public static final String VIDEO_VIEW_HISTORY_CACHE_KEY_PREFIX = "video:view:history:";

    public static final int VIDEO_RECOMMEND_SIZE = 20; // 推荐视频数量
    @Resource
    private RedisService redisService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户模型初始化
     *
     * @param userId
     * @param tagIds
     */
    @Override
    public void initUserModel(Long userId, List<Long> tagIds) {
        String key = VIDEO_MEMBER_MODEL_CACHE_KEY_PREFIX + userId;
        // 标签id转为string，值为标签的概率
        Map<String, Double> modelMap = new HashMap<>();
        if (!ObjectUtils.isEmpty(tagIds)) {
            int size = tagIds.size();
            // 将标签分为百分比等分概率, todo 不可能超过100个分类
            double probabilityValue = (double) 100 / size;
            for (Long tagId : tagIds) {
                modelMap.put(tagId.toString(), probabilityValue);
            }
        }
        // todo 先删除用户模型
        redisService.setCacheMap(key, modelMap);
        // todo 为用户模型设置ttl
    }

    /**
     * 用于给用户推送视频 -> 兴趣推送
     * 推送 X 视频,包含一条和性别有关
     *
     * @return videoIds
     */
    @Override
    public List<String> getVideoIdsByUserModel(Long userId) {
        // todo 根据member查询其兴趣模型是否为空，为空则创建模型
        Map<String, Double> modelMap = redisService.getCacheMap(VIDEO_MEMBER_MODEL_CACHE_KEY_PREFIX + userId.toString());
        if (Objects.isNull(modelMap) || modelMap.isEmpty()) {
            List<Long> tagIds = new ArrayList<>(10);
            tagIds.add(1L);
            tagIds.add(18L);
            tagIds.add(20L);
            tagIds.add(34L);
            tagIds.add(5L);
            tagIds.add(60L);
            tagIds.add(66L);
            tagIds.add(77L);
            tagIds.add(88L);
            tagIds.add(9L);
            initUserModel(userId, tagIds);
        }
        // 创建结果集
        Set<String> videoIds = new HashSet<>(VIDEO_RECOMMEND_SIZE);
        // 随机获取视频id
        List<String> list = redisService.pipeline(connection -> {
            for (Long tagId : random20TagIdsFromUserModel(modelMap)) {
                String key = VIDEO_TAG_VIDEOS_CACHE_KEY_PREFIX + tagId;
                log.debug("key:{}", key);
                byte[] bytes = connection.sRandMember(redisTemplate.getStringSerializer().serialize(key));
                if (bytes != null) {
                    return redisTemplate.getStringSerializer().deserialize(bytes);
                }
            }
            return null;
        });
        // 获取到的videoIds去重
        Set<String> setVideoIds = list.stream().filter(StringUtils::isNotNull).map(Object::toString).collect(Collectors.toSet());
//        Set<String> videoSetIds = completeVideoIdsFromTagIds(setVideoIds, random20TagIdsFromUserModel(modelMap));
//        log.debug("videoSetIds:{}", videoSetIds);
        // 根据视频观看历史去重
//            List<String> simpIds = redisService.pipeline(connection -> {
//                for (String id : videoSetIds) {
//                    String key = VIDEO_VIEW_HISTORY_CACHE_KEY_PREFIX + id + ":" + userId;
//                    byte[] bytes = connection.get(redisTemplate.getStringSerializer().serialize(key));
//                    if (bytes != null) {
//                        return redisTemplate.getStringSerializer().deserialize(bytes);
//                    }
//                }
//                return null;
//            });
//            simpIds = simpIds.stream().filter(StringUtils::isNotNull).collect(Collectors.toList());
//
//            // todo 根据已筛选去重
//
//            if (!ObjectUtils.isEmpty(simpIds)) {
//                for (Object simpId : simpIds) {
//                    String l = simpId.toString();
//                    if (videoSetIds.contains(l)) {
//                        videoSetIds.remove(l);
//                    }
//                }
//            }

        videoIds.addAll(setVideoIds);
        int videoIdsSize = videoIds.size();
        log.debug("videoIds size:{}", videoIdsSize);
        // todo 不够10条数据就随机取标签补全20条，男生推美女，女生推帅哥 o.0

        // 随机补全视频id,根据性别: 男：美女(10) 女：帅哥(1) todo 或者递归再次随机根据模型筛选出视频
//        if (videoIdsSize < VIDEO_RECOMMEND_SIZE) {
//            String sex = member.getSex();
//            int requestNum = VIDEO_RECOMMEND_SIZE - videoIdsSize;
//            log.debug("requestNum:{}", requestNum);
//            for (int i = 0; i < requestNum; i++) {
//                String videoId = randomVideoIdFromTag("1".equals(sex) ? 20L : 1L);
//                log.debug("add videoId:{}", videoId);
//                videoIds.add(videoId);
//            }
//        }
        return new ArrayList<>(videoIds);
    }

    /**
     * 从用户模型随机20个标签
     */
    public List<Long> random20TagIdsFromUserModel(Map<String, Double> modelMap) {
        // 标签ids数组 【1，1，1，2，2，66，7，7，7，7】
        String[] probabilityArray = initProbabilityArray(modelMap);
        // 获取视频
        final Random random = new Random();
        // 取出指定量的标签
        List<Long> tagIds = new ArrayList<>();
        for (int i = 0; i < VIDEO_RECOMMEND_SIZE; i++) {
            String tagId = probabilityArray[random.nextInt(probabilityArray.length)];
            tagIds.add(Long.parseLong(tagId));
        }
        return tagIds;
    }

    /**
     * 初始化概率数组 -> 保存的元素是标签id
     */
    public String[] initProbabilityArray(Map<String, Double> modelMap) {
        // key: 标签id  value：概率
        Map<String, Integer> probabilityMap = new HashMap<>();
        int size = modelMap.size();
        // field个数
        AtomicInteger num = new AtomicInteger(0);
        modelMap.forEach((k, v) -> {
            // 标签的概率 防止结果为0,每个同等加上标签数
            int probability = (v.intValue() + size) / size;
            probabilityMap.put(k, probability);
            num.getAndAdd(probability);
        });
        // 返回结果初始化
        String[] probabilityArray = new String[num.get()];
        AtomicInteger index = new AtomicInteger(0);
        // 遍历probabilityMap，将每个tagId及其概率p存入probabilityArray中
        probabilityMap.forEach((tagId, p) -> {
            int i = index.get();
            int limit = i + p;
            while (i < limit) {
                probabilityArray[i++] = tagId;
            }
            index.set(limit);
        });
        return probabilityArray;
    }

    /**
     * 从用户模型的标签中随机视频ids
     *
     * @param tagIds
     * @return
     */
    public Set<String> completeVideoIdsFromTagIds(Set<String> videoIds, List<Long> tagIds) {
        if (videoIds.size() >= VIDEO_RECOMMEND_SIZE) {
            return videoIds;
        }
        List<String> list = redisService.pipeline(connection -> {
            for (Long tagId : tagIds) {
                String key = VIDEO_TAG_VIDEOS_CACHE_KEY_PREFIX + tagId;
                log.debug("key:{}", key);
                byte[] bytes = connection.sRandMember(Objects.requireNonNull(redisTemplate.getStringSerializer().serialize(key)));
                if (bytes != null) {
                    return redisTemplate.getStringSerializer().deserialize(bytes);
                }
            }
            return null;
        });
        // 获取到的videoIds去重
        Set<String> collect = list.stream().filter(StringUtils::isNotNull).map(Object::toString).collect(Collectors.toSet());
        videoIds.addAll(collect);
        return videoIds;
    }

    public String randomVideoIdFromTag(Long tagId) {
        String key = VIDEO_TAG_VIDEOS_CACHE_KEY_PREFIX + tagId;
        return redisTemplate.opsForSet().randomMember(key).toString();
    }

}

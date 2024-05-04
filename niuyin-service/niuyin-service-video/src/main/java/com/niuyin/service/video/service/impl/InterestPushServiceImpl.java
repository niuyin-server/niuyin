package com.niuyin.service.video.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niuyin.common.core.service.RedisService;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoTag;
import com.niuyin.model.video.vo.UserModel;
import com.niuyin.model.video.vo.UserModelField;
import com.niuyin.service.video.service.IVideoTagRelationService;
import com.niuyin.service.video.service.IVideoTagService;
import com.niuyin.service.video.service.InterestPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.niuyin.service.video.constants.InterestPushConstant.*;

/**
 * InterestPushServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/6
 **/
@Slf4j
@Service
public class InterestPushServiceImpl implements InterestPushService {

    @Resource
    private RedisService redisService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private IVideoTagService videoTagService;

    @Resource
    private IVideoTagRelationService videoTagRelationService;

    final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将标签对应的视频集合存入redis的set
     */
    @Async
    @Override
    public void cacheVideoToTagRedis(String videoId, List<Long> tagsIds) {
//        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
//            for (Long tagId : tagsIds) {
//                connection.sAdd((VIDEO_TAG_VIDEOS_CACHE_KEY_PREFIX + tagId.toString()).getBytes(), video.getVideoId().getBytes());
//            }
//            return null;
//        });
        tagsIds.forEach(tagId -> {
            redisTemplate.opsForSet().add(VIDEO_TAG_VIDEOS_CACHE_KEY_PREFIX + tagId.toString(), videoId);
        });
    }

    @Async
    @Override
    public void cacheVideoToCategoryRedis(String videoId, List<Long> categoryIds) {
        categoryIds.forEach(id -> {
            redisTemplate.opsForSet().add(VIDEO_CATEGORY_VIDEOS_CACHE_KEY_PREFIX + id.toString(), videoId);
        });
    }

    @Async
    @Override
    public void deleteVideoFromTagRedis(String videoId, List<Long> tagsIds) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Long tagId : tagsIds) {
                connection.sRem((VIDEO_TAG_VIDEOS_CACHE_KEY_PREFIX + tagId).getBytes(), videoId.getBytes());
            }
            return null;
        });
    }

    @Async
    @Override
    public void deleteVideoFromCategoryRedis(Video video, List<Long> categoryIds) {
        categoryIds.forEach(id -> {
            redisTemplate.opsForSet().remove(VIDEO_CATEGORY_VIDEOS_CACHE_KEY_PREFIX + id, video.getVideoId());
        });
    }

    /**
     * 根据分类推送视频
     *
     * @param categoryId
     * @return
     */
    @Override
    public Collection<String> listVideoIdByCategoryId(Long categoryId) {
        // 随机推送10个
        List<Object> list = redisTemplate.opsForSet().randomMembers(VIDEO_CATEGORY_VIDEOS_CACHE_KEY_PREFIX + categoryId.toString(), 10);
        if (list.isEmpty() || StringUtils.isNull(list)) {
            return null;
        }
        // 可能会有null
        HashSet<String> result = new HashSet<>();
        for (Object aLong : list) {
            if (aLong != null) {
                result.add(aLong.toString());
            }
        }
        // todo 不足10条补足10条
        return result;
    }

    /**
     * 初始化用户模型 可在用户 注册 时  随机抽选标签  进行初始化 每次刷视频该视频刷到一定时长进行模型更新
     *
     * @param userId 用户id
     * @param tagIds
     */
    @Async
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
     * 更新用户模型
     *
     * @param userModel
     */
    @Async
    @Override
    public void updateUserModel(UserModel userModel) {
        log.debug("userModel:{}", userModel);
        Long userId = userModel.getUserId();
        if (userId != null) {
            List<UserModelField> models = userModel.getModels();
            // 获取用户模型
            String key = VIDEO_MEMBER_MODEL_CACHE_KEY_PREFIX + userId;
            Map<Object, Object> modelMap = redisTemplate.opsForHash().entries(key);
            log.debug("modelMap:{}", modelMap);
            if (StringUtils.isNull(modelMap)) {
                modelMap = new HashMap<>();
            }
            for (UserModelField model : models) {
                // 修改用户模型
                if (modelMap.containsKey(model.getTagId().toString())) {
                    modelMap.put(model.getTagId().toString(), Double.parseDouble(modelMap.get(model.getTagId().toString()).toString()) + model.getScore());
                    Object o = modelMap.get(model.getTagId().toString());
                    if (o == null || Double.parseDouble(o.toString()) > 0.0) {
                        modelMap.remove(o);
                    }
                } else {
                    modelMap.put(model.getTagId().toString(), model.getScore());
                }
            }

            // 每个标签概率同等加上标签数，再同等除以标签数  防止数据膨胀
            int tagSize = modelMap.keySet().size();
            for (Object o : modelMap.keySet()) {
                modelMap.put(o.toString(), (Double.parseDouble(modelMap.get(o.toString()).toString()) + tagSize) / tagSize);
            }
            // 更新用户模型
            log.debug("modelMap:{}", modelMap);
            redisTemplate.opsForHash().putAll(key, modelMap);
        }

    }

    /**
     * 从用户模型随机10个标签
     *
     * @param member
     * @return
     */
    public List<Long> random10TagIdsFromUserModel(Member member) {
        Long userId = member.getUserId();
        // 从模型中拿概率 获取hashKey对应的所有概率键值
        Map<String, Double> modelMap = redisService.getCacheMap(VIDEO_MEMBER_MODEL_CACHE_KEY_PREFIX + userId.toString());
        // 标签ids数组 【1，1，1，2，2，66，7，7，7，7】
        String[] probabilityArray = initProbabilityArray(modelMap);
        // 获取视频
        final Random random = new Random();
        // 取出指定量的标签
        List<Long> tagIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String tagId = probabilityArray[random.nextInt(probabilityArray.length)];
            tagIds.add(Long.parseLong(tagId));
        }
        return tagIds;
    }

    @Override
    public Collection<String> getVideoIdsByUserModel(Member member) {
        // todo 根据member查询其兴趣模型是否为空，为空则创建模型
        Map<String, Double> modelMap = redisService.getCacheMap(VIDEO_MEMBER_MODEL_CACHE_KEY_PREFIX + member.getUserId().toString());
        if (StringUtils.isNull(modelMap) || modelMap.isEmpty()) {
            // 初始化模型,随机标签
            initUserModel(member.getUserId(), videoTagService.random10VideoTags().stream().map(VideoTag::getTagId).collect(Collectors.toList()));
        }
        // 创建结果集
        Set<String> videoIds = new HashSet<>(10);
        // 随机获取
        List<String> list = redisService.pipeline(connection -> {
            for (Long tagId : random10TagIdsFromUserModel(member)) {
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
        Set<String> videoSetIds = completeVideoIdsFromTagIds(setVideoIds, random10TagIdsFromUserModel(member));
        log.debug("videoSetIds:{}", videoSetIds);
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

        videoIds.addAll(videoSetIds);
        int videoIdsSize = videoIds.size();
        log.debug("videoIds size:{}", videoIdsSize);
        // todo 不够10条数据就随机取标签补全10条，男生推美女，女生推帅哥 o.0

        // 随机补全视频id,根据性别: 男：美女(10) 女：帅哥(1) todo 或者递归再次随机根据模型筛选出视频
        if (videoIdsSize < 10) {
            String sex = member.getSex();
            int requestNum = 10 - videoIdsSize;
            log.debug("requestNum:{}", requestNum);
            for (int i = 0; i < requestNum; i++) {
                String videoId = randomVideoIdFromTag("1".equals(sex) ? 20L : 1L);
                log.debug("add videoId:{}", videoId);
                videoIds.add(videoId);
            }
        }
        return videoIds;
    }

    /**
     * 从用户模型的标签中随机视频ids
     *
     * @param tagIds
     * @return
     */
    public Set<String> completeVideoIdsFromTagIds(Set<String> videoIds, List<Long> tagIds) {
        if (videoIds.size() >= 10) {
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

    @Override
    public Collection<String> getVideoIdsByTagIds(List<Long> tagIds) {
        List<String> tagKeys = new ArrayList<>();
        for (Long tagId : tagIds) {
            tagKeys.add("video:tag:videos" + tagId);
        }
        return this.sRandom(tagKeys).stream().map(Object::toString).collect(Collectors.toList());
    }

//    public Long randomHotVideoId() {
//        final Object o = redisTemplate.opsForZSet().randomMember(RedisConstant.HOT_RANK);
//        try {
//            return objectMapper.readValue(o.toString(), HotVideo.class).getVideoId();
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    public Long randomVideoId(Boolean sex) {
//        String key = RedisConstant.SYSTEM_STOCK + (sex ? "美女" : "宠物");
//        final Object o = redisCacheUtil.sRandom(key);
//        if (o != null) {
//            return Long.parseLong(o.toString());
//        }
//        return null;
//    }

    // 随机获取视频id
    public Long getVideoId(Random random, String[] probabilityArray) {
        String tagId = probabilityArray[random.nextInt(probabilityArray.length)];
        // 获取对应所有视频
        String key = "video:tag:videos" + tagId;
        Object o = redisTemplate.opsForSet().randomMember(key);
        if (o != null) {
            return Long.parseLong(o.toString());
        }
        return null;
    }

    public Set<Object> sRandom(List<String> keys) {
        List<Object> list = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String key : keys) {
                connection.sRandMember(key.getBytes());
            }
            return null;
        });
        return list.stream().filter(StringUtils::isNotNull).collect(Collectors.toSet());
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


}

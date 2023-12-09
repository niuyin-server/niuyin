package com.niuyin.service.video;

import com.niuyin.common.service.RedisService;
import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoTag;
import com.niuyin.model.video.domain.VideoTagRelation;
import com.niuyin.service.video.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 兴趣推送测试 xq
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/6
 **/
@Slf4j
@SpringBootTest
public class InterestPushTest {

    private static final String VIDEO_TAG_VIDEOS_CACHE_KEY_PREFIX = "video:tag:videos:";
    private static final String VIDEO_CATEGORY_VIDEOS_CACHE_KEY_PREFIX = "video:category:videos:";
    private static final String VIDEO_MEMBER_MODEL_CACHE_KEY_PREFIX = "video:member:model:";
    // 视频观看历史，使用redis保存7天
    private static final String VIDEO_VIEW_HISTORY_CACHE_KEY_PREFIX = "video:view:history:";

    @Resource
    private IVideoTagService videoTagService;

    @Resource
    private IVideoTagRelationService videoTagRelationService;

    @Resource
    private InterestPushService interestPushService;

    @Resource
    private IVideoService videoService;

    @Resource
    private IVideoCategoryRelationService videoCategoryRelationService;

    @DubboReference
    private DubboMemberService dubboMemberService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedisService redisService;

    @Test
    @DisplayName("初始化标签视频库")
    void initTagVideos() {
        List<Video> videoList = videoService.list();
        videoList.forEach(v -> {
            // 存入标签库
            interestPushService.cacheVideoToTagRedis(v.getVideoId(), videoTagRelationService.queryVideoTagIdsByVideoId(v.getVideoId()));
        });

    }

    @Test
    @DisplayName("初始化分类视频库")
    void initCategoryVideos() {
        List<Video> videoList = videoService.list();
        videoList.forEach(v -> {
            // 存入分类库
            interestPushService.cacheVideoToCategoryRedis(v, videoCategoryRelationService.queryVideoCategoryIdsByVideoId(v.getVideoId()));
        });

    }

    @Test
    @DisplayName("初始化视频观看历史")
    void initVideoViewHistory() {

    }

    @Test
    @DisplayName("初始化用户模型")
    void initMemberModel() {
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
        interestPushService.initUserModel(2L, tagIds);
    }

    @Test
    @DisplayName("推送视频")
    void testGetVideoIdsByUserModel() {
        Member member = dubboMemberService.apiGetById(2L);
        Collection<String> videoIdsByUserModel = interestPushService.getVideoIdsByUserModel(member);
        videoIdsByUserModel.forEach(System.out::println);

//        Object object = redisTemplate.opsForSet().randomMember("video:tag:videos:1");
//        System.out.println("object = " + object);
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

    @Test
    @DisplayName("推送分类视频")
    void testCVideo() {
        Collection<String> videoIds = interestPushService.listVideoIdByCategoryId(11L);
        videoIds.forEach(System.out::println);
    }


}

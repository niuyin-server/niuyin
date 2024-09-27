package com.niuyin.service.search.schedule;

import com.niuyin.common.cache.annotations.RedissonLock;
import com.niuyin.common.cache.service.RedisService;
import com.niuyin.dubbo.api.DubboBehaveService;
import com.niuyin.model.search.dto.VideoSearchKeywordDTO;
import com.niuyin.model.search.dubbo.VideoBehaveData;
import com.niuyin.model.video.enums.IkAnalyzeTypeEnum;
import com.niuyin.service.search.constant.VideoHotTitleCacheConstants;
import com.niuyin.service.search.domain.VideoSearchHistory;
import com.niuyin.service.search.domain.VideoSearchVO;
import com.niuyin.service.search.service.EsIkAnalyzeService;
import com.niuyin.service.search.service.VideoSearchHistoryService;
import com.niuyin.service.search.service.VideoSearchService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * HotVideoTask
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/7
 **/
@Slf4j
@Component
public class HotSearchSchedule {

    @Resource
    private RedisService redisService;

    @Resource
    private VideoSearchHistoryService videoSearchHistoryService;

    @Resource
    private VideoSearchService videoSearchService;

    @Resource
    private DubboBehaveService dubboBehaveService;

    @Resource
    private EsIkAnalyzeService esIkAnalyzeService;

    public final static String SEARCH_KEY = "search:hot_search_task_lock";

    /**
     * 计算热搜 定时任务
     * 添加分布式锁
     */
//    @RedissonLock(prefixKey = SEARCH_KEY)
    @SneakyThrows
    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void computeHotSearch() {
        List<String> list = new ArrayList<>();
//        List<Term> termList;

        //将分词存入集合中
        for (VideoSearchHistory allSearch : videoSearchHistoryService.findTodaySearchRecord()) {
            Set<String> ikAnalyzeSetResult = esIkAnalyzeService.getIkAnalyzeSetResult(allSearch.getKeyword(), IkAnalyzeTypeEnum.IK_SMART);
//            termList = HanLP.segment(allSearch.getKeyword());
            for (String term : ikAnalyzeSetResult) {
                if (term.length() != 1) {
                    list.add(term);
                }
            }
        }

        //将集合中的而分词封装到map中，并且计数
        Map<String, Integer> map = new HashMap<>();
        for (String s : list) {
            map.put(s, map.getOrDefault(s, 0) + 1);
        }
        //根据每个分词出现的频率进行降序排序
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(map.entrySet());
        entryList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        //将排序后的结果封装到map中
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        //将分装好的map中的key取出存入集合
        List<String> keyList = new ArrayList<>(sortedMap.keySet());
        List<VideoSearchVO> videoSearchVOS = new ArrayList<>();
        int i = 0;
        for (String s : keyList) {
            if (i < 10) {
                VideoSearchKeywordDTO videoSearchKeywordDTO = new VideoSearchKeywordDTO();
                videoSearchKeywordDTO.setKeyword(s);
                videoSearchKeywordDTO.setPageNum(0);
                videoSearchKeywordDTO.setPageSize(1);
                List<VideoSearchVO> searchVOS = videoSearchService.searchAllVideoFromES(videoSearchKeywordDTO);
                if (!CollectionUtils.isEmpty(searchVOS)) {
                    videoSearchVOS.addAll(searchVOS);
                }
                i++;
            }
        }
        List<String> videoHotTitles = videoSearchVOS.stream().map(VideoSearchVO::getVideoTitle).collect(Collectors.toList());
        for (int j = 0; j < videoHotTitles.size(); j++) {
            VideoBehaveData videoBehaveData = dubboBehaveService.apiGetVideoBehaveData(videoSearchVOS.get(j).getVideoId());
            // 算分
            if (Objects.nonNull(videoBehaveData)) {
                double score = videoBehaveData.getViewCount() + videoBehaveData.getLikeCount() * 2
                        + videoBehaveData.getCommentCount() * 3 + videoBehaveData.getFavoriteCount() * 4;
                redisService.setCacheZSet(VideoHotTitleCacheConstants.VIDEO_HOT_TITLE_PREFIX, videoHotTitles.get(j), score);
            }

        }
    }

}

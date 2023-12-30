package com.niuyin.service.search.schedule;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.niuyin.common.service.RedisService;
import com.niuyin.model.search.dto.VideoSearchKeywordDTO;
import com.niuyin.service.search.constant.VideoHotTitleCacheConstants;
import com.niuyin.service.search.domain.VideoSearchHistory;
import com.niuyin.service.search.domain.VideoSearchVO;
import com.niuyin.service.search.service.VideoSearchHistoryService;
import com.niuyin.service.search.service.VideoSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;


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

    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void computeHotSearch() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        List<Term> termList;

        //将分词存入集合中
        for (VideoSearchHistory allSearch : videoSearchHistoryService.findTodaySearchRecord()) {
            termList = HanLP.segment(allSearch.getKeyword());
            for (Term term : termList) {
                if (term.word.length() != 1) {
                    list.add(term.word);
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
            if (i < 5) {
                VideoSearchKeywordDTO videoSearchKeywordDTO = new VideoSearchKeywordDTO();
                videoSearchKeywordDTO.setKeyword(s);
                videoSearchKeywordDTO.setPageNum(0);
                videoSearchKeywordDTO.setPageSize(1);
                videoSearchVOS.addAll(videoSearchService.searchAllVideoFromES(videoSearchKeywordDTO));
                i++;
            } else {
                break;
            }
        }
        ArrayList<String> videoHotTitles = new ArrayList<>();
        for (VideoSearchVO videoSearchVO : videoSearchVOS) {
            videoHotTitles.add(videoSearchVO.getVideoTitle());
        }
        for (int j = 0; j < videoHotTitles.size(); j++) {
            Integer videoViewNum = redisService.getCacheMapValue(VideoHotTitleCacheConstants.VIDEO_VIEW_NUM_MAP_KEY, videoSearchVOS.get(j).getVideoId());
            if (videoViewNum != null) {
                redisService.setCacheZSet(VideoHotTitleCacheConstants.VIDEO_HOT_TITLE_PREFIX, videoHotTitles.get(j), ((double) videoViewNum) / 100);
            }
        }
    }

}

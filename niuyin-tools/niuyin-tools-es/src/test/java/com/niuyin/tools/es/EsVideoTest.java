package com.niuyin.tools.es;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.search.vo.VideoSearchVO;
import com.niuyin.model.video.domain.Video;
import com.niuyin.tools.es.mapper.VideoMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class EsVideoTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private RedisService redisService;

    @Resource
    private VideoMapper videoMapper;

    /**
     * 注意：数据量的导入，如果数据量过大，需要分页导入
     *
     * @throws Exception
     */
    @Test
    @DisplayName("所有视频同步到es")
    public void init() throws Exception {
        List<Video> videos = videoMapper.selectExistVideoList();
        List<VideoSearchVO> searchVOS = new ArrayList<>();
        videos.forEach(v -> {
            VideoSearchVO videoSearchVO = new VideoSearchVO();
            videoSearchVO.setVideoId(v.getVideoId());
            videoSearchVO.setVideoTitle(v.getVideoTitle());
            // localdatetime转换为date
            videoSearchVO.setPublishTime(Date.from(v.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()));
            videoSearchVO.setCoverImage(v.getCoverImage());
            videoSearchVO.setVideoUrl(v.getVideoUrl());
            videoSearchVO.setPublishType(v.getPublishType());
            videoSearchVO.setUserId(v.getUserId());
            // 获取用户信息
            Member userCache = redisService.getCacheObject("member:userinfo:" + v.getUserId());
            if (StringUtils.isNotNull(userCache)) {
                videoSearchVO.setUserNickName(userCache.getNickName());
                videoSearchVO.setUserAvatar(userCache.getAvatar());
            } else {
                videoSearchVO.setUserNickName("-");
                videoSearchVO.setUserAvatar("-");
            }
            searchVOS.add(videoSearchVO);
        });
        //2.批量导入到es索引库
        BulkRequest bulkRequest = new BulkRequest("search_video");

        for (VideoSearchVO vo : searchVOS) {
            IndexRequest indexRequest = new IndexRequest().id(vo.getVideoId()).source(JSON.toJSONString(vo), XContentType.JSON);
            //批量添加数据
            bulkRequest.add(indexRequest);
        }
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

    }

    @Test
    public void testEsInsertOneVideo() {
        Video video = videoMapper.selectById("1183178243432251392f0c31774");
        VideoSearchVO videoSearchVO = new VideoSearchVO();
        videoSearchVO.setVideoId(video.getVideoId());
        videoSearchVO.setVideoTitle(video.getVideoTitle());
        videoSearchVO.setPublishTime(Date.from(video.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()));
        videoSearchVO.setCoverImage(video.getCoverImage());
        videoSearchVO.setVideoUrl(video.getVideoUrl());
        videoSearchVO.setUserId(2L);
        videoSearchVO.setUserNickName("-");
        videoSearchVO.setUserAvatar("-");

        IndexRequest indexRequest = new IndexRequest("search_video");
        indexRequest.id(videoSearchVO.getVideoId())
                .source(JSON.toJSONString(videoSearchVO), XContentType.JSON);
        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("sync es error ==> {}", e.getMessage());
        }
    }

}

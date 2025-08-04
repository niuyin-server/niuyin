package com.niuyin.tools.es;

import com.niuyin.model.search.vo.VideoSearchVO;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoTag;
import com.niuyin.tools.es.mapper.VideoMapper;
import com.niuyin.tools.es.mapper.VideoTagRelationMapper;
import com.niuyin.tools.es.repository.VideoEsRepository;
import com.niuyin.tools.es.service.VideoEsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Query;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@SpringBootTest
public class EsVideoTest {

    @Resource
    private VideoMapper videoMapper;
    @Resource
    private VideoTagRelationMapper videoTagRelationMapper;
    @Resource
    private VideoEsService videoEsService;
    @Resource
    private ElasticsearchOperations elasticsearchOperations;
    @Resource
    private VideoEsRepository videoEsRepository;

    /**
     * 🚨注意：数据量的导入，如果数据量过大，需要分页导入
     */
    @Test
    @DisplayName("所有视频同步到es")
    public void initNew() {
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
            // 视频标签
            List<VideoTag> videoTagList = videoTagRelationMapper.selectTagNamesByVideoId(v.getVideoId());
            String[] tags = videoTagList.stream().map(VideoTag::getTag).toArray(String[]::new);
            videoSearchVO.setTags(tags);
            searchVOS.add(videoSearchVO);
        });
        //2.批量导入到es索引库
        searchVOS.forEach(v -> {
            videoEsService.save(v);
        });
    }

    @Test
    @DisplayName("删除所有文档")
    void testDeleteAllDoc() {
        // 删除所有文档但保留索引
        elasticsearchOperations.delete(Query.findAll(), VideoSearchVO.class);
    }

    @Test
    @DisplayName("根据id查询")
    void testFindById() {
        // 根据id查询
        Optional<VideoSearchVO> videoSearchVO = videoEsRepository.findById("11824067155559710728920bea6");
        videoSearchVO.ifPresent(v -> {
            log.info("根据id搜索视频结果:{}", v);
        });
    }

    @Test
    @DisplayName("根据VideoTitle查询")
    void testFindByVideoTitle() {
        // 根据id查询
        List<VideoSearchVO> res = videoEsRepository.findByVideoTitle("我的世界");
        res.forEach(v -> {
            log.info("根据VideoTitle搜索视频结果:{}", v);
        });
    }

}

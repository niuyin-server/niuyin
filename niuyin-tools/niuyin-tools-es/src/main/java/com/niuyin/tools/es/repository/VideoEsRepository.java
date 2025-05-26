package com.niuyin.tools.es.repository;

import com.niuyin.model.search.vo.VideoSearchVO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * VideoEsRepository
 *
 * @AUTHOR: roydon
 * @DATE: 2025/4/29
 **/
@Repository
public interface VideoEsRepository extends ElasticsearchRepository<VideoSearchVO, String> {

    /**
     * 根据视频标题查询
     */
    List<VideoSearchVO> findByVideoTitle(String videoTitle);
}

package com.niuyin.service.search.service;

import com.niuyin.model.search.dto.PageDTO;
import com.niuyin.model.search.dto.VideoSearchKeywordDTO;
import com.niuyin.service.search.domain.VideoSearchVO;

import java.util.List;
import java.util.Set;

/**
 * VideoSyncEsService
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
public interface VideoSearchService {

    /**
     * 视频同步新增到es
     *
     * @param videoId
     */
    void videoSync(String videoId);

    /**
     * 更新视频索引文档
     */
    void updateVideoDoc(String json);

    /**
     * 删除文档
     */
    void deleteVideoDoc(String videoId);

    /**
     * es分页搜索视频 、保存搜索记录，查询搜索记录
     *
     * @param dto
     */
    List<VideoSearchVO> searchVideoFromES(VideoSearchKeywordDTO dto) throws Exception;

    /**
     * 查询当天所有的搜索记录
     *
     * @param dto
     * @return
     * @throws Exception
     */
    List<VideoSearchVO> searchAllVideoFromES(VideoSearchKeywordDTO dto) throws Exception;

    /**
     * 从redis中获取热搜排行榜
     *
     * @return
     */
    Set findSearchHot(PageDTO pageDTO);
}

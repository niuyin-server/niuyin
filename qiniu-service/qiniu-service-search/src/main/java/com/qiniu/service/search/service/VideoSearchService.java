package com.qiniu.service.search.service;

import com.qiniu.model.search.dto.VideoSearchKeywordDTO;
import com.qiniu.service.search.domain.VideoSearchVO;

import java.util.List;

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
     * @param json videoSearchVO json
     */
    void videoSync(String json);

    /**
     * 更新视频索引文档
     */
    void updateVideoDoc(String json);

    /**
     * 删除文档
     */
    void deleteVideoDoc(String videoId);

    /**
     * es分页搜索视频
     *
     * @param dto
     */
    List<VideoSearchVO> searchVideoFromES(VideoSearchKeywordDTO dto) throws Exception;

}

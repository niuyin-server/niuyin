package com.niuyin.service.creator.service;

import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.creator.dto.VideoPageDTO;

/**
 * CreatorService
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/5
 **/
public interface CreatorService {
    /**
     * 视频分页
     *
     * @param videoPageDTO
     * @return
     */
    PageDataInfo queryVideoPage(VideoPageDTO videoPageDTO);
}

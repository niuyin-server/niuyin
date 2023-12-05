package com.niuyin.service.creator.service.impl;

import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.creator.dto.VideoPageDTO;
import com.niuyin.model.video.domain.Video;
import com.niuyin.service.creator.mapper.VideoMapper;
import com.niuyin.service.creator.service.CreatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * CreatorServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/5
 **/
@Slf4j
@Service
public class CreatorServiceImpl implements CreatorService {

    @Resource
    private VideoMapper videoMapper;

    /**
     * 视频分页
     *
     * @param videoPageDTO
     * @return
     */
    @Override
    public PageDataInfo queryVideoPage(VideoPageDTO videoPageDTO) {
        videoPageDTO.setPageNum((videoPageDTO.getPageNum() - 1) * videoPageDTO.getPageSize());
        List<Video> videoList = videoMapper.selectVideoPage(videoPageDTO);
        if (videoList.isEmpty()) {
            return PageDataInfo.emptyPage();
        }
        return PageDataInfo.genPageData(videoList, videoMapper.selectVideoPageCount(videoPageDTO));
    }
}

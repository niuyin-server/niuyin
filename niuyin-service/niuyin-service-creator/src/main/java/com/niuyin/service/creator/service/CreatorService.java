package com.niuyin.service.creator.service;

import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.creator.dto.VideoPageDTO;
import com.niuyin.model.creator.dto.videoCompilationPageDTO;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 视频合集分页
     *
     * @param videoCompilationPageDTO
     * @return
     */
    PageDataInfo queryVideoCompilationPage(videoCompilationPageDTO videoCompilationPageDTO);

    /**
     * 上传图文视频图片
     *
     * @param file
     * @return
     */
    String uploadVideoImage(MultipartFile file);

    /**
     * 上传视频
     *
     * @param file
     * @return
     */
    String uploadVideo(MultipartFile file);

}

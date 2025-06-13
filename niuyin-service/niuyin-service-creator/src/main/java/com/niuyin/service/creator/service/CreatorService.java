package com.niuyin.service.creator.service;

import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.model.creator.dto.VideoPageDTO;
import com.niuyin.model.creator.dto.videoCompilationPageDTO;
import com.niuyin.model.creator.vo.DashboardAmountVO;
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
    PageData queryVideoPage(VideoPageDTO videoPageDTO);

    /**
     * 视频合集分页
     *
     * @param videoCompilationPageDTO
     * @return
     */
    PageData queryVideoCompilationPage(videoCompilationPageDTO videoCompilationPageDTO);

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

    /**
     * 分片上传视频
     *
     * @param file
     * @return
     */
    String multipartUploadVideo(MultipartFile file);

    /**
     * 视频播放量
     */
    DashboardAmountVO dashboardAmount();

}

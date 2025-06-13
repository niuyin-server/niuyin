package com.niuyin.service.creator.service.impl;

import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.common.core.exception.CustomException;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.common.enums.HttpCodeEnum;
import com.niuyin.model.creator.dto.VideoPageDTO;
import com.niuyin.model.creator.dto.videoCompilationPageDTO;
import com.niuyin.model.creator.vo.DashboardAmountItem;
import com.niuyin.model.creator.vo.DashboardAmountVO;
import com.niuyin.model.video.domain.UserVideoCompilation;
import com.niuyin.model.video.domain.Video;
import com.niuyin.service.creator.mapper.VideoMapper;
import com.niuyin.service.creator.service.CreatorService;
import com.niuyin.starter.file.service.AliyunOssService;
import com.niuyin.starter.file.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
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

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    AliyunOssService aliyunOssService;

    /**
     * 视频分页
     *
     * @param videoPageDTO
     * @return
     */
    @Override
    public PageData queryVideoPage(VideoPageDTO videoPageDTO) {
        videoPageDTO.setUserId(UserContext.getUserId());
        videoPageDTO.setPageNum((videoPageDTO.getPageNum() - 1) * videoPageDTO.getPageSize());
        List<Video> videoList = videoMapper.selectVideoPage(videoPageDTO);
        if (videoList.isEmpty()) {
            return PageData.emptyPage();
        }
        return PageData.genPageData(videoList, videoMapper.selectVideoPageCount(videoPageDTO));
    }

    /**
     * 视频合集分页
     *
     * @param videoCompilationPageDTO
     * @return
     */
    @Override
    public PageData queryVideoCompilationPage(videoCompilationPageDTO videoCompilationPageDTO) {
        videoCompilationPageDTO.setUserId(UserContext.getUserId());
        videoCompilationPageDTO.setPageNum((videoCompilationPageDTO.getPageNum() - 1) * videoCompilationPageDTO.getPageSize());
        List<UserVideoCompilation> compilationList = videoMapper.selectVideoCompilationPage(videoCompilationPageDTO);
        if (compilationList.isEmpty()) {
            return PageData.emptyPage();
        }
        return PageData.genPageData(compilationList, videoMapper.selectVideoCompilationPageCount(videoCompilationPageDTO));
    }

    /**
     * 上传图文视频图片
     *
     * @param file
     * @return
     */
    @Override
    public String uploadVideoImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isNull(originalFilename)) {
            throw new CustomException(HttpCodeEnum.IMAGE_TYPE_FOLLOW);
        }
        // todo 对文件大小进行判断
        // 对原始文件名进行判断
        if (originalFilename.endsWith(".png")
                || originalFilename.endsWith(".jpg")
                || originalFilename.endsWith(".jpeg")
                || originalFilename.endsWith(".webp")) {
            return aliyunOssService.uploadFile(file, "video");
        } else {
            throw new CustomException(HttpCodeEnum.IMAGE_TYPE_FOLLOW);
        }
    }

    /**
     * 上传视频
     *
     * @param file
     * @return
     */
    @Override
    public String uploadVideo(MultipartFile file) {
        return aliyunOssService.uploadVideoFile(file, "video");
    }

    /**
     * 分片上传视频
     *
     * @param file
     * @return
     */
    @Override
    public String multipartUploadVideo(MultipartFile file) {
        return aliyunOssService.multipartUploadVideoFile(file, "video");
    }

    /**
     * 视频播放量
     */
    @Override
    public DashboardAmountVO dashboardAmount() {
        // todo 添加每日10点的定时任务缓存到redis

        Long userId = UserContext.getUserId();
        DashboardAmountVO dashboardAmountVO = new DashboardAmountVO();
        Long videoPlayCount = videoMapper.selectVideoPlayAmount(userId);
        dashboardAmountVO.setPlayAmount(new DashboardAmountItem(videoPlayCount, videoMapper.selectVideoPlayAmountAdd(userId), videoMapper.selectVideoPlayAmount7Day(userId)));
        dashboardAmountVO.setIndexViewAmount(new DashboardAmountItem(userId, userId));
        dashboardAmountVO.setFansAmount(new DashboardAmountItem(videoMapper.selectFansAmount(userId), videoMapper.selectFansAmountAdd(userId), videoMapper.selectFansAmount7Day(userId)));
        dashboardAmountVO.setLikeAmount(new DashboardAmountItem(videoMapper.selectVideoLikeAmount(userId), videoMapper.selectVideoLikeAmountAdd(userId), videoMapper.selectVideoLikeAmount7Day(userId)));
        dashboardAmountVO.setCommentAmount(new DashboardAmountItem(videoMapper.selectVideoCommentAmount(userId), videoMapper.selectVideoCommentAmountAdd(userId), videoMapper.selectVideoCommentAmount7Day(userId)));
        dashboardAmountVO.setShareAmount(new DashboardAmountItem(1L, 0L));
        return dashboardAmountVO;
    }

}

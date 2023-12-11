package com.niuyin.service.creator.controller.v1;

import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.creator.dto.VideoPageDTO;
import com.niuyin.model.creator.dto.videoCompilationPageDTO;
import com.niuyin.model.creator.vo.DashboardAmountVO;
import com.niuyin.service.creator.service.CreatorService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * CreatorController
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/5
 **/
@RestController
@RequestMapping("/api/v1")
public class CreatorController {

    @Resource
    private CreatorService creatorService;

    /**
     * 视频分页
     */
    @PostMapping("/videoPage")
    public PageDataInfo videoPage(@RequestBody VideoPageDTO videoPageDTO) {
        return creatorService.queryVideoPage(videoPageDTO);
    }

    /**
     * 视频合集分页
     */
    @PostMapping("/videoCompilationPage")
    public PageDataInfo videoCompilationPage(@RequestBody videoCompilationPageDTO videoCompilationPageDTO) {
        return creatorService.queryVideoCompilationPage(videoCompilationPageDTO);
    }

    /**
     * 上传视频图文图片
     */
    @PostMapping("/upload-video-image")
    public R<String> uploadVideoImage(@RequestParam("file") MultipartFile file) {
        return R.ok(creatorService.uploadVideoImage(file));
    }

    /**
     * 上传视频
     */
    @PostMapping("/upload-video")
    public R<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        return R.ok(creatorService.uploadVideo(file));
    }

    /**
     * 视频播放量等流向数据
     */
    @GetMapping("/dashboard-amount")
    public R<DashboardAmountVO> dashboardAmount() {
        return R.ok(creatorService.dashboardAmount());
    }

}

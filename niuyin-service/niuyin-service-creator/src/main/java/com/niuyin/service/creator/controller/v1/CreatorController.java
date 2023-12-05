package com.niuyin.service.creator.controller.v1;

import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.creator.dto.VideoPageDTO;
import com.niuyin.model.creator.dto.videoCompilationPageDTO;
import com.niuyin.service.creator.service.CreatorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}

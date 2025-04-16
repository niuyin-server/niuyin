package com.niuyin.service.video.controller.v1;

import com.niuyin.service.video.service.IVideoPositionService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 视频定位表(VideoPosition)表控制层
 *
 * @author roydon
 * @since 2023-11-21 15:44:14
 */
@RestController
@RequestMapping("/api/v1/videoPosition")
public class VideoPositionController {

    @Resource
    private IVideoPositionService videoPositionService;

}


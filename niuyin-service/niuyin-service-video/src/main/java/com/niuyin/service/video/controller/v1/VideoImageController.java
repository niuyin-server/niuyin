package com.niuyin.service.video.controller.v1;

import com.niuyin.service.video.service.IVideoImageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 视频图片关联表(VideoImage)表控制层
 *
 * @author roydon
 * @since 2023-11-20 21:18:59
 */
@RestController
@RequestMapping("/api/v1/videoImage")
public class VideoImageController {

    @Resource
    private IVideoImageService videoImageService;


}


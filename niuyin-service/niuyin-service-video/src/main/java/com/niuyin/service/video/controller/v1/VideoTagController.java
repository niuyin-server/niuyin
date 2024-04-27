package com.niuyin.service.video.controller.v1;

import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.video.domain.VideoTag;
import com.niuyin.service.video.service.IVideoTagService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 视频标签表(VideoTag)表控制层
 *
 * @author roydon
 * @since 2023-11-11 16:05:08
 */
@RestController
@RequestMapping("/api/v1/tag")
public class VideoTagController {

    @Resource
    private IVideoTagService videoTagService;

    /**
     * 新增标签
     */
    @PostMapping("/save")
    public R<Long> saveTag(@Validated @RequestBody VideoTag videoTag) {
        return R.ok(videoTagService.saveTag(videoTag).getTagId());
    }

}


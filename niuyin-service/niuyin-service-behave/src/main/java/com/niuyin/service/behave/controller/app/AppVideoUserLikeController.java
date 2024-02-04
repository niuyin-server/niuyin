package com.niuyin.service.behave.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.feign.video.RemoteVideoService;
import com.niuyin.model.behave.domain.VideoUserLike;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.service.behave.service.IVideoUserLikeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 点赞表(VideoUserLike)表控制层
 *
 * @author lzq
 * @since 2023-10-30 14:32:56
 */
@RestController
@RequestMapping("/api/v1/app/like")
public class AppVideoUserLikeController {

    @Resource
    private IVideoUserLikeService videoUserLikeService;

    /**
     * 我的点赞分页查询
     */
    @PostMapping("/myLikePage")
    public PageDataInfo myLikePageForApp(@RequestBody VideoPageDto pageDto) {
        return videoUserLikeService.queryMyLikeVideoPageForApp(pageDto);
    }

}


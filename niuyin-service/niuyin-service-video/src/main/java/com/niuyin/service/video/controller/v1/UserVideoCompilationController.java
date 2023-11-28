package com.niuyin.service.video.controller.v1;

import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.video.domain.UserVideoCompilation;
import com.niuyin.model.video.dto.UserVideoCompilationPageDTO;
import com.niuyin.service.video.service.IUserVideoCompilationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 用户视频合集表(UserVideoCompilation)表控制层
 *
 * @author roydon
 * @since 2023-11-27 18:08:37
 */
@RestController
@RequestMapping("/api/v1/userVideoCompilation")
public class UserVideoCompilationController {

    @Resource
    private IUserVideoCompilationService userVideoCompilationService;

    /**
     * 创建合集
     */
    @PostMapping()
    public R<Boolean> createVideoCompilation(@RequestBody UserVideoCompilation userVideoCompilation) {
        userVideoCompilation.setUserId(UserContext.getUserId());
        userVideoCompilation.setCreateTime(LocalDateTime.now());
        return R.ok(userVideoCompilationService.save(userVideoCompilation));
    }

    /**
     * 分页我的合集
     */
    @PostMapping("/mp")
    public PageDataInfo videoCompilationMyPage(@RequestBody UserVideoCompilationPageDTO pageDTO) {
        return userVideoCompilationService.videoCompilationMyPage(pageDTO);
    }

    /**
     * 分页用户合集
     */
    @PostMapping("/up")
    public PageDataInfo videoCompilationUserPage(@RequestBody UserVideoCompilationPageDTO pageDTO) {
        return userVideoCompilationService.videoCompilationUserPage(pageDTO);
    }

}


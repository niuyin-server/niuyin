package com.niuyin.service.behave.controller.v1;

import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.R;
import com.niuyin.model.behave.enums.UserVideoBehaveEnum;
import com.niuyin.service.behave.service.IUserVideoBehaveService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户视频行为表(UserVideoBehave)表控制层
 *
 * @author roydon
 * @since 2024-04-19 14:21:12
 */
@RestController
@RequestMapping("/api/v1/userVideoBehave")
public class UserVideoBehaveController {

    @Resource
    private IUserVideoBehaveService userVideoBehaveService;

    /**
     * 同步视频观看行为接口
     */
    @GetMapping("/syncViewBehave/{videoId}")
    public R<Boolean> syncUserVideoBehave(@PathVariable("videoId") String videoId) {
        if (UserContext.hasLogin()) {
            return R.ok(userVideoBehaveService.syncUserVideoBehave(UserContext.getUserId(), videoId, UserVideoBehaveEnum.VIEW));
        }
        return R.ok();
    }

}


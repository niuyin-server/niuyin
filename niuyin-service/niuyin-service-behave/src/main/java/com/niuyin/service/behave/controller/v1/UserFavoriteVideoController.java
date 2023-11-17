package com.niuyin.service.behave.controller.v1;

import com.niuyin.common.domain.R;
import com.niuyin.model.behave.domain.UserFavoriteVideo;
import com.niuyin.model.behave.dto.UserFavoriteVideoDTO;
import com.niuyin.service.behave.service.IUserFavoriteVideoService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (UserFavoriteVideo)表控制层
 *
 * @author lzq
 * @since 2023-11-17 10:16:03
 */
@RestController
@RequestMapping("/api/v1/userFavoriteVideo")
public class UserFavoriteVideoController {

    @Resource
    private IUserFavoriteVideoService userFavoriteVideoService;

    @PostMapping()
    public R<Boolean> getDetails(@RequestBody UserFavoriteVideoDTO userFavoriteVideoDTO) {
        return R.ok(userFavoriteVideoService.videoFavorites(userFavoriteVideoDTO));
    }

}


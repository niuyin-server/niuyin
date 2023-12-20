package com.niuyin.service.video.controller.v1;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.niuyin.feign.social.RemoteSocialService;
import com.niuyin.feign.member.RemoteMemberService;
import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.feign.behave.RemoteBehaveService;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.behave.domain.VideoUserComment;
import com.niuyin.model.video.dto.VideoCategoryPageDTO;
import com.niuyin.model.video.vo.VideoCategoryVo;
import com.niuyin.model.video.vo.VideoVO;
import com.niuyin.service.video.constants.VideoCacheConstants;
import com.niuyin.service.video.service.IVideoCategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * (VideoCategory)表控制层
 *
 * @author lzq
 * @since 2023-10-30 19:41:13
 */
@RestController
@RequestMapping("/api/v1")
public class VideoCategoryController {

    @Resource
    private IVideoCategoryService videoCategoryService;

    @Resource
    private RemoteMemberService remoteMemberService;

    @Resource
    private RedisService redisService;

    @Resource
    private RemoteBehaveService remoteBehaveService;

    @Resource
    private RemoteSocialService remoteSocialService;

    @GetMapping("/category")
    public R<?> getAllCategory() {
        List<VideoCategoryVo> categoryNames = videoCategoryService.selectAllCategory();
        return R.ok(categoryNames);
    }

    @PostMapping("/category/page")
    public PageDataInfo categoryVideoPage(@RequestBody VideoCategoryPageDTO pageDTO) {
        return videoCategoryService.selectVideoByCategory(pageDTO);
    }

    /**
     * 根据分类推送10条视频
     */
    @GetMapping("/category/pushVideo/{categoryId}")
    public R<?> categoryVideoPage(@PathVariable Long categoryId) {
        return R.ok(videoCategoryService.pushVideoByCategory(categoryId));
    }

}


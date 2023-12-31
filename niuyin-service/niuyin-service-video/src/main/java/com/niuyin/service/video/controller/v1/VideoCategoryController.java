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
        IPage<Video> videoIPage = videoCategoryService.selectVideoByCategory(pageDTO);
        List<Video> records = videoIPage.getRecords();
        if (StringUtils.isNull(records)) {
            return new PageDataInfo();
        }
        List<VideoVO> videoVOList = new ArrayList<>();
        records.forEach(v -> {
            VideoVO videoVO = BeanCopyUtils.copyBean(v, VideoVO.class);
            // 封装点赞数，观看量，评论量
            Integer cacheLikeNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_LIKE_NUM_MAP_KEY, v.getVideoId());
            Integer cacheViewNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_VIEW_NUM_MAP_KEY, v.getVideoId());
            Integer cacheFavoriteNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_FAVORITE_NUM_MAP_KEY, v.getVideoId());
            videoVO.setLikeNum(StringUtils.isNull(cacheLikeNum) ? 0L : cacheLikeNum);
            videoVO.setViewNum(StringUtils.isNull(cacheViewNum) ? 0L : cacheViewNum);
            videoVO.setFavoritesNum(StringUtils.isNull(cacheFavoriteNum) ? 0L : cacheFavoriteNum);
            LambdaQueryWrapper<VideoUserComment> commentQW = new LambdaQueryWrapper<>();
            commentQW.eq(VideoUserComment::getVideoId, v.getVideoId());
            videoVO.setCommentNum(remoteBehaveService.getCommentCountByVideoId(videoVO.getVideoId()).getData());
            // 封装用户信息
            Member poublishUser = remoteMemberService.userInfoById(v.getUserId()).getData();
            videoVO.setUserNickName(StringUtils.isNull(poublishUser) ? null : poublishUser.getNickName());
            videoVO.setUserAvatar(StringUtils.isNull(poublishUser) ? null : poublishUser.getAvatar());
            // 是否关注
            Boolean weatherFollow = remoteSocialService.weatherfollow(v.getUserId()).getData();
            videoVO.setWeatherFollow(!StringUtils.isNull(weatherFollow) && weatherFollow);
            videoVOList.add(videoVO);
        });
        return PageDataInfo.genPageData(videoVOList, videoIPage.getTotal());
    }

}


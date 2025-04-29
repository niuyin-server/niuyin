package com.niuyin.service.search.controller.v1;

import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.dubbo.api.DubboBehaveService;
import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.dubbo.api.DubboVideoService;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.search.dto.PageDTO;
import com.niuyin.model.search.dto.VideoSearchKeywordDTO;
import com.niuyin.model.search.dto.VideoSearchSuggestDTO;
import com.niuyin.service.search.domain.VideoSearchVO;
import com.niuyin.service.search.domain.vo.VideoSearchUserVO;
import com.niuyin.service.search.service.VideoSearchService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * VideoSearchController
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
@RestController
@RequestMapping("/api/v1/video")
public class VideoSearchController {

    @Resource
    private VideoSearchService videoSearchService;

    @DubboReference
    private DubboMemberService dubboMemberService;

    @DubboReference
    private DubboVideoService dubboVideoService;

    @DubboReference
    private DubboBehaveService dubboBehaveService;

    /**
     * 分页搜索视频
     */
    @PostMapping()
    public R<List<VideoSearchUserVO>> searchVideo(@RequestBody VideoSearchKeywordDTO dto) {
        List<VideoSearchVO> videoSearchVOS = videoSearchService.searchVideoFromES(dto);
        if (StringUtils.isNull(videoSearchVOS) || videoSearchVOS.isEmpty()) {
            return R.ok();
        }
        List<VideoSearchUserVO> res = BeanCopyUtils.copyBeanList(videoSearchVOS, VideoSearchUserVO.class);
        // 封装用户，视频点赞量，喜欢量。。。
        res.forEach(v -> {
            // 用户头像
            Member member = dubboMemberService.apiGetById(v.getUserId());
            v.setUserNickName(member.getNickName());
            v.setUserAvatar(member.getAvatar());
            // 图文视频
            v.setImageList(dubboVideoService.apiGetVideoImagesByVideoId(v.getVideoId()));
            // 是否点赞、是否收藏
            v.setWeatherLike(dubboVideoService.apiWeatherLikeVideo(v.getVideoId(), UserContext.getUserId()));
            v.setWeatherFavorite(dubboVideoService.apiWeatherFavoriteVideo(v.getVideoId(), UserContext.getUserId()));
            // 行为数据：点赞数、评论数、收藏数
            v.setLikeNum(dubboBehaveService.apiGetVideoLikeNum(v.getVideoId()));
            v.setCommentNum(dubboBehaveService.apiGetVideoCommentNum(v.getVideoId()));
            v.setFavoritesNum(dubboBehaveService.apiGetVideoFavoriteNum(v.getVideoId()));
            // todo 社交数据、是否关注用户
            v.setWeatherFollow(false);
        });
        return R.ok(res);
    }

    @DeleteMapping("/{videoId}")
    public R<?> deleteVideo(@PathVariable("videoId") String videoId) {
        videoSearchService.deleteVideoDoc(videoId);
        return R.ok();
    }

    /**
     * 牛音热搜
     */
    @PostMapping("/search/hot")
    @Cacheable(value = "hotSearchs", key = "'hotSearchs' + #pageDTO.pageNum + '_' + #pageDTO.pageSize")
    public R<?> getSearchHot(@RequestBody PageDTO pageDTO) {
        return R.ok(videoSearchService.findSearchHot(pageDTO));
    }

    /**
     * 视频搜索建议
     */
    @PostMapping("/suggest")
    public R<?> getVideoSearchSuggest(@RequestBody VideoSearchSuggestDTO videoSearchSuggestDTO) {
        return R.ok(videoSearchService.pushVideoSearchSuggest(videoSearchSuggestDTO));
    }
}

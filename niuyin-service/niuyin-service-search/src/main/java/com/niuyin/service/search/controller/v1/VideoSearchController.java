package com.niuyin.service.search.controller.v1;

import com.niuyin.common.domain.R;
import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.feign.member.RemoteMemberService;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.search.dto.PageDTO;
import com.niuyin.model.search.dto.VideoSearchKeywordDTO;
import com.niuyin.service.search.domain.VideoSearchVO;
import com.niuyin.service.search.domain.vo.VideoSearchUserVO;
import com.niuyin.service.search.service.VideoSearchService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    @Resource
    private RemoteMemberService remoteMemberService;

    /**
     * 分页搜索视频
     *
     * @param dto
     * @return
     * @throws Exception
     */
    @PostMapping()
    public R<List<VideoSearchUserVO>> searchVideo(@RequestBody VideoSearchKeywordDTO dto) throws Exception {
        List<VideoSearchVO> videoSearchVOS = videoSearchService.searchVideoFromES(dto);
        List<VideoSearchUserVO> res = new ArrayList<>();
        // 封装用户，视频点赞量，喜欢量。。。
        videoSearchVOS.forEach(v -> {
            VideoSearchUserVO videoSearchUserVO = BeanCopyUtils.copyBean(v, VideoSearchUserVO.class);
            Member user = remoteMemberService.userInfoById(v.getUserId()).getData();
            videoSearchUserVO.setAvatar(user.getAvatar());
            res.add(videoSearchUserVO);
        });
        return R.ok(res);
    }

    @DeleteMapping("/{videoId}")
    public R<?> deleteVideo(@PathVariable("videoId") String videoId) {
        videoSearchService.deleteVideoDoc(videoId);
        return R.ok();
    }

    /**
     * 热搜标签展示
     *
     * @param pageDTO
     * @return
     */
    @PostMapping("/search/hot")
    @Cacheable(value = "hotSearchs", key = " 'hotSearchs' + #pageDTO.pageNum + '_' + #pageDTO.pageSize")
    public R<?> getSearchHot(@RequestBody PageDTO pageDTO) {
        return R.ok(videoSearchService.findSearchHot(pageDTO));
    }
}

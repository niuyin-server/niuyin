package com.qiniu.service.search.controller.v1;

import com.qiniu.common.domain.R;
import com.qiniu.common.utils.bean.BeanCopyUtils;
import com.qiniu.feign.user.RemoteUserService;
import com.qiniu.model.search.dto.VideoSearchKeywordDTO;
import com.qiniu.model.user.domain.User;
import com.qiniu.service.search.domain.VideoSearchVO;
import com.qiniu.service.search.domain.vo.VideoSearchUserVO;
import com.qiniu.service.search.service.VideoSearchService;
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
    private RemoteUserService remoteUserService;

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
            User user = remoteUserService.userInfoById(v.getUserId()).getData();
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


}

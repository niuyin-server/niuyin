package com.niuyin.feign.behave;

import com.niuyin.common.constant.ServiceNameConstants;
import com.niuyin.common.domain.R;
import com.niuyin.feign.behave.fallback.RemoteBehaveServiceFallback;
import com.niuyin.feign.video.fallback.RemoteVideoServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * RemoteBehaveService
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/4
 **/
@FeignClient(contextId = "remoteBehaveService", value = ServiceNameConstants.BEHAVE_SERVICE, fallbackFactory = RemoteBehaveServiceFallback.class)
public interface RemoteBehaveService {

    /**
     * 获取指定视频评论量
     *
     * @param videoId
     * @return
     */
    @GetMapping("/api/v1/comment/{videoId}")
    R<Long> getCommentCountByVideoId(@PathVariable("videoId") String videoId);

    @DeleteMapping("/api/v1/video/{videoId}")
    R<?> deleteVideoDocumentByVideoId(@PathVariable("videoId") String videoId);

    @DeleteMapping("/{videoId}")
    R<?> deleteVideoLikeRecord(@PathVariable String videoId);

    @DeleteMapping("/{videoId}")
    R<?> deleteVideoFavoriteRecordByVideoId(@PathVariable String videoId);

    /**
     * 是否点赞某视频
     *
     * @param videoId
     * @return
     */
    @GetMapping("/api/v1/like/weather/{videoId}")
    R<Boolean> weatherLike(@PathVariable("videoId") String videoId);

    /**
     * 是否收藏某视频
     *
     * @param videoId
     * @return
     */
    @GetMapping("/api/v1/favorite/weather/{videoId}")
    R<Boolean> weatherFavorite(@PathVariable("videoId") String videoId);
}

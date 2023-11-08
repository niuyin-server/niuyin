package com.qiniu.feign.behave;

import com.qiniu.common.constant.ServiceNameConstants;
import com.qiniu.common.domain.R;
import com.qiniu.feign.video.fallback.RemoteVideoServiceFallback;
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
@FeignClient(contextId = "remoteBehaveService", value = ServiceNameConstants.BEHAVE_SERVICE, fallbackFactory = RemoteVideoServiceFallback.class)
public interface RemoteBehaveService {

    /**
     * 获取指定视频评论量
     *
     * @param videoId
     * @return
     */
    @GetMapping("/api/v1/comment/{videoId}")
    R<Long> getCommentCountByVideoId(@PathVariable("videoId") String videoId);

    @DeleteMapping("/api/v1/video/{vodeoId}")
    R<?> deleteVideoDocumentByVideoId(@PathVariable("videoId") String videoId);

    @DeleteMapping("/{videoId}")
    public R<?> deleteVideoLikeRecord(@PathVariable String videoId);

    @DeleteMapping("/{videoId}")
    public R<?> deleteVideoFavoriteRecordByVideoId(@PathVariable String videoId);
}

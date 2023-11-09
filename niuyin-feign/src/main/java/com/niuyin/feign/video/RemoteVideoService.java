package com.niuyin.feign.video;

import com.niuyin.common.constant.ServiceNameConstants;
import com.niuyin.common.domain.R;
import com.niuyin.feign.video.fallback.RemoteVideoServiceFallback;
import com.niuyin.model.video.domain.Video;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * RemoteVideoService
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/28
 **/
@FeignClient(contextId = "remoteVideoService", value = ServiceNameConstants.VIDEO_SERVICE, fallbackFactory = RemoteVideoServiceFallback.class)
public interface RemoteVideoService {

    @GetMapping("/api/v1/{videoIds}")
    R<List<Video>> queryVideoByVideoIds(@PathVariable("videoIds") List<String> videoIds);

}

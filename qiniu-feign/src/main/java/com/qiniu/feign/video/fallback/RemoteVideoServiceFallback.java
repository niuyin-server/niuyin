package com.qiniu.feign.video.fallback;

import com.qiniu.common.domain.R;
import com.qiniu.feign.video.RemoteVideoService;
import com.qiniu.model.video.domain.Video;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * RemoteVideoServiceFallback
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/28
 **/
@Component
public class RemoteVideoServiceFallback implements FallbackFactory<RemoteVideoService> {

    /**
     * @param throwable
     * @return
     */
    @Override
    public RemoteVideoService create(Throwable throwable) {
        return new RemoteVideoService() {
            @Override
            public R<List<Video>> queryVideoByVideoIds(List<String> videoIds) {
                return R.fail(null);
            }
        };
    }
}

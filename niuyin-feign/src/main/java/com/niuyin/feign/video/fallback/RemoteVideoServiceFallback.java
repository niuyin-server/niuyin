package com.niuyin.feign.video.fallback;

import com.niuyin.feign.video.RemoteVideoService;
import com.niuyin.common.domain.R;
import com.niuyin.model.video.domain.Video;
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

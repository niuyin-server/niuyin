package com.qiniu.feign.behave.fallback;

import com.qiniu.common.domain.R;
import com.qiniu.feign.behave.RemoteBehaveService;
import com.qiniu.feign.video.RemoteVideoService;
import com.qiniu.model.video.domain.Video;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * RemoteBehaveServiceFallback
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/28
 **/
@Component
public class RemoteBehaveServiceFallback implements FallbackFactory<RemoteBehaveService> {

    @Override
    public RemoteBehaveService create(Throwable throwable) {
        return new RemoteBehaveService() {
            @Override
            public R<Long> getCommentCountByVideoId(String videoId) {
                return R.fail(null);
            }

            /**
             * @param videoId
             * @return
             */
            @Override
            public R<?> deleteVideoDocumentByVideoId(String videoId) {

                return R.fail(null);
            }

            /**
             * @param videoId
             * @return
             */
            @Override
            public R<?> deleteVideoLikeRecord(String videoId) {
                return R.fail(null);
            }

            /**
             * @param videoId
             * @return
             */
            @Override
            public R<?> deleteVideoFavoriteRecordByVideoId(String videoId) {
                return R.fail(null);
            }
        };
    }
}

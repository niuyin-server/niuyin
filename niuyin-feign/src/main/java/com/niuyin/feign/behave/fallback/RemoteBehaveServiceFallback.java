package com.niuyin.feign.behave.fallback;

import com.niuyin.common.domain.R;
import com.niuyin.feign.behave.RemoteBehaveService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

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

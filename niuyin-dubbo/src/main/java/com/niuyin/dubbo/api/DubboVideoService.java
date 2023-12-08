package com.niuyin.dubbo.api;

import java.util.List;

/**
 * 服务提供者：video
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/7
 **/
public interface DubboVideoService {

    /**
     * 同步视频标签库
     */
    void apiSyncVideoTagStack(String videoId, List<Long> tagsIds);

}

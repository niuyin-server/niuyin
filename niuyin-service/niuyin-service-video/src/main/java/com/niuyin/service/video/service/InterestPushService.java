package com.niuyin.service.video.service;

import com.niuyin.model.member.domain.Member;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.vo.UserModel;

import java.util.Collection;
import java.util.List;

/**
 * 兴趣推送，根据标签，分类等构建用户模型
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/6
 **/
public interface InterestPushService {

    /**
     * 将视频推入标签库
     */
    void cacheVideoToTagRedis(String videoId, List<Long> tagsIds);

    /**
     * 添加分类库,用于后续随机推送分类视频, todo 一个视频可能有多个分类
     */
    void cacheVideoToCategoryRedis(String videoId, List<Long> categoryIds);

    /**
     * 删除标签内视频
     */
    void deleteVideoFromTagRedis(String videoId, List<Long> tagsIds);

    /**
     * 删除分类库中的视频
     */
    void deleteVideoFromCategoryRedis(Video video, List<Long> categoryIds);

    /**
     * 根据分类id随机推送视频
     */
    Collection<String> listVideoIdByCategoryId(Long categoryId);

    /**
     * 用户模型初始化
     */
    void initUserModel(Long userId, List<Long> tagIds);

    /**
     * 用户模型修改概率 : 可分批次发送
     * 修改场景:
     * 1.观看浏览量到达总时长1/5  +1概率
     * 2.观看浏览量未到总时长1/5 -0.5概率
     * 3.点赞视频  +2概率
     * 4.收藏视频  +3概率
     */
    void updateUserModel(UserModel userModel);

    /**
     * 用于给用户推送视频 -> 兴趣推送
     * 推送 X 视频,包含一条和性别有关
     *
     * @param member 传id和sex
     * @return videoIds
     */
    Collection<String> getVideoIdsByUserModel(Member member);

    /**
     * 根据标签ids获取视频id
     */
    Collection<String> getVideoIdsByTagIds(List<Long> tagIds);

}

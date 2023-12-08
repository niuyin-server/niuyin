package com.niuyin.model.video.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户标签兴趣推荐模型，存储到redis，map
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/6
 **/
@Data
public class UserModel {
    private Long userId;
    private List<UserModelField> models;

    /**
     * 构建用户模型
     *
     * @param userId
     * @param tags
     * @param videoId
     * @param score
     * @return
     */
    public static UserModel buildUserModel(Long userId, List<String> tags, Long videoId, Double score) {
        final UserModel userModel = new UserModel();
        final ArrayList<UserModelField> models = new ArrayList<>();
        userModel.setUserId(userId);
        for (String tag : tags) {
            final UserModelField model = new UserModelField();
            model.setTag(tag);
            model.setVideoId(videoId);
            model.setScore(score);
            models.add(model);
        }
        userModel.setModels(models);
        return userModel;
    }
}

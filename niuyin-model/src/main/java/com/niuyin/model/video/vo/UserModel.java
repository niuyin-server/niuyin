package com.niuyin.model.video.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户标签兴趣推荐模型，存储到redis，map
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/6
 **/
@Data
public class UserModel implements Serializable {
    private static final long serialVersionUID = 2L;
    private Long userId;
    private List<UserModelField> models;

    /**
     * 构建用户模型
     *
     * @param userId
     * @param tags
     * @param score
     * @return
     */
    public static UserModel buildUserModel(Long userId, List<Long> tags, Double score) {
        UserModel userModel = new UserModel();
        List<UserModelField> models = new ArrayList<>();
        userModel.setUserId(userId);
        for (Long tag : tags) {
            UserModelField model = new UserModelField();
            model.setTagId(tag);
            model.setScore(score);
            models.add(model);
        }
        userModel.setModels(models);
        return userModel;
    }
}

package com.niuyin.model.recommend.modal;

import lombok.Data;

/**
 * UserVideoScore
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/27
 **/
@Data
public class UserVideoScore {
    private Long userId;
    private String videoId;
    private String score;
}

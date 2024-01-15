package com.niuyin.model.video.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户模型field
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/6
 **/
@Data
public class UserModelField implements Serializable {
    private static final long serialVersionUID = 785378785L;
    private Long tagId;
    private Double score;
}

package com.niuyin.model.search.dubbo;

import lombok.Data;

import java.io.Serializable;

/**
 * VideoBehaveData
 *
 * @AUTHOR: roydon
 * @DATE: 2024/5/19
 **/
@Data
public class VideoBehaveData implements Serializable {
    private static final long serialVersionUID = 112321L;

    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Long favoriteCount;
}

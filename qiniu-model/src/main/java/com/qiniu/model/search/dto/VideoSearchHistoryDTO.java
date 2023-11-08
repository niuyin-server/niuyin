package com.qiniu.model.search.dto;

import lombok.Data;

/**
 * VideoSearchDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 * 视频搜索记录DTO，传递搜索记录id即可
 **/
@Data
public class VideoSearchHistoryDTO {

    /**
     * 接收搜索历史记录id
     */
    private String id;

}

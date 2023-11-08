package com.qiniu.model.search.dto;

import lombok.Data;
import java.util.Date;

/**
 * VideoSearchWordDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 * es搜索dto
 **/
@Data
public class VideoSearchKeywordDTO {

    /**
     * 关键词
     */
    private String keyword;

    private Integer pageNum;
    private Integer pageSize;

    /**
     * 最小时间
     */
    private Date minBehotTime;

    public int getFromIndex() {
        if (this.pageNum < 1) {
            return 0;
        }
        if (this.pageSize < 1) {
            this.pageSize = 10;
        }
        return this.pageSize * (pageNum - 1);
    }

}

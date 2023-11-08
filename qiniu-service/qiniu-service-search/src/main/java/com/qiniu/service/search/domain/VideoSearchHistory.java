
package com.qiniu.service.search.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 视频搜索记录表
 */
@Data
@Document("video_search_history")
public class VideoSearchHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 搜索词
     */
    private String keyword;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

}

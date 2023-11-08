package com.qiniu.service.search.domain.vo;

import com.qiniu.service.search.domain.VideoSearchVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * VideoSearchUserVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/3
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoSearchUserVO extends VideoSearchVO {
    private String avatar;
    private Long likeNum = 0L;
    private Long favoriteNum = 0L;
    private Long commentNum = 0L;
}

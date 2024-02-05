package com.niuyin.model.notice.vo;

import com.niuyin.model.notice.domain.Notice;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * NoticeVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/16
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class NoticeVO extends Notice {
    private String nickName;
    private String operateAvatar;
    private String videoCoverImage;
}

package com.niuyin.model.behave.vo.app;

import com.niuyin.model.behave.domain.VideoUserComment;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * app端视频父评论
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class AppVideoUserCommentParentVO extends VideoUserComment {

    // 评论者昵称
    private String nickName;
    // 评论者头像
    private String avatar;
    // 子评论数量
    private Long childrenCount;

}

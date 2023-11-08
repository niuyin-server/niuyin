package com.qiniu.model.video.vo;

import com.qiniu.model.video.domain.VideoUserComment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * VideoUserCommentVO
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoUserCommentVO extends VideoUserComment {
    // 评论者昵称
    private String nickName;
    // 评论者头像
    private String avatar;

    // 被回复者id
    private Long replayUserId;
    // 被回复者昵称
    private String replayUserNickName;

    // 子评论，默认二级
    private List<VideoUserCommentVO> children;
}

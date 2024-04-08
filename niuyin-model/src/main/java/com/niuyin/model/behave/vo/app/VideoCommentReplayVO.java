package com.niuyin.model.behave.vo.app;

import com.niuyin.model.behave.domain.VideoUserComment;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * VideoCommentReplayVO
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoCommentReplayVO extends VideoUserComment {
    // 评论者昵称
    private String nickName;
    // 评论者头像
    private String avatar;

    // 被回复者id
    private Long replayUserId;
    // 被回复者昵称
    private String replayUserNickName;

}

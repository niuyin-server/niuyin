package com.niuyin.model.behave.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.niuyin.model.behave.domain.VideoNote;
import com.niuyin.model.video.vo.Author;
import lombok.Data;

/**
 * 视频笔记表(VideoNote)实体类
 *
 * @author roydon
 * @since 2024-05-05 18:51:04
 */
@Data
@TableName("video_note")
public class VideoNoteVO extends VideoNote {

   private Author author; // 作者

}


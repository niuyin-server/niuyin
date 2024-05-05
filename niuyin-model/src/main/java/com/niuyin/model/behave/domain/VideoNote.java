package com.niuyin.model.behave.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 视频笔记表(VideoNote)实体类
 *
 * @author roydon
 * @since 2024-05-05 18:51:04
 */
@Data
@TableName("video_note")
public class VideoNote implements Serializable {
    private static final long serialVersionUID = 136140011238956718L;
    /**
     * 笔记id
     */
    @TableId(value = "note_id", type = IdType.AUTO)
    private Long noteId;
    /**
     * 视频id
     */
    @NotBlank
    private String videoId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 笔记title
     */
    @Size(min = 1, max = 100)
    private String noteTitle;
    /**
     * 笔记内容
     */
    @Size(min = 1)
    private String noteContent;
    /**
     * 状态：0默认1禁止2删除
     *
     * @see com.niuyin.model.behave.enums.VideoNoteDelFlagEnum
     */
    private String delFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private Long createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    private Long updateBy;

    private String remark;


}


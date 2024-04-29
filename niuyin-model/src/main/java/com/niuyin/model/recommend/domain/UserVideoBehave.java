//package com.niuyin.model.recommend.domain;
//
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.fasterxml.jackson.annotation.JsonFormat;
//import lombok.Data;
//
//import java.io.Serializable;
//import java.time.LocalDateTime;
//
///**
// * 用户视频行为表(UserVideoBehave)实体类
// *
// * @author roydon
// * @since 2024-04-27 18:56:47
// */
//@Data
//@TableName("user_video_behave")
//public class UserVideoBehave implements Serializable {
//    private static final long serialVersionUID = 431298952421868019L;
//    /**
//     * id
//     */
//    @TableId(value = "behave_id", type = IdType.AUTO)
//    private Long behaveId;
//    /**
//     * 用户id
//     */
//    private Long userId;
//    /**
//     * 用户行为0无行为1观看2点赞3评论4收藏
//     */
//    private String userBehave;
//    /**
//     * 视频ID
//     */
//    private String videoId;
//    /**
//     * 创建时间
//     */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    private LocalDateTime createTime;
//
//
//}
//

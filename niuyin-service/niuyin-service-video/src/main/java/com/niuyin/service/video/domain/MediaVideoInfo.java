package com.niuyin.service.video.domain;

import lombok.Data;
import ws.schild.jave.info.MultimediaInfo;

/**
 * 视频详情
 *
 * @AUTHOR: roydon
 * @DATE: 2024/2/4
 **/
@Data
public class MediaVideoInfo {

    private String format = null;
    private long duration = -1L;
    private String decoder;
    private int bitRate = -1;
    private float frameRate = -1.0F;
    private Integer width;
    private Integer height;

    public MediaVideoInfo(MultimediaInfo multimediaInfo) {
        this.format = multimediaInfo.getFormat();
        this.duration = multimediaInfo.getDuration();
        if(multimediaInfo.getVideo()!= null){
            this.decoder = multimediaInfo.getVideo().getDecoder();
            this.bitRate = multimediaInfo.getVideo().getBitRate();
            this.frameRate = multimediaInfo.getVideo().getFrameRate();
        }
        if (multimediaInfo.getVideo().getSize() != null) {
            this.width = multimediaInfo.getVideo().getSize().getWidth();
            this.height = multimediaInfo.getVideo().getSize().getHeight();
        }
    }

}

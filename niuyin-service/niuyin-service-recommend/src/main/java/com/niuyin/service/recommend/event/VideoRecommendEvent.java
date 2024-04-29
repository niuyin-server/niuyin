package com.niuyin.service.recommend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class VideoRecommendEvent extends ApplicationEvent {

    private final Long userId;

    public VideoRecommendEvent(Object source,  Long userId) {
        super(source);
        this.userId = userId;
    }
}

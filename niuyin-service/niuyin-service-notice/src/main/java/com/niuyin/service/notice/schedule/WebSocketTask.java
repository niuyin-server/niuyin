package com.niuyin.service.notice.schedule;

import com.alibaba.fastjson2.JSON;
import com.niuyin.model.notice.vo.WebSocketBaseResp;
import com.niuyin.service.notice.controller.v1.WebSocketServer;
import com.niuyin.service.notice.enums.HeartCheckMsgEnums;
import com.niuyin.service.notice.enums.WebSocketMsgType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class WebSocketTask {

    /**
     * 每30秒进行一次websocket心跳检测
     */
//    @Scheduled(fixedRate = 1000 * 30)
    public void wsHeartCheck() {
        int num = 0;
        try {
            String ping = HeartCheckMsgEnums.PING.getInfo();
            WebSocketBaseResp<String> msg = WebSocketBaseResp.build(WebSocketMsgType.HEART_CHECK.getCode(), ping);
            num = WebSocketServer.sendPing(JSON.toJSONString(msg));
        } finally {
            log.info("websocket心跳检测结果，共【{}】个连接", num);
        }
    }
}

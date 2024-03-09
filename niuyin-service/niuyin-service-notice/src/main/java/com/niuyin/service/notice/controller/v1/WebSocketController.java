package com.niuyin.service.notice.controller.v1;

import com.niuyin.common.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.PathParam;

/**
 * WebSocketController
 *
 * @AUTHOR: roydon
 * @DATE: 2024/2/29
 **/
@Slf4j
@RestController
@RequestMapping("/api/v1/ws")
public class WebSocketController {

    @GetMapping("/push")
    public void pushNotice(@PathParam("msg") String msg) {
        WebSocketServer.sendOneMessage(UserContext.getUserId(), msg);
    }

}

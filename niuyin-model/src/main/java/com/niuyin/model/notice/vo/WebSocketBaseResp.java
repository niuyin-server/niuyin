package com.niuyin.model.notice.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocketBaseResp
 *
 * @AUTHOR: roydon
 * @DATE: 2024/3/8
 **/
@NoArgsConstructor
@Data
public class WebSocketBaseResp<T> {

    /**
     * @see com.niuyin.service.notice.enums.WebSocketMsgType
     */
    private String type;
    private T msg;

    public static <T> WebSocketBaseResp<T> build(String type, T msg){
        WebSocketBaseResp<T> res = new WebSocketBaseResp<>();
        res.setType(type);
        res.setMsg(msg);
        return res;
    }

}

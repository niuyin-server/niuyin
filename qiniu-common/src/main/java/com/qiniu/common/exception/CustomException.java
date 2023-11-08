package com.qiniu.common.exception;

import com.qiniu.model.common.enums.HttpCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException {

    private HttpCodeEnum httpCodeEnum;

}

package com.niuyin.common.core.exception;

import com.niuyin.model.common.enums.HttpCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException {

    private HttpCodeEnum httpCodeEnum;

}

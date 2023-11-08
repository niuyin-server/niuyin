package com.qiniu.common.exception;

import com.qiniu.common.domain.R;
import com.qiniu.model.common.enums.HttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileUploadBase;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@Slf4j
@ControllerAdvice  //控制器增强类
public class ExceptionCatch {

    /**
     * 处理不可控异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R<?> exception(Exception e) {
        e.printStackTrace();
        log.error("catch exception:{}", e.getMessage());
        return R.fail(HttpCodeEnum.HAS_ERROR.getCode(), HttpCodeEnum.HAS_ERROR.getMsg());
    }

    /**
     * 处理可控异常  自定义异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public R<?> exception(CustomException e) {
        log.error("catch exception:{}", e.getMessage());
        return R.fail(e.getHttpCodeEnum().getCode(), e.getHttpCodeEnum().getMsg());
    }

    @ExceptionHandler(value = MultipartException.class)
    @ResponseBody
    public R<?> handleBusinessException(MaxUploadSizeExceededException e) {
        log.error("catch exception:{}", e.getMessage());
        String msg;
        if (e.getCause().getCause() instanceof FileUploadBase.FileSizeLimitExceededException) {
            msg = "上传文件过大[单文件大小不得超过1M]";
            log.error("上传文件过大[单文件大小不得超过{}]", e.getMaxUploadSize());
        } else if (e.getCause().getCause() instanceof FileUploadBase.SizeLimitExceededException) {
            msg = "上传文件过大[总上传文件大小不得超过10M]";
            log.error("上传文件过大[单文件大小不得超过{}]", e.getMaxUploadSize());
        } else {
            msg = HttpCodeEnum.FILE_SIZE_ERROR.getMsg();
        }
        return R.fail(HttpCodeEnum.FILE_SIZE_ERROR.getCode(), msg);
    }

}

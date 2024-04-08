package com.niuyin.common.exception;

import com.niuyin.common.domain.R;
import com.niuyin.model.common.enums.HttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileUploadBase;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@Slf4j
@RestControllerAdvice  //控制器增强类
public class ExceptionCatch {

    /**
     * 处理不可控异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Throwable.class)
    public R<?> exception(Throwable e) {
        log.error("catch exception:{}", e);
        return R.fail(HttpCodeEnum.HAS_ERROR.getCode(), e.getMessage());
    }

    /**
     * 处理可控异常  RuntimeException
     *
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public R<?> exception(RuntimeException e) {
        log.error("Catch RuntimeException: {}", e);
        return R.fail(e.getMessage());
    }

    /**
     * 处理可控异常  自定义异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<?> exception(CustomException e) {
        log.error("Catch CustomException: code: {} msg: {}", e.getHttpCodeEnum().getCode(), e.getHttpCodeEnum().getMsg());
        return R.fail(e.getHttpCodeEnum().getCode(), e.getHttpCodeEnum().getMsg());
    }

    /**
     * 处理文件上传异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MultipartException.class)
    public R<?> handleBusinessException(MaxUploadSizeExceededException e) {
        log.error("Catch MultipartException:{}", e.getMessage());
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

    /**
     * 请求体验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return R.fail(message);
    }

}

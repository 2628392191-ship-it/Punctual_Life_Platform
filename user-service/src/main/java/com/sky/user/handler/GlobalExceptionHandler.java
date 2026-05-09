package com.sky.user.handler;

import com.sky.gateway.utils.common.exception.LoginFailedException;
import com.sky.gateway.utils.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoginFailedException.class)
    public Result<?> handleLoginFailedException(LoginFailedException e) {
        log.warn("登录失败: {}", e.getMessage());
        return Result.error(e.getMessage());
    }
}

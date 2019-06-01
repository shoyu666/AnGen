package com.xining.angen.annotation;

/**
 * 校验失败异常
 *
 * @author xining
 * @since 2019/5/23
 */
public class InvalidValidaException extends IllegalArgumentException {
    public InvalidValidaException(String message) {
        super(message);
    }
}

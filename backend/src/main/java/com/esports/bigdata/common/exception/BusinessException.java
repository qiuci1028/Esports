package com.esports.bigdata.common.exception;

import com.esports.bigdata.common.ResultCode;
import lombok.Getter;

/**
 * 业务异常：用于 Service 层抛出，被 GlobalExceptionHandler 统一处理
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.BUSINESS_ERROR.getCode();
    }

    public BusinessException(ResultCode rc) {
        super(rc.getMsg());
        this.code = rc.getCode();
    }

    public BusinessException(ResultCode rc, String detail) {
        super(rc.getMsg() + "：" + detail);
        this.code = rc.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}

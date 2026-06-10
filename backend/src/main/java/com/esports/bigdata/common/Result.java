package com.esports.bigdata.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一返回结构：code / msg / data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String msg;
    private T data;
    private Long ts;

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data, System.currentTimeMillis());
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, msg, null, System.currentTimeMillis());
    }

    public static <T> Result<T> fail(ResultCode rc) {
        return new Result<>(rc.getCode(), rc.getMsg(), null, System.currentTimeMillis());
    }

    public static <T> Result<T> fail(ResultCode rc, String detail) {
        return new Result<>(rc.getCode(), rc.getMsg() + "：" + detail, null, System.currentTimeMillis());
    }
}

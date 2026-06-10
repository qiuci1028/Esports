package com.esports.bigdata.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局错误码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(0, "ok"),

    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或 Token 失效"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    BUSINESS_ERROR(1000, "业务异常"),
    DATA_NOT_FOUND(1001, "数据不存在"),
    DATA_DUPLICATE(1002, "数据已存在"),
    DATA_INVALID(1003, "数据校验失败"),

    THIRD_PARTY_ERROR(2000, "第三方接口异常"),
    RIOT_API_LIMIT(2001, "Riot API 限速"),
    RIOT_API_TIMEOUT(2002, "Riot API 超时");

    private final Integer code;
    private final String msg;
}

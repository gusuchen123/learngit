package com.imooc.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author gusuchen
 * Created in 2018-01-11 16:12
 * Description: API 格式封装，统一的数据返回格式
 * Modified by:
 */
@Data
public class ApiResponse {

    // 自定义请求状态编码
    private int code;

    // 自定义请求响应信息描述
    private String message;

    // 请求目标数据
    private Object data;

    // 是否还有更多信息
    private boolean more;

    public ApiResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 默认是成功的来确认状态
    public ApiResponse() {
        this.code = Status.SUCCESS.getCode();
        this.message = Status.SUCCESS.getStandardMessage();
    }

    // 常用的静态类方法，便于代码复用
    public static ApiResponse ofMessage(Integer code, String message) {
        return new ApiResponse(code, message, null);
    }

    public static ApiResponse ofSuccess(Object data) {
        return new ApiResponse(Status.SUCCESS.getCode(), Status.SUCCESS.getStandardMessage(), data);
    }

    public static ApiResponse ofStatus(Status status) {
        return new ApiResponse(status.getCode(), status.getStandardMessage(), null);
    }

    // 定义常用的结果，使用内部枚举类来表示
    @Getter
    @AllArgsConstructor
    public enum Status {
        SUCCESS(200, "OK"),
        BAD_REQUEST(400, "Bad Request"),
        NOT_FOUND(404, "Not Found"),
        ACCESS_ERROR(403, "Access Error"),
        INTERNAL_SERVER_ERROR(500, "Unknown Internal Error"),
        NOT_VALID_PARAM(40005, "Not Valid Params"),
        NOT_SUPPORTED_OPERATION(40006, "Not Supported Operation"),
        NOT_LOGIN(50000, "Not Login");

        private int code;
        private String standardMessage;

    }
}

package com.imooc.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author gusuchen
 * Created in 2018-01-15 19:28
 * Description: 单个结果集 服务接口通用结构
 * Modified by:
 */
@Data
@AllArgsConstructor
public class ServiceResult<T> {
    private boolean success;

    private String message;

    private T result;

    public ServiceResult(boolean success) {
        this.success = success;
    }

    public ServiceResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static <T> ServiceResult<T> ofSuccess() {
        return new ServiceResult(true);
    }

    public static <T> ServiceResult<T> ofResult(T result) {
        ServiceResult<T> serviceResult = new ServiceResult(true);
        serviceResult.setResult(result);
        return serviceResult;
    }

    public static <T> ServiceResult<T> ofMessage(boolean success, String message) {
        return new ServiceResult(success, message);
    }

    public static <T> ServiceResult<T> notFound() {
        return new ServiceResult(false, Message.NOT_FOUND.getValue());
    }

    public enum Message {
        NOT_FOUND("Not Found Resource !"),
        NOT_LOGIN("User Not Login");

        @Getter public String value;

        Message(String value) {
            this.value = value;
        }

    }
}

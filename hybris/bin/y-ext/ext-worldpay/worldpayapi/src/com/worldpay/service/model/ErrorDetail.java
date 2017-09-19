package com.worldpay.service.model;

import java.io.Serializable;

/**
 * POJO representation of the Error details
 */
public class ErrorDetail implements Serializable {

    private String code;
    private String message;

    public ErrorDetail(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ErrorDetail [code=" + code + ", message=" + message + "]";
    }
}

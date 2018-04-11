package com.worldpay.service.response;

import com.worldpay.service.model.ErrorDetail;

/**
 * Template implementation of a {@link ServiceResponse} providing order code and {@link ErrorDetail}
 */
public abstract class AbstractServiceResponse implements ServiceResponse {

    private String orderCode;
    private ErrorDetail errorDetail;
    private String cookie;

    @Override
    public String getOrderCode() {
        return orderCode;
    }

    @Override
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    @Override
    public ErrorDetail getErrorDetail() {
        return errorDetail;
    }

    @Override
    public void setError(ErrorDetail errorDetail) {
        this.errorDetail = errorDetail;
    }

    @Override
    public boolean isError() {
        return errorDetail != null;
    }

    @Override
    public String getCookie() {
        return cookie;
    }

    @Override
    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}

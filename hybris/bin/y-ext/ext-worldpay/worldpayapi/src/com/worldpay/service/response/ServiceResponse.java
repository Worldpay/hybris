package com.worldpay.service.response;

import com.worldpay.service.model.ErrorDetail;

/**
 * Interface representation of a Service Response that will be returned from the calls through to Worldpay.
 * <p/>
 * <p>Actual implementation must at least determine if there are errors and populate the {@link ErrorDetail} or provide the order code</p>
 */
public interface ServiceResponse {

    String getOrderCode();

    void setOrderCode(String orderCode);

    ErrorDetail getErrorDetail();

    void setError(ErrorDetail errorDetail);

    boolean isError();

    String getCookie();

    void setCookie(String cookie);
}

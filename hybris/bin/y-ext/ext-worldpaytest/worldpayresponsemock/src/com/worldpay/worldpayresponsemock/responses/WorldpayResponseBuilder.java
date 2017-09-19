package com.worldpay.worldpayresponsemock.responses;

import com.worldpay.internal.model.PaymentService;

import javax.servlet.http.HttpServletRequest;

/**
 * Building Worldpay response
 */
public interface WorldpayResponseBuilder {

    /**
     *
     * @param request
     * @param httpServletRequest
     * @return
     */
    PaymentService buildRedirectResponse(PaymentService request, HttpServletRequest httpServletRequest);
}

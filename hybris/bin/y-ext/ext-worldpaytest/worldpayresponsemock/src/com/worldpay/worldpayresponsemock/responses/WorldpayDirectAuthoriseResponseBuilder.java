package com.worldpay.worldpayresponsemock.responses;

import com.worldpay.internal.model.PaymentService;

/**
 * Building direct authorise response from Worldpay
 */
public interface WorldpayDirectAuthoriseResponseBuilder {

    /**
     *
     * @param request
     * @return
     */
    PaymentService buildDirectResponse(PaymentService request);
}

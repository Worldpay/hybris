package com.worldpay.worldpayresponsemock.responses;

import com.worldpay.internal.model.PaymentService;

/**
 * Building capture response
 */
public interface WorldpayCaptureResponseBuilder {

    /**
     * Building capture response
     * @param request
     * @return
     */
    PaymentService buildCaptureResponse(PaymentService request);
}

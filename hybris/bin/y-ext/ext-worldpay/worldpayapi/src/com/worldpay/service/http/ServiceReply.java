package com.worldpay.service.http;

import com.worldpay.internal.model.PaymentService;

/**
 * POJO representation of the ServiceReply returned from all service calls through to Worldpay
 */
public class ServiceReply {

    private PaymentService paymentService;
    private String cookie;

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(final String cookie) {
        this.cookie = cookie;
    }
}

package com.worldpay.service.request;

import com.worldpay.service.model.MerchantInfo;

public class SecondThreeDSecurePaymentRequest extends AbstractServiceRequest {

    private String sessionId;

    /**
     * Constructor for the all fields
     *
     * @param merchantInfo
     * @param orderCode
     * @param sessionId
     */
    public SecondThreeDSecurePaymentRequest(final MerchantInfo merchantInfo, final String orderCode, final String sessionId) {
        super(merchantInfo, orderCode);
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "SecondThreeDSecurePaymentRequest{" +
                "sessionId='" + sessionId + '\'' +
                '}';
    }
}

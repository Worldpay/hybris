package com.worldpay.service.response;

import com.worldpay.data.PaymentReply;

/**
 * This class represents the details that are passed back from a call to
 * {@link com.worldpay.service.WorldpayServiceGateway#redirectAuthorise(com.worldpay.service.request.RedirectAuthoriseServiceRequest) redirectAuthorise()} in the
 * {@link com.worldpay.service.WorldpayServiceGateway}
 * <p/>
 * <p>On top of the standard parameters it provides a {@link com.worldpay.service.model.RedirectReference} with details of the url that the user should be redirected to in order to take payment</p>
 */
public class SecondThreeDSecurePaymentServiceResponse extends AbstractServiceResponse {

    private String merchantCode;
    private String version;
    private PaymentReply paymentReply;

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(final String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public PaymentReply getPaymentReply() {
        return paymentReply;
    }

    public void setPaymentReply(final PaymentReply paymentReply) {
        this.paymentReply = paymentReply;
    }

    @Override
    public String toString() {
        return "SecondThreeDSecurePaymentServiceResponse{" +
            "merchantCode='" + merchantCode + '\'' +
            ", version='" + version + '\'' +
            ", paymentReply=" + paymentReply +
            '}';
    }
}

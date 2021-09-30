package com.worldpay.service.response;

import com.worldpay.data.PaymentReply;
import com.worldpay.data.RedirectReference;

/**
 * This class represents the details that are passed back from a call to {@link com.worldpay.service.WorldpayServiceGateway#orderInquiry(com.worldpay.service.request.AbstractServiceRequest) orderInquiry()} in the
 * {@link com.worldpay.service.WorldpayServiceGateway}
 * <p/>
 * <p>On top of the standard parameters it provides a {@link PaymentReply} with details of the latest status of the payment</p>
 */
public class OrderInquiryServiceResponse extends AbstractServiceResponse {

    private PaymentReply paymentReply;
    private RedirectReference reference;

    public PaymentReply getPaymentReply() {
        return paymentReply;
    }

    public void setPaymentReply(PaymentReply paymentReply) {
        this.paymentReply = paymentReply;
    }

    public void setReference(final RedirectReference reference) {
        this.reference = reference;
    }

    public RedirectReference getReference() {
        return reference;
    }
}

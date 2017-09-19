package com.worldpay.service.model.token;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.CreateToken;
import com.worldpay.internal.model.PaymentTokenCreate;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

public class CardTokenRequest implements InternalModelTransformer, Serializable {

    private TokenRequest tokenRequest;
    private String authenticatedShopperId;
    private Payment payment;

    public CardTokenRequest(final String authenticatedShopperId, final TokenRequest tokenRequest, final Payment payment) {
        this.tokenRequest = tokenRequest;
        this.authenticatedShopperId = authenticatedShopperId;
        this.payment = payment;
    }

    @Override
    public InternalModelObject transformToInternalModel() throws WorldpayModelTransformationException {
        final PaymentTokenCreate paymentTokenCreate = new PaymentTokenCreate();
        if (authenticatedShopperId != null) {
            paymentTokenCreate.setAuthenticatedShopperID(authenticatedShopperId);
        }

        if (tokenRequest != null) {
            paymentTokenCreate.setCreateToken((CreateToken) tokenRequest.transformToInternalModel());
        }

        paymentTokenCreate.getPaymentInstrumentOrCSEDATA().add(payment.transformToInternalModel());
        return paymentTokenCreate;
    }

    public TokenRequest getTokenRequest() {
        return tokenRequest;
    }

    public String getAuthenticatedShopperId() {
        return authenticatedShopperId;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setTokenRequest(final TokenRequest tokenRequest) {
        this.tokenRequest = tokenRequest;
    }

    public void setAuthenticatedShopperId(final String authenticatedShopperId) {
        this.authenticatedShopperId = authenticatedShopperId;
    }

    public void setPayment(final Payment payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "CardTokenRequest{" +
                "tokenRequest=" + tokenRequest +
                ", authenticatedShopperId='" + authenticatedShopperId + '\'' +
                ", payment=" + payment +
                '}';
    }
}

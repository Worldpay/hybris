package com.worldpay.service.model.token;


import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.AuthenticatedShopperID;
import com.worldpay.internal.model.CreateToken;
import com.worldpay.internal.model.PaymentTokenCreate;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.StoredCredentials;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;
import java.util.Optional;

public class CardTokenRequest implements InternalModelTransformer, Serializable {

    private TokenRequest tokenRequest;
    private String authenticatedShopperId;
    private Payment payment;
    private StoredCredentials storedCredentials;

    public CardTokenRequest(final TokenRequest tokenRequest, final String authenticatedShopperId, final Payment payment, final StoredCredentials storedCredentials) {
        this.tokenRequest = tokenRequest;
        this.authenticatedShopperId = authenticatedShopperId;
        this.payment = payment;
        this.storedCredentials = storedCredentials;
    }

    public CardTokenRequest(final TokenRequest tokenRequest, final String authenticatedShopperId, final Payment payment) {
        this.tokenRequest = tokenRequest;
        this.authenticatedShopperId = authenticatedShopperId;
        this.payment = payment;
    }

    public CardTokenRequest(final TokenRequest tokenRequest, final Payment payment) {
        this.tokenRequest = tokenRequest;
        this.payment = payment;
    }

    @Override
    public InternalModelObject transformToInternalModel() throws WorldpayModelTransformationException {
        final PaymentTokenCreate paymentTokenCreate = new PaymentTokenCreate();

        Optional.ofNullable(authenticatedShopperId)
            .map(shopperId -> {
                final AuthenticatedShopperID intAuthenticatedShopperID = new AuthenticatedShopperID();
                intAuthenticatedShopperID.setvalue(shopperId);
                return intAuthenticatedShopperID;
            })
            .ifPresent(paymentTokenCreate::setAuthenticatedShopperID);

        Optional.ofNullable(tokenRequest)
            .map(TokenRequest::transformToInternalModel)
            .map(CreateToken.class::cast)
            .ifPresent(paymentTokenCreate::setCreateToken);

        Optional.ofNullable(storedCredentials)
            .map(StoredCredentials::transformToInternalModel)
            .ifPresent(paymentTokenCreate::setStoredCredentials);

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

    public StoredCredentials getStoredCredentials() {
        return storedCredentials;
    }

    public void setStoredCredentials(final StoredCredentials storedCredentials) {
        this.storedCredentials = storedCredentials;
    }

    @Override
    public String toString() {
        return "CardTokenRequest{" +
            "tokenRequest=" + tokenRequest +
            ", authenticatedShopperId='" + authenticatedShopperId + '\'' +
            ", payment=" + payment +
            ", storedCredentials=" + storedCredentials +
            '}';
    }
}

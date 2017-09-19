package com.worldpay.service.model.token;

import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.PaymentTokenDelete;
import com.worldpay.internal.model.TokenReason;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

public class DeleteTokenRequest implements InternalModelTransformer, Serializable {

    private String authenticatedShopperID;
    private String paymentTokenID;
    private TokenRequest tokenRequest;

    public DeleteTokenRequest(final String paymentTokenID, final String authenticatedShopperID, final TokenRequest tokenRequest) {
        this.authenticatedShopperID = authenticatedShopperID;
        this.tokenRequest = tokenRequest;
        this.paymentTokenID = paymentTokenID;
    }

    @Override
    public InternalModelObject transformToInternalModel() {
        final PaymentTokenDelete intPaymentTokenDelete = new PaymentTokenDelete();

        intPaymentTokenDelete.setAuthenticatedShopperID(authenticatedShopperID);
        intPaymentTokenDelete.setPaymentTokenID(paymentTokenID);
        intPaymentTokenDelete.setTokenEventReference(tokenRequest.getTokenEventReference());

        final TokenReason tokenReason = new TokenReason();
        tokenReason.setvalue(tokenRequest.getTokenReason());
        intPaymentTokenDelete.setTokenReason(tokenReason);

        return intPaymentTokenDelete;
    }

    public String getAuthenticatedShopperID() {
        return authenticatedShopperID;
    }

    public void setAuthenticatedShopperID(final String authenticatedShopperID) {
        this.authenticatedShopperID = authenticatedShopperID;
    }

    public TokenRequest getTokenRequest() {
        return tokenRequest;
    }

    public void setTokenReason(final TokenRequest tokenRequest) {
        this.tokenRequest = tokenRequest;
    }

    public String getPaymentTokenID() {
        return paymentTokenID;
    }

    public void setPaymentTokenID(final String paymentTokenID) {
        this.paymentTokenID = paymentTokenID;
    }

}

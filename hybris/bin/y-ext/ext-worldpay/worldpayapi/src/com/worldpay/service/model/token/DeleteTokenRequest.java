package com.worldpay.service.model.token;

import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.PaymentTokenDelete;
import com.worldpay.internal.model.PaymentTokenID;
import com.worldpay.internal.model.TokenReason;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

public class DeleteTokenRequest implements InternalModelTransformer, Serializable {

    private String authenticatedShopperId;
    private String paymentTokenId;
    private TokenRequest tokenRequest;

    public DeleteTokenRequest(final String paymentTokenId, final String authenticatedShopperId, final TokenRequest tokenRequest) {
        this.authenticatedShopperId = authenticatedShopperId;
        this.tokenRequest = tokenRequest;
        this.paymentTokenId = paymentTokenId;
    }

    @Override
    public InternalModelObject transformToInternalModel() {
        final PaymentTokenDelete intPaymentTokenDelete = new PaymentTokenDelete();
        if (tokenRequest.isMerchantToken()) {
            intPaymentTokenDelete.setTokenScope("merchant");
        }
        intPaymentTokenDelete.setAuthenticatedShopperID(authenticatedShopperId);

        final PaymentTokenID paymentTokenIDWrapper = new PaymentTokenID();
        paymentTokenIDWrapper.setvalue(paymentTokenId);
        intPaymentTokenDelete.setPaymentTokenID(paymentTokenIDWrapper);
        intPaymentTokenDelete.setTokenEventReference(tokenRequest.getTokenEventReference());

        final TokenReason tokenReason = new TokenReason();
        tokenReason.setvalue(tokenRequest.getTokenReason());
        intPaymentTokenDelete.setTokenReason(tokenReason);

        return intPaymentTokenDelete;
    }

    public String getAuthenticatedShopperId() {
        return authenticatedShopperId;
    }

    public void setAuthenticatedShopperId(final String authenticatedShopperId) {
        this.authenticatedShopperId = authenticatedShopperId;
    }

    public TokenRequest getTokenRequest() {
        return tokenRequest;
    }

    public void setTokenReason(final TokenRequest tokenRequest) {
        this.tokenRequest = tokenRequest;
    }

    public String getPaymentTokenId() {
        return paymentTokenId;
    }

    public void setPaymentTokenId(final String paymentTokenId) {
        this.paymentTokenId = paymentTokenId;
    }

}

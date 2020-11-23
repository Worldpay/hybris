package com.worldpay.service.model.token;

import com.worldpay.service.model.ErrorDetail;
import com.worldpay.service.model.payment.Card;

import java.io.Serializable;

public class TokenReply implements Serializable {

    private String authenticatedShopperID;
    private String tokenEventReference;
    private String tokenReason;
    private TokenDetails tokenDetails;
    private Card paymentInstrument;
    private ErrorDetail error;

    public String getAuthenticatedShopperID() {
        return authenticatedShopperID;
    }

    public void setAuthenticatedShopperID(String authenticatedShopperID) {
        this.authenticatedShopperID = authenticatedShopperID;
    }

    public String getTokenEventReference() {
        return tokenEventReference;
    }

    public void setTokenEventReference(String tokenEventReference) {
        this.tokenEventReference = tokenEventReference;
    }

    public String getTokenReason() {
        return tokenReason;
    }

    public void setTokenReason(String tokenReason) {
        this.tokenReason = tokenReason;
    }

    public TokenDetails getTokenDetails() {
        return tokenDetails;
    }

    public void setTokenDetails(final TokenDetails tokenDetails) {
        this.tokenDetails = tokenDetails;
    }

    public Card getPaymentInstrument() {
        return paymentInstrument;
    }

    public void setPaymentInstrument(final Card paymentInstrument) {
        this.paymentInstrument = paymentInstrument;
    }

    public ErrorDetail getError() {
        return error;
    }

    public void setError(final ErrorDetail error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "TokenReply{" +
            "authenticatedShopperID='" + authenticatedShopperID + '\'' +
            ", tokenEventReference='" + tokenEventReference + '\'' +
            ", tokenReason='" + tokenReason + '\'' +
            ", tokenDetails=" + tokenDetails +
            ", paymentInstrument=" + paymentInstrument +
            ", error=" + error +
            '}';
    }
}

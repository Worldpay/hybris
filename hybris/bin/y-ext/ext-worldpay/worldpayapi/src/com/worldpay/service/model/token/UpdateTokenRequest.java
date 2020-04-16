package com.worldpay.service.model.token;

import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.PaymentInstrument;
import com.worldpay.internal.model.PaymentTokenID;
import com.worldpay.internal.model.PaymentTokenUpdate;
import com.worldpay.internal.model.TokenReason;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

public class UpdateTokenRequest implements InternalModelTransformer, Serializable {

    private String paymentTokenId;
    private String authenticatedShopperID;
    private CardDetails cardDetails;
    private TokenRequest tokenRequest;
    private boolean merchantToken;

    public UpdateTokenRequest(final String paymentTokenId, final String authenticatedShopperID, final CardDetails cardDetails, final TokenRequest tokenRequest, final boolean merchantToken) {
        this.paymentTokenId = paymentTokenId;
        this.authenticatedShopperID = authenticatedShopperID;
        this.cardDetails = cardDetails;
        this.tokenRequest = tokenRequest;
        this.merchantToken = merchantToken;
    }

    @Override
    public InternalModelObject transformToInternalModel() {
        final PaymentTokenUpdate intPaymentTokenUpdate = new PaymentTokenUpdate();
        if (merchantToken) {
            intPaymentTokenUpdate.setTokenScope("merchant");
            intPaymentTokenUpdate.setAuthenticatedShopperID(null);
        } else {
            intPaymentTokenUpdate.setAuthenticatedShopperID(authenticatedShopperID);
        }

        final PaymentTokenID paymentTokenIDWrapper = new PaymentTokenID();
        paymentTokenIDWrapper.setvalue(paymentTokenId);
        intPaymentTokenUpdate.setPaymentTokenID(paymentTokenIDWrapper);
        intPaymentTokenUpdate.setTokenEventReference(tokenRequest.getTokenEventReference());

        final PaymentInstrument intPaymentInstrument = new PaymentInstrument();
        final com.worldpay.internal.model.CardDetails intCardDetails = (com.worldpay.internal.model.CardDetails) cardDetails.transformToInternalModel();
        intPaymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().add(intCardDetails);
        intPaymentTokenUpdate.setPaymentInstrument(intPaymentInstrument);
        final TokenReason tokenReason = new TokenReason();
        tokenReason.setvalue(tokenRequest.getTokenReason());
        intPaymentTokenUpdate.setTokenReason(tokenReason);

        return intPaymentTokenUpdate;
    }

    public TokenRequest getTokenRequest() {
        return tokenRequest;
    }

    public String getPaymentTokenId() {
        return paymentTokenId;
    }

    public void setAuthenticatedShopperID(String authenticatedShopperID) {
        this.authenticatedShopperID = authenticatedShopperID;
    }

    public void setTokenRequest(TokenRequest tokenRequest) {
        this.tokenRequest = tokenRequest;
    }

    public void setPaymentTokenId(String paymentTokenId) {
        this.paymentTokenId = paymentTokenId;
    }

    public String getAuthenticatedShopperID() {
        return authenticatedShopperID;
    }

    public void setCardDetails(CardDetails cardDetails) {
        this.cardDetails = cardDetails;
    }

    public CardDetails getCardDetails() {
        return cardDetails;
    }

    public boolean isMerchantToken() {
        return merchantToken;
    }

    public void setMerchantToken(final boolean merchantToken) {
        this.merchantToken = merchantToken;
    }
}

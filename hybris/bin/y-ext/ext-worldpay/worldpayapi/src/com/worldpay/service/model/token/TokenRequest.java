package com.worldpay.service.model.token;

import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.CreateToken;
import com.worldpay.internal.model.TokenReason;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

public class TokenRequest implements InternalModelTransformer, Serializable {

    private String tokenEventReference;
    private String tokenReason;
    private boolean merchantToken;

    public TokenRequest(final String tokenEventReference, final String tokenReason) {
        this.tokenEventReference = tokenEventReference;
        this.tokenReason = tokenReason;
        this.merchantToken = false;
    }

    public TokenRequest(final String tokenEventReference, final String tokenReason, final boolean merchantToken) {
        this.tokenEventReference = tokenEventReference;
        this.tokenReason = tokenReason;
        this.merchantToken = merchantToken;
    }

    @Override
    public InternalModelObject transformToInternalModel() {
        final CreateToken intCreateToken = new CreateToken();
        if (tokenReason != null) {
            final TokenReason intTokenReason = new TokenReason();
            intTokenReason.setvalue(tokenReason);
            intCreateToken.setTokenReason(intTokenReason);
        }
        intCreateToken.setTokenEventReference(tokenEventReference);
        if (merchantToken) {
            intCreateToken.setTokenScope("merchant");
        } else {
            intCreateToken.setTokenScope("shopper");
        }
        return intCreateToken;
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

    public void setTokenReason(final String tokenReason) {
        this.tokenReason = tokenReason;
    }

    public boolean isMerchantToken() {
        return merchantToken;
    }

    public void setMerchantToken(final boolean merchantToken) {
        this.merchantToken = merchantToken;
    }
}

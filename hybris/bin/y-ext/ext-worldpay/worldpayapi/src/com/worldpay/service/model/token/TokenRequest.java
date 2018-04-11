package com.worldpay.service.model.token;

import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.CreateToken;
import com.worldpay.internal.model.TokenReason;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

public class TokenRequest implements InternalModelTransformer, Serializable {

    private String tokenEventReference;
    private String tokenReason;

    public TokenRequest(final String tokenEventReference, final String tokenReason) {
        this.tokenEventReference = tokenEventReference;
        this.tokenReason = tokenReason;
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
}

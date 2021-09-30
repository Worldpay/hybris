package com.worldpay.service.response;

import com.worldpay.data.token.UpdateTokenReply;

public class UpdateTokenResponse extends AbstractServiceResponse {

    private UpdateTokenReply updateTokenReply;

    public UpdateTokenReply getUpdateTokenReply() {
        return updateTokenReply;
    }

    public void setUpdateTokenReply(final UpdateTokenReply updateTokenReply) {
        this.updateTokenReply = updateTokenReply;
    }
}

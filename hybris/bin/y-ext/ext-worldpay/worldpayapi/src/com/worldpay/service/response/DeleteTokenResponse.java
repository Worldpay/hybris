package com.worldpay.service.response;

import com.worldpay.data.token.DeleteTokenReply;

/**
 * Object representing the response of a delete token request.
 */
public class DeleteTokenResponse extends AbstractServiceResponse {

    private DeleteTokenReply deleteTokenReply;

    public DeleteTokenReply getDeleteTokenReply() {
        return deleteTokenReply;
    }

    public void setDeleteTokenResponse(DeleteTokenReply deleteTokenReply) {
        this.deleteTokenReply = deleteTokenReply;
    }
}

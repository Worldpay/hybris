package com.worldpay.service.response;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.data.token.TokenReply;
import com.worldpay.service.request.CreateTokenServiceRequest;

/**
 * This class represents the details that are passed back from a call to {@link WorldpayServiceGateway#createToken(CreateTokenServiceRequest)} in the
 * WorldpayServiceGateway
 *
 */
public class CreateTokenResponse extends AbstractServiceResponse {

    private TokenReply token;

    public TokenReply getToken() {
        return token;
    }

    public void setToken(final TokenReply token) {
        this.token = token;
    }
}

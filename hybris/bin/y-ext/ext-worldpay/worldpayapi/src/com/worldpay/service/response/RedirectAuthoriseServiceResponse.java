package com.worldpay.service.response;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.RedirectReference;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;

/**
 * This class represents the details that are passed back from a call to {@link WorldpayServiceGateway#redirectAuthorise(RedirectAuthoriseServiceRequest) redirectAuthorise()} in the
 * WorldpayServiceGateway
 * <p/>
 * <p>On top of the standard parameters it provides a {@link RedirectReference} with details of the url that the user should be redirected to in order to take payment</p>
 */
public class RedirectAuthoriseServiceResponse extends AbstractServiceResponse {

    private RedirectReference redirectReference;
    private TokenReply token;

    public RedirectReference getRedirectReference() {
        return redirectReference;
    }

    public void setRedirectReference(RedirectReference redirectReference) {
        this.redirectReference = redirectReference;
    }

    public TokenReply getToken() {
        return token;
    }

    public void setToken(TokenReply token) {
        this.token = token;
    }
}

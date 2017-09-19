package com.worldpay.service.response;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.request.AuthorisationCodeServiceRequest;

/**
 * This class represents the details that are passed back from a call to {@link WorldpayServiceGateway#authorisationCode(AuthorisationCodeServiceRequest) authorisationCode()} in the
 * WorldpayServiceGateway
 * <p/>
 * <p>On top of the standard parameters it provides the authorisation code</p>
 */
public class AuthorisationCodeServiceResponse extends AbstractServiceResponse {

    private String authorisationCode;

    public String getAuthorisationCode() {
        return authorisationCode;
    }

    public void setAuthorisationCode(String authorisationCode) {
        this.authorisationCode = authorisationCode;
    }
}

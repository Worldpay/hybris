package com.worldpay.service.request;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.MerchantInfo;

/**
 * This class represents the details that must be passed to a call to {@link WorldpayServiceGateway#authorisationCode(AuthorisationCodeServiceRequest) authorisationCode()} in the
 * WorldpayServiceGateway
 * <p/>
 * <p>On top of the standard parameters it provides the authorisation code that needs to be sent</p>
 */
public class AuthorisationCodeServiceRequest extends AbstractServiceRequest {

    private String authorisationCode;

    protected AuthorisationCodeServiceRequest(WorldpayConfig config, MerchantInfo merchantInfo, String orderCode) {
        super(config, merchantInfo, orderCode);
    }

    /**
     * Static convenience method for creating an instance of the AuthorisationCodeServiceRequest
     *
     * @param config            worldpayConfig to be used in the Worldpay call
     * @param merch             merchantInfo to be used in the Worldpay call
     * @param orderCode         orderCode to be used in the Worldpay call
     * @param authorisationCode authorisationCode to be used in the Worldpay call
     * @return new instance of the AuthorisationCodeServiceRequest initialised with input parameters
     */
    public static AuthorisationCodeServiceRequest createAuthorisationCodeRequest(WorldpayConfig config, MerchantInfo merch, String orderCode, String authorisationCode) {
        if (config == null || merch == null || orderCode == null || authorisationCode == null) {
            throw new IllegalArgumentException("WorldpayConfig, MerchantInfo, Order Code and Authorisation Code cannot be null");
        }
        AuthorisationCodeServiceRequest authorisationCodeRequest = new AuthorisationCodeServiceRequest(config, merch, orderCode);
        authorisationCodeRequest.setAuthorisationCode(authorisationCode);

        return authorisationCodeRequest;
    }

    public String getAuthorisationCode() {
        return authorisationCode;
    }

    public void setAuthorisationCode(String authorisationCode) {
        this.authorisationCode = authorisationCode;
    }
}
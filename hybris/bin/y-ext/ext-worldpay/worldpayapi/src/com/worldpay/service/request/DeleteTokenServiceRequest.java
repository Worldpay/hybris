package com.worldpay.service.request;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.token.DeleteTokenRequest;
import com.worldpay.service.model.token.TokenRequest;

/**
 * This class represents the details that must be passed to a call to {@link WorldpayServiceGateway#deleteToken(DeleteTokenServiceRequest)} in the WorldpayServiceGateway
 * <p/>
 * <p>It sets the relevant details into the Delete Token Request object that are required to be sent</p>
 */
public class DeleteTokenServiceRequest extends AbstractServiceRequest {

    private DeleteTokenRequest deleteTokenRequest;

    /**
     * Default constructor that takes the full list of fields
     *  @param merchantInfo
     * @param orderCode
     */
    protected DeleteTokenServiceRequest(MerchantInfo merchantInfo, String orderCode) {
        super(merchantInfo, orderCode);
    }

    /**
     * Static convenience method for creating an instance of the DeleteTokenServiceRequest
     *
     * @param merchantInfo           merchantInfo to be used in the Worldpay call
     * @param authenticatedShopperId unique reference for the customer using the payment method
     * @param paymentTokenId         the payment token id to update
     * @param tokenRequest           contains the information to request a new token
     * @return new instance of the UpdateTokenServiceRequest initialised with input parameters
     */
    public static DeleteTokenServiceRequest deleteTokenRequest(final MerchantInfo merchantInfo, final String authenticatedShopperId, final String paymentTokenId, final TokenRequest tokenRequest) {
        checkParameters("DeleteTokenServiceRequest", merchantInfo);

        final DeleteTokenServiceRequest request = new DeleteTokenServiceRequest(merchantInfo, null);
        request.setDeleteTokenRequest(new DeleteTokenRequest(paymentTokenId, authenticatedShopperId, tokenRequest));
        return request;
    }


    public DeleteTokenRequest getDeleteTokenRequest() {
        return deleteTokenRequest;
    }

    public void setDeleteTokenRequest(DeleteTokenRequest deleteTokenRequest) {
        this.deleteTokenRequest = deleteTokenRequest;
    }
}

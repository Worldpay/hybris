package com.worldpay.service.request;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.model.token.UpdateTokenRequest;


/**
 * This class represents the details that must be passed to a call to {@link WorldpayServiceGateway#updateToken(UpdateTokenServiceRequest)} in the WorldpayServiceGateway
 * <p/>
 * <p>It sets the relevant details into the Card Token Request object that are required to be sent</p>
 */
public class UpdateTokenServiceRequest extends AbstractServiceRequest {

    private UpdateTokenRequest updateTokenRequest;

    /**
     * Instantiates a new Update token service request.
     *
     * @param merchantInfo the merchant info
     * @param orderCode    the order code
     */
    public UpdateTokenServiceRequest(final MerchantInfo merchantInfo, final String orderCode) {
        super(merchantInfo, orderCode);
    }

    /**
     * Static convenience method for creating an instance of the UpdateTokenServiceRequest with shopper scope
     *
     * @param merchantInfo           merchantInfo to be used in the Worldpay call
     * @param authenticatedShopperId unique reference for the customer using the payment method
     * @param paymentTokenId         the payment token id to update
     * @param tokenRequest           contains the information to request a new token
     * @param cardDetails            payment to be used in the Worldpay call
     * @return new instance of the UpdateTokenServiceRequest initialised with input parameters
     */
    public static UpdateTokenServiceRequest updateTokenRequestWithShopperScope(final MerchantInfo merchantInfo, final String authenticatedShopperId, final String paymentTokenId, final TokenRequest tokenRequest, final CardDetails cardDetails) {
        checkParameters("UpdateTokenServiceRequest", merchantInfo, cardDetails);

        final UpdateTokenServiceRequest request = new UpdateTokenServiceRequest(merchantInfo, null);
        request.setUpdateTokenRequest(new UpdateTokenRequest(paymentTokenId, authenticatedShopperId, cardDetails, tokenRequest, false));
        return request;
    }

    /**
     * Static convenience method for creating an instance of the UpdateTokenServiceRequest with merchant scope
     *
     * @param merchantInfo           merchantInfo to be used in the Worldpay call
     * @param authenticatedShopperId unique reference for the customer using the payment method
     * @param paymentTokenId         the payment token id to update
     * @param tokenRequest           contains the information to request a new token
     * @param cardDetails            payment to be used in the Worldpay call
     * @return new instance of the UpdateTokenServiceRequest initialised with input parameters
     */
    public static UpdateTokenServiceRequest updateTokenRequestWithMerchantScope(final MerchantInfo merchantInfo, final String authenticatedShopperId, final String paymentTokenId, final TokenRequest tokenRequest, final CardDetails cardDetails) {
        checkParameters("UpdateTokenServiceRequest", merchantInfo, cardDetails);

        final UpdateTokenServiceRequest request = new UpdateTokenServiceRequest(merchantInfo, null);
        request.setUpdateTokenRequest(new UpdateTokenRequest(paymentTokenId, authenticatedShopperId, cardDetails, tokenRequest, true));
        return request;
    }

    public UpdateTokenRequest getUpdateTokenRequest() {
        return updateTokenRequest;
    }

    public void setUpdateTokenRequest(final UpdateTokenRequest updateTokenRequest) {
        this.updateTokenRequest = updateTokenRequest;
    }
}

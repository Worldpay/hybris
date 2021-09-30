package com.worldpay.service.request;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.data.MerchantInfo;
import com.worldpay.data.payment.Payment;
import com.worldpay.data.token.CardTokenRequest;
import com.worldpay.data.token.TokenRequest;


/**
 * This class represents the details that must be passed to a call to {@link WorldpayServiceGateway#createToken(CreateTokenServiceRequest)} in the WorldpayServiceGateway
 * <p/>
 * <p>It sets the relevant details into the Card Token Request object that are required to be sent</p>
 */
public class CreateTokenServiceRequest extends AbstractServiceRequest {

    private CardTokenRequest cardTokenRequest;

    protected CreateTokenServiceRequest(final MerchantInfo merchantInfo, final String orderCode) {
        super(merchantInfo, orderCode);
    }

    /**
     * Static convenience method for creating an instance of the CreateTokenServiceRequest
     *
     * @param merchantInfo           merchantInfo to be used in the Worldpay call
     * @param authenticatedShopperId unique reference for the customer using the payment method
     * @param payment                payment to be used in the Worldpay call
     * @param tokenRequest           contains the information to request a new token
     * @return new instance of the CreateTokenServiceRequest initialised with input parameters
     */
    public static CreateTokenServiceRequest createTokenRequestForShopperToken(final MerchantInfo merchantInfo, final String authenticatedShopperId, final Payment payment, final TokenRequest tokenRequest) {
        checkParameters("CreateTokenServiceRequest", merchantInfo, payment);

        final CreateTokenServiceRequest request = new CreateTokenServiceRequest(merchantInfo, null);
        final CardTokenRequest cardTokenRequest = new CardTokenRequest();
        cardTokenRequest.setAuthenticatedShopperId(authenticatedShopperId);
        cardTokenRequest.setPayment(payment);
        cardTokenRequest.setTokenRequest(tokenRequest);
        request.setCardTokenRequest(cardTokenRequest);
        return request;
    }

    /**
     * Static convenience method for creating an instance of the CreateTokenServiceRequest
     *
     * @param merchantInfo merchantInfo to be used in the Worldpay call
     * @param payment      payment to be used in the Worldpay call
     * @param tokenRequest contains the information to request a new token
     * @return new instance of the CreateTokenServiceRequest initialised with input parameters
     */
    public static CreateTokenServiceRequest createTokenRequestForMerchantToken(final MerchantInfo merchantInfo, final Payment payment, final TokenRequest tokenRequest) {
        checkParameters("CreateTokenServiceRequest", merchantInfo, payment);

        final CreateTokenServiceRequest request = new CreateTokenServiceRequest(merchantInfo, null);
        final CardTokenRequest cardTokenRequest = new CardTokenRequest();
        cardTokenRequest.setPayment(payment);
        cardTokenRequest.setTokenRequest(tokenRequest);
        request.setCardTokenRequest(cardTokenRequest);
        return request;
    }

    public CardTokenRequest getCardTokenRequest() {
        return cardTokenRequest;
    }

    public void setCardTokenRequest(final CardTokenRequest cardTokenRequest) {
        this.cardTokenRequest = cardTokenRequest;
    }
}

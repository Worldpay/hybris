package com.worldpay.service.request;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.token.CardTokenRequest;
import com.worldpay.service.model.token.TokenRequest;


/**
 * This class represents the details that must be passed to a call to {@link WorldpayServiceGateway#createToken(CreateTokenServiceRequest)} in the WorldpayServiceGateway
 * <p/>
 * <p>It sets the relevant details into the Card Token Request object that are required to be sent</p>
 */
public class CreateTokenServiceRequest extends AbstractServiceRequest {

    private CardTokenRequest cardTokenRequest;

    protected CreateTokenServiceRequest(final WorldpayConfig config, final MerchantInfo merchantInfo, final String orderCode) {
        super(config, merchantInfo, orderCode);
    }

    /**
     * Static convenience method for creating an instance of the CreateTokenServiceRequest
     * @param config                    WorldpayConfig to be used in the Worldpay call
     * @param merchantInfo              merchantInfo to be used in the Worldpay call
     * @param authenticatedShopperId    unique reference for the customer using the payment method
     * @param payment                   payment to be used in the Worldpay call
     * @param tokenRequest              contains the information to request a new token
     * @return new instance of the CreateTokenServiceRequest initialised with input parameters
     */
    public static CreateTokenServiceRequest createTokenRequest(final WorldpayConfig config, final MerchantInfo merchantInfo, final String authenticatedShopperId, final Payment payment, final TokenRequest tokenRequest) {
        checkParameters("CreateTokenServiceRequest", config, merchantInfo, payment);

        final CreateTokenServiceRequest request = new CreateTokenServiceRequest(config, merchantInfo, null);
        request.setCardTokenRequest(new CardTokenRequest(authenticatedShopperId, tokenRequest, payment));
        return request;
    }

    public CardTokenRequest getCardTokenRequest() {
        return cardTokenRequest;
    }

    public void setCardTokenRequest(final CardTokenRequest cardTokenRequest) {
        this.cardTokenRequest = cardTokenRequest;
    }
}

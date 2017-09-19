package com.worldpay.service.request;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.token.TokenRequest;

/**
 * This class represents the details that must be passed to a call to {@link WorldpayServiceGateway#directAuthorise(DirectAuthoriseServiceRequest)} (DirectAuthoriseServiceRequest)
 * directAuthorise()} in the WorldpayServiceGateway
 * <p/>
 * <p>It sets the relevant details into the Order object that are required to be sent</p>
 */
public class DirectAuthoriseServiceRequest extends AuthoriseServiceRequest {

    protected DirectAuthoriseServiceRequest(WorldpayConfig config, MerchantInfo merchantInfo, String orderCode) {
        super(config, merchantInfo, orderCode);
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest
     *
     * @param config             WorldpayConfig to be used in the Worldpay call
     * @param merchantInfo       merchantInfo to be used in the Worldpay call
     * @param orderInfo          orderInfo to be used in the Worldpay call
     * @param payment            payment to be used in the Worldpay call
     * @param shopper            shopper information to be used in the Worldpay call
     * @param session            session to be used in the Worldpay call
     * @param shippingAddress    shippingAddress to be used in the Worldpay call
     * @param billingAddress     billingAddress to be used in the Worldpay call
     * @param statementNarrative statementNarrative to be used in the Worldpay call
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createDirectAuthoriseRequest(
            final WorldpayConfig config, final MerchantInfo merchantInfo, final BasicOrderInfo orderInfo, final Payment payment,
            final Shopper shopper, final Session session, final Address shippingAddress,
            final Address billingAddress, String statementNarrative) {
        checkParameters("DirectAuthoriseServiceRequest", config, merchantInfo, orderInfo);
        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(config, merchantInfo, orderInfo.getOrderCode());
        final PaymentDetails paymentDetails = new PaymentDetails(payment, session);
        final Order reqOrder = createOrder(orderInfo, shopper, shippingAddress, billingAddress, statementNarrative, paymentDetails);

        authRequest.setOrder(reqOrder);

        return authRequest;
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest
     *
     * @param config             WorldpayConfig to be used in the Worldpay call
     * @param merchantInfo       merchantInfo to be used in the Worldpay call
     * @param orderInfo          orderInfo to be used in the Worldpay call
     * @param payment            payment to be used in the Worldpay call
     * @param shopper            shopper information to be used in the Worldpay call
     * @param session            session to be used in the Worldpay call
     * @param shippingAddress    shippingAddress to be used in the Worldpay call
     * @param billingAddress     billingAddress to be used in the Worldpay call
     * @param statementNarrative statementNarrative to be used in the Worldpay call
     * @param orderLines         orderLines containing the items ordered
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createKlarnaDirectAuthoriseRequest(
            final WorldpayConfig config, final MerchantInfo merchantInfo, final BasicOrderInfo orderInfo, final Payment payment,
            final Shopper shopper, final Session session, final Address shippingAddress,
            final Address billingAddress, String statementNarrative, final OrderLines orderLines) {
        checkParameters("DirectAuthoriseServiceRequest", config, merchantInfo, orderInfo);
        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(config, merchantInfo, orderInfo.getOrderCode());
        final PaymentDetails paymentDetails = new PaymentDetails(payment, session);
        final Order reqOrder = createOrder(orderInfo, shopper, shippingAddress, billingAddress, statementNarrative, paymentDetails);
        reqOrder.setOrderLines(orderLines);
        authRequest.setOrder(reqOrder);

        return authRequest;
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest
     *
     * @param config             WorldpayConfig to be used in the Worldpay call
     * @param merchantInfo       merchantInfo to be used in the Worldpay call
     * @param orderInfo          orderInfo to be used in the Worldpay call
     * @param payment            token to be used in the Worldpay call which is linked to a billing address
     * @param shopper            shopper information to be used in the Worldpay call
     * @param statementNarrative statementNarrative to be used in the Worldpay call
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createTokenisedDirectAuthoriseRequest(final WorldpayConfig config, final MerchantInfo merchantInfo,
                                                                                      final BasicOrderInfo orderInfo, final Payment payment,
                                                                                      final Shopper shopper, final Address shippingAddress, String statementNarrative) {
        checkParameters("DirectAuthoriseServiceRequest", config, merchantInfo, orderInfo, shopper);
        checkInstanceOfToken(payment);
        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(config, merchantInfo, orderInfo.getOrderCode());
        final PaymentDetails paymentDetails = new PaymentDetails(payment, shopper.getSession());
        // Passing billing address as null as the token has one in Worldpay.
        final Order reqOrder = createOrder(orderInfo, shopper, shippingAddress, null, statementNarrative, paymentDetails);
        authRequest.setOrder(reqOrder);
        return authRequest;
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest that will request a new Token to be created.
     *
     * @param config             WorldpayConfig to be used in the Worldpay call
     * @param merch              merchantInfo to be used in the Worldpay call
     * @param orderInfo          orderInfo to be used in the Worldpay call
     * @param payment            payment to be used in the Worldpay call
     * @param shopper            shopper information to be used in the Worldpay call
     * @param session            session to be used in the Worldpay call
     * @param shippingAddress    shippingAddress to be used in the Worldpay call
     * @param billingAddress     billingAddress to be used in the Worldpay call
     * @param statementNarrative statementNarrative to be used in the Worldpay call
     * @param tokenRequest       contains the information to request a new token
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createTokenAndDirectAuthoriseRequest(final WorldpayConfig config, final MerchantInfo merch,
                                                                                     final BasicOrderInfo orderInfo, final Payment payment,
                                                                                     final Shopper shopper, final Session session,
                                                                                     final Address shippingAddress,
                                                                                     final Address billingAddress, String statementNarrative,
                                                                                     final TokenRequest tokenRequest) {
        checkParameters("TokenAndDirectAuthoriseRequest", config, merch, orderInfo, tokenRequest);
        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(config, merch, orderInfo.getOrderCode());
        final PaymentDetails paymentDetails = new PaymentDetails(payment, session);
        final Order reqOrder = createOrder(orderInfo, shopper, shippingAddress, billingAddress, statementNarrative, paymentDetails);
        reqOrder.setTokenRequest(tokenRequest);
        authRequest.setOrder(reqOrder);

        return authRequest;
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest with
     * parameters passed back from the 3D security call.
     * Note - the other details passed in must match the original DirectAuthoriseServiceRequest.
     *
     * @param config    WorldpayConfig to be used in the Worldpay call
     * @param merch     merchantInfo to be used in the Worldpay call
     * @param orderInfo orderInfo to be used in the Worldpay call
     * @param session   session to be used in the Worldpay call
     * @param paRes     paRes to be used in the Worldpay call
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createDirect3DAuthoriseRequest(
            final WorldpayConfig config, final MerchantInfo merch, final BasicOrderInfo orderInfo, final Session session, String paRes) {
        checkParameters("Direct3DAuthoriseRequest", config, merch, orderInfo);

        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(config, merch, orderInfo.getOrderCode());
        final Order reqOrder = new Order(orderInfo.getOrderCode(), null, null);
        reqOrder.setSession(session);
        reqOrder.setPaResponse(paRes);
        authRequest.setOrder(reqOrder);
        return authRequest;
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest with
     * parameters passed back from the 3D security call.
     * Note - the other details passed in must match the original DirectAuthoriseServiceRequest.
     *
     * @param config             WorldpayConfig to be used in the Worldpay call
     * @param merch              merchantInfo to be used in the Worldpay call
     * @param orderInfo          orderInfo to be used in the Worldpay call
     * @param payment            payment to be used in the Worldpay call
     * @param shopper            shopper information to be used in the Worldpay call
     * @param session            session to be used in the Worldpay call
     * @param paRes              paRes to be used in the Worldpay call
     * @param echoData           echoData to be used in the Worldpay call
     * @param shippingAddress    shippingAddress to be used in the Worldpay call
     * @param billingAddress     billingAddress to be used in the Worldpay call
     * @param statementNarrative statementNarrative to be used in the Worldpay call
     * @param tokenRequest       contains the reference related to the token creation request
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createTokenAndDirect3DAuthoriseRequest(
            final WorldpayConfig config, final MerchantInfo merch, final BasicOrderInfo orderInfo, final Payment payment,
            final Shopper shopper, final Session session, String paRes, String echoData,
            final Address shippingAddress, final Address billingAddress, String statementNarrative, final TokenRequest tokenRequest) {
        checkParameters("TokenAndDirect3DAuthoriseRequest", config, merch, orderInfo, tokenRequest);
        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(config, merch, orderInfo.getOrderCode());
        final PaymentDetails paymentDetails = new PaymentDetails(payment, session, paRes);
        final Order reqOrder = createOrder(orderInfo, shopper, shippingAddress, billingAddress, statementNarrative, paymentDetails);
        reqOrder.setEchoData(echoData);
        reqOrder.setTokenRequest(tokenRequest);
        authRequest.setOrder(reqOrder);
        return authRequest;
    }

    private static Order createOrder(final BasicOrderInfo orderInfo, final Shopper shopper, final Address shippingAddress, final Address billingAddress, final String statementNarrative, final PaymentDetails paymentDetails) {
        final Order reqOrder = new Order(orderInfo.getOrderCode(), orderInfo.getDescription(), orderInfo.getAmount());
        reqOrder.setPaymentDetails(paymentDetails);
        reqOrder.setShopper(shopper);
        reqOrder.setShippingAddress(shippingAddress);
        reqOrder.setBillingAddress(billingAddress);
        reqOrder.setStatementNarrative(statementNarrative);
        return reqOrder;
    }
}


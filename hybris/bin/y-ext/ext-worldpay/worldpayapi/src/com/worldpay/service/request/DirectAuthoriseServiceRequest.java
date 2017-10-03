package com.worldpay.service.request;

import com.worldpay.enums.order.DynamicInteractionType;
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

    private static final String DIRECT_AUTHORISE_SERVICE_REQUEST = "DirectAuthoriseServiceRequest";
    private static final String TOKEN_AND_DIRECT_AUTHORISE_REQUEST = "TokenAndDirectAuthoriseRequest";

    protected DirectAuthoriseServiceRequest(MerchantInfo merchantInfo, String orderCode) {
        super(merchantInfo, orderCode);
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest
     *
     * @param merchantInfo           merchantInfo to be used in the Worldpay call
     * @param orderInfo              orderInfo to be used in the Worldpay call
     * @param payment                payment to be used in the Worldpay call
     * @param shopper                shopper information to be used in the Worldpay call
     * @param session                session to be used in the Worldpay call
     * @param shippingAddress        shippingAddress to be used in the Worldpay call
     * @param billingAddress         billingAddress to be used in the Worldpay call
     * @param statementNarrative     statementNarrative to be used in the Worldpay call
     * @param dynamicInteractionType contains the source interaction of the order
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createDirectAuthoriseRequest(
            final MerchantInfo merchantInfo, final BasicOrderInfo orderInfo, final Payment payment,
            final Shopper shopper, final Session session, final Address shippingAddress,
            final Address billingAddress, final String statementNarrative, final DynamicInteractionType dynamicInteractionType) {
        checkParameters(DIRECT_AUTHORISE_SERVICE_REQUEST, merchantInfo, orderInfo);
        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(merchantInfo, orderInfo.getOrderCode());
        final PaymentDetails paymentDetails = new PaymentDetails(payment, session);
        final Order reqOrder = createOrder(orderInfo, shopper, shippingAddress, billingAddress, statementNarrative, paymentDetails, dynamicInteractionType);

        authRequest.setOrder(reqOrder);

        return authRequest;
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest
     *
     * @param merchantInfo           merchantInfo to be used in the Worldpay call
     * @param orderInfo              orderInfo to be used in the Worldpay call
     * @param payment                payment to be used in the Worldpay call
     * @param shopper                shopper information to be used in the Worldpay call
     * @param session                session to be used in the Worldpay call
     * @param shippingAddress        shippingAddress to be used in the Worldpay call
     * @param billingAddress         billingAddress to be used in the Worldpay call
     * @param statementNarrative     statementNarrative to be used in the Worldpay call
     * @param orderLines             orderLines containing the items ordered
     * @param dynamicInteractionType contains the source interaction of the order
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createKlarnaDirectAuthoriseRequest(
            final MerchantInfo merchantInfo, final BasicOrderInfo orderInfo, final Payment payment,
            final Shopper shopper, final Session session, final Address shippingAddress,
            final Address billingAddress, final String statementNarrative, final OrderLines orderLines, final DynamicInteractionType dynamicInteractionType) {
        checkParameters(DIRECT_AUTHORISE_SERVICE_REQUEST, merchantInfo, orderInfo);
        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(merchantInfo, orderInfo.getOrderCode());
        final PaymentDetails paymentDetails = new PaymentDetails(payment, session);
        final Order reqOrder = createOrder(orderInfo, shopper, shippingAddress, billingAddress, statementNarrative, paymentDetails, dynamicInteractionType);
        reqOrder.setOrderLines(orderLines);
        authRequest.setOrder(reqOrder);

        return authRequest;
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest
     *
     * @param merchantInfo           merchantInfo to be used in the Worldpay call
     * @param orderInfo              orderInfo to be used in the Worldpay call
     * @param payment                token to be used in the Worldpay call which is linked to a billing address
     * @param shopper                shopper information to be used in the Worldpay call
     * @param shippingAddress        shippingAddress to be used in the Worldpay call
     * @param statementNarrative     statementNarrative to be used in the Worldpay call
     * @param dynamicInteractionType contains the source interaction of the order
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createTokenisedDirectAuthoriseRequest(final MerchantInfo merchantInfo,
                                                                                      final BasicOrderInfo orderInfo, final Payment payment,
                                                                                      final Shopper shopper, final Address shippingAddress, final String statementNarrative, final DynamicInteractionType dynamicInteractionType) {
        checkParameters(DIRECT_AUTHORISE_SERVICE_REQUEST, merchantInfo, orderInfo, shopper);
        checkInstanceOfToken(payment);
        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(merchantInfo, orderInfo.getOrderCode());
        final PaymentDetails paymentDetails = new PaymentDetails(payment, shopper.getSession());
        // Passing billing address as null as the token has one in Worldpay.
        final Order reqOrder = createOrder(orderInfo, shopper, shippingAddress, null, statementNarrative, paymentDetails, dynamicInteractionType);
        authRequest.setOrder(reqOrder);
        return authRequest;
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest that will request a new Token to be created.
     *
     * @param merch                  merchantInfo to be used in the Worldpay call
     * @param orderInfo              orderInfo to be used in the Worldpay call
     * @param payment                payment to be used in the Worldpay call
     * @param shopper                shopper information to be used in the Worldpay call
     * @param session                session to be used in the Worldpay call
     * @param shippingAddress        shippingAddress to be used in the Worldpay call
     * @param billingAddress         billingAddress to be used in the Worldpay call
     * @param statementNarrative     statementNarrative to be used in the Worldpay call
     * @param tokenRequest           contains the information to request a new token
     * @param dynamicInteractionType contains the source interaction of the order
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createTokenAndDirectAuthoriseRequest(final MerchantInfo merch,
                                                                                     final BasicOrderInfo orderInfo, final Payment payment,
                                                                                     final Shopper shopper, final Session session,
                                                                                     final Address shippingAddress,
                                                                                     final Address billingAddress, String statementNarrative,
                                                                                     final TokenRequest tokenRequest,
                                                                                     final DynamicInteractionType dynamicInteractionType) {
        checkParameters(TOKEN_AND_DIRECT_AUTHORISE_REQUEST, merch, orderInfo, tokenRequest);
        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(merch, orderInfo.getOrderCode());
        final PaymentDetails paymentDetails = new PaymentDetails(payment, session);
        final Order reqOrder = createOrder(orderInfo, shopper, shippingAddress, billingAddress, statementNarrative, paymentDetails, dynamicInteractionType);
        reqOrder.setTokenRequest(tokenRequest);
        authRequest.setOrder(reqOrder);

        return authRequest;
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest with
     * parameters passed back from the 3D security call.
     * Note - the other details passed in must match the original DirectAuthoriseServiceRequest.
     *
     * @param merch     merchantInfo to be used in the Worldpay call
     * @param orderInfo orderInfo to be used in the Worldpay call
     * @param session   session to be used in the Worldpay call
     * @param paRes     paRes to be used in the Worldpay call
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createDirect3DAuthoriseRequest(
            final MerchantInfo merch, final BasicOrderInfo orderInfo, final Session session, String paRes) {
        checkParameters("Direct3DAuthoriseRequest", merch, orderInfo);

        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(merch, orderInfo.getOrderCode());

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
     * @param merch                  merchantInfo to be used in the Worldpay call
     * @param orderInfo              orderInfo to be used in the Worldpay call
     * @param payment                payment to be used in the Worldpay call
     * @param shopper                shopper information to be used in the Worldpay call
     * @param session                session to be used in the Worldpay call
     * @param paRes                  paRes to be used in the Worldpay call
     * @param echoData               echoData to be used in the Worldpay call
     * @param shippingAddress        shippingAddress to be used in the Worldpay call
     * @param billingAddress         billingAddress to be used in the Worldpay call
     * @param statementNarrative     statementNarrative to be used in the Worldpay call
     * @param tokenRequest           contains the reference related to the token creation request
     * @param dynamicInteractionType contains the source interaction of the order
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createTokenAndDirect3DAuthoriseRequest(
            final MerchantInfo merch, final BasicOrderInfo orderInfo, final Payment payment,
            final Shopper shopper, final Session session, final String paRes, final String echoData,
            final Address shippingAddress, final Address billingAddress, final String statementNarrative, final TokenRequest tokenRequest, final DynamicInteractionType dynamicInteractionType) {
        checkParameters("TokenAndDirect3DAuthoriseRequest", merch, orderInfo, tokenRequest);
        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(merch, orderInfo.getOrderCode());
        final PaymentDetails paymentDetails = new PaymentDetails(payment, session, paRes);
        final Order reqOrder = createOrder(orderInfo, shopper, shippingAddress, billingAddress, statementNarrative, paymentDetails, dynamicInteractionType);
        reqOrder.setEchoData(echoData);
        reqOrder.setTokenRequest(tokenRequest);
        authRequest.setOrder(reqOrder);
        return authRequest;
    }

    private static Order createOrder(final BasicOrderInfo orderInfo,
                                     final Shopper shopper,
                                     final Address shippingAddress,
                                     final Address billingAddress,
                                     final String statementNarrative,
                                     final PaymentDetails paymentDetails,
                                     final DynamicInteractionType dynamicInteractionType) {
        final Order reqOrder = new Order(orderInfo.getOrderCode(), orderInfo.getDescription(), orderInfo.getAmount());
        reqOrder.setPaymentDetails(paymentDetails);
        reqOrder.setShopper(shopper);
        reqOrder.setShippingAddress(shippingAddress);
        reqOrder.setBillingAddress(billingAddress);
        reqOrder.setStatementNarrative(statementNarrative);
        reqOrder.setDynamicInteractionType(dynamicInteractionType);
        return reqOrder;
    }
}


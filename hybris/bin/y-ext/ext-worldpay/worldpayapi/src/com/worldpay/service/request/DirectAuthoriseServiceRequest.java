package com.worldpay.service.request;

import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.Payment;

/**
 * This class represents the details that must be passed to a call to {@link WorldpayServiceGateway#directAuthorise(DirectAuthoriseServiceRequest)} (DirectAuthoriseServiceRequest)
 * directAuthorise()} in the WorldpayServiceGateway
 * <p/>
 * <p>It sets the relevant details into the Order object that are required to be sent</p>
 */
public class DirectAuthoriseServiceRequest extends AuthoriseServiceRequest {

    private static final String DIRECT_AUTHORISE_SERVICE_REQUEST = "DirectAuthoriseServiceRequest";

    protected DirectAuthoriseServiceRequest(final MerchantInfo merchantInfo, final String orderCode) {
        super(merchantInfo, orderCode);
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest
     *
     * @param requestParameters - the request parameter
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createDirectAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        final MerchantInfo merchantInfo = requestParameters.getMerchantInfo();
        final BasicOrderInfo orderInfo = requestParameters.getOrderInfo();
        checkParameters(DIRECT_AUTHORISE_SERVICE_REQUEST, merchantInfo, orderInfo, merchantInfo.getMerchantCode(), merchantInfo.getMerchantPassword());
        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(merchantInfo, orderInfo.getOrderCode());
        final Shopper shopper = requestParameters.getShopper();
        final PaymentDetails paymentDetails = new PaymentDetails(requestParameters.getPayment(), shopper != null ? shopper.getSession() : null);
        final Order reqOrder = createOrder(orderInfo, shopper, requestParameters.getShippingAddress(), requestParameters.getBillingAddress(), requestParameters.getStatementNarrative(), paymentDetails, requestParameters.getDynamicInteractionType());

        authRequest.setOrder(reqOrder);

        return authRequest;
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest
     *
     * @param requestParameters - the request parameter
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input requestParameters
     */
    public static DirectAuthoriseServiceRequest createKlarnaDirectAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        final MerchantInfo merchantInfo = requestParameters.getMerchantInfo();
        final BasicOrderInfo orderInfo = requestParameters.getOrderInfo();

        checkParameters(DIRECT_AUTHORISE_SERVICE_REQUEST, merchantInfo, orderInfo);

        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(merchantInfo, orderInfo.getOrderCode());

        final Shopper shopper = requestParameters.getShopper();

        final PaymentDetails paymentDetails = new PaymentDetails(requestParameters.getPayment(), shopper != null ? shopper.getSession() : null);
        final Order reqOrder = createOrder(orderInfo, shopper, requestParameters.getShippingAddress(), requestParameters.getBillingAddress(), requestParameters.getStatementNarrative(), paymentDetails, requestParameters.getDynamicInteractionType());

        reqOrder.setOrderLines(requestParameters.getOrderLines());
        authRequest.setOrder(reqOrder);

        return authRequest;
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest
     *
     * @param requestParameters - the request parameter
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createTokenisedDirectAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        final BasicOrderInfo orderInfo = requestParameters.getOrderInfo();
        final Shopper shopper = requestParameters.getShopper();
        final MerchantInfo merchantInfo = requestParameters.getMerchantInfo();
        checkParameters(DIRECT_AUTHORISE_SERVICE_REQUEST, merchantInfo, orderInfo, shopper);
        checkInstanceOfToken(requestParameters.getPayment());
        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(merchantInfo, orderInfo.getOrderCode());
        final PaymentDetails paymentDetails = new PaymentDetails(requestParameters.getPayment(), shopper != null ? shopper.getSession() : null);
        // Passing billing address as null as the token has one in Worldpay.
        final Order reqOrder = createOrder(orderInfo, shopper, requestParameters.getShippingAddress(), requestParameters.getBillingAddress(), requestParameters.getStatementNarrative(), paymentDetails, requestParameters.getDynamicInteractionType());
        reqOrder.setAdditional3DSData(requestParameters.getAdditional3DSData());
        reqOrder.setRiskData(requestParameters.getRiskData());
        authRequest.setOrder(reqOrder);
        return authRequest;
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest with an ApplePay payment method
     *
     * @param requestParameters - the request parameter
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createApplePayDirectAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        final MerchantInfo merchantInfo = requestParameters.getMerchantInfo();
        final BasicOrderInfo orderInfo = requestParameters.getOrderInfo();
        final Shopper shopper = requestParameters.getShopper();
        checkParameters(DIRECT_AUTHORISE_SERVICE_REQUEST, merchantInfo, orderInfo, shopper);
        final Payment payment = requestParameters.getPayment();
        checkInstanceOfApplePay(payment);
        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(merchantInfo, orderInfo.getOrderCode());
        final PaymentDetails paymentDetails = new PaymentDetails(payment, shopper != null ? shopper.getSession() : null);
        // Passing billing address as null as the token has one in Worldpay.
        final Order reqOrder = createOrder(orderInfo, shopper, requestParameters.getShippingAddress(), requestParameters.getBillingAddress(), requestParameters.getStatementNarrative(), paymentDetails, requestParameters.getDynamicInteractionType());
        authRequest.setOrder(reqOrder);
        return authRequest;
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest with a GooglePay payment method
     *
     * @param requestParameters - the request parameter
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createGooglePayDirectAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        final MerchantInfo merchantInfo = requestParameters.getMerchantInfo();
        final BasicOrderInfo orderInfo = requestParameters.getOrderInfo();
        final Shopper shopper = requestParameters.getShopper();
        checkParameters(DIRECT_AUTHORISE_SERVICE_REQUEST, merchantInfo, orderInfo, shopper);
        final Payment payment = requestParameters.getPayment();
        checkInstanceOfGooglePay(payment);
        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(merchantInfo, orderInfo.getOrderCode());
        final PaymentDetails paymentDetails = new PaymentDetails(payment, shopper != null ? shopper.getSession() : null);
        // Passing billing address as null as the token has one in Worldpay.
        final Order reqOrder = createOrder(orderInfo, shopper, requestParameters.getShippingAddress(), requestParameters.getBillingAddress(), requestParameters.getStatementNarrative(), paymentDetails, requestParameters.getDynamicInteractionType());
        authRequest.setOrder(reqOrder);
        return authRequest;
    }

    /**
     * Static convenience method for creating an instance of the DirectAuthoriseServiceRequest with
     * parameters passed back from the 3D security call.
     * Note - the other details passed in must match the original DirectAuthoriseServiceRequest.
     *
     * @param requestParameters - the request parameter
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createDirect3DAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        final BasicOrderInfo orderInfo = requestParameters.getOrderInfo();
        final MerchantInfo merchantInfo = requestParameters.getMerchantInfo();
        checkParameters("Direct3DAuthoriseRequest", merchantInfo, orderInfo);

        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(merchantInfo, orderInfo.getOrderCode());

        final Order reqOrder = new Order(orderInfo.getOrderCode(), null, null);
        reqOrder.setSession(requestParameters.getShopper().getSession());
        reqOrder.setPaResponse(requestParameters.getPaRes());
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


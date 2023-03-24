package com.worldpay.service.request;

import com.worldpay.data.*;
import com.worldpay.data.payment.Payment;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.util.WorldpayInternalModelTransformerUtil;

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
        final Order reqOrder = new OrderBuilder()
            .withOrderInfo(orderInfo)
            .withShopper(requestParameters.getShopper())
            .withShippingAddress(requestParameters.getShippingAddress())
            .withBillingAddress(requestParameters.getBillingAddress())
            .withStatementNarrative(requestParameters.getStatementNarrative())
            .withPaymentDetails(WorldpayInternalModelTransformerUtil.createPaymentDetailsFromRequestParameters(requestParameters))
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .build();
        reqOrder.setOrderLines(requestParameters.getOrderLines());
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

        final Order reqOrder = new OrderBuilder()
            .withOrderInfo(orderInfo)
            .withShopper(requestParameters.getShopper())
            .withShippingAddress(requestParameters.getShippingAddress())
            .withBillingAddress(requestParameters.getBillingAddress())
            .withStatementNarrative(requestParameters.getStatementNarrative())
            .withPaymentDetails(WorldpayInternalModelTransformerUtil.createPaymentDetailsFromRequestParameters(requestParameters))
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .withOrderLines(requestParameters.getOrderLines())
            .withDeviceSession(requestParameters.getDeviceSession())
            .whitGuaranteedPaymentsAttribute(requestParameters.getGuaranteedPaymentsData())
            .whitCheckoutId(requestParameters.getCheckoutId())
            .build();

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

        // Passing billing address as null as the token has one in Worldpay.
        final Order order = new OrderBuilder()
            .withOrderInfo(orderInfo)
            .withShopper(shopper)
            .withShippingAddress(requestParameters.getShippingAddress())
            .withBillingAddress(requestParameters.getBillingAddress())
            .withStatementNarrative(requestParameters.getStatementNarrative())
            .withPaymentDetails(WorldpayInternalModelTransformerUtil.createPaymentDetailsFromRequestParameters(requestParameters))
            .withDynamicInteractionType(requestParameters.getDynamicInteractionType())
            .withRiskData(requestParameters.getRiskData())
            .withAdditional3DSData(requestParameters.getAdditional3DSData())
            .withFraudSightAttribute(requestParameters.getFraudSightData())
            .withDeviceSession(requestParameters.getDeviceSession())
            .withLevel23Data(requestParameters.getBranchSpecificExtension())
            .whitGuaranteedPaymentsAttribute(requestParameters.getGuaranteedPaymentsData())
            .whitCheckoutId(requestParameters.getCheckoutId())
            .build();
        authRequest.setOrder(order);
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
        // Passing billing address as null as the token has one in Worldpay.
        final Order order = new OrderBuilder()
            .withOrderInfo(orderInfo)
            .withShopper(shopper)
            .withShippingAddress(requestParameters.getShippingAddress())
            .withBillingAddress(requestParameters.getBillingAddress())
            .withPaymentDetails(WorldpayInternalModelTransformerUtil.createPaymentDetailsFromRequestParameters(requestParameters))
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .withDeviceSession(requestParameters.getDeviceSession())
            .whitGuaranteedPaymentsAttribute(requestParameters.getGuaranteedPaymentsData())
            .whitCheckoutId(requestParameters.getCheckoutId())
            .build();
        authRequest.setOrder(order);
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
        // Passing billing address as null as the token has one in Worldpay.
        final Order reqOrder = new OrderBuilder()
            .withOrderInfo(orderInfo)
            .withShopper(shopper)
            .withShippingAddress(requestParameters.getShippingAddress())
            .withBillingAddress(requestParameters.getBillingAddress())
            .withStatementNarrative(requestParameters.getStatementNarrative())
            .withPaymentDetails(WorldpayInternalModelTransformerUtil.createPaymentDetailsFromRequestParameters(requestParameters))
            .withTokenRequest(requestParameters.getTokenRequest())
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .withDeviceSession(requestParameters.getDeviceSession())
            .whitGuaranteedPaymentsAttribute(requestParameters.getGuaranteedPaymentsData())
            .whitCheckoutId(requestParameters.getCheckoutId())
            .build();
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

        final Order reqOrder = new Order();
        reqOrder.setOrderCode(orderInfo.getOrderCode());
        reqOrder.setSession(requestParameters.getShopper().getSession());
        reqOrder.setPaResponse(requestParameters.getPaRes());
        authRequest.setOrder(reqOrder);
        return authRequest;
    }

    /**
     * Creates a DirectAuthoriseServiceRequest given the requestParameters passed
     *
     * @param requestParameters
     * @return new instance of the DirectAuthoriseServiceRequest initialised with input parameters
     */
    public static DirectAuthoriseServiceRequest createDirectTokenAndAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        final DirectAuthoriseServiceRequest authRequest = new DirectAuthoriseServiceRequest(requestParameters.getMerchantInfo(), requestParameters.getOrderInfo().getOrderCode());

        final Order reqOrder = new OrderBuilder()
            .withOrderInfo(requestParameters.getOrderInfo())
            .withShopper(requestParameters.getShopper())
            .withTokenRequest(requestParameters.getTokenRequest())
            .withShippingAddress(requestParameters.getShippingAddress())
            .withBillingAddress(requestParameters.getBillingAddress())
            .withStatementNarrative(requestParameters.getStatementNarrative())
            .withPaymentDetails(WorldpayInternalModelTransformerUtil.createPaymentDetailsFromRequestParameters(requestParameters))
            .withDynamicInteractionType(requestParameters.getDynamicInteractionType())
            .withRiskData(requestParameters.getRiskData())
            .withAdditional3DSData(requestParameters.getAdditional3DSData())
            .withFraudSightAttribute(requestParameters.getFraudSightData())
            .withDeviceSession(requestParameters.getDeviceSession())
            .withLevel23Data(requestParameters.getBranchSpecificExtension())
            .whitGuaranteedPaymentsAttribute(requestParameters.getGuaranteedPaymentsData())
            .whitCheckoutId(requestParameters.getCheckoutId())
            .build();
        authRequest.setOrder(reqOrder);
        return authRequest;
    }
}


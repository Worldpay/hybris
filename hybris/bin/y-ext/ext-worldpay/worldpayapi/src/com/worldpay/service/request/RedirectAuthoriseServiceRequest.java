package com.worldpay.service.request;

import com.worldpay.data.MerchantInfo;
import com.worldpay.data.Order;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.util.WorldpayInternalModelTransformerUtil;

/**
 * This class represents the details that must be passed to a call to {@link WorldpayServiceGateway#redirectAuthorise(RedirectAuthoriseServiceRequest) redirectAuthorise()} in the
 * WorldpayServiceGateway
 * <p/>
 * <p>It sets the relevant details into the Order object that are required to be sent</p>
 */
public class RedirectAuthoriseServiceRequest extends AuthoriseServiceRequest {

    protected RedirectAuthoriseServiceRequest(final MerchantInfo merchantInfo, final String orderCode) {
        super(merchantInfo, orderCode);
    }

    /**
     * Static convenience method for creating an instance of the RedirectAuthoriseServiceRequest
     *
     * @param requestParameters
     * @return
     */
    public static RedirectAuthoriseServiceRequest createRedirectAuthoriseRequest(final AuthoriseRequestParameters requestParameters) {
        final RedirectAuthoriseServiceRequest authRequest = new RedirectAuthoriseServiceRequest(requestParameters.getMerchantInfo(), requestParameters.getOrderInfo().getOrderCode());

        final Order reqOrder = new OrderBuilder()
            .withOrderInfo(requestParameters.getOrderInfo())
            .withInstallationId(requestParameters.getInstallationId())
            .withIncludedPaymentMethods(requestParameters.getIncludedPTs())
            .withExcludedPaymentMethods(requestParameters.getExcludedPTs())
            .withOrderContent(requestParameters.getOrderContent())
            .withShopper(requestParameters.getShopper())
            .withTokenRequest(requestParameters.getTokenRequest())
            .withShippingAddress(requestParameters.getShippingAddress())
            .withBillingAddress(requestParameters.getBillingAddress())
            .withStatementNarrative(requestParameters.getStatementNarrative())
            .withPaymentDetails(WorldpayInternalModelTransformerUtil.createPaymentDetailsFromRequestParameters(requestParameters))
            .withDynamicInteractionType(requestParameters.getDynamicInteractionType())
            .withStoredCredentials(requestParameters.getStoredCredentials())
            .withPaymentMethodAttribute(requestParameters.getPaymentMethodAttributes())
            .withOrderLines(requestParameters.getOrderLines())
            .withRiskData(requestParameters.getRiskData())
            .withFraudSightAttribute(requestParameters.getFraudSightData())
            .withLevel23Data(requestParameters.getBranchSpecificExtension())
            .withMandateType(requestParameters.getMandateType())
            .withDeviceSession(requestParameters.getDeviceSession())
            .withGuaranteedPaymentsAttribute(requestParameters.getGuaranteedPaymentsData())
            .withCheckoutId(requestParameters.getCheckoutId())
            .withAlternativeShippingAddress(requestParameters.getAlternativeShippingAddress())
            .build();
        authRequest.setOrder(reqOrder);
        return authRequest;
    }
}

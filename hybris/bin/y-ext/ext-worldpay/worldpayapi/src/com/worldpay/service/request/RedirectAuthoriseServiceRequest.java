package com.worldpay.service.request;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.Order;
import com.worldpay.service.model.PaymentDetails;

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
        final PaymentDetails paymentDetails = new PaymentDetails(requestParameters.getPayment(), requestParameters.getShopper().getSession(), requestParameters.getStoredCredentials());

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
            .withPaymentDetails(paymentDetails)
            .withDynamicInteractionType(requestParameters.getDynamicInteractionType())
            .withStoredCredentials(requestParameters.getStoredCredentials())
            .withPaymentMethodAttribute(requestParameters.getPaymentMethodAttributes())
            .withOrderLines(requestParameters.getOrderLines())
            .withRiskData(requestParameters.getRiskData())
            .build();
        authRequest.setOrder(reqOrder);
        return authRequest;
    }
}

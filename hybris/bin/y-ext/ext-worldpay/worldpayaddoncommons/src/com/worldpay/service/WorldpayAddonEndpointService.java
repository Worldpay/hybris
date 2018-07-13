package com.worldpay.service;

/**
 * Service for getting addon specific endpoints
 */
public interface WorldpayAddonEndpointService {

    /**
     * Returns the endpoint for CheckoutSummaryPage
     * @return
     */
    String getCheckoutSummaryPage();

    /**
     * Returns the endpoint for CSEPaymentDetailsPage
     * @return
     */
    String getCSEPaymentDetailsPage();

    /**
     * Returns the endpoint for HostedOrderPostPage
     * @return
     */
    String getHostedOrderPostPage();

    /**
     * Returns the endpoint for AutoSubmit3DSecure
     * @return
     */
    String getAutoSubmit3DSecure();

    /**
     * Returns the endpoint for BillingAddressForm
     * @return
     */
    String getBillingAddressForm();

    /**
     * Returns the endpoint for KlarnaResponsePage
     * @return
     */
    String getKlarnaResponsePage();
}

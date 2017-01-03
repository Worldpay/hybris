package com.worldpay.service;

/**
 * Service for getting addon specific endpoints
 */
public interface WorldpayAddonEndpointService {

    String getCheckoutSummaryPage();
    String getCSEPaymentDetailsPage();
    String getHostedOrderPostPage();
    String getAutoSubmit3DSecure();

    String getBillingAddressForm();
    String getBillingAddressInPaymentForm();

    String getGlobalErrorsFragment();
}

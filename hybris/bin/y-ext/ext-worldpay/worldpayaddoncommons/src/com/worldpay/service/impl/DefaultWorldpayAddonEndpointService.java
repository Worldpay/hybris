package com.worldpay.service.impl;

import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import javax.annotation.Resource;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayAddonEndpointService implements WorldpayAddonEndpointService {

    protected static final String WORLDPAY_ADDON_PREFIX = "worldpay.addon.prefix";
    protected static final String UNDEFINED_PREFIX = "undefined";

    protected static final String CHECKOUTSUMMARYPAGE = "pages/checkout/multi/worldpayCheckoutSummaryPage";
    protected static final String CSEPAYMENTDETAILSPAGE = "pages/checkout/multi/worldpayCSEPaymentPage";
    protected static final String HOSTEDORDERPOSTPAGE = "pages/checkout/multi/hostedOrderPostPage";
    protected static final String AUTOSUBMIT3DSECURE = "pages/checkout/multi/autoSubmit3DSecure";

    protected static final String BILLINGADDRESSFORM = "fragments/checkout/worldpayBillingAddressForm";

    private static final String KLARNA_RESPONSE_PAGE = "pages/klarna/klarnaResponseContentPage";

    @Resource
    private ConfigurationService configurationService;

    @Override
    public String getCheckoutSummaryPage() {
        return getEndpoint(CHECKOUTSUMMARYPAGE);
    }

    @Override
    public String getCSEPaymentDetailsPage() {
        return getEndpoint(CSEPAYMENTDETAILSPAGE);
    }

    @Override
    public String getHostedOrderPostPage() {
        return getEndpoint(HOSTEDORDERPOSTPAGE);
    }

    @Override
    public String getAutoSubmit3DSecure() {
        return getEndpoint(AUTOSUBMIT3DSECURE);
    }

    @Override
    public String getBillingAddressForm() {
        return getEndpoint(BILLINGADDRESSFORM);
    }

    protected String getEndpoint(final String path) {
        return configurationService.getConfiguration().getString(WORLDPAY_ADDON_PREFIX, UNDEFINED_PREFIX) + path;
    }

    @Override
    public String getKlarnaResponsePage() {
        return getEndpoint(KLARNA_RESPONSE_PAGE);
    }
}

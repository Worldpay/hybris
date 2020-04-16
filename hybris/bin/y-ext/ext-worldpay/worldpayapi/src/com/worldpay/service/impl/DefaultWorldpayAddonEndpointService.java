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

    protected static final String CHECKOUT_SUMMARY_PAGE = "pages/checkout/multi/worldpayCheckoutSummaryPage";
    protected static final String CSE_PAYMENT_DETAILS_PAGE = "pages/checkout/multi/worldpayCSEPaymentPage";
    protected static final String HOSTED_ORDER_POST_PAGE = "pages/checkout/multi/hostedOrderPostPage";
    protected static final String AUTO_SUBMIT_3D_SECURE = "pages/checkout/multi/autoSubmit3DSecure";

    protected static final String DDC_IFRAME_3D_SECURE_FLEX = "pages/checkout/multi/threedsflex/worldpayDDCIframe";
    protected static final String THREEDSECURE_FLEX_CHALLENGE_IFRAME = "pages/checkout/multi/threedsflex/worldpayChallengeIframe";

    protected static final String BILLING_ADDRESS_FORM = "fragments/checkout/worldpayBillingAddressForm";

    private static final String KLARNA_RESPONSE_PAGE = "pages/klarna/klarnaResponseContentPage";
    private static final String AUTO_SUBMIT_3D_SECURE_FLEX = "pages/checkout/multi/autoSubmit3DSecureFlex";

    @Resource
    private ConfigurationService configurationService;

    @Override
    public String getCheckoutSummaryPage() {
        return getEndpoint(CHECKOUT_SUMMARY_PAGE);
    }

    @Override
    public String getCSEPaymentDetailsPage() {
        return getEndpoint(CSE_PAYMENT_DETAILS_PAGE);
    }

    @Override
    public String getHostedOrderPostPage() {
        return getEndpoint(HOSTED_ORDER_POST_PAGE);
    }

    @Override
    public String getAutoSubmit3DSecure() {
        return getEndpoint(AUTO_SUBMIT_3D_SECURE);
    }

    @Override
    public String getBillingAddressForm() {
        return getEndpoint(BILLING_ADDRESS_FORM);
    }

    @Override
    public String getKlarnaResponsePage() {
        return getEndpoint(KLARNA_RESPONSE_PAGE);
    }

    @Override
    public String getDdcIframe3dSecureFlex() {
        return getEndpoint(DDC_IFRAME_3D_SECURE_FLEX);
    }

    protected String getEndpoint(final String path) {
        return configurationService.getConfiguration().getString(WORLDPAY_ADDON_PREFIX, UNDEFINED_PREFIX) + path;
    }

    @Override
    public String getAutoSubmit3DSecureFlex() {
        return getEndpoint(AUTO_SUBMIT_3D_SECURE_FLEX);
    }

    @Override
    public String getChallengeIframe3dSecureFlex() {
        return getEndpoint(THREEDSECURE_FLEX_CHALLENGE_IFRAME);
    }
}

package com.worldpay.service;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.impl.AbstractWorldpayUrlService;

public class OCCWorldpayUrlService extends AbstractWorldpayUrlService implements WorldpayUrlService {

    protected final WorldpayUrlService worldpayUrlService;

    public OCCWorldpayUrlService(final WorldpayUrlService worldpayUrlService) {
        this.worldpayUrlService = worldpayUrlService;
    }

    @Override
    public String getFullSuccessURL() throws WorldpayConfigurationException {
        return worldpayUrlService.getFullSuccessURL();
    }

    @Override
    public String getFullPendingURL() throws WorldpayConfigurationException {
        return worldpayUrlService.getFullPendingURL();
    }

    @Override
    public String getFullFailureURL() throws WorldpayConfigurationException {
        return worldpayUrlService.getFullFailureURL();
    }

    @Override
    public String getFullCancelURL() throws WorldpayConfigurationException {
        return worldpayUrlService.getFullCancelURL();
    }

    @Override
    public String getFullErrorURL() throws WorldpayConfigurationException {
        return worldpayUrlService.getFullErrorURL();
    }

    @Override
    public String getFullThreeDSecureTermURL() throws WorldpayConfigurationException {
        return worldpayUrlService.getFullThreeDSecureTermURL();
    }

    @Override
    public String getFullThreeDSecureQuoteTermURL() throws WorldpayConfigurationException {
        return worldpayUrlService.getFullThreeDSecureQuoteTermURL();
    }

    @Override
    public String getFullTermsUrl() throws WorldpayConfigurationException {
        return worldpayUrlService.getFullTermsUrl();
    }

    @Override
    public String getBaseWebsiteUrlForSite() throws WorldpayConfigurationException {
        return worldpayUrlService.getBaseWebsiteUrlForSite();
    }

    @Override
    public String getKlarnaConfirmationURL() throws WorldpayConfigurationException {
        return worldpayUrlService.getKlarnaConfirmationURL();
    }

    @Override
    public String getWebsiteUrlForCurrentSite() {
        return worldpayUrlService.getWebsiteUrlForCurrentSite();
    }

    @Override
    public String getFullThreeDSecureFlexFlowReturnUrl() {
        return getThreeDSecureFlexFlowReturnUrl();
    }

    @Override
    public String getFullThreeDSecureFlexAutosubmitUrl() throws WorldpayConfigurationException {
        return worldpayUrlService.getFullThreeDSecureFlexAutosubmitUrl();
    }


}

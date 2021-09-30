package com.worldpay.service.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.model.WorldpayThreeDS2JsonWebTokenConfigurationModel;
import com.worldpay.service.WorldpayUrlService;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;

/**
 * Default implementation of the {@link WorldpayUrlService}
 * <p>
 * Provides a mechanism for building a fully qualified site url from an absolute path given that the paths are provided
 * via configuration
 * </p>
 */
public class DefaultWorldpayUrlService extends AbstractWorldpayUrlService {

    private static final String EMPTY_STRING = "";

    protected final BaseSiteService baseSiteService;
    protected final SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    public DefaultWorldpayUrlService(final BaseSiteService baseSiteService,
                                     final SiteBaseUrlResolutionService siteBaseUrlResolutionService) {
        this.baseSiteService = baseSiteService;
        this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
    }

    @Override
    public String getWebsiteUrlForCurrentSite() {
        return siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSiteService.getCurrentBaseSite(), true, null);
    }

    /**
     * Resolves a given URL to a full URL including server and port, etc.
     *
     * @param url      - the URL to resolve
     * @param isSecure - flag to indicate whether the final URL should use a secure connection or not.
     * @return a full URL including HTTP protocol, server, port, path etc.
     */
    protected String getFullUrl(final String url, final boolean isSecure) throws WorldpayConfigurationException {
        final BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();

        final String fullResponseUrl = siteBaseUrlResolutionService.getWebsiteUrlForSite(currentBaseSite, isSecure, url);
        if (fullResponseUrl == null) {
            throw new WorldpayConfigurationException("The URL returned from the SiteBaseUrlResolutionService is null. Please check the configuration of the HOP URL's");
        }
        return fullResponseUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullSuccessURL() throws WorldpayConfigurationException {
        return getFullUrl(getSuccessPath(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullPendingURL() throws WorldpayConfigurationException {
        return getFullUrl(getPendingPath(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullFailureURL() throws WorldpayConfigurationException {
        return getFullUrl(getFailurePath(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullCancelURL() throws WorldpayConfigurationException {
        return getFullUrl(getCancelPath(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullErrorURL() throws WorldpayConfigurationException {
        return getFullUrl(getErrorPath(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullThreeDSecureTermURL() throws WorldpayConfigurationException {
        return getFullUrl(getThreeDSecureTermPath(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullThreeDSecureQuoteTermURL() throws WorldpayConfigurationException {
        return getFullUrl(getThreeDSecureQuoteTermPath(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullTermsUrl() throws WorldpayConfigurationException {
        return getFullUrl(getTermsPath(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBaseWebsiteUrlForSite() throws WorldpayConfigurationException {
        return getFullUrl(EMPTY_STRING, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKlarnaConfirmationURL() throws WorldpayConfigurationException {
        return getFullUrl(getKlarnaConfirmationPath(), true);
    }

    @Override
    public String getFullThreeDSecureFlexFlowReturnUrl() throws WorldpayConfigurationException {
        return getFullUrl(getThreeDS2Configuration().getFlowReturnUrl(), true);
    }

    @Override
    public String getFullThreeDSecureFlexAutosubmitUrl() throws WorldpayConfigurationException {
        return getFullUrl(getThreeDS2Configuration().getAuthSubmit(), true);
    }

    @Override
    public WorldpayThreeDS2JsonWebTokenConfigurationModel getThreeDS2Configuration() {
        return baseSiteService.getCurrentBaseSite().getWebMerchantConfiguration().getThreeDSFlexJsonWebTokenSettings();
    }
}

package com.worldpay.service;

import com.worldpay.service.impl.DefaultWorldpayUrlService;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.site.BaseSiteService;

public class OCCWorldpayUrlService extends DefaultWorldpayUrlService implements WorldpayUrlService {

    public OCCWorldpayUrlService(final BaseSiteService baseSiteService,
                                 final SiteBaseUrlResolutionService siteBaseUrlResolutionService) {
        super(baseSiteService, siteBaseUrlResolutionService);
    }

    @Override
    public String getFullThreeDSecureFlexFlowReturnUrl() {
        return getThreeDS2Configuration().getOccFlowReturnUrl();
    }
}

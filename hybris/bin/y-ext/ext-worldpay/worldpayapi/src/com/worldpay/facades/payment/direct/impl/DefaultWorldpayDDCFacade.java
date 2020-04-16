package com.worldpay.facades.payment.direct.impl;

import com.worldpay.config.merchant.ThreeDSFlexJsonWebTokenCredentials;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.facades.payment.direct.WorldpayDDCFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.service.payment.WorldpayJsonWebTokenService;

import java.util.Optional;

/**
 * Implementation of the DDC Facade to retrieve the information related to the DDC information
 */
public class DefaultWorldpayDDCFacade implements WorldpayDDCFacade {

    private final WorldpayJsonWebTokenService worldpayJsonWebTokenService;
    private final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;

    /**
     * Default constructor
     *
     * @param worldpayJsonWebTokenService      injected
     * @param worldpayMerchantConfigDataFacade injected
     */
    public DefaultWorldpayDDCFacade(final WorldpayJsonWebTokenService worldpayJsonWebTokenService, final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade) {
        this.worldpayJsonWebTokenService = worldpayJsonWebTokenService;
        this.worldpayMerchantConfigDataFacade = worldpayMerchantConfigDataFacade;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createJsonWebTokenForDDC() {
        final WorldpayMerchantConfigData merchantConfigData = worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData();
        return worldpayJsonWebTokenService.createJsonWebTokenForDDC(merchantConfigData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventOriginDomainForDDC() {
        return Optional.ofNullable(worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData())
                .map(WorldpayMerchantConfigData::getThreeDSFlexJsonWebTokenSettings)
                .map(ThreeDSFlexJsonWebTokenCredentials::getEventOriginDomain)
                .orElse(null);
    }
}

package com.worldpay.facades.payment.hosted.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.payment.WorldpayAfterRedirectValidationService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class DefaultWorldpayAfterRedirectValidationFacade implements com.worldpay.facades.payment.hosted.WorldpayAfterRedirectValidationFacade {

    private static final Logger LOG = LogManager.getLogger(DefaultWorldpayAfterRedirectValidationFacade.class);
    private static final String WORLDPAY_MERCHANT_CODE = "worldpayMerchantCode";

    protected final SessionService sessionService;
    protected final WorldpayMerchantInfoService worldpayMerchantInfoService;
    protected final WorldpayAfterRedirectValidationService worldpayAfterRedirectValidationService;

    public DefaultWorldpayAfterRedirectValidationFacade(final SessionService sessionService,
                                                        final WorldpayMerchantInfoService worldpayMerchantInfoService,
                                                        final WorldpayAfterRedirectValidationService worldpayAfterRedirectValidationService) {
        this.sessionService = sessionService;
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
        this.worldpayAfterRedirectValidationService = worldpayAfterRedirectValidationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateRedirectResponse(final Map<String, String> worldpayResponse) {
        final String merchantCode = sessionService.getAttribute(WORLDPAY_MERCHANT_CODE);
        try {
            final MerchantInfo merchantInfo = worldpayMerchantInfoService.getMerchantInfoByCode(merchantCode);
            return worldpayAfterRedirectValidationService.validateRedirectResponse(merchantInfo, worldpayResponse);
        } catch (final WorldpayConfigurationException e) {
            LOG.error("There was an error getting the configuration for the merchants: [{}]", e.getMessage(), e);
        }
        return false;
    }
}

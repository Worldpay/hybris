package com.worldpay.strategy.impl;

import com.worldpay.merchant.configuration.services.WorldpayMerchantConfigurationService;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import de.hybris.platform.assistedserviceservices.AssistedServiceService;

/**
 * Default implementation of Worldpay merchant strategy
 */
public class DefaultWorldpayMerchantStrategy implements WorldpayMerchantStrategy {

    private AssistedServiceService assistedServiceService;
    private WorldpayMerchantConfigurationService worldpayMerchantConfigurationService;

    public DefaultWorldpayMerchantStrategy(final AssistedServiceService assistedServiceService,
                                           final WorldpayMerchantConfigurationService worldpayMerchantConfigurationService) {
        this.assistedServiceService = assistedServiceService;
        this.worldpayMerchantConfigurationService = worldpayMerchantConfigurationService;
    }

    @Override
    public WorldpayMerchantConfigurationModel getMerchant() {
        if (isASMEnabled()) {
            return worldpayMerchantConfigurationService.getCurrentAsmConfiguration();
        } else {
            return worldpayMerchantConfigurationService.getCurrentWebConfiguration();
        }
    }

    protected boolean isASMEnabled() {
        return assistedServiceService.getAsmSession() != null && assistedServiceService.getAsmSession().getAgent() != null;
    }
}

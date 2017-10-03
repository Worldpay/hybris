package com.worldpay.strategy.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.merchant.WorldpayMerchantConfigDataService;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

/**
 * Default implementation of Worldpay merchant strategy
 */
public class DefaultWorldpayMerchantStrategy implements WorldpayMerchantStrategy {
    private static final String WEB = "web";
    private static final String ASM_MERCHANT = "asm";
    private static final String REPLENISHMENT_MERCHANT = "replenishment";

    private WorldpayMerchantConfigDataService worldpayMerchantConfigDataService;
    private AssistedServiceService assistedServiceService;

    @Override
    public WorldpayMerchantConfigData getMerchant() {
        final Map<String, WorldpayMerchantConfigData> merchantConfiguration = worldpayMerchantConfigDataService.getMerchantConfiguration();
        if (isASMEnabled()) {
            return merchantConfiguration.get(ASM_MERCHANT);
        } else {
            return merchantConfiguration.get(WEB);
        }
    }

    @Override
    public WorldpayMerchantConfigData getReplenishmentMerchant() {
        return worldpayMerchantConfigDataService.getMerchantConfiguration().get(REPLENISHMENT_MERCHANT);
    }

    protected boolean isASMEnabled() {
        return assistedServiceService.getAsmSession() != null && assistedServiceService.getAsmSession().getAgent() != null;
    }

    @Required
    public void setWorldpayMerchantConfigDataService(final WorldpayMerchantConfigDataService worldpayMerchantConfigDataService) {
        this.worldpayMerchantConfigDataService = worldpayMerchantConfigDataService;
    }

    @Required
    public void setAssistedServiceService(final AssistedServiceService assistedServiceService) {
        this.assistedServiceService = assistedServiceService;
    }
}

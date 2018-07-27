package com.worldpay.service.apm.strategy.impl;

import com.worldpay.core.services.WorldpayBankConfigurationLookupService;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.apm.strategy.APMAvailabilityStrategy;
import de.hybris.platform.core.model.order.CartModel;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@see APMAvailabilityStrategy}
 * <p>
 * Strategy that checks the availability of an APM based on the countries defined in an {@link WorldpayAPMConfigurationModel}
 */
public class APMAvailabilityBankStrategy implements APMAvailabilityStrategy {

    private WorldpayBankConfigurationLookupService worldpayBankConfigurationLookupService;

    /**
     * Returns true if the APM is not a bank or if the configured bank list for the APM is not empty.
     *
     * @param apmConfiguration The configuration of the APM to check {@link WorldpayAPMConfigurationModel}
     * @param cartModel        The current order {@link CartModel} used to get the country to send the order to.
     * @return Returns true if the APM is not a bank or if the configured bank list for the APM is not empty.
     */
    @Override
    public boolean isAvailable(final WorldpayAPMConfigurationModel apmConfiguration, final CartModel cartModel) {
        if (!apmConfiguration.getBank()) {
            return true;
        } else {
            return CollectionUtils.isNotEmpty(worldpayBankConfigurationLookupService.getActiveBankConfigurationsForCode(apmConfiguration.getCode()));
        }
    }

    @Required
    public void setWorldpayBankConfigurationLookupService(WorldpayBankConfigurationLookupService worldpayBankConfigurationLookupService) {
        this.worldpayBankConfigurationLookupService = worldpayBankConfigurationLookupService;
    }
}

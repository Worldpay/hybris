package com.worldpay.service.payment.impl;

import com.worldpay.merchant.configuration.services.WorldpayMerchantConfigurationService;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpayAdditionalDataRequestStrategy;
import com.worldpay.service.payment.WorldpayLevel23Strategy;
import com.worldpay.service.request.AuthoriseRequestParameters.AuthoriseRequestParametersCreator;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.Optional;

/**
 * Default implementation of {@link WorldpayLevel23Strategy}.
 */
public class DefaultWorldpayEFTPOSRoutingStrategy implements WorldpayAdditionalDataRequestStrategy {

    protected final WorldpayMerchantConfigurationService worldpayMerchantConfigurationService;

    public DefaultWorldpayEFTPOSRoutingStrategy(WorldpayMerchantConfigurationService worldpayMerchantConfigurationService) {
        this.worldpayMerchantConfigurationService = worldpayMerchantConfigurationService;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void populateRequestWithAdditionalData(final AbstractOrderModel cart,
                                                  final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                  final AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {
        final WorldpayMerchantConfigurationModel currentWebConfiguration = Optional.ofNullable(worldpayMerchantConfigurationService)
                .map(WorldpayMerchantConfigurationService::getCurrentWebConfiguration)
                .orElse(null);

        if (currentWebConfiguration != null && Boolean.TRUE.equals(currentWebConfiguration.getRoutingEnabled())) {
            authoriseRequestParametersCreator.withRoutingMID(currentWebConfiguration.getRoutingMID());
        }
    }
}

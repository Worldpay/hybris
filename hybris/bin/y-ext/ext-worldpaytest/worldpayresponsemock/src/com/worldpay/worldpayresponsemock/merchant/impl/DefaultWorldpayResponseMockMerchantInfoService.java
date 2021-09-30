package com.worldpay.worldpayresponsemock.merchant.impl;

import com.worldpay.merchant.configuration.services.WorldpayMerchantConfigurationService;
import com.worldpay.merchant.impl.DefaultWorldpayMerchantInfoService;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import com.worldpay.worldpayresponsemock.merchant.WorldpayResponseMockMerchantInfoService;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayResponseMockMerchantInfoService extends DefaultWorldpayMerchantInfoService implements WorldpayResponseMockMerchantInfoService {

    public DefaultWorldpayResponseMockMerchantInfoService(final WorldpayMerchantStrategy worldpayMerchantStrategy,
                                                          final WorldpayMerchantConfigurationService worldpayMerchantConfigurationService) {
        super(worldpayMerchantStrategy, worldpayMerchantConfigurationService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAllMerchantCodes() {
        return worldpayMerchantConfigurationService.getAllSystemActiveSiteMerchantConfigurations().stream()
            .map(WorldpayMerchantConfigurationModel::getCode)
            .collect(toSet());
    }
}

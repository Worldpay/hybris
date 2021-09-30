package com.worldpay.facades.payment.merchant.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import de.hybris.platform.servicelayer.dto.converter.Converter;


/**
 * {@inheritDoc}
 */
public class DefaultWorldpayMerchantConfigDataFacade implements WorldpayMerchantConfigDataFacade {

    protected final WorldpayMerchantStrategy worldpayMerchantStrategy;
    protected final Converter<WorldpayMerchantConfigurationModel, WorldpayMerchantConfigData> worldPayMerchantConfigDataConverter;

    public DefaultWorldpayMerchantConfigDataFacade(final WorldpayMerchantStrategy worldpayMerchantStrategy,
                                                   final Converter<WorldpayMerchantConfigurationModel, WorldpayMerchantConfigData> worldPayMerchantConfigDataConverter) {
        this.worldpayMerchantStrategy = worldpayMerchantStrategy;
        this.worldPayMerchantConfigDataConverter = worldPayMerchantConfigDataConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayMerchantConfigData getCurrentSiteMerchantConfigData() {
        return worldPayMerchantConfigDataConverter.convert(worldpayMerchantStrategy.getMerchant());
    }
}

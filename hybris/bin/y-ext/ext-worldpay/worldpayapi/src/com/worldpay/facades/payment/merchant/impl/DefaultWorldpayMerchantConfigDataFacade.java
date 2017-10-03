package com.worldpay.facades.payment.merchant.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import org.springframework.beans.factory.annotation.Required;


/**
 * {@inheritDoc}
 */
public class DefaultWorldpayMerchantConfigDataFacade implements WorldpayMerchantConfigDataFacade {

    private WorldpayMerchantStrategy worldpayMerchantStrategy;

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayMerchantConfigData getCurrentSiteMerchantConfigData() {
        return worldpayMerchantStrategy.getMerchant();
    }

    @Required
    public void setWorldpayMerchantStrategy(final WorldpayMerchantStrategy worldpayMerchantStrategy) {
        this.worldpayMerchantStrategy = worldpayMerchantStrategy;
    }
}

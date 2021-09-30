package com.worldpay.strategy;

import com.worldpay.model.WorldpayMerchantConfigurationModel;

/**
 * Exposes methods to retrieve the configured merchants.
 */
public interface WorldpayMerchantStrategy {

    /**
     * Returns the merchant configured
     *
     * @return {@link WorldpayMerchantConfigurationModel}
     */
    WorldpayMerchantConfigurationModel getMerchant();

}

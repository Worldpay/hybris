package com.worldpay.strategy;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;

/**
 * Exposes methods to retrieve the configured merchants.
 */
public interface WorldpayMerchantStrategy {

    /**
     * Returns the merchant configured
     *
     * @return
     */
    WorldpayMerchantConfigData getMerchant();

    /**
     * Returns the merchant configured for Replenishment
     *
     * @return
     */
    WorldpayMerchantConfigData getReplenishmentMerchant();
}

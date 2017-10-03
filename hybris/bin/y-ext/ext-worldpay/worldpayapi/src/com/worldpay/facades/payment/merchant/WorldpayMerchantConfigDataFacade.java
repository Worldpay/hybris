package com.worldpay.facades.payment.merchant;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;

/**
 * Exposes methods to handle the Worldpay merchant configuration.
 */
public interface WorldpayMerchantConfigDataFacade {
    /**
     * Gets the merchant to be used on the current site
     *
     * @return {@link WorldpayMerchantConfigData}
     */
    WorldpayMerchantConfigData getCurrentSiteMerchantConfigData();

}

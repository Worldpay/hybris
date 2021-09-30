package com.worldpay.merchant.configuration.services;

import com.worldpay.model.WorldpayMerchantConfigurationModel;

import java.util.Set;

/**
 * Manages the merchant configurations
 */
public interface WorldpayMerchantConfigurationService {

    /**
     * Returns the WorldpayMerchantConfigurationModel for the current site and web configuration
     *
     * @return a {@link WorldpayMerchantConfigurationModel}
     */
    WorldpayMerchantConfigurationModel getCurrentWebConfiguration();

    /**
     * Returns the WorldpayMerchantConfigurationModel for the current site and asm configuration
     *
     * @return a {@link WorldpayMerchantConfigurationModel}
     */
    WorldpayMerchantConfigurationModel getCurrentAsmConfiguration();

    /**
     * Returns the set of WorldpayMerchantConfigurationModel for the current site
     *
     * @return a {@link Set<WorldpayMerchantConfigurationModel>}
     */
    Set<WorldpayMerchantConfigurationModel> getAllCurrentSiteMerchantConfigurations();

    /**
     * Returns the set of WorldpayMerchantConfigurationModel related to all the active sites in the system
     *
     * @return a {@link Set<WorldpayMerchantConfigurationModel>}
     */
    Set<WorldpayMerchantConfigurationModel> getAllSystemActiveSiteMerchantConfigurations();
}

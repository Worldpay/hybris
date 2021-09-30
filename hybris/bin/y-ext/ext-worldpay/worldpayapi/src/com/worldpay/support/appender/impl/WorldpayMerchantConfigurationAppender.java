package com.worldpay.support.appender.impl;

import com.worldpay.merchant.configuration.services.WorldpayMerchantConfigurationService;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import com.worldpay.support.appender.WorldpaySupportEmailAppender;

import java.util.Collection;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.join;

/**
 * Implementation of {@see WorldpaySupportEmailAppender } to include the merchants configured
 */
public class WorldpayMerchantConfigurationAppender extends WorldpaySupportEmailAppender {

    protected final WorldpayMerchantConfigurationService worldpayMerchantConfigurationService;

    public WorldpayMerchantConfigurationAppender(final WorldpayMerchantConfigurationService worldpayMerchantConfigurationService) {
        this.worldpayMerchantConfigurationService = worldpayMerchantConfigurationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String appendContent() {
        final StringBuilder merchantConfiguration = new StringBuilder();
        merchantConfiguration.append(System.lineSeparator()).append("Merchant Configuration:").append(System.lineSeparator());
        final Set<WorldpayMerchantConfigurationModel> allSystemActiveSiteMerchantConfigurations = worldpayMerchantConfigurationService.getAllSystemActiveSiteMerchantConfigurations();
        for (WorldpayMerchantConfigurationModel configuredMerchant : allSystemActiveSiteMerchantConfigurations) {
            merchantConfiguration.append(ONE_TAB).append("Configured Merchant Id: ").append(configuredMerchant.getIdentifier()).append(System.lineSeparator());
            merchantConfiguration.append(TWO_TABS).append("Merchant Code: ").append(configuredMerchant.getCode()).append(System.lineSeparator());
            merchantConfiguration.append(TWO_TABS).append("Merchant InstallationId: ").append(configuredMerchant.getInstallationId()).append(System.lineSeparator());
            merchantConfiguration.append(TWO_TABS).append("Mac Validation: ").append(configuredMerchant.getMacValidation()).append(System.lineSeparator());
            merchantConfiguration.append(TWO_TABS).append("Included payment methods: ");
            final Collection<String> includedPaymentTypes = configuredMerchant.getIncludedPaymentTypes();
            if (includedPaymentTypes != null) {
                merchantConfiguration.append(join(includedPaymentTypes, ","));
            } else {
                merchantConfiguration.append("None");
            }
            merchantConfiguration.append(System.lineSeparator()).append(TWO_TABS).append("Excluded payment methods: ");
            final Collection<String> excludedPaymentTypes = configuredMerchant.getExcludedPaymentTypes();
            if (excludedPaymentTypes != null) {
                merchantConfiguration.append(join(excludedPaymentTypes, ",")).append(System.lineSeparator());
            } else {
                merchantConfiguration.append("None").append(System.lineSeparator());
            }
        }
        return merchantConfiguration.toString();
    }
}

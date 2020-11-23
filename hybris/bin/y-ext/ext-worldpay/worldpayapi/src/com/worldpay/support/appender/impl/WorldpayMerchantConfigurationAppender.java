package com.worldpay.support.appender.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.support.appender.WorldpaySupportEmailAppender;
import de.hybris.platform.core.Registry;
import org.springframework.beans.factory.BeanFactoryUtils;

import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.join;

/**
 * Implementation of {@see WorldpaySupportEmailAppender } to include the merchants configured
 */
public class WorldpayMerchantConfigurationAppender extends WorldpaySupportEmailAppender {

    /**
     * {@inheritDoc}
     */
    @Override
    public String appendContent() {
        final StringBuilder merchantConfiguration = new StringBuilder();
        merchantConfiguration.append(System.lineSeparator()).append("Merchant Configuration:").append(System.lineSeparator());
        final Map<String, WorldpayMerchantConfigData> merchantConfigurations = getConfiguredMerchants();
        for (Map.Entry<String, WorldpayMerchantConfigData> configuredMerchant : merchantConfigurations.entrySet()) {
            merchantConfiguration.append(ONE_TAB).append("Configured Merchant Bean Name: ").append(configuredMerchant.getKey()).append(System.lineSeparator());
            merchantConfiguration.append(TWO_TABS).append("Merchant Code: ").append(configuredMerchant.getValue().getCode()).append(System.lineSeparator());
            merchantConfiguration.append(TWO_TABS).append("Merchant InstallationId: ").append(configuredMerchant.getValue().getInstallationId()).append(System.lineSeparator());
            merchantConfiguration.append(TWO_TABS).append("Mac Validation: ").append(configuredMerchant.getValue().getMacValidation()).append(System.lineSeparator());
            merchantConfiguration.append(TWO_TABS).append("Included payment methods: ");
            final List<String> includedPaymentTypes = configuredMerchant.getValue().getIncludedPaymentTypes();
            if (includedPaymentTypes != null) {
                merchantConfiguration.append(join(includedPaymentTypes, ","));
            } else {
                merchantConfiguration.append("None");
            }
            merchantConfiguration.append(System.lineSeparator()).append(TWO_TABS).append("Excluded payment methods: ");
            final List<String> excludedPaymentTypes = configuredMerchant.getValue().getExcludedPaymentTypes();
            if (excludedPaymentTypes != null) {
                merchantConfiguration.append(join(excludedPaymentTypes, ",")).append(System.lineSeparator());
            } else {
                merchantConfiguration.append("None").append(System.lineSeparator());
            }
        }
        return merchantConfiguration.toString();
    }

    protected Map<String, WorldpayMerchantConfigData> getConfiguredMerchants() {
        return BeanFactoryUtils.beansOfTypeIncludingAncestors(Registry.getApplicationContext(), WorldpayMerchantConfigData.class);
    }
}

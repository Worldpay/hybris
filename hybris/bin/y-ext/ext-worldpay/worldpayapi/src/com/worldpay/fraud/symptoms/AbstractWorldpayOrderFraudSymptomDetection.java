package com.worldpay.fraud.symptoms;

import com.worldpay.constants.WorldpayapiConstants;
import de.hybris.platform.fraud.strategy.AbstractOrderFraudSymptomDetection;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * The abstract implementation of the {@link AbstractOrderFraudSymptomDetection}.
 * The fraud score limit is retrieved from the {@code worldpayapi.fraud.scoreLimit} property.
 */
public abstract class AbstractWorldpayOrderFraudSymptomDetection extends AbstractOrderFraudSymptomDetection {

    private static final String SCORE_LIMIT_PROPERTY_NAME = WorldpayapiConstants.EXTENSIONNAME + ".fraud.scoreLimit";

    private ConfigurationService configurationService;

    protected double getScoreLimit() {
        final String configuredScoreLimit = configurationService.getConfiguration().getString(SCORE_LIMIT_PROPERTY_NAME);
        return Double.parseDouble(configuredScoreLimit);
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}

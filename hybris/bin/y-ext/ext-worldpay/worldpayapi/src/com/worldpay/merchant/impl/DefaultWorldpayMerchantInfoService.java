package com.worldpay.merchant.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContextAware;

import java.text.MessageFormat;
import java.util.List;

/**
 * This implementation of {@link WorldpayMerchantInfoService} uses looks the configured map of merchants configured in Spring
 * using the hybris {@link SiteConfigService} and the spring capabilities to retrieve a bean defined in the application context using
 * {@link ApplicationContextAware}
 */
public class DefaultWorldpayMerchantInfoService implements WorldpayMerchantInfoService {

    private WorldpayMerchantStrategy worldpayMerchantStrategy;

    @Autowired
    private List<WorldpayMerchantConfigData> configuredMerchants;

    /**
     * {@inheritDoc}
     */
    @Override
    public MerchantInfo getCurrentSiteMerchant() {
        final WorldpayMerchantConfigData currentSiteMerchantConfigData = worldpayMerchantStrategy.getMerchant();
        return createMerchantInfo(currentSiteMerchantConfigData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MerchantInfo getReplenishmentMerchant() {
        final WorldpayMerchantConfigData replensihmentMerchantConfigData = worldpayMerchantStrategy.getReplenishmentMerchant();
        return createMerchantInfo(replensihmentMerchantConfigData);
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public MerchantInfo getMerchantInfoByCode(final String merchantCode) throws WorldpayConfigurationException {
        final WorldpayMerchantConfigData worldpayMerchantConfigData = configuredMerchants.stream()
                .filter(config -> config.getCode().equals(merchantCode))
                .findAny()
                .orElseThrow(() -> new WorldpayConfigurationException(MessageFormat.format("No merchant configuration found for merchant code {0}", merchantCode)));
        return createMerchantInfo(worldpayMerchantConfigData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MerchantInfo getMerchantInfoFromTransaction(final PaymentTransactionModel paymentTransactionModel) throws WorldpayConfigurationException {
        return getMerchantInfoByCode(paymentTransactionModel.getRequestToken());

    }

    protected MerchantInfo createMerchantInfo(final WorldpayMerchantConfigData worldpayMerchantConfigData) {
        final MerchantInfo merchantInfo = new MerchantInfo(worldpayMerchantConfigData.getCode(), worldpayMerchantConfigData.getPassword());
        if (worldpayMerchantConfigData.getMacValidation()) {
            merchantInfo.setUsingMacValidation(true);
            merchantInfo.setMacSecret(worldpayMerchantConfigData.getMacSecret());
        }
        return merchantInfo;
    }

    @Required
    public void setWorldpayMerchantStrategy(final WorldpayMerchantStrategy worldpayMerchantStrategy) {
        this.worldpayMerchantStrategy = worldpayMerchantStrategy;
    }

    public void setConfiguredMerchants(final List<WorldpayMerchantConfigData> configuredMerchants) {
        this.configuredMerchants = configuredMerchants;
    }

    public List<WorldpayMerchantConfigData> getConfiguredMerchants() {
        return configuredMerchants;
    }
}

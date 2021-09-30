package com.worldpay.merchant.impl;

import com.worldpay.data.MerchantInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.merchant.configuration.services.WorldpayMerchantConfigurationService;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.ApplicationContextAware;

import java.text.MessageFormat;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This implementation of {@link WorldpayMerchantInfoService} uses looks the configured map of merchants configured in Spring
 * using the hybris {@link SiteConfigService} and the spring capabilities to retrieve a bean defined in the application context using
 * {@link ApplicationContextAware}
 */
public class DefaultWorldpayMerchantInfoService implements WorldpayMerchantInfoService {

    private static final String CURRENT_BASE_SITE_CANNOT_BE_NULL = "Current base site cannot be null";

    protected final WorldpayMerchantStrategy worldpayMerchantStrategy;
    protected final WorldpayMerchantConfigurationService worldpayMerchantConfigurationService;

    public DefaultWorldpayMerchantInfoService(final WorldpayMerchantStrategy worldpayMerchantStrategy,
                                              final WorldpayMerchantConfigurationService worldpayMerchantConfigurationService) {
        this.worldpayMerchantStrategy = worldpayMerchantStrategy;
        this.worldpayMerchantConfigurationService = worldpayMerchantConfigurationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MerchantInfo getCurrentSiteMerchant() {
        final WorldpayMerchantConfigurationModel currentSiteMerchantConfig = worldpayMerchantStrategy.getMerchant();
        return createMerchantInfo(currentSiteMerchantConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MerchantInfo getReplenishmentMerchant(final BaseSiteModel site) {
        checkArgument(site != null, CURRENT_BASE_SITE_CANNOT_BE_NULL);
        checkArgument(site.getReplenishmentMerchantConfiguration() != null, "The Replenishment for site " + site.getUid() + " cannot be null");

        return createMerchantInfo(site.getReplenishmentMerchantConfiguration());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MerchantInfo getMerchantInfoByCode(final String merchantCode) throws WorldpayConfigurationException {
        final WorldpayMerchantConfigurationModel worldpayMerchantConfiguration = worldpayMerchantConfigurationService.getAllSystemActiveSiteMerchantConfigurations()
            .stream()
            .filter(merchantConfig -> merchantCode.equalsIgnoreCase(merchantConfig.getCode()))
            .findAny()
            .orElseThrow(() -> new WorldpayConfigurationException(MessageFormat.format("No merchant configuration found for merchant code {0}", merchantCode)));
        return createMerchantInfo(worldpayMerchantConfiguration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MerchantInfo getMerchantInfoFromTransaction(final PaymentTransactionModel paymentTransactionModel) throws WorldpayConfigurationException {
        return getMerchantInfoByCode(paymentTransactionModel.getRequestToken());

    }

    protected MerchantInfo createMerchantInfo(final WorldpayMerchantConfigurationModel worldpayMerchantConfiguration) {
        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantCode(worldpayMerchantConfiguration.getCode());
        merchantInfo.setMerchantPassword(worldpayMerchantConfiguration.getPassword());
        merchantInfo.setUsingMacValidation(false);
        if (BooleanUtils.isTrue(worldpayMerchantConfiguration.getMacValidation())) {
            merchantInfo.setUsingMacValidation(true);
            merchantInfo.setMacSecret(worldpayMerchantConfiguration.getMacSecret());
        }
        return merchantInfo;
    }
}

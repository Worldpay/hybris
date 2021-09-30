package com.worldpay.merchant.configuration.services.impl;

import com.worldpay.merchant.configuration.services.WorldpayMerchantConfigurationService;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.site.BaseSiteService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;

/**
 * Default implementation of the {@link WorldpayMerchantConfigurationService}
 */
public class DefaultWorldpayMerchantConfigurationService implements WorldpayMerchantConfigurationService {

    private static final String CURRENT_BASE_SITE_CANNOT_BE_NULL = "Current base site cannot be null";

    protected final BaseSiteService baseSiteService;

    public DefaultWorldpayMerchantConfigurationService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayMerchantConfigurationModel getCurrentWebConfiguration() {
        final BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
        checkArgument(currentBaseSite != null, CURRENT_BASE_SITE_CANNOT_BE_NULL);

        return currentBaseSite.getWebMerchantConfiguration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayMerchantConfigurationModel getCurrentAsmConfiguration() {
        final BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
        checkArgument(currentBaseSite != null, CURRENT_BASE_SITE_CANNOT_BE_NULL);

        return currentBaseSite.getAsmMerchantConfiguration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<WorldpayMerchantConfigurationModel> getAllCurrentSiteMerchantConfigurations() {
        final BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
        checkArgument(currentBaseSite != null, CURRENT_BASE_SITE_CANNOT_BE_NULL);

        return Set.of(currentBaseSite.getWebMerchantConfiguration(), currentBaseSite.getAsmMerchantConfiguration(), currentBaseSite.getReplenishmentMerchantConfiguration());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<WorldpayMerchantConfigurationModel> getAllSystemActiveSiteMerchantConfigurations() {
        final List<CMSSiteModel> allActiveSites = baseSiteService.getAllBaseSites()
            .stream()
            .map(CMSSiteModel.class::cast)
            .filter(CMSSiteModel::getActive)
            .collect(Collectors.toList());

        final Set<WorldpayMerchantConfigurationModel> systemMerchantConfigurations = new HashSet<>();
        allActiveSites.forEach(site -> systemMerchantConfigurations.addAll(asList(
            site.getWebMerchantConfiguration(),
            site.getAsmMerchantConfiguration(),
            site.getReplenishmentMerchantConfiguration())));
        systemMerchantConfigurations.remove(null);

        return systemMerchantConfigurations;
    }
}

package com.worldpay.service.impl;

import com.worldpay.core.dao.WorldpayIntegrationVersionDao;
import com.worldpay.model.IntegrationVersionModel;
import com.worldpay.service.WorldpayIntegrationVersionService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class  DefaultWorldpayIntegrationVersionService implements WorldpayIntegrationVersionService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpayAuthorisationResultService.class);


    protected final ModelService modelService;
    protected final ConfigurationService configurationService;
    protected final WorldpayIntegrationVersionDao worldpayIntegrationVersionDao;

    private static final String WORLDPAY_INTEGRATION_VERSION = "worldpay.release.version";

    public DefaultWorldpayIntegrationVersionService(final ModelService modelService,
                                                    final ConfigurationService configurationService,
                                                    final WorldpayIntegrationVersionDao worldpayIntegrationVersionDao) {
        this.modelService = modelService;
        this.configurationService = configurationService;
        this.worldpayIntegrationVersionDao = worldpayIntegrationVersionDao;
    }

    protected IntegrationVersionModel recordCurrentIntegrationVersion() {
        final String integrationVersion = configurationService.getConfiguration().getString(WORLDPAY_INTEGRATION_VERSION);

        if (StringUtils.isNotBlank(integrationVersion)) {

            final IntegrationVersionModel newIntegrationVersionModel = modelService.create(IntegrationVersionModel.class);
            newIntegrationVersionModel.setVersionNumber(integrationVersion);
            newIntegrationVersionModel.setDate(new java.util.Date());
            modelService.save(newIntegrationVersionModel);

            return newIntegrationVersionModel;
        }

        return null;
    }

    @Override
    public String getPreviousThreeIntegrationVersions() {
        final List<IntegrationVersionModel> lastVersions = worldpayIntegrationVersionDao.findLastThreeVersions();
        if (CollectionUtils.isNotEmpty(lastVersions) && lastVersions.size() > 1) {
            return lastVersions.stream()
                    .map(IntegrationVersionModel::getVersionNumber)
                    .collect(Collectors.joining(","));
        } else {
            return StringUtils.EMPTY;
        }
    }

    @Override
    public IntegrationVersionModel getIntegrationVersionByNumber(final String versionNumber) {
        try {
            return worldpayIntegrationVersionDao.findByVersionNumber(versionNumber);
        } catch (ModelNotFoundException e) {
            return this.recordCurrentIntegrationVersion();
        } catch (AmbiguousIdentifierException e) {
            LOG.warn(String.format("Multiple IntegrationVersionModel found for version number: %s", versionNumber));
            return null;
        }
    }

    @Override
    public String getCurrentIntegrationVersionValue() {
        final String integrationVersion = configurationService.getConfiguration().getString(WORLDPAY_INTEGRATION_VERSION);

        return Optional.ofNullable(getIntegrationVersionByNumber(integrationVersion))
                .map(IntegrationVersionModel::getVersionNumber)
                .orElse(StringUtils.EMPTY);
    }
}

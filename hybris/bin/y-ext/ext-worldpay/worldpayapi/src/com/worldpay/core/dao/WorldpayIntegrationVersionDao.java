package com.worldpay.core.dao;

import com.worldpay.model.IntegrationVersionModel;
import java.util.List;

/**
 * Data access to {@link IntegrationVersionModel}
 *
 */
public interface WorldpayIntegrationVersionDao {

    /**
     * Finds the last three {@link IntegrationVersionModel} by date descending
     *
     * @return {@link IntegrationVersionModel}
     */
    List<IntegrationVersionModel> findLastThreeVersions();

    /**
     * Finds an {@link IntegrationVersionModel} by version number
     *
     * @param versionNumber the version number
     * @return {@link IntegrationVersionModel}
     */
    IntegrationVersionModel findByVersionNumber(final String versionNumber);
}

package com.worldpay.core.dao;

import com.worldpay.model.WorldpayAPMComponentModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;

import java.util.Collection;
import java.util.List;

/**
 * Data access to {@link WorldpayAPMComponentModel}
 */
public interface WorldpayAPMComponentDao {

    /**
     * Find all {@link WorldpayAPMComponentModel} for the given catalog versions.
     *
     * @param catalogVersionModels catalog versions.
     * @return a list of {@link WorldpayAPMComponentModel}.
     */
    List<WorldpayAPMComponentModel> findAllApmComponents(final Collection<CatalogVersionModel> catalogVersionModels);
}

package com.worldpay.core.dao;

import com.worldpay.model.WorldpayAPMComponentModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

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

    /**
     * Find {@link WorldpayAPMComponentModel} for the given catalog versions and apm code.
     *
     * @param catalogVersionModels catalog versions.
     * @param apmCode apm code.
     * @return {@link WorldpayAPMComponentModel}.
     */
    WorldpayAPMComponentModel findApmComponentByCode(final Collection<CatalogVersionModel> catalogVersionModels, final String apmCode) throws ModelNotFoundException, AmbiguousIdentifierException;
}

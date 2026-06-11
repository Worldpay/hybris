package com.worldpay.core.services.impl;

import java.util.List;

import com.worldpay.core.dao.WorldpayAPMComponentDao;
import com.worldpay.core.services.WorldpayAPMComponentService;
import com.worldpay.model.WorldpayAPMComponentModel;
import com.worldpay.service.apm.APMAvailabilityService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayAPMComponentService implements WorldpayAPMComponentService {

    protected final WorldpayAPMComponentDao worldpayAPMComponentDao;
    protected final CatalogVersionService catalogVersionService;
    protected final CartService cartService;
    protected final APMAvailabilityService apmAvailabilityService;

    public DefaultWorldpayAPMComponentService(final WorldpayAPMComponentDao worldpayAPMComponentDao,
                                              final CatalogVersionService catalogVersionService,
                                              final CartService cartService,
                                              final APMAvailabilityService apmAvailabilityService) {
        this.worldpayAPMComponentDao = worldpayAPMComponentDao;
        this.catalogVersionService = catalogVersionService;
        this.cartService = cartService;
        this.apmAvailabilityService = apmAvailabilityService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WorldpayAPMComponentModel> getAllAvailableWorldpayAPMComponents() {
        return worldpayAPMComponentDao.findAllApmComponents(catalogVersionService.getSessionCatalogVersions()).stream()
            .filter(worldpayAPMComponentModel -> worldpayAPMComponentModel.getApmConfiguration() != null)
            .filter(worldpayAPMComponentModel -> apmAvailabilityService.isAvailable(worldpayAPMComponentModel.getApmConfiguration(), cartService.getSessionCart()))
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayAPMComponentModel getWorldpayAPMComponentByCode(final String apmCode) {
        try {
            return worldpayAPMComponentDao.findApmComponentByCode(catalogVersionService.getSessionCatalogVersions(), apmCode);
        } catch (final ModelNotFoundException | AmbiguousIdentifierException e) {
            return null;
        }
    }
}

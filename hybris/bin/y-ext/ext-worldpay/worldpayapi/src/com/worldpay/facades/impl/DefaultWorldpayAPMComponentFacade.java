package com.worldpay.facades.impl;

import com.worldpay.core.services.WorldpayAPMComponentService;
import com.worldpay.data.cms.WorldpayAPMComponentData;
import com.worldpay.facades.WorldpayAPMComponentFacade;
import com.worldpay.model.WorldpayAPMComponentModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayAPMComponentFacade implements WorldpayAPMComponentFacade {

    protected final WorldpayAPMComponentService worldpayAPMComponentService;
    protected final Converter<WorldpayAPMComponentModel, WorldpayAPMComponentData> worldpayAPMComponentConverter;

    public DefaultWorldpayAPMComponentFacade(final WorldpayAPMComponentService worldpayAPMComponentService,
                                             final Converter<WorldpayAPMComponentModel, WorldpayAPMComponentData> worldpayAPMComponentConverter) {
        this.worldpayAPMComponentService = worldpayAPMComponentService;
        this.worldpayAPMComponentConverter = worldpayAPMComponentConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WorldpayAPMComponentData> getAllAvailableWorldpayAPMComponents() {
        return worldpayAPMComponentConverter.convertAll(worldpayAPMComponentService.getAllAvailableWorldpayAPMComponents());
    }
}

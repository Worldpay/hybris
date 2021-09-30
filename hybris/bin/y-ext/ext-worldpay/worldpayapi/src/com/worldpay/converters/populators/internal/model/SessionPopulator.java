package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Session;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.Session} with the information of a {@link Session}.
 */
public class SessionPopulator implements Populator<Session, com.worldpay.internal.model.Session> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final Session source, final com.worldpay.internal.model.Session target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        Optional.ofNullable(source.getShopperIPAddress())
            .ifPresent(target::setShopperIPAddress);

        Optional.ofNullable(source.getId())
            .ifPresent(target::setId);
    }
}

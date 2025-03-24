package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Exemption;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ExemptionPopulator implements Populator<Exemption, com.worldpay.internal.model.Exemption> {

    @Override
    public void populate(final Exemption source, final com.worldpay.internal.model.Exemption target) throws ConversionException {
        target.setPlacement(source.getPlacement());
        target.setType(source.getType());
    }
}

package com.worldpay.converters.populators.reverse.internal.model;

import com.worldpay.data.Exemption;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ExemptionReversePopulator implements Populator<com.worldpay.internal.model.Exemption, Exemption> {

    @Override
    public void populate(final com.worldpay.internal.model.Exemption source, final Exemption target) throws ConversionException {
        target.setPlacement(source.getPlacement());
        target.setType(source.getType());
    }

}

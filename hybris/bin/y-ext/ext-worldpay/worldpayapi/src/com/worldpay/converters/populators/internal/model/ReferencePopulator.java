package com.worldpay.converters.populators.internal.model;

import com.worldpay.internal.model.Reference;
import com.worldpay.data.LineItemReference;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.Reference} with the information of a {@link LineItemReference}
 */
public class ReferencePopulator implements Populator<LineItemReference, Reference> {

    /**
     * Populates the data from the {@link LineItemReference} to a {@link Reference}
     *
     * @param source a {@link LineItemReference} from Worldpay
     * @param target a {@link Reference} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final LineItemReference source, final Reference target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        target.setId(source.getId());
        target.setvalue(source.getValue());
    }
}

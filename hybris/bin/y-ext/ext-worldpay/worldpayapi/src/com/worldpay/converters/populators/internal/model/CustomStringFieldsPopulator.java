package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.CustomStringFields;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.CustomStringFields} with the information of a {@link CustomStringFields}
 */
public class CustomStringFieldsPopulator implements Populator<CustomStringFields, com.worldpay.internal.model.CustomStringFields> {

    /**
     * Populates the data from the {@link CustomStringFields} to a {@link com.worldpay.internal.model.CustomStringFields}
     * @param source a {@link CustomStringFields} from Worldpay
     * @param target a {@link com.worldpay.internal.model.CustomStringFields} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final CustomStringFields source, final com.worldpay.internal.model.CustomStringFields target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        target.setCustomStringField1(source.getCustomStringField1());
        target.setCustomStringField2(source.getCustomStringField2());
        target.setCustomStringField3(source.getCustomStringField3());
        target.setCustomStringField4(source.getCustomStringField4());
        target.setCustomStringField5(source.getCustomStringField5());
        target.setCustomStringField6(source.getCustomStringField6());
        target.setCustomStringField7(source.getCustomStringField7());
        target.setCustomStringField8(source.getCustomStringField8());
        target.setCustomStringField9(source.getCustomStringField9());
        target.setCustomStringField10(source.getCustomStringField10());
    }
}

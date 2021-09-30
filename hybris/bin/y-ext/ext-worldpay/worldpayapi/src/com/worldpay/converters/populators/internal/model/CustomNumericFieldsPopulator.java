package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.CustomNumericFields;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.CustomNumericFields} with the information of a {@link CustomNumericFields}
 */
public class CustomNumericFieldsPopulator implements Populator<CustomNumericFields, com.worldpay.internal.model.CustomNumericFields> {

    /**
     * Populates the data from the {@link CustomNumericFields} to a {@link com.worldpay.internal.model.CustomNumericFields}
     *
     * @param source a {@link CustomNumericFields} from Worldpay
     * @param target a {@link com.worldpay.internal.model.CustomNumericFields} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final CustomNumericFields source,
                         final com.worldpay.internal.model.CustomNumericFields target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        Optional.ofNullable(source.getCustomNumericField1())
            .map(Number::toString)
            .ifPresent(target::setCustomNumericField1);

        Optional.ofNullable(source.getCustomNumericField2())
            .map(Number::toString)
            .ifPresent(target::setCustomNumericField2);

        Optional.ofNullable(source.getCustomNumericField3())
            .map(Number::toString)
            .ifPresent(target::setCustomNumericField3);

        Optional.ofNullable(source.getCustomNumericField4())
            .map(Number::toString)
            .ifPresent(target::setCustomNumericField4);

        Optional.ofNullable(source.getCustomNumericField5())
            .map(Number::toString)
            .ifPresent(target::setCustomNumericField5);

        Optional.ofNullable(source.getCustomNumericField6())
            .map(Number::toString)
            .ifPresent(target::setCustomNumericField6);

        Optional.ofNullable(source.getCustomNumericField7())
            .map(Number::toString)
            .ifPresent(target::setCustomNumericField7);

        Optional.ofNullable(source.getCustomNumericField8())
            .map(Number::toString)
            .ifPresent(target::setCustomNumericField8);

        Optional.ofNullable(source.getCustomNumericField9())
            .map(Number::toString)
            .ifPresent(target::setCustomNumericField9);

        Optional.ofNullable(source.getCustomNumericField10())
            .map(Number::toString)
            .ifPresent(target::setCustomNumericField10);
    }
}

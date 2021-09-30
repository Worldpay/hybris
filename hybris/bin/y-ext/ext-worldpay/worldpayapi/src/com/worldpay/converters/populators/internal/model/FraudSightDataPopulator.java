package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.CustomNumericFields;
import com.worldpay.data.CustomStringFields;
import com.worldpay.data.FraudSightData;
import com.worldpay.data.ShopperFields;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.FraudSightData} with the information of a {@link FraudSightData}
 */
public class FraudSightDataPopulator implements Populator<FraudSightData, com.worldpay.internal.model.FraudSightData> {

    protected final Converter<ShopperFields, com.worldpay.internal.model.ShopperFields> internalShopperFieldsConverter;
    protected final Converter<CustomNumericFields, com.worldpay.internal.model.CustomNumericFields> internalCustomNumericFieldsConverter;
    protected final Converter<CustomStringFields, com.worldpay.internal.model.CustomStringFields> internalCustomStringFieldsConverter;

    public FraudSightDataPopulator(final Converter<ShopperFields, com.worldpay.internal.model.ShopperFields> internalShopperFieldsConverter,
                                   final Converter<CustomNumericFields, com.worldpay.internal.model.CustomNumericFields> internalCustomNumericFieldsConverter,
                                   final Converter<CustomStringFields, com.worldpay.internal.model.CustomStringFields> internalCustomStringFieldsConverter) {
        this.internalShopperFieldsConverter = internalShopperFieldsConverter;
        this.internalCustomNumericFieldsConverter = internalCustomNumericFieldsConverter;
        this.internalCustomStringFieldsConverter = internalCustomStringFieldsConverter;
    }

    /**
     * Populates the data from the {@link FraudSightData} to a {@link com.worldpay.internal.model.FraudSightData}
     *
     * @param source a {@link FraudSightData} from Worldpay
     * @param target a {@link com.worldpay.internal.model.FraudSightData} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final FraudSightData source,
                         final com.worldpay.internal.model.FraudSightData target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        Optional.ofNullable(source.getShopperFields())
            .map(internalShopperFieldsConverter::convert)
            .ifPresent(target::setShopperFields);

        Optional.ofNullable(source.getCustomNumericFields())
            .map(internalCustomNumericFieldsConverter::convert)
            .ifPresent(target::setCustomNumericFields);

        Optional.ofNullable(source.getCustomStringFields())
            .map(internalCustomStringFieldsConverter::convert)
            .ifPresent(target::setCustomStringFields);
    }
}

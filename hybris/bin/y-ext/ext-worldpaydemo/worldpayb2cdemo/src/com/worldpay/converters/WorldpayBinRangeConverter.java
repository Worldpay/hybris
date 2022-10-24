package com.worldpay.converters;

import com.worldpay.data.WorldpayBinRangeData;
import com.worldpay.model.WorldpayBinRangeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Converts from WorldpayBinRangeModel to WorldpayBinRangeData
 */
public class WorldpayBinRangeConverter implements Converter<WorldpayBinRangeModel, WorldpayBinRangeData> {

    /**
     * Converts a {@link WorldpayBinRangeModel} to a {@link WorldpayBinRangeData} setting in the target the necessary values.
     * @param source
     * @return {@link WorldpayBinRangeData}
     * @throws ConversionException
     */
    @Override
    public WorldpayBinRangeData convert(final WorldpayBinRangeModel source) throws ConversionException {
        return convert(source, new WorldpayBinRangeData());
    }

    /**
     * Converts a {@link WorldpayBinRangeModel} to a {@link WorldpayBinRangeData} setting in the target the necessary values.
     * @param source
     * @return {@link WorldpayBinRangeData}
     * @throws ConversionException
     */
    @Override
    public WorldpayBinRangeData convert(final WorldpayBinRangeModel source, final WorldpayBinRangeData target) throws ConversionException {

        validateParameterNotNull(source, "Source cannot be null");

        target.setCardIssuer(source.getCardIssuer());
        target.setCardType(source.getCardType());
        target.setCardName(source.getCardName());
        target.setCountryCode(source.getCountryCode());
        target.setCardNotes(source.getNotes());

        return target;
    }
}

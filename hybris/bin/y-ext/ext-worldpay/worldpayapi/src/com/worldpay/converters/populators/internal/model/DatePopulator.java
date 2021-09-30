package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Date;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.Date} with the information of a {@link Date}
 */
public class DatePopulator implements Populator<Date, com.worldpay.internal.model.Date> {

    /**
     * Populates the data from the {@link Date} to a {@link com.worldpay.internal.model.Date}
     *
     * @param source a {@link Date} from Worldpay
     * @param target a {@link com.worldpay.internal.model.Date} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final Date source, final com.worldpay.internal.model.Date target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        target.setYear(source.getYear());
        target.setMonth(source.getMonth());
        target.setDayOfMonth(source.getDayOfMonth());
        target.setHour(source.getHour());
        target.setMinute(source.getMinute());
        target.setSecond(source.getSecond());
    }
}

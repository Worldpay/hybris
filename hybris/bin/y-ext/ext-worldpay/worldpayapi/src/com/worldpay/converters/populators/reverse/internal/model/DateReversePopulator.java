package com.worldpay.converters.populators.reverse.internal.model;

import com.worldpay.data.Date;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link Date} with the information of a {@link com.worldpay.internal.model.Date}
 */
public class DateReversePopulator implements Populator<com.worldpay.internal.model.Date, Date> {

    /**
     * Populates the data from the {@link com.worldpay.internal.model.Date} to a {@link Date}
     *
     * @param source a {@link com.worldpay.internal.model.Date} from Worldpay
     * @param target a {@link Date} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final com.worldpay.internal.model.Date source, final Date target) throws ConversionException {
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

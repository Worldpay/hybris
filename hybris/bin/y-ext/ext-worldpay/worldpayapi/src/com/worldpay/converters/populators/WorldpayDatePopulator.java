package com.worldpay.converters.populators;


import de.hybris.platform.converters.Populator;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills data from an {@link Date } to a {@link com.worldpay.data.Date}
 */
public class WorldpayDatePopulator implements Populator<Date, com.worldpay.data.Date> {

    /**
     * Fills the necessary fields from an {@link Date} into a {@link com.worldpay.data.Date}
     *
     * @param source an {@link Date} that contains the information
     * @param target an {@link com.worldpay.data.Date} that receives the information
     */
    @Override
    public void populate(final Date source, final com.worldpay.data.Date target) {
        validateParameterNotNull(source, "Parameter source (Date) cannot be null");

        final LocalDateTime sourceDate = source.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        target.setYear(String.valueOf(sourceDate.getYear()));
        target.setMonth(String.valueOf(sourceDate.getMonthValue()));
        target.setDayOfMonth(String.valueOf(sourceDate.getDayOfMonth()));
        target.setHour(String.valueOf(sourceDate.getHour()));
        target.setMinute(String.valueOf(sourceDate.getMinute()));
        target.setSecond(String.valueOf(sourceDate.getSecond()));
    }
}

package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Browser;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.Browser} with the information of a {@link Browser}
 */
public class BrowserPopulator implements Populator<Browser, com.worldpay.internal.model.Browser> {

    /**
     * Populates the data from the {@link Browser} to a {@link com.worldpay.internal.model.Browser}
     *
     * @param source a {@link Browser} from Worldpay
     * @param target a {@link com.worldpay.internal.model.Browser} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final Browser source, final com.worldpay.internal.model.Browser target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        target.setAcceptHeader(source.getAcceptHeader());
        target.setDeviceOS(source.getDeviceOS());
        target.setDeviceType(source.getDeviceType());
        target.setHttpAcceptLanguage(source.getHttpAcceptLanguage());
        target.setHttpReferer(source.getHttpReferer());
        target.setUserAgentHeader(source.getUserAgentHeader());

    }
}

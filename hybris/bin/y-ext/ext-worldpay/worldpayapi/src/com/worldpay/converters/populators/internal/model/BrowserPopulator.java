package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Browser;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

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

        Optional.ofNullable(source.getAcceptHeader())
                .ifPresent(target::setAcceptHeader);
        Optional.ofNullable(source.getDeviceOS())
                .ifPresent(target::setDeviceOS);
        Optional.ofNullable(source.getDeviceType())
                .ifPresent(target::setDeviceType);
        Optional.ofNullable(source.getHttpAcceptLanguage())
                .ifPresent(target::setHttpAcceptLanguage);
        Optional.ofNullable(source.getHttpReferer())
                .ifPresent(target::setHttpReferer);
        Optional.ofNullable(source.getUserAgentHeader())
                .ifPresent(target::setUserAgentHeader);
        Optional.ofNullable(source.getLanguage())
                .ifPresent(target::setBrowserLanguage);
        Optional.ofNullable(source.getTimeZone())
                .ifPresent(target::setTimeZone);
        Optional.ofNullable(source.getJavascriptEnabled())
                .map(String::valueOf)
                .ifPresent(target::setBrowserJavaScriptEnabled);
        Optional.ofNullable(source.getJavaEnabled())
                .map(String::valueOf)
                .ifPresent(target::setBrowserJavaEnabled);
        Optional.ofNullable(source.getColorDepth())
                .map(String::valueOf)
                .ifPresent(target::setBrowserColourDepth);
        Optional.ofNullable(source.getScreenHeight())
                .map(String::valueOf)
                .ifPresent(target::setBrowserScreenHeight);
        Optional.ofNullable(source.getScreenWidth())
                .map(String::valueOf)
                .ifPresent(target::setBrowserScreenWidth);
    }
}

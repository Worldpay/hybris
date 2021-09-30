package com.worldpay.converters.populators.internal.model.applepay;

import com.worldpay.internal.model.APPLEPAYSSL;
import com.worldpay.data.applepay.ApplePay;
import com.worldpay.data.applepay.Header;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link APPLEPAYSSL} with the information of a {@link ApplePay}
 */
public class ApplePayPopulator implements Populator<ApplePay, APPLEPAYSSL> {

    protected final Converter<Header, com.worldpay.internal.model.Header> internalHeaderConverter;

    public ApplePayPopulator(final Converter<Header, com.worldpay.internal.model.Header> internalHeaderConverter) {
        this.internalHeaderConverter = internalHeaderConverter;
    }

    /**
     * Populates the data from the {@link ApplePay} to a {@link APPLEPAYSSL}
     *
     * @param source a {@link ApplePay} from Worldpay
     * @param target a {@link APPLEPAYSSL} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final ApplePay source, final APPLEPAYSSL target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        Optional.ofNullable(source.getHeader())
            .map(internalHeaderConverter::convert)
            .ifPresent(target::setHeader);

        target.setData(source.getData());
        target.setSignature(source.getSignature());
        target.setTokenRequestorID(source.getTokenRequestorID());
        target.setVersion(source.getVersion());
    }
}

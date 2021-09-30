package com.worldpay.converters.populators.internal.model.applepay;

import com.worldpay.data.applepay.Header;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.Header} with the information of a {@link Header}
 */
public class HeaderPopulator implements Populator<Header, com.worldpay.internal.model.Header> {

    /**
     * Populates the data from the {@link Header} to a {@link com.worldpay.internal.model.Header}
     *
     * @param source a {@link Header} from Worldpay
     * @param target a {@link com.worldpay.internal.model.Header} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final Header source, final com.worldpay.internal.model.Header target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        target.setApplicationData(source.getApplicationData());
        target.setEphemeralPublicKey(source.getEphemeralPublicKey());
        target.setPublicKeyHash(source.getPublicKeyHash());
        target.setTransactionId(source.getTransactionId());
    }
}

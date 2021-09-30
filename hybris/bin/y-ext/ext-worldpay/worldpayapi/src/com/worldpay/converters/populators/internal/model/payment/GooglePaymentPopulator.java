package com.worldpay.converters.populators.internal.model.payment;

import com.worldpay.internal.model.PAYWITHGOOGLESSL;
import com.worldpay.data.payment.PayWithGoogleSSL;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.PAYWITHGOOGLESSL} with the information of a {@link PayWithGoogleSSL}.
 */
public class GooglePaymentPopulator implements Populator<PayWithGoogleSSL, PAYWITHGOOGLESSL> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final PayWithGoogleSSL source, final PAYWITHGOOGLESSL target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        Optional.ofNullable(source.getProtocolVersion())
            .ifPresent(target::setProtocolVersion);

        Optional.ofNullable(source.getSignature())
            .ifPresent(target::setSignature);

        Optional.ofNullable(source.getSignedMessage())
            .ifPresent(target::setSignedMessage);
    }
}

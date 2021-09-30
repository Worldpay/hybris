package com.worldpay.converters.populators.internal.model;

import com.worldpay.enums.payment.storedCredentials.MerchantInitiatedReason;
import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.data.payment.StoredCredentials;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.StoredCredentials} with the information of a {@link StoredCredentials}.
 */
public class StoredCredentialsPopulator implements Populator<StoredCredentials, com.worldpay.internal.model.StoredCredentials> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final StoredCredentials source,
                         final com.worldpay.internal.model.StoredCredentials target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        Optional.ofNullable(source.getMerchantInitiatedReason())
            .map(MerchantInitiatedReason::name)
            .ifPresent(target::setMerchantInitiatedReason);

        Optional.ofNullable(source.getUsage())
            .map(Usage::name)
            .ifPresent(target::setUsage);

        target.setSchemeTransactionIdentifier(source.getSchemeTransactionIdentifier());
    }
}

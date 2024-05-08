package com.worldpay.converters.populators.internal.model.payment;

import com.worldpay.internal.model.IDEALSSL;
import com.worldpay.data.payment.AlternativeShopperBankCodePayment;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.IDEALSSL} with the information of a {@link AlternativeShopperBankCodePayment}.
 */
public class IdealPaymentPopulator implements Populator<AlternativeShopperBankCodePayment, IDEALSSL> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final AlternativeShopperBankCodePayment source, final IDEALSSL target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        Optional.ofNullable(source.getSuccessURL())
            .ifPresent(target::setSuccessURL);

        Optional.ofNullable(source.getFailureURL())
            .ifPresent(target::setFailureURL);

        Optional.ofNullable(source.getCancelURL())
            .ifPresent(target::setCancelURL);

        Optional.ofNullable(source.getPendingURL())
            .ifPresent(target::setPendingURL);
    }
}

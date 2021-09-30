package com.worldpay.converters.populators.internal.model.payment;

import com.worldpay.data.Amount;
import com.worldpay.data.payment.PayAsOrder;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.PayAsOrder} with the information of a {@link PayAsOrder}.
 */
public class PayAsOrderPopulator implements Populator<PayAsOrder, com.worldpay.internal.model.PayAsOrder> {

    protected final Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverter;

    public PayAsOrderPopulator(final Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverter) {
        this.internalAmountConverter = internalAmountConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final PayAsOrder source,
                         final com.worldpay.internal.model.PayAsOrder target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        Optional.ofNullable(source.getOriginalMerchantCode())
            .ifPresent(target::setMerchantCode);

        Optional.ofNullable(source.getOriginalOrderCode())
            .ifPresent(target::setOrderCode);

        Optional.ofNullable(source.getAmount())
            .map(internalAmountConverter::convert)
            .ifPresent(target::setAmount);

        Optional.ofNullable(source.getCvc())
            .ifPresent(target::setCvc);
    }
}

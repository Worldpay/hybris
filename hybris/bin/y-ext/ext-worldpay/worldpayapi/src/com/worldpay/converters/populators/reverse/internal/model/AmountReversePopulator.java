package com.worldpay.converters.populators.reverse.internal.model;

import com.worldpay.data.Amount;
import com.worldpay.enums.DebitCreditIndicator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link Amount} with the information of a {@link com.worldpay.internal.model.Amount}
 */
public class AmountReversePopulator implements Populator<com.worldpay.internal.model.Amount, Amount> {

    /**
     * Populates the data from the {@link com.worldpay.internal.model.Amount} to a {@link Amount}
     *
     * @param source a {@link com.worldpay.internal.model.Amount} from Worldpay
     * @param target a {@link Amount} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final com.worldpay.internal.model.Amount source, Amount target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        target.setValue(source.getValue());
        target.setCurrencyCode(source.getCurrencyCode());
        target.setExponent(source.getExponent());
        Optional.ofNullable(source.getDebitCreditIndicator())
            .map(String::toUpperCase)
            .map(DebitCreditIndicator::valueOf)
            .ifPresent(target::setDebitCreditIndicator);
    }
}

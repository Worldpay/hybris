package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Address;
import com.worldpay.data.Amount;
import com.worldpay.enums.DebitCreditIndicator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.Amount} with the information of a {@link Amount}
 */
public class AmountPopulator implements Populator<Amount, com.worldpay.internal.model.Amount> {

    /**
     * Populates the data from the {@link Amount} to a {@link com.worldpay.internal.model.Amount}
     *
     * @param source a {@link Amount} from Worldpay
     * @param target a {@link com.worldpay.internal.model.Amount} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final Amount source, final com.worldpay.internal.model.Amount target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        target.setValue(source.getValue());
        target.setCurrencyCode(source.getCurrencyCode());
        target.setExponent(source.getExponent());

        Optional.ofNullable(source.getDebitCreditIndicator())
            .map(DebitCreditIndicator::getCode)
            .ifPresent(target::setDebitCreditIndicator);
    }
}

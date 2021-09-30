package com.worldpay.converters.populators.internal.model.threeds2;

import com.worldpay.data.Amount;
import com.worldpay.data.threeds2.TransactionRiskDataGiftCardAmount;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.TransactionRiskDataGiftCardAmount} with the information of a {@link TransactionRiskDataGiftCardAmount}
 */
public class TransactionRiskDataGiftCardAmountPopulator implements Populator<TransactionRiskDataGiftCardAmount, com.worldpay.internal.model.TransactionRiskDataGiftCardAmount> {

    protected final Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverter;

    public TransactionRiskDataGiftCardAmountPopulator(final Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverter) {
        this.internalAmountConverter = internalAmountConverter;
    }

    /**
     * Populates the data from the {@link TransactionRiskDataGiftCardAmount} to a {@link com.worldpay.internal.model.TransactionRiskDataGiftCardAmount}
     *
     * @param source a {@link TransactionRiskDataGiftCardAmount} from Worldpay
     * @param target a {@link com.worldpay.internal.model.TransactionRiskDataGiftCardAmount} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final TransactionRiskDataGiftCardAmount source, final com.worldpay.internal.model.TransactionRiskDataGiftCardAmount target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        Optional.ofNullable(source.getAmount())
            .map(internalAmountConverter::convert)
            .ifPresent(target::setAmount);
    }
}

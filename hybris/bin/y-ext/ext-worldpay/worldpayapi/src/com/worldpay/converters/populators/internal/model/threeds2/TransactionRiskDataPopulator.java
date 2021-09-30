package com.worldpay.converters.populators.internal.model.threeds2;


import com.worldpay.internal.model.TransactionRiskDataPreOrderDate;
import com.worldpay.data.Date;
import com.worldpay.data.threeds2.TransactionRiskData;
import com.worldpay.data.threeds2.TransactionRiskDataGiftCardAmount;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.TransactionRiskData} with the information of a {@link TransactionRiskData}
 */
public class TransactionRiskDataPopulator implements Populator<TransactionRiskData, com.worldpay.internal.model.TransactionRiskData> {

    protected final Converter<Date, com.worldpay.internal.model.Date> internalDateConverter;
    protected final Converter<TransactionRiskDataGiftCardAmount, com.worldpay.internal.model.TransactionRiskDataGiftCardAmount> internalTransactionRiskDataGiftCardAmountConverter;

    public TransactionRiskDataPopulator(final Converter<Date, com.worldpay.internal.model.Date> internalDateConverter,
                                        final Converter<TransactionRiskDataGiftCardAmount, com.worldpay.internal.model.TransactionRiskDataGiftCardAmount> internalTransactionRiskDataGiftCardAmountConverter) {
        this.internalDateConverter = internalDateConverter;
        this.internalTransactionRiskDataGiftCardAmountConverter = internalTransactionRiskDataGiftCardAmountConverter;
    }

    /**
     * Populates the data from the {@link TransactionRiskData} to a {@link com.worldpay.internal.model.TransactionRiskData}
     *
     * @param source a {@link TransactionRiskData} from Worldpay
     * @param target a {@link com.worldpay.internal.model.TransactionRiskData} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final TransactionRiskData source, final com.worldpay.internal.model.TransactionRiskData target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        target.setDeliveryEmailAddress(source.getDeliveryEmailAddress());
        target.setDeliveryTimeframe(source.getDeliveryTimeframe());
        target.setGiftCardCount(source.getGiftCardCount());
        target.setPreOrderPurchase(source.getPreOrderPurchase());
        target.setReorderingPreviousPurchases(source.getReorderingPreviousPurchases());
        target.setShippingMethod(source.getShippingMethod());

        Optional.ofNullable(source.getTransactionRiskDataPreOrderDate()).ifPresent(date -> {
            final TransactionRiskDataPreOrderDate transactionRiskDataPreOrderDate = new TransactionRiskDataPreOrderDate();
            transactionRiskDataPreOrderDate.setDate(internalDateConverter.convert(date));
            target.setTransactionRiskDataPreOrderDate(transactionRiskDataPreOrderDate);
        });

        Optional.ofNullable(source.getTransactionRiskDataGiftCardAmount())
            .map(internalTransactionRiskDataGiftCardAmountConverter::convert)
            .ifPresent(target::setTransactionRiskDataGiftCardAmount);
    }
}

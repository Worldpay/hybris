package com.worldpay.converters.populators.internal.model;

import com.worldpay.internal.model.*;
import com.worldpay.data.Amount;
import com.worldpay.data.Date;
import com.worldpay.data.Item;
import com.worldpay.data.Purchase;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.Purchase} with the information of a {@link Purchase}
 */
public class PurchasePopulator implements Populator<Purchase, com.worldpay.internal.model.Purchase> {

    protected final Converter<Item, com.worldpay.internal.model.Item> internalItemConverter;
    protected final Converter<Date, com.worldpay.internal.model.Date> internalDateConverter;
    protected final Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverter;

    public PurchasePopulator(final Converter<Item, com.worldpay.internal.model.Item> internalItemConverter,
                             final Converter<Date, com.worldpay.internal.model.Date> internalDateConverter,
                             final Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverter) {
        this.internalItemConverter = internalItemConverter;
        this.internalDateConverter = internalDateConverter;
        this.internalAmountConverter = internalAmountConverter;
    }

    /**
     * Populates the data from the {@link Purchase} to a {@link com.worldpay.internal.model.Purchase}
     *
     * @param source a {@link Purchase} from Worldpay
     * @param target a {@link com.worldpay.internal.model.Purchase} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final Purchase source,
                         final com.worldpay.internal.model.Purchase target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        target.setInvoiceReferenceNumber(source.getInvoiceReferenceNumber());
        target.setCustomerReference(source.getCustomerReference());
        target.setCardAcceptorTaxId(source.getCardAcceptorTaxId());
        target.setDestinationCountryCode(source.getDestinationCountryCode());
        target.setDestinationPostalCode(source.getDestinationPostalCode());
        target.setTaxExempt(String.valueOf(source.isTaxExempt()));

        Optional.ofNullable(source.getItem())
            .ifPresent(items -> target.getItem().addAll(internalItemConverter.convertAll(items)));

        Optional.ofNullable(source.getOrderDate()).ifPresent(date -> {
            final OrderDate intOrderDate = new OrderDate();
            intOrderDate.setDate(internalDateConverter.convert(date));
            target.setOrderDate(intOrderDate);
        });

        Optional.ofNullable(source.getSalesTax()).ifPresent(amount -> {
            final SalesTax intSalesTax = new SalesTax();
            intSalesTax.setAmount(internalAmountConverter.convert(amount));
            target.setSalesTax(intSalesTax);
        });

        Optional.ofNullable(source.getDiscountAmount()).ifPresent(amount -> {
            final DiscountAmount intDiscountAmount = new DiscountAmount();
            intDiscountAmount.setAmount(internalAmountConverter.convert(amount));
            target.setDiscountAmount(intDiscountAmount);
        });

        Optional.ofNullable(source.getShippingAmount()).ifPresent(amount -> {
            final ShippingAmount intShippingAmount = new ShippingAmount();
            intShippingAmount.setAmount(internalAmountConverter.convert(amount));
            target.setShippingAmount(intShippingAmount);
        });

        Optional.ofNullable(source.getDutyAmount()).ifPresent(amount -> {
            final DutyAmount intDutyAmount = new DutyAmount();
            intDutyAmount.setAmount(internalAmountConverter.convert(amount));
            target.setDutyAmount(intDutyAmount);
        });
    }
}

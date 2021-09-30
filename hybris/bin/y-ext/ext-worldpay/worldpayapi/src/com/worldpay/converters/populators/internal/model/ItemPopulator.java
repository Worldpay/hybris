package com.worldpay.converters.populators.internal.model;

import com.worldpay.internal.model.*;
import com.worldpay.data.Amount;
import com.worldpay.data.Item;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.Item} with the information of a {@link Item}
 */
public class ItemPopulator implements Populator<Item, com.worldpay.internal.model.Item> {

    protected final Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverter;

    public ItemPopulator(final Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverter) {
        this.internalAmountConverter = internalAmountConverter;
    }

    /**
     * Populates the data from the {@link Item} to a {@link com.worldpay.internal.model.Item}
     *
     * @param source a {@link Item} from Worldpay
     * @param target a {@link com.worldpay.internal.model.Item} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final Item source,
                         final com.worldpay.internal.model.Item target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        target.setProductCode(source.getProductCode());
        target.setCommodityCode(source.getCommodityCode());
        target.setQuantity(source.getQuantity());
        target.setUnitOfMeasure(source.getUnitOfMeasure());

        Optional.ofNullable(source.getDescription()).ifPresent(description -> {
            final Description intDescription = new Description();
            intDescription.setvalue(description);
            target.setDescription(intDescription);
        });

        Optional.ofNullable(source.getUnitCost()).ifPresent(unitCost -> {
            final UnitCost intUnitCost = new UnitCost();
            intUnitCost.setAmount(internalAmountConverter.convert(unitCost));
            target.setUnitCost(intUnitCost);
        });

        Optional.ofNullable(source.getItemTotal()).ifPresent(itemTotal -> {
            final ItemTotal intItemTotal = new ItemTotal();
            intItemTotal.setAmount(internalAmountConverter.convert(itemTotal));
            target.setItemTotal(intItemTotal);
        });

        Optional.ofNullable(source.getItemTotalWithTax()).ifPresent(itemTotalWithTax -> {
            final ItemTotalWithTax intItemTotalWithTax = new ItemTotalWithTax();
            intItemTotalWithTax.setAmount(internalAmountConverter.convert(itemTotalWithTax));
            target.setItemTotalWithTax(intItemTotalWithTax);
        });

        Optional.ofNullable(source.getItemDiscountAmount()).ifPresent(itemDiscountAmount -> {
            final ItemDiscountAmount intItemDiscountAmount = new ItemDiscountAmount();
            intItemDiscountAmount.setAmount(internalAmountConverter.convert(itemDiscountAmount));
            target.setItemDiscountAmount(intItemDiscountAmount);
        });

        Optional.ofNullable(source.getTaxAmount()).ifPresent(taxAmount -> {
            final TaxAmount intTaxAmount = new TaxAmount();
            intTaxAmount.setAmount(internalAmountConverter.convert(taxAmount));
            target.setTaxAmount(intTaxAmount);
        });
    }
}

package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.LineItem;
import com.worldpay.data.LineItemReference;
import com.worldpay.enums.lineItem.LineItemType;
import com.worldpay.internal.model.Discount;
import com.worldpay.internal.model.Physical;
import com.worldpay.internal.model.ShippingFee;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.LineItem} with the information of a {@link LineItem}
 */
public class LineItemPopulator implements Populator<LineItem, com.worldpay.internal.model.LineItem> {

    protected Converter<LineItemReference, com.worldpay.internal.model.Reference> internalReferenceConverter;

    public LineItemPopulator(final Converter<LineItemReference, com.worldpay.internal.model.Reference> internalReferenceConverter) {
        this.internalReferenceConverter = internalReferenceConverter;
    }

    /**
     * Populates the data from the {@link LineItem} to a {@link com.worldpay.internal.model.LineItem}
     *
     * @param source a {@link LineItem} from Worldpay
     * @param target a {@link com.worldpay.internal.model.LineItem} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final LineItem source, final com.worldpay.internal.model.LineItem target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        target.setName(source.getName());
        target.setQuantity(source.getQuantity());
        target.setQuantityUnit(source.getQuantityUnit());
        target.setUnitPrice(source.getUnitPrice());
        target.setTaxRate(source.getTaxRate());
        target.setTotalAmount(source.getTotalAmount());
        target.setTotalTaxAmount(source.getTotalTaxAmount());
        target.setTotalDiscountAmount(source.getTotalDiscountAmount());

        Optional.ofNullable(source.getLineItemReference())
            .map(internalReferenceConverter::convert)
            .ifPresent(target::setReference);

        final List<Object> targetList = target.getPhysicalOrDiscountOrShippingFeeOrDigitalOrGiftCardOrSalesTaxTypeOrStoreCreditOrSurcharge();
        Optional.ofNullable(source.getLineItemType())
            .map(this::getIntLineItemType)
            .ifPresent(targetList::add);
    }

    private Object getIntLineItemType(final LineItemType type) {
        switch (type) {
            case DISCOUNT:
                return new Discount();
            case PHYSICAL:
                return new Physical();
            case SHIPPING_FEE:
                return new ShippingFee();
        }
        return null;
    }
}

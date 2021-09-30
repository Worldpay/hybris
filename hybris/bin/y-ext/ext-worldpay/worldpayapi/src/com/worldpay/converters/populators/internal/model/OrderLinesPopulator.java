package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.LineItem;
import com.worldpay.data.OrderLines;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.OrderLines} with the information of a {@link OrderLines}
 */
public class OrderLinesPopulator implements Populator<OrderLines, com.worldpay.internal.model.OrderLines> {

    protected Converter<LineItem, com.worldpay.internal.model.LineItem> internalLineItemConverter;

    public OrderLinesPopulator(final Converter<LineItem, com.worldpay.internal.model.LineItem> internalLineItemConverter) {
        this.internalLineItemConverter = internalLineItemConverter;
    }

    /**
     * Populates the data from the {@link OrderLines} to a {@link com.worldpay.internal.model.OrderLines}
     *
     * @param source a {@link OrderLines} from Worldpay
     * @param target a {@link com.worldpay.internal.model.OrderLines} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final OrderLines source, final com.worldpay.internal.model.OrderLines target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        target.setOrderTaxAmount(source.getOrderTaxAmount());
        target.setTermsURL(source.getTermsURL());

        final List<com.worldpay.internal.model.LineItem> targetList = target.getLineItem();
        Optional.ofNullable(source.getLineItems())
            .map(internalLineItemConverter::convertAll)
            .ifPresent(targetList::addAll);
    }
}

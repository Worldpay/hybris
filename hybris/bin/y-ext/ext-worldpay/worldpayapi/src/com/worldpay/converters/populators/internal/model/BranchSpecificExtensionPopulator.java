package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.BranchSpecificExtension;
import com.worldpay.data.Order;
import com.worldpay.data.Purchase;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.Order} with the information of a {@link Order}.
 */
public class BranchSpecificExtensionPopulator implements Populator<BranchSpecificExtension, com.worldpay.internal.model.BranchSpecificExtension> {

    private final Converter<Purchase, com.worldpay.internal.model.Purchase> internalPurchaseConverter;

    public BranchSpecificExtensionPopulator(final Converter<Purchase, com.worldpay.internal.model.Purchase> internalPurchaseConverter) {
        this.internalPurchaseConverter = internalPurchaseConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final BranchSpecificExtension source,
                         final com.worldpay.internal.model.BranchSpecificExtension target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        final List<Object> targetList = target.getAirlineOrPurchaseOrHotelOrLodging();
        Optional.ofNullable(source.getPurchase())
            .map(internalPurchaseConverter::convertAll)
            .ifPresent(targetList::addAll);
    }
}

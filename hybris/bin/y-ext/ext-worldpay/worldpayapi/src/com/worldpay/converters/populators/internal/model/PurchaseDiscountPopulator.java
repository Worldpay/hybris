package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.PurchaseDiscount;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.PurchaseDiscount} with the information of a {@link PurchaseDiscount}
 */
public class PurchaseDiscountPopulator implements Populator<PurchaseDiscount, com.worldpay.internal.model.PurchaseDiscount> {

    /**
     * Populates the data from the {@link PurchaseDiscount} to a {@link com.worldpay.internal.model.PurchaseDiscount}
     *
     * @param source a {@link PurchaseDiscount} from Worldpay
     * @param target a {@link com.worldpay.internal.model.PurchaseDiscount} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final PurchaseDiscount source, final com.worldpay.internal.model.PurchaseDiscount target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        Optional.ofNullable(source.getPurchaseDiscountCode()).ifPresent(target::setPurchaseDiscountCode);
        Optional.ofNullable(source.getPurchaseDiscountAmount()).ifPresent(target::setPurchaseDiscountAmount);
        Optional.ofNullable(source.getPurchaseDiscountPercentage()).ifPresent(target::setPurchaseDiscountPercentage);
    }

}

package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Product;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.Product} with the information of a {@link Product}
 */
public class ProductPopulator implements Populator<Product, com.worldpay.internal.model.Product> {

    /**
     * Populates the data from the {@link Product} to a {@link com.worldpay.internal.model.Product}
     *
     * @param source a {@link Product} from Worldpay
     * @param target a {@link com.worldpay.internal.model.Product} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final Product source, final com.worldpay.internal.model.Product target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        Optional.ofNullable(source.getItemId()).ifPresent(target::setItemId);
        Optional.ofNullable(source.getItemName()).ifPresent(target::setItemName);
        Optional.ofNullable(source.getItemPrice()).ifPresent(target::setItemPrice);
        Optional.ofNullable(source.getItemQuantity()).ifPresent(target::setItemQuantity);
        Optional.ofNullable(source.getItemCategory()).ifPresent(target::setItemCategory);
        Optional.ofNullable(source.getItemIsDigital()).ifPresent(target::setItemIsDigital);
        Optional.ofNullable(source.getItemSubCategory()).ifPresent(target::setItemSubCategory);
    }

}

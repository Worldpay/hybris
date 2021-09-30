package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.PaymentMethodAttribute;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.PaymentMethodAttribute} with the information of a {@link PaymentMethodAttribute}
 */
public class PaymentMethodAttributePopulator implements Populator<PaymentMethodAttribute, com.worldpay.internal.model.PaymentMethodAttribute> {

    /**
     * Populates the data from the {@link PaymentMethodAttribute} to a {@link com.worldpay.internal.model.PaymentMethodAttribute}
     *
     * @param source a {@link PaymentMethodAttribute} from Worldpay
     * @param target a {@link com.worldpay.internal.model.PaymentMethodAttribute} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final PaymentMethodAttribute source, final com.worldpay.internal.model.PaymentMethodAttribute target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        target.setAttrName(source.getAttrName());
        target.setAttrValue(source.getAttrValue());
        target.setPaymentMethod(source.getPaymentMethod());
    }
}

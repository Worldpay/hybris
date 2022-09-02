package com.worldpay.converters.populators.internal.model.klarna;

import com.worldpay.data.klarna.KlarnaPayment;
import com.worldpay.internal.model.KLARNAV2SSL;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link KLARNAV2SSL} with the information of a {@link KlarnaPayment}
 */
public class KlarnaV2PaymentPopulator implements Populator<KlarnaPayment, KLARNAV2SSL> {

    /**
     * Populates the data from the {@link KlarnaPayment} to a {@link KLARNAV2SSL}
     *
     * @param source a {@link KlarnaPayment} from Worldpay.
     * @param target a {@link KLARNAV2SSL} in Worldpay.
     * @throws ConversionException if the conversion fails.
     */
    @Override
    public void populate(KlarnaPayment source, KLARNAV2SSL target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        Optional.ofNullable(source.getCancelURL()).ifPresent(target::setCancelURL);
        Optional.ofNullable(source.getFailureURL()).ifPresent(target::setFailureURL);
        Optional.ofNullable(source.getPendingURL()).ifPresent(target::setPendingURL);
        Optional.ofNullable(source.getSuccessURL()).ifPresent(target::setSuccessURL);
        Optional.ofNullable(source.getLocale()).ifPresent(target::setLocale);
        Optional.ofNullable(source.getShopperCountryCode()).ifPresent(target::setShopperCountryCode);

    }
}

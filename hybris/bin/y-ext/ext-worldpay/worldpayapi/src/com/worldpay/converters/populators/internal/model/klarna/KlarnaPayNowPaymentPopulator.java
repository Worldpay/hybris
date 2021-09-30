package com.worldpay.converters.populators.internal.model.klarna;

import com.worldpay.internal.model.KLARNAPAYNOWSSL;
import com.worldpay.data.klarna.KlarnaPayment;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link KLARNAPAYNOWSSL} with the information of a {@link KlarnaPayment}
 */
public class KlarnaPayNowPaymentPopulator implements Populator<KlarnaPayment, KLARNAPAYNOWSSL> {

    /**
     * Populates the data from the {@link KlarnaPayment} to a {@link KLARNAPAYNOWSSL}
     *
     * @param source a {@link KlarnaPayment} from Worldpay
     * @param target a {@link KLARNAPAYNOWSSL} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final KlarnaPayment source, final KLARNAPAYNOWSSL target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        target.setCancelURL(source.getCancelURL());
        target.setFailureURL(source.getFailureURL());
        target.setPendingURL(source.getPendingURL());
        target.setSuccessURL(source.getSuccessURL());
        target.setLocale(source.getLocale());
        target.setShopperCountryCode(source.getShopperCountryCode());
    }
}

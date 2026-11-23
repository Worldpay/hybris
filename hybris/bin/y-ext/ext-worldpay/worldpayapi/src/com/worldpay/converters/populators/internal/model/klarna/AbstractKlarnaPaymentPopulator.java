package com.worldpay.converters.populators.internal.model.klarna;

import com.worldpay.converters.populators.internal.model.payment.AbstractAlternativePaymentPopulator;
import com.worldpay.data.klarna.KlarnaPayment;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Base populator for the Klarna payment targets.
 *
 * @param <T> the Worldpay internal Klarna payment target type
 */
public abstract class AbstractKlarnaPaymentPopulator<T> extends AbstractAlternativePaymentPopulator<KlarnaPayment, T> {

    /**
     * Populates the common Klarna payment data into the target.
     *
     * @param source a {@link KlarnaPayment} from Worldpay
     * @param target the Worldpay internal Klarna payment target
     * @throws ConversionException if the conversion fails
     */
    @Override
    public void populate(final KlarnaPayment source, final T target) throws ConversionException {
        super.populate(source, target);

        setLocale(target, source.getLocale());
        setShopperCountryCode(target, source.getShopperCountryCode());
    }

    protected abstract void setLocale(final T target, final String value);

    protected abstract void setShopperCountryCode(final T target, final String value);
}

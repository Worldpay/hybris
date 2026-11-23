package com.worldpay.converters.populators.internal.model.payment;

import com.worldpay.data.payment.AlternativePayment;
import com.worldpay.internal.model.CancelURL;
import com.worldpay.internal.model.FailureURL;
import com.worldpay.internal.model.PendingURL;
import com.worldpay.internal.model.SuccessURL;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Base populator for the alternative payment targets. Populates the redirect URLs common to all of them.
 *
 * @param <S> the alternative payment source type
 * @param <T> the Worldpay internal payment target type
 */
public abstract class AbstractAlternativePaymentPopulator<S extends AlternativePayment, T> implements Populator<S, T> {

    /**
     * Populates the common alternative payment data into the target.
     *
     * @param source an {@link AlternativePayment} from Worldpay
     * @param target the Worldpay internal payment target
     * @throws ConversionException if the conversion fails
     */
    @Override
    public void populate(final S source, final T target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        Optional.ofNullable(source.getSuccessURL())
                .ifPresent(url -> {
                    final SuccessURL successURL = new SuccessURL();
                    successURL.setValue(url);
                    setSuccessURL(target, successURL);
                });

        Optional.ofNullable(source.getFailureURL())
                .ifPresent(url -> {
                    final FailureURL failureURL = new FailureURL();
                    failureURL.setValue(url);
                    setFailureURL(target, failureURL);
                });

        Optional.ofNullable(source.getCancelURL())
                .ifPresent(url -> {
                    final CancelURL cancelURL = new CancelURL();
                    cancelURL.setValue(url);
                    setCancelURL(target, cancelURL);
                });

        Optional.ofNullable(source.getPendingURL())
                .ifPresent(url -> {
                    final PendingURL pendingURL = new PendingURL();
                    pendingURL.setValue(url);
                    setPendingURL(target, pendingURL);
                });
    }

    protected abstract void setSuccessURL(final T target, final SuccessURL value);

    protected abstract void setFailureURL(final T target, final FailureURL value);

    protected abstract void setCancelURL(final T target, final CancelURL value);

    protected abstract void setPendingURL(final T target, final PendingURL value);
}

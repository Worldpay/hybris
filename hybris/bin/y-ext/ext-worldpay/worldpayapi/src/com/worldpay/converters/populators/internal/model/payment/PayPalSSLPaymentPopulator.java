package com.worldpay.converters.populators.internal.model.payment;

import com.worldpay.data.payment.AlternativePayment;
import com.worldpay.internal.model.CancelURL;
import com.worldpay.internal.model.FailureURL;
import com.worldpay.internal.model.PAYPALSSL;
import com.worldpay.internal.model.PendingURL;
import com.worldpay.internal.model.SuccessURL;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.site.BaseSiteService;

import java.util.Optional;

/**
 * Populator that fills the necessary details on a {@link PAYPALSSL} with the information of a {@link AlternativePayment}.
 */
public class PayPalSSLPaymentPopulator extends AbstractAlternativePaymentPopulator<AlternativePayment, PAYPALSSL> {

    protected final BaseSiteService baseSiteService;

    public PayPalSSLPaymentPopulator(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final AlternativePayment source, final PAYPALSSL target) throws ConversionException {
        super.populate(source, target);

        Optional.ofNullable(baseSiteService.getCurrentBaseSite())
                .map(baseSite -> baseSite.getPaypalSSLIntent().toString().toLowerCase())
                .ifPresent(target::setIntent);
    }

    @Override
    protected void setSuccessURL(final PAYPALSSL target, final SuccessURL value) {
        target.setSuccessURL(value);
    }

    @Override
    protected void setFailureURL(final PAYPALSSL target, final FailureURL value) {
        target.setFailureURL(value);
    }

    @Override
    protected void setCancelURL(final PAYPALSSL target, final CancelURL value) {
        target.setCancelURL(value);
    }

    @Override
    protected void setPendingURL(final PAYPALSSL target, final PendingURL value) {
        target.setPendingURL(value);
    }
}

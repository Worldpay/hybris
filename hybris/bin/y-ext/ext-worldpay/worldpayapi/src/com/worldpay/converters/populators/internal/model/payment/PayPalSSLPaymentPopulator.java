package com.worldpay.converters.populators.internal.model.payment;

import com.worldpay.data.payment.AlternativePayment;
import com.worldpay.internal.model.PAYPALSSL;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.site.BaseSiteService;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link PAYPALSSL} with the information of a {@link AlternativePayment}.
 */
public class PayPalSSLPaymentPopulator implements Populator<AlternativePayment, PAYPALSSL> {

    protected final BaseSiteService baseSiteService;

    public PayPalSSLPaymentPopulator(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final AlternativePayment source, final PAYPALSSL target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        Optional.ofNullable(baseSiteService.getCurrentBaseSite())
                .map(baseSite -> baseSite.getPaypalSSLIntent().toString().toLowerCase())
                .ifPresent(target::setIntent);
        Optional.ofNullable(source.getCancelURL()).ifPresent(target::setCancelURL);
        Optional.ofNullable(source.getFailureURL()).ifPresent(target::setFailureURL);
        Optional.ofNullable(source.getPendingURL()).ifPresent(target::setPendingURL);
        Optional.ofNullable(source.getSuccessURL()).ifPresent(target::setSuccessURL);
    }

}

package com.worldpay.converters.populators.internal.model.klarna;

import com.worldpay.data.klarna.KlarnaPayment;
import com.worldpay.internal.model.CancelURL;
import com.worldpay.internal.model.FailureURL;
import com.worldpay.internal.model.KLARNAV2SSL;
import com.worldpay.internal.model.PendingURL;
import com.worldpay.internal.model.SuccessURL;

/**
 * Populator that fills the necessary details on a {@link KLARNAV2SSL} with the information of a {@link KlarnaPayment}
 */
public class KlarnaV2PaymentPopulator extends AbstractKlarnaPaymentPopulator<KLARNAV2SSL> {

    @Override
    protected void setSuccessURL(final KLARNAV2SSL target, final SuccessURL value) {
        target.setSuccessURL(value);
    }

    @Override
    protected void setFailureURL(final KLARNAV2SSL target, final FailureURL value) {
        target.setFailureURL(value);
    }

    @Override
    protected void setCancelURL(final KLARNAV2SSL target, final CancelURL value) {
        target.setCancelURL(value);
    }

    @Override
    protected void setPendingURL(final KLARNAV2SSL target, final PendingURL value) {
        target.setPendingURL(value);
    }

    @Override
    protected void setLocale(final KLARNAV2SSL target, final String value) {
        target.setLocale(value);
    }

    @Override
    protected void setShopperCountryCode(final KLARNAV2SSL target, final String value) {
        target.setShopperCountryCode(value);
    }
}

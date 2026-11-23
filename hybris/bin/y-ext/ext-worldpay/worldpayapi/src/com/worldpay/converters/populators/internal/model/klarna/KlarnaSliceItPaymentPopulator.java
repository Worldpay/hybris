package com.worldpay.converters.populators.internal.model.klarna;

import com.worldpay.data.klarna.KlarnaPayment;
import com.worldpay.internal.model.CancelURL;
import com.worldpay.internal.model.FailureURL;
import com.worldpay.internal.model.KLARNASLICEITSSL;
import com.worldpay.internal.model.PendingURL;
import com.worldpay.internal.model.SuccessURL;

/**
 * Populator that fills the necessary details on a {@link KLARNASLICEITSSL} with the information of a {@link KlarnaPayment}
 */
public class KlarnaSliceItPaymentPopulator extends AbstractKlarnaPaymentPopulator<KLARNASLICEITSSL> {

    @Override
    protected void setSuccessURL(final KLARNASLICEITSSL target, final SuccessURL value) {
        target.setSuccessURL(value);
    }

    @Override
    protected void setFailureURL(final KLARNASLICEITSSL target, final FailureURL value) {
        target.setFailureURL(value);
    }

    @Override
    protected void setCancelURL(final KLARNASLICEITSSL target, final CancelURL value) {
        target.setCancelURL(value);
    }

    @Override
    protected void setPendingURL(final KLARNASLICEITSSL target, final PendingURL value) {
        target.setPendingURL(value);
    }

    @Override
    protected void setLocale(final KLARNASLICEITSSL target, final String value) {
        target.setLocale(value);
    }

    @Override
    protected void setShopperCountryCode(final KLARNASLICEITSSL target, final String value) {
        target.setShopperCountryCode(value);
    }
}

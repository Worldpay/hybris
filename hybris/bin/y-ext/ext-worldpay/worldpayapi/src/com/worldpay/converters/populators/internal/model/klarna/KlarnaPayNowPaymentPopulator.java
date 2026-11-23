package com.worldpay.converters.populators.internal.model.klarna;

import com.worldpay.data.klarna.KlarnaPayment;
import com.worldpay.internal.model.CancelURL;
import com.worldpay.internal.model.FailureURL;
import com.worldpay.internal.model.KLARNAPAYNOWSSL;
import com.worldpay.internal.model.PendingURL;
import com.worldpay.internal.model.SuccessURL;

/**
 * Populator that fills the necessary details on a {@link KLARNAPAYNOWSSL} with the information of a {@link KlarnaPayment}
 */
public class KlarnaPayNowPaymentPopulator extends AbstractKlarnaPaymentPopulator<KLARNAPAYNOWSSL> {

    @Override
    protected void setSuccessURL(final KLARNAPAYNOWSSL target, final SuccessURL value) {
        target.setSuccessURL(value);
    }

    @Override
    protected void setFailureURL(final KLARNAPAYNOWSSL target, final FailureURL value) {
        target.setFailureURL(value);
    }

    @Override
    protected void setCancelURL(final KLARNAPAYNOWSSL target, final CancelURL value) {
        target.setCancelURL(value);
    }

    @Override
    protected void setPendingURL(final KLARNAPAYNOWSSL target, final PendingURL value) {
        target.setPendingURL(value);
    }

    @Override
    protected void setLocale(final KLARNAPAYNOWSSL target, final String value) {
        target.setLocale(value);
    }

    @Override
    protected void setShopperCountryCode(final KLARNAPAYNOWSSL target, final String value) {
        target.setShopperCountryCode(value);
    }
}

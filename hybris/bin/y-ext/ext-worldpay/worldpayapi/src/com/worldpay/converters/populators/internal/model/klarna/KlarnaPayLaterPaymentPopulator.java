package com.worldpay.converters.populators.internal.model.klarna;

import com.worldpay.data.klarna.KlarnaPayment;
import com.worldpay.internal.model.CancelURL;
import com.worldpay.internal.model.FailureURL;
import com.worldpay.internal.model.KLARNAPAYLATERSSL;
import com.worldpay.internal.model.PendingURL;
import com.worldpay.internal.model.SuccessURL;

/**
 * Populator that fills the necessary details on a {@link KLARNAPAYLATERSSL} with the information of a {@link KlarnaPayment}
 */
public class KlarnaPayLaterPaymentPopulator extends AbstractKlarnaPaymentPopulator<KLARNAPAYLATERSSL> {

    @Override
    protected void setSuccessURL(final KLARNAPAYLATERSSL target, final SuccessURL value) {
        target.setSuccessURL(value);
    }

    @Override
    protected void setFailureURL(final KLARNAPAYLATERSSL target, final FailureURL value) {
        target.setFailureURL(value);
    }

    @Override
    protected void setCancelURL(final KLARNAPAYLATERSSL target, final CancelURL value) {
        target.setCancelURL(value);
    }

    @Override
    protected void setPendingURL(final KLARNAPAYLATERSSL target, final PendingURL value) {
        target.setPendingURL(value);
    }

    @Override
    protected void setLocale(final KLARNAPAYLATERSSL target, final String value) {
        target.setLocale(value);
    }

    @Override
    protected void setShopperCountryCode(final KLARNAPAYLATERSSL target, final String value) {
        target.setShopperCountryCode(value);
    }
}

package com.worldpay.converters.populators.internal.model.payment;

import com.worldpay.data.payment.AlternativeShopperBankCodePayment;
import com.worldpay.internal.model.CancelURL;
import com.worldpay.internal.model.FailureURL;
import com.worldpay.internal.model.IDEALSSL;
import com.worldpay.internal.model.PendingURL;
import com.worldpay.internal.model.SuccessURL;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.IDEALSSL} with the information of a {@link AlternativeShopperBankCodePayment}.
 */
public class IdealPaymentPopulator extends AbstractAlternativePaymentPopulator<AlternativeShopperBankCodePayment, IDEALSSL> {

    @Override
    protected void setSuccessURL(final IDEALSSL target, final SuccessURL value) {
        target.setSuccessURL(value);
    }

    @Override
    protected void setFailureURL(final IDEALSSL target, final FailureURL value) {
        target.setFailureURL(value);
    }

    @Override
    protected void setCancelURL(final IDEALSSL target, final CancelURL value) {
        target.setCancelURL(value);
    }

    @Override
    protected void setPendingURL(final IDEALSSL target, final PendingURL value) {
        target.setPendingURL(value);
    }
}

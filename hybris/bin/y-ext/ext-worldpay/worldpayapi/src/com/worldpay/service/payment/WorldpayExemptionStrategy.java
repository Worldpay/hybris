package com.worldpay.service.payment;

import com.worldpay.data.PaymentReply;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;


/**
 * Strategy to handle the Exemption logic.
 */
public interface WorldpayExemptionStrategy {


    /**
     * Checks if the Exemption is enabled for the session site
     *
     * @return true if enabled, false otherwise
     */
    boolean isExemptionEnabled();

    /**
     * Checks if the Exemption is enabled for the given site
     *
     * @param baseSite the given site
     * @return true if enabled, false otherwise
     */
    boolean isExemptionEnabled(BaseSiteModel baseSite);

    /**
     * Sets the exemption on a paymentTransactionModel
     *
     * @param paymentTransactionModel the payment transaction
     * @param paymentReply            the payment response from Worldpay
     */
    void addExemptionResponse(final PaymentTransactionModel paymentTransactionModel, final PaymentReply paymentReply);

}

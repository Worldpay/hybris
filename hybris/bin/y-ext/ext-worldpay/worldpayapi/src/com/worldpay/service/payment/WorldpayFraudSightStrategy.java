package com.worldpay.service.payment;

import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.data.FraudSightData;
import com.worldpay.data.PaymentReply;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

/**
 * Strategy to handle the Fraud Sight logic.
 */
public interface WorldpayFraudSightStrategy {

    /**
     * Checks if the Fraud Sight is enabled for the session site
     *
     * @return true if enabled, false otherwise
     */
    boolean isFraudSightEnabled();

    /**
     * Checks if the Fraud Sight is enabled for the given site
     *
     * @param baseSite the given site
     * @return true if enabled, false otherwise
     */
    boolean isFraudSightEnabled(BaseSiteModel baseSite);

    /**
     * Creates the FraudSightData based on the abstractOrder details and worldpayAdditionalInfoData. Override this method if you want to customize
     * the logic related to the Fraud Sight payload
     *
     * @param abstractOrder              the cart
     * @param worldpayAdditionalInfoData the additional info
     * @return a populated {@link FraudSightData}
     */
    FraudSightData createFraudSightData(AbstractOrderModel abstractOrder, WorldpayAdditionalInfoData worldpayAdditionalInfoData);

    /**
     * Sets the fraud sight on a paymentTransactionEntryModel
     *
     * @param paymentTransactionModel the payment transaction
     * @param paymentReply            the payment response from Worldpay
     */
    void addFraudSight(final PaymentTransactionModel paymentTransactionModel, final PaymentReply paymentReply);

}

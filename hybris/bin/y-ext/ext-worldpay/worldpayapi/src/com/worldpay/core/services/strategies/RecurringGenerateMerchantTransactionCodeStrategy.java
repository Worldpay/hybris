package com.worldpay.core.services.strategies;

import de.hybris.platform.commerceservices.strategies.GenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 * Used to generate a unique id for Merchant transaction
 */
public interface RecurringGenerateMerchantTransactionCodeStrategy extends GenerateMerchantTransactionCodeStrategy {

    /**
     * Generates a unique id for a {@link de.hybris.platform.payment.model.PaymentTransactionModel}
     * @param abstractOrderModel An order
     * @return A unique identifier
     */
    String generateCode(AbstractOrderModel abstractOrderModel);

}

package com.worldpay.transaction;

import com.worldpay.data.Amount;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Util class for Payment transaction
 */
public interface WorldpayPaymentTransactionUtils {

    /**
     * Generates payment transaction code
     *
     * @param paymentTransaction the payment transaction
     * @return the code generated
     */
    String generatePaymentTransactionCode(PaymentTransactionModel paymentTransaction);

    /**
     * Gets the payment transaction dependency map from spring
     *
     * @return the spring map
     */
    Map<PaymentTransactionType, PaymentTransactionType> getPaymentTransactionDependency();

    /**
     * Gets the currency model object from the Amount
     *
     * @param amount the amount
     * @return the {@link CurrencyModel}
     */
    CurrencyModel getCurrencyFromAmount(Amount amount);

    /**
     * Converts the amount to a BigDecimal object
     *
     * @param amount the amount to convert
     * @return the big decimal value
     */
    BigDecimal convertAmount(Amount amount);

    /**
     * Gets the authorised amount tolerance from the configuration
     *
     * @return returns the configuration value
     */
    double getAuthoriseAmountToleranceFromConfig();
}

package com.worldpay.transaction.impl;

import com.worldpay.data.Amount;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.transaction.EntryCodeStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionUtils;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.util.Map;

public class DefaultWorldpayPaymentTransactionUtils implements WorldpayPaymentTransactionUtils {

    protected final EntryCodeStrategy entryCodeStrategy;
    protected final CommonI18NService commonI18NService;
    protected final Map<PaymentTransactionType, PaymentTransactionType> paymentTransactionDependency;
    protected final WorldpayOrderService worldpayOrderService;
    protected final ConfigurationService configurationService;

    public DefaultWorldpayPaymentTransactionUtils(final EntryCodeStrategy entryCodeStrategy,
                                                  final CommonI18NService commonI18NService,
                                                  final Map<PaymentTransactionType, PaymentTransactionType> paymentTransactionDependency,
                                                  final WorldpayOrderService worldpayOrderService,
                                                  final ConfigurationService configurationService) {
        this.entryCodeStrategy = entryCodeStrategy;
        this.commonI18NService = commonI18NService;
        this.paymentTransactionDependency = paymentTransactionDependency;
        this.worldpayOrderService = worldpayOrderService;
        this.configurationService = configurationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generatePaymentTransactionCode(final PaymentTransactionModel paymentTransaction) {
        return entryCodeStrategy.generateCode(paymentTransaction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<PaymentTransactionType, PaymentTransactionType> getPaymentTransactionDependency() {
        return paymentTransactionDependency;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CurrencyModel getCurrencyFromAmount(final Amount amount) {
        return commonI18NService.getCurrency(amount.getCurrencyCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal convertAmount(final Amount amount) {
        return worldpayOrderService.convertAmount(amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getAuthoriseAmountToleranceFromConfig() {
        return configurationService.getConfiguration().getDouble("worldpayapi.authoriseamount.validation.tolerance");
    }

}

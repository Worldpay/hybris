package com.worldpay.strategies.paymenttransaction.impl;

import com.worldpay.notification.processors.WorldpayOrderNotificationHandler;
import com.worldpay.strategies.paymenttransaction.WorldpayPaymentTransactionTypeStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;

/**
 * Default implementation of authorized type strategy
 */
public class DefaultWorldpayAuthorizedPaymentTransactionTypeStrategy implements WorldpayPaymentTransactionTypeStrategy {

    protected final WorldpayOrderNotificationHandler worldpayOrderNotificationHandler;
    protected final WorldpayPaymentTransactionService worldpayPaymentTransactionService;

    public DefaultWorldpayAuthorizedPaymentTransactionTypeStrategy(final WorldpayOrderNotificationHandler worldpayOrderNotificationHandler,
                                                                   final WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayOrderNotificationHandler = worldpayOrderNotificationHandler;
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processModificationMessage(final OrderModel order, final WorldpayOrderModificationModel orderModification) {
        worldpayPaymentTransactionService.getNotPendingPaymentTransactionEntriesForType(order.getPaymentTransactions().get(0), PaymentTransactionType.AUTHORIZATION)
            .forEach(paymentTransactionEntryModel -> worldpayOrderNotificationHandler.setNonDefectiveAndProcessed(orderModification));
    }
}

package com.worldpay.strategies.paymenttransaction.impl;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.strategies.paymenttransaction.WorldpayPaymentTransactionTypeStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;

/**
 * Default implementation of authorized type strategy
 */
public class DefaultWorldpayAuthorizedPaymentTransactionTypeStrategy implements WorldpayPaymentTransactionTypeStrategy {

    protected final OrderNotificationService orderNotificationService;
    protected final WorldpayPaymentTransactionService worldpayPaymentTransactionService;

    public DefaultWorldpayAuthorizedPaymentTransactionTypeStrategy(final OrderNotificationService orderNotificationService,
                                                                   final WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.orderNotificationService = orderNotificationService;
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processModificationMessage(final OrderModel order, final WorldpayOrderModificationModel orderModification) {
        worldpayPaymentTransactionService.getNotPendingPaymentTransactionEntriesForType(order.getPaymentTransactions().get(0), PaymentTransactionType.AUTHORIZATION)
            .forEach(paymentTransactionEntryModel -> orderNotificationService.setNonDefectiveAndProcessed(orderModification));
    }
}

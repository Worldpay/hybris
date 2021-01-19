package com.worldpay.strategies.paymenttransaction.impl;

import com.worldpay.notification.processors.WorldpayOrderNotificationHandler;
import com.worldpay.strategies.paymenttransaction.WorldpayPaymentTransactionTypeStrategy;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import static java.text.MessageFormat.format;

/**
 * Default implementation of refused type strategy
 */
public class DefaultWorldpayRefusedPaymentTransactionTypeStrategy implements WorldpayPaymentTransactionTypeStrategy {

    private static final Logger LOG = LogManager.getLogger(DefaultWorldpayRefusedPaymentTransactionTypeStrategy.class);

    protected final WorldpayOrderNotificationHandler worldpayOrderNotificationHandler;

    public DefaultWorldpayRefusedPaymentTransactionTypeStrategy(final WorldpayOrderNotificationHandler worldpayOrderNotificationHandler) {
        this.worldpayOrderNotificationHandler = worldpayOrderNotificationHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processModificationMessage(final OrderModel order, final WorldpayOrderModificationModel orderModification) {
        LOG.info(format("Marking order modification with [{0}] transaction for refused worldpayOrder [{1}] as processed", PaymentTransactionType.REFUSED, orderModification.getWorldpayOrderCode()));
        worldpayOrderNotificationHandler.setNonDefectiveAndProcessed(orderModification);
    }
}

package com.worldpay.strategies.paymenttransaction.impl;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategies.paymenttransaction.WorldpayPaymentTransactionTypeStrategy;
import com.worldpay.worldpaynotifications.enums.DefectiveReason;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.OrderModel;

/**
 * Default implementation of voided type strategy
 */
public class DefaultWorldpayVoidedPaymentTransactionTypeStrategy implements WorldpayPaymentTransactionTypeStrategy {

    protected final OrderNotificationService orderNotificationService;

    public DefaultWorldpayVoidedPaymentTransactionTypeStrategy(final OrderNotificationService orderNotificationService) {
        this.orderNotificationService = orderNotificationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processModificationMessage(final OrderModel order, final WorldpayOrderModificationModel orderModification) {
        final OrderNotificationMessage notificationMessage = orderNotificationService.deserialiseNotification(orderModification.getOrderNotificationMessage());
        try {
            orderNotificationService.processOrderNotificationMessage(notificationMessage, orderModification);
            orderNotificationService.setNonDefectiveAndProcessed(orderModification);
        } catch (final WorldpayConfigurationException e) {
            orderNotificationService.setDefectiveReason(orderModification, DefectiveReason.PROCESSING_ERROR);
            orderNotificationService.setDefectiveModification(orderModification, null, true);
        }
    }
}

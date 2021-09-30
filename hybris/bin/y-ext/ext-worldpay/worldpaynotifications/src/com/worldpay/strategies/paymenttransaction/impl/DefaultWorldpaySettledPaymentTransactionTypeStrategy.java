package com.worldpay.strategies.paymenttransaction.impl;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategies.paymenttransaction.WorldpayPaymentTransactionTypeStrategy;
import com.worldpay.worldpaynotifications.enums.DefectiveReason;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.OrderModel;

/**
 * Default implementation of settled type strategy
 */
public class DefaultWorldpaySettledPaymentTransactionTypeStrategy implements WorldpayPaymentTransactionTypeStrategy {

    protected final OrderNotificationService orderNotificationService;

    public DefaultWorldpaySettledPaymentTransactionTypeStrategy(final OrderNotificationService orderNotificationService) {
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

        } catch (WorldpayConfigurationException e) {
            orderNotificationService.setDefectiveReason(orderModification, DefectiveReason.PROCESSING_ERROR);
            orderNotificationService.setDefectiveModification(orderModification, null, true);
        }
    }
}

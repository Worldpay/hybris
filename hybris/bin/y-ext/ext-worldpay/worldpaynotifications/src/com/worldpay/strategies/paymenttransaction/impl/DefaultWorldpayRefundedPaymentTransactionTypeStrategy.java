package com.worldpay.strategies.paymenttransaction.impl;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategies.WorldpayOrderModificationRefundProcessStrategy;
import com.worldpay.strategies.paymenttransaction.WorldpayPaymentTransactionTypeStrategy;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.OrderModel;

import static com.worldpay.worldpaynotifications.enums.DefectiveReason.INVALID_AUTHENTICATED_SHOPPER_ID;

/**
 * Default implementation of refund type strategy
 */
public class DefaultWorldpayRefundedPaymentTransactionTypeStrategy implements WorldpayPaymentTransactionTypeStrategy {

    protected final WorldpayOrderModificationRefundProcessStrategy worldpayOrderModificationRefundProcessStrategy;
    protected final OrderNotificationService orderNotificationService;

    public DefaultWorldpayRefundedPaymentTransactionTypeStrategy(final WorldpayOrderModificationRefundProcessStrategy worldpayOrderModificationRefundProcessStrategy,
                                                                 final OrderNotificationService orderNotificationService) {
        this.worldpayOrderModificationRefundProcessStrategy = worldpayOrderModificationRefundProcessStrategy;
        this.orderNotificationService = orderNotificationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processModificationMessage(final OrderModel order, final WorldpayOrderModificationModel orderModification) {
        final OrderNotificationMessage notificationMessage = orderNotificationService.deserialiseNotification(orderModification.getOrderNotificationMessage());
        if (worldpayOrderModificationRefundProcessStrategy.processRefundFollowOn(order, notificationMessage)) {
            try {
                orderNotificationService.processOrderNotificationMessage(notificationMessage, orderModification);
                orderNotificationService.setNonDefectiveAndProcessed(orderModification);
            } catch (final WorldpayConfigurationException e) {
                orderNotificationService.setDefectiveReason(orderModification, INVALID_AUTHENTICATED_SHOPPER_ID);
                orderNotificationService.setDefectiveModification(orderModification, null, true);
            }
        } else {
            orderNotificationService.setDefectiveModification(orderModification, null, true);
        }
    }
}

package com.worldpay.strategies.paymenttransaction.impl;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.notification.processors.WorldpayOrderNotificationHandler;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategies.WorldpayOrderModificationRefundProcessStrategy;
import com.worldpay.strategies.paymenttransaction.WorldpayPaymentTransactionTypeStrategy;
import com.worldpay.util.OrderModificationSerialiser;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.OrderModel;

import static com.worldpay.worldpaynotifications.enums.DefectiveReason.INVALID_AUTHENTICATED_SHOPPER_ID;

/**
 * Default implementation of refund type strategy
 */
public class DefaultWorldpayRefundedPaymentTransactionTypeStrategy implements WorldpayPaymentTransactionTypeStrategy {

    protected final WorldpayOrderModificationRefundProcessStrategy worldpayOrderModificationRefundProcessStrategy;
    protected final WorldpayOrderNotificationHandler worldpayOrderNotificationHandler;
    protected final OrderNotificationService orderNotificationService;
    protected final OrderModificationSerialiser orderModificationSerialiser;

    public DefaultWorldpayRefundedPaymentTransactionTypeStrategy(final WorldpayOrderModificationRefundProcessStrategy worldpayOrderModificationRefundProcessStrategy,
                                                                 final WorldpayOrderNotificationHandler worldpayOrderNotificationHandler,
                                                                 final OrderNotificationService orderNotificationService,
                                                                 final OrderModificationSerialiser orderModificationSerialiser) {
        this.worldpayOrderModificationRefundProcessStrategy = worldpayOrderModificationRefundProcessStrategy;
        this.worldpayOrderNotificationHandler = worldpayOrderNotificationHandler;
        this.orderNotificationService = orderNotificationService;
        this.orderModificationSerialiser = orderModificationSerialiser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processModificationMessage(final OrderModel order, final WorldpayOrderModificationModel orderModification) {
        final OrderNotificationMessage notificationMessage = orderModificationSerialiser.deserialise(orderModification.getOrderNotificationMessage());
        if (worldpayOrderModificationRefundProcessStrategy.processRefundFollowOn(order, notificationMessage)) {
            try {
                orderNotificationService.processOrderNotificationMessage(notificationMessage, orderModification);
                worldpayOrderNotificationHandler.setNonDefectiveAndProcessed(orderModification);
            } catch (final WorldpayConfigurationException e) {
                worldpayOrderNotificationHandler.setDefectiveReason(orderModification, INVALID_AUTHENTICATED_SHOPPER_ID);
                worldpayOrderNotificationHandler.setDefectiveModification(orderModification, null, true);
            }
        } else {
            worldpayOrderNotificationHandler.setDefectiveModification(orderModification, null, true);
        }
    }
}

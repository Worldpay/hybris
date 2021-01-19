package com.worldpay.strategies.paymenttransaction.impl;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.notification.processors.WorldpayOrderNotificationHandler;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategies.paymenttransaction.WorldpayPaymentTransactionTypeStrategy;
import com.worldpay.util.OrderModificationSerialiser;
import com.worldpay.worldpaynotifications.enums.DefectiveReason;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.OrderModel;

/**
 * Default implementation of settled type strategy
 */
public class DefaultWorldpaySettledPaymentTransactionTypeStrategy implements WorldpayPaymentTransactionTypeStrategy {

    protected final OrderNotificationService orderNotificationService;
    protected final OrderModificationSerialiser orderModificationSerialiser;
    protected final WorldpayOrderNotificationHandler worldpayOrderNotificationHandler;

    public DefaultWorldpaySettledPaymentTransactionTypeStrategy(final OrderNotificationService orderNotificationService,
                                                                final OrderModificationSerialiser orderModificationSerialiser,
                                                                final WorldpayOrderNotificationHandler worldpayOrderNotificationHandler) {
        this.orderNotificationService = orderNotificationService;
        this.orderModificationSerialiser = orderModificationSerialiser;
        this.worldpayOrderNotificationHandler = worldpayOrderNotificationHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processModificationMessage(final OrderModel order, final WorldpayOrderModificationModel orderModification) {
        final OrderNotificationMessage notificationMessage = orderModificationSerialiser.deserialise(orderModification.getOrderNotificationMessage());
        try {
            orderNotificationService.processOrderNotificationMessage(notificationMessage, orderModification);
            worldpayOrderNotificationHandler.setNonDefectiveAndProcessed(orderModification);

        } catch (WorldpayConfigurationException e) {
            worldpayOrderNotificationHandler.setDefectiveReason(orderModification, DefectiveReason.INVALID_AUTHENTICATED_SHOPPER_ID);
            worldpayOrderNotificationHandler.setDefectiveModification(orderModification, null, true);
        }
    }
}

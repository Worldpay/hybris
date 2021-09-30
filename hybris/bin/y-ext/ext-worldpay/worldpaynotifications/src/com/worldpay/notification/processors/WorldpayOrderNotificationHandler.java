package com.worldpay.notification.processors;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;

/**
 * Exposes methods that interact with the worldpayOrderModificationModel
 */
public interface WorldpayOrderNotificationHandler {


    /**
     * Handles order notifications and triggers business process if needed
     *
     * @param paymentTransactionType the payment transaction type
     * @param orderModificationModel order modification
     * @param orderModel             the order related to the modification
     * @param notificationMessage    the notification message
     * @throws WorldpayConfigurationException
     */
    void handleNotificationBusinessProcess(PaymentTransactionType paymentTransactionType,
                                           WorldpayOrderModificationModel orderModificationModel,
                                           OrderModel orderModel,
                                           OrderNotificationMessage notificationMessage) throws WorldpayConfigurationException;

}

package com.worldpay.notification.processors;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.worldpaynotifications.enums.DefectiveReason;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;

/**
 * Exposes methods that interact with the worldpayOrderModificationModel
 */
public interface WorldpayOrderNotificationHandler {

    /**
     * Sets the notification as defective
     * Logs the message from the provided exception
     * Sets the notification as processed by the given processed flag
     * Saves the notification
     *
     * @param orderModificationModel
     * @param exception
     * @param processed
     */
    void setDefectiveModification(WorldpayOrderModificationModel orderModificationModel, Exception exception, boolean processed);

    /**
     * Sets the defective reason given to the order notification.
     * Increments the defectiveCounter in the order notification.
     *
     * @param orderModificationModel
     * @param defectiveReason
     */
    void setDefectiveReason(WorldpayOrderModificationModel orderModificationModel, DefectiveReason defectiveReason);

    /**
     * Sets the notification as processed and non defective
     * Saves the notification
     *
     * @param modification
     */
    void setNonDefectiveAndProcessed(WorldpayOrderModificationModel modification);

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

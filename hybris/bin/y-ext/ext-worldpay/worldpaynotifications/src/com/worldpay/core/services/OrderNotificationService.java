package com.worldpay.core.services;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.worldpaynotifications.enums.DefectiveReason;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;

import java.util.List;


/**
 * Order Notification Service interface. Service is responsible for processing the order notification from worldpay and
 * to organising the calls to the DAO to fetch required objects using the world pay order number.
 */
public interface OrderNotificationService {

    /**
     * Processes the order notification message from worldpay. - For an authorised message, saves the card information
     * against the customer for future use and invokes the place order business process which moves the order through the
     * subsequent states.
     *
     * @param orderNotificationMessage - The order notification message from worldpay.
     */
    void processOrderNotificationMessage(OrderNotificationMessage orderNotificationMessage, WorldpayOrderModificationModel worldpayOrderModification) throws WorldpayConfigurationException;

    /**
     * Verifies if the order notification is valid or not
     *
     * @param notificationMessage the notification to verify
     * @param orderModel          the order related to the notification
     * @return true if notification is valid, false otherwise
     */
    boolean isNotificationValid(OrderNotificationMessage notificationMessage, AbstractOrderModel orderModel);

    /**
     * Finds existing modifications similar to the passed as parameter.
     *
     * @param orderModificationModel the latest creation date
     * @return the list of {@link WorldpayOrderModificationModel}
     */
    List<WorldpayOrderModificationModel> getExistingModifications(WorldpayOrderModificationModel orderModificationModel);

    /**
     * Serialises the Order Notification Message.
     *
     * @param orderNotificationMessage {@link OrderNotificationMessage}
     * @return serialised order notification message
     */
    String serialiseNotification(OrderNotificationMessage orderNotificationMessage);

    /**
     * Deserialises the Order Notification Message.
     *
     * @param json serialised {@link OrderNotificationMessage}
     * @return deserialised {@link OrderNotificationMessage}
     */
    OrderNotificationMessage deserialiseNotification(String json);

    /**
     * Finds unprocessed order modifications by payment transaction type
     *
     * @param paymentTransactionType {@link PaymentTransactionType}
     * @return the list of {@link WorldpayOrderModificationModel}
     */
    List<WorldpayOrderModificationModel> getUnprocessedOrderModificationsByType(PaymentTransactionType paymentTransactionType);


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
}

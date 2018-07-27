package com.worldpay.notification.processors;

import com.worldpay.worldpaynotifications.enums.DefectiveReason;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;

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
    void setDefectiveModification(final WorldpayOrderModificationModel orderModificationModel, final Exception exception, boolean processed);

    /**
     * Sets the defective reason given to the order notification.
     * Increments the defectiveCounter in the order notification.
     *
     * @param orderModificationModel
     * @param defectiveReason
     */
    void setDefectiveReason(final WorldpayOrderModificationModel orderModificationModel, final DefectiveReason defectiveReason);

    /**
     * Sets the notification as processed and non defective
     * Saves the notification
     *
     * @param modification
     */
    void setNonDefectiveAndProcessed(final WorldpayOrderModificationModel modification);
}

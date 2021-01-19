package com.worldpay.strategies.paymenttransaction;

import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.OrderModel;

/**
 * Worldpay payment transaction strategy interface
 */
public interface WorldpayPaymentTransactionTypeStrategy {

    /**
     * Processes the modification message for the given order
     *
     * @param order             the order related to the notification
     * @param orderModification the order modification
     */
    void processModificationMessage(OrderModel order, WorldpayOrderModificationModel orderModification);
}

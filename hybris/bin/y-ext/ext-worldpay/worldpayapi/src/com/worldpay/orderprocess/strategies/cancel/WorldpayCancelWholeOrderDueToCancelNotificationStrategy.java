package com.worldpay.orderprocess.strategies.cancel;

import de.hybris.platform.orderprocessing.model.OrderProcessModel;

/**
 * Strategy responsible of doing the order cancellations due to Worldpay cancel notification
 */
public interface WorldpayCancelWholeOrderDueToCancelNotificationStrategy {

    /**
     * Fulfill the order cancellation steps
     * @param orderProcessModel
     */
    void cancelOrder(final OrderProcessModel orderProcessModel);
}

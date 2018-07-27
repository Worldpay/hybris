package com.worldpay.strategies;

import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.CartModel;

/**
 * Provides methods to place orders from server to server notification.
 */
public interface WorldpayPlaceOrderFromNotificationStrategy {

    /**
     * Places an order from the given cart and the information from the orderNotification
     *
     * @param orderModificationModel notification
     * @param cart                   cart
     */
    void placeOrderFromNotification(final WorldpayOrderModificationModel orderModificationModel, final CartModel cart);
}

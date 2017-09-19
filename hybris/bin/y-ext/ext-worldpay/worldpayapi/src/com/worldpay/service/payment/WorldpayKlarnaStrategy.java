package com.worldpay.service.payment;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.model.OrderLines;
import de.hybris.platform.core.model.order.CartModel;

/**
 * Strategy to create the order lines necessary for Klarna Integration
 */
public interface WorldpayKlarnaStrategy {

    /**
     * @param cartModel the current cart
     * @return the {@link OrderLines} with the correct values for Klarna
     * @throws WorldpayConfigurationException Exception thrown when the URL for the terms and conditions is not configured
     */
    OrderLines createOrderLines(CartModel cartModel) throws WorldpayConfigurationException;
}

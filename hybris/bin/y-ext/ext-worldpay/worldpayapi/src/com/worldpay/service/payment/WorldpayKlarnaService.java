package com.worldpay.service.payment;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.data.OrderLines;
import de.hybris.platform.core.model.order.CartModel;

/**
 * Service to create the order lines necessary for Klarna Integration and other Klarna functionalities
 */
public interface WorldpayKlarnaService {

    /**
     * @param cartModel the current cart
     * @return the {@link OrderLines} with the correct values for Klarna
     * @throws WorldpayConfigurationException Exception thrown when the URL for the terms and conditions is not configured
     */
    OrderLines createOrderLines(CartModel cartModel) throws WorldpayConfigurationException;

    /**
     * Checks whether  the paymentCode passed is or not a Klarna valid payment method type code
     *
     * @param paymentCode the payment code
     * @return true if it is klarna payment, false otherwise
     */
    boolean isKlarnaPaymentType(final String paymentCode);
}

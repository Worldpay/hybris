package com.worldpay.core.services;

import de.hybris.platform.core.model.order.OrderModel;

/**
 * Service providing extended Worldpay order service functionality.
 *
 * @spring.bean worldpayCartService
 */
public interface WorldpayHybrisOrderService {

    /**
     * Sets worldpay decline code on the {@link OrderModel}.
     *
     * @param worldpayOrderCode the worldpay order code
     * @param declineCode       the decline code
     */
    void setWorldpayDeclineCodeOnOrder(final String worldpayOrderCode, final String declineCode);

    /**
     * Returns the order by WorldpayOrderCode
     *
     * @param worldpayOrderCode
     * @return
     */
    OrderModel findOrderByWorldpayOrderCode(String worldpayOrderCode);

    /**
     * Returns the order code by WorldpayOrderCode
     *
     * @param worldpayOrderCode
     * @return
     */
    String findOrderCodeByWorldpayOrderCode(String worldpayOrderCode);

}

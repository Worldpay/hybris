package com.worldpay.core.dao;

import de.hybris.platform.core.model.order.OrderModel;

import java.util.List;

/**
 * Data access to {@link OrderModel}
 *
 */
public interface WorldpayHybrisOrderDao {

    /**
     * Returns a {@link List} of {@link OrderModel} which are related to a specific Worldpay order code
     *
     * @param worldpayOrderCode Worldpay order code
     * @return list of {@link OrderModel}
     */
    OrderModel findOrderByWorldpayOrderCode(final String worldpayOrderCode);

}

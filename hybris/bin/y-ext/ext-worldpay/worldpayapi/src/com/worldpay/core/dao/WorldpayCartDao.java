package com.worldpay.core.dao;

import de.hybris.platform.core.model.order.CartModel;

/**
 * Data access to {@link CartModel}
 *
 */
public interface WorldpayCartDao {

    /**
     * Returns a {@link CartModel} which is related to a specific Worldpay order code
     *
     * @param worldpayOrderCode Worldpay order code
     * @return {@link CartModel}
     */
    CartModel findCartByWorldpayOrderCode(final String worldpayOrderCode);
}

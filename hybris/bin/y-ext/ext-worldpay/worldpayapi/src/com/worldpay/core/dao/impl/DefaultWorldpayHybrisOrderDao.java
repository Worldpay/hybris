package com.worldpay.core.dao.impl;

import com.worldpay.core.dao.WorldpayHybrisOrderDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.Collections;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayHybrisOrderDao extends AbstractItemDao implements WorldpayHybrisOrderDao {

    protected static final String QUERY_BY_WORLDPAY_ORDER_CODE = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE +
                                                        "} WHERE {worldpayOrderCode} = ?worldpayOrderCode AND {versionID} is null";
    protected static final String PARAM_WORLD_PAY_ORDER_CODE = "worldpayOrderCode";

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderModel findOrderByWorldpayOrderCode(final String worldpayOrderCode) {
        final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(QUERY_BY_WORLDPAY_ORDER_CODE);
        fQuery.addQueryParameters(Collections.singletonMap(PARAM_WORLD_PAY_ORDER_CODE, worldpayOrderCode));
        return searchUnique(fQuery);
    }
}

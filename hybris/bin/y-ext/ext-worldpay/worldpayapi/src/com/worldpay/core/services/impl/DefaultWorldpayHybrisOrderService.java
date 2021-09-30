package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayHybrisOrderDao;
import com.worldpay.core.services.WorldpayHybrisOrderService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayHybrisOrderService implements WorldpayHybrisOrderService {

    protected final WorldpayHybrisOrderDao worldpayHybrisOrderDao;
    protected final ModelService modelService;

    public DefaultWorldpayHybrisOrderService(final WorldpayHybrisOrderDao worldpayHybrisOrderDao, final ModelService modelService) {
        this.worldpayHybrisOrderDao = worldpayHybrisOrderDao;
        this.modelService = modelService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWorldpayDeclineCodeOnOrder(final String worldpayOrderCode, final String declineCode) {

        final OrderModel orderModel = findOrderByWorldpayOrderCode(worldpayOrderCode);
        orderModel.setWorldpayDeclineCode(declineCode);
        modelService.save(orderModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderModel findOrderByWorldpayOrderCode(final String worldpayOrderCode) {
        return worldpayHybrisOrderDao.findOrderByWorldpayOrderCode(worldpayOrderCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String findOrderCodeByWorldpayOrderCode(final String worldpayOrderCode) {
        return findOrderByWorldpayOrderCode(worldpayOrderCode).getCode();
    }
}

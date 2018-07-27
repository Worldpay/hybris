package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayHybrisOrderDao;
import com.worldpay.core.services.WorldpayHybrisOrderService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayHybrisOrderService implements WorldpayHybrisOrderService {

    private WorldpayHybrisOrderDao worldpayHybrisOrderDao;
    private ModelService modelService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWorldpayDeclineCodeOnOrder(final String worldpayOrderCode, final String declineCode) {

        final OrderModel orderModel = findOrderByWorldpayOrderCode(worldpayOrderCode);
        orderModel.setWorldpayDeclineCode(declineCode);
        modelService.save(orderModel);
    }

    @Override
    public OrderModel findOrderByWorldpayOrderCode(final String worldpayOrderCode) {
        return worldpayHybrisOrderDao.findOrderByWorldpayOrderCode(worldpayOrderCode);
    }

    @Override
    public String findOrderCodeByWorldpayOrderCode(final String worldpayOrderCode) {
        return findOrderByWorldpayOrderCode(worldpayOrderCode).getCode();
    }

    @Required
    public void setWorldpayHybrisOrderDao(final WorldpayHybrisOrderDao worldpayHybrisOrderDao) {
        this.worldpayHybrisOrderDao = worldpayHybrisOrderDao;
    }

    public WorldpayHybrisOrderDao getWorldpayHybrisOrderDao() {
        return worldpayHybrisOrderDao;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public ModelService getModelService() {
        return modelService;
    }
}

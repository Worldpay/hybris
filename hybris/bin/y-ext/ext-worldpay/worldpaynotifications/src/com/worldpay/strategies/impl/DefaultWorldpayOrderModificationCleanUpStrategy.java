package com.worldpay.strategies.impl;

import com.worldpay.dao.OrderModificationDao;
import com.worldpay.strategies.WorldpayOrderModificationCleanUpStrategy;
import com.worldpay.util.WorldpayUtil;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;

/**
 * Default implementation of the {@link WorldpayOrderModificationCleanUpStrategy}.
 * <p>
 * Retrieves all processed Order Modifications created before the certain amount of time
 * and removes them from the database.
 * </p>
 */
public class DefaultWorldpayOrderModificationCleanUpStrategy implements WorldpayOrderModificationCleanUpStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpayOrderModificationCleanUpStrategy.class);

    protected final ModelService modelService;
    protected final OrderModificationDao orderModificationDao;

    public DefaultWorldpayOrderModificationCleanUpStrategy(final ModelService modelService,
                                                           final OrderModificationDao orderModificationDao) {
        this.modelService = modelService;
        this.orderModificationDao = orderModificationDao;
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayOrderModificationCleanUpStrategy#doCleanUp(int)
     */
    @Override
    public void doCleanUp(int days) {
        List<WorldpayOrderModificationModel> processedOrderModificationsBeforeDate = getOrderModificationDao().findProcessedOrderModificationsBeforeDate(WorldpayUtil.createDateInPast(days));
        processedOrderModificationsBeforeDate.forEach(orderModificationModel -> {
            LOG.info("Deleting Order Modification Model with Worldpay Order code: {}", orderModificationModel.getWorldpayOrderCode());
            getModelService().remove(orderModificationModel);
        });
    }

    public ModelService getModelService() {
        return modelService;
    }

    public OrderModificationDao getOrderModificationDao() {
        return orderModificationDao;
    }

}

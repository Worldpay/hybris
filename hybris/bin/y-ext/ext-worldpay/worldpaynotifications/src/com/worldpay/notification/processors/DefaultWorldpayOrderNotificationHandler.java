package com.worldpay.notification.processors;

import com.worldpay.dao.OrderModificationDao;
import com.worldpay.worldpaynotifications.enums.DefectiveReason;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

import static java.text.MessageFormat.format;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayOrderNotificationHandler implements WorldpayOrderNotificationHandler {
    private static final Logger LOG = Logger.getLogger(DefaultWorldpayOrderNotificationHandler.class);

    private ModelService modelService;
    private OrderModificationDao orderModificationDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefectiveModification(final WorldpayOrderModificationModel orderModificationModel, final Exception exception, final boolean processed) {
        orderModificationModel.setDefective(Boolean.TRUE);
        orderModificationModel.setProcessed(processed);
        modelService.save(orderModificationModel);
        if (exception != null) {
            LOG.error(format("There was an error processing message [{0}]. Reason: [{1}]", orderModificationModel.getPk(), exception.getMessage()), exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefectiveReason(final WorldpayOrderModificationModel orderModificationModel, final DefectiveReason defectiveReason) {
        orderModificationModel.setDefectiveReason(defectiveReason);
        final List<WorldpayOrderModificationModel> existingModifications = orderModificationDao.findExistingModifications(orderModificationModel);

        int defectiveCounter = getDefectiveCounter(orderModificationModel) + existingModifications.stream()
                .mapToInt(this::getDefectiveCounter)
                .sum();
        existingModifications.forEach(modelService::remove);

        orderModificationModel.setDefectiveCounter(defectiveCounter + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNonDefectiveAndProcessed(final WorldpayOrderModificationModel modification) {
        modification.setProcessed(Boolean.TRUE);
        modification.setDefective(Boolean.FALSE);
        modelService.save(modification);
    }

    private int getDefectiveCounter(final WorldpayOrderModificationModel modification) {
        return modification.getDefectiveCounter() == null ? 0 : modification.getDefectiveCounter();
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setOrderModificationDao(final OrderModificationDao orderModificationDao) {
        this.orderModificationDao = orderModificationDao;
    }
}

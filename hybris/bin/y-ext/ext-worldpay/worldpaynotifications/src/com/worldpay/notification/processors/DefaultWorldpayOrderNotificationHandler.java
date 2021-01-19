package com.worldpay.notification.processors;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.dao.ProcessDefinitionDao;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.worldpaynotifications.enums.DefectiveReason;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Set;

import static com.worldpay.worldpaynotifications.enums.DefectiveReason.INVALID_AUTHENTICATED_SHOPPER_ID;
import static java.text.MessageFormat.format;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayOrderNotificationHandler implements WorldpayOrderNotificationHandler {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayOrderNotificationHandler.class);

    protected final ModelService modelService;
    protected final OrderNotificationService orderNotificationService;
    protected final ProcessDefinitionDao processDefinitionDao;
    protected final Set<OrderStatus> nonTriggeringOrderStatuses;
    protected final BusinessProcessService businessProcessService;

    public DefaultWorldpayOrderNotificationHandler(final ModelService modelService,
                                                   final OrderNotificationService orderNotificationService,
                                                   final ProcessDefinitionDao processDefinitionDao,
                                                   final Set<OrderStatus> nonTriggeringOrderStatuses,
                                                   final BusinessProcessService businessProcessService) {
        this.modelService = modelService;
        this.orderNotificationService = orderNotificationService;
        this.processDefinitionDao = processDefinitionDao;
        this.nonTriggeringOrderStatuses = nonTriggeringOrderStatuses;
        this.businessProcessService = businessProcessService;
    }

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
        final List<WorldpayOrderModificationModel> existingModifications = orderNotificationService.getExistingModifications(orderModificationModel);

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleNotificationBusinessProcess(final PaymentTransactionType paymentTransactionType,
                                                  final WorldpayOrderModificationModel orderModificationModel,
                                                  final OrderModel orderModel,
                                                  final OrderNotificationMessage notificationMessage) throws WorldpayConfigurationException {
        final List<BusinessProcessModel> businessProcessModels = processDefinitionDao.findWaitingOrderProcesses(orderModel.getCode(), paymentTransactionType);
        if (businessProcessModels.size() == 1) {
            if (orderNotificationService.isNotificationValid(notificationMessage, orderModel)) {
                orderNotificationService.processOrderNotificationMessage(notificationMessage, orderModificationModel);
                setNonDefectiveAndProcessed(orderModificationModel);
                if (!nonTriggeringOrderStatuses.contains(orderModel.getStatus())) {
                    triggerOrderProcessEvent(paymentTransactionType, businessProcessModels.get(0));
                }
            } else {
                LOG.error(format("Received modification with invalid shopperId. The Modification has been marked as defective. worldpayOrderCode = [{0}], type=[{1}]",
                    orderModificationModel.getWorldpayOrderCode(), orderModificationModel.getType()));
                setDefectiveReason(orderModificationModel, INVALID_AUTHENTICATED_SHOPPER_ID);
                setDefectiveModification(orderModificationModel, null, true);
            }
        } else if (businessProcessModels.size() > 1) {
            LOG.error(format("Must only be one businessProcess found, number found: [{0}] " +
                "for order [{1}] and transactionType [{2}]", businessProcessModels.size(), orderModel.getCode(), paymentTransactionType));
        }
    }

    private int getDefectiveCounter(final WorldpayOrderModificationModel modification) {
        return modification.getDefectiveCounter() == null ? 0 : modification.getDefectiveCounter();
    }

    private void triggerOrderProcessEvent(final PaymentTransactionType paymentTransactionType, final BusinessProcessModel businessProcessModel) {
        final String eventName = getEventName(paymentTransactionType, businessProcessModel);
        businessProcessService.triggerEvent(eventName);
    }

    private String getEventName(final PaymentTransactionType paymentTransactionType, final BusinessProcessModel businessProcess) {
        return businessProcess.getCode() + "_" + paymentTransactionType;
    }
}

package com.worldpay.notification.processors;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.dao.ProcessDefinitionDao;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static com.worldpay.worldpaynotifications.enums.DefectiveReason.INVALID_AUTHENTICATED_SHOPPER_ID;
import static java.text.MessageFormat.format;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayOrderNotificationHandler implements WorldpayOrderNotificationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpayOrderNotificationHandler.class);

    protected final OrderNotificationService orderNotificationService;
    protected final ProcessDefinitionDao processDefinitionDao;
    protected final Set<OrderStatus> nonTriggeringOrderStatuses;
    protected final BusinessProcessService businessProcessService;

    public DefaultWorldpayOrderNotificationHandler(final OrderNotificationService orderNotificationService,
                                                   final ProcessDefinitionDao processDefinitionDao,
                                                   final Set<OrderStatus> nonTriggeringOrderStatuses,
                                                   final BusinessProcessService businessProcessService) {
        this.orderNotificationService = orderNotificationService;
        this.processDefinitionDao = processDefinitionDao;
        this.nonTriggeringOrderStatuses = nonTriggeringOrderStatuses;
        this.businessProcessService = businessProcessService;
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
                orderNotificationService.setNonDefectiveAndProcessed(orderModificationModel);
                if (!nonTriggeringOrderStatuses.contains(orderModel.getStatus())) {
                    triggerOrderProcessEvent(paymentTransactionType, businessProcessModels.get(0));
                }
            } else {
                LOG.error("Received modification with invalid shopperId. The Modification has been marked as defective. worldpayOrderCode = [{}], type=[{}]",
                    orderModificationModel.getWorldpayOrderCode(), orderModificationModel.getType());
                orderNotificationService.setDefectiveReason(orderModificationModel, INVALID_AUTHENTICATED_SHOPPER_ID);
                orderNotificationService.setDefectiveModification(orderModificationModel, null, true);
            }
        } else if (businessProcessModels.size() > 1) {
            LOG.error("Must only be one businessProcess found, number found: [{}] " +
                "for order [{}] and transactionType [{}]", businessProcessModels.size(), orderModel.getCode(), paymentTransactionType);
        }
    }

    private void triggerOrderProcessEvent(final PaymentTransactionType paymentTransactionType, final BusinessProcessModel businessProcessModel) {
        final String eventName = getEventName(paymentTransactionType, businessProcessModel);
        businessProcessService.triggerEvent(eventName);
    }

    private String getEventName(final PaymentTransactionType paymentTransactionType, final BusinessProcessModel businessProcess) {
        return businessProcess.getCode() + "_" + paymentTransactionType;
    }
}

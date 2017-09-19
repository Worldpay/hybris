package com.worldpay.strategies.impl;

import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategies.WorldpayOrderModificationRefundProcessStrategy;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static de.hybris.platform.basecommerce.enums.ReturnStatus.CANCELED;
import static de.hybris.platform.basecommerce.enums.ReturnStatus.PAYMENT_REVERSED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.REFUND_FOLLOW_ON;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayomsOrderModificationRefundProcessStrategy implements WorldpayOrderModificationRefundProcessStrategy {

    private static final Logger LOG = LogManager.getLogger(DefaultWorldpayomsOrderModificationRefundProcessStrategy.class);

    private BusinessProcessService businessProcessService;
    private ModelService modelService;

    @Override
    public boolean processRefundFollowOn(final OrderModel orderModel, final OrderNotificationMessage notificationMessage) {
        final String refundReference = notificationMessage.getPaymentReply().getRefundReference();
        if (refundReference != null) {
            final List<ReturnRequestModel> returnRequests = orderModel.getReturnRequests();
            if (CollectionUtils.isNotEmpty(returnRequests)) {
                final Optional<ReturnRequestModel> returnRequest = findMatchingReturnRequest(refundReference, returnRequests);
                if (returnRequest.isPresent()) {
                    final ReturnRequestModel matchingReturnRequest = returnRequest.get();
                    matchingReturnRequest.setStatus(PAYMENT_REVERSED);
                    modelService.save(matchingReturnRequest);
                    final Collection<ReturnProcessModel> returnProcesses = matchingReturnRequest.getReturnProcess();
                    // Assuming one return process per returnRequest
                    final ReturnProcessModel returnProcessModel = returnProcesses.iterator().next();
                    triggerOrderProcessEvent(REFUND_FOLLOW_ON, returnProcessModel);
                    return true;
                }
            }
        } else {
            LOG.warn("Refund reference is null. The refund will never be processed against any of the return requests of the order");
        }
        return false;
    }

    protected Optional<ReturnRequestModel> findMatchingReturnRequest(final String refundReference, final List<ReturnRequestModel> returnRequests) {
        return returnRequests.stream().filter(returnRequest -> !CANCELED.equals(returnRequest.getStatus())).filter(returnRequest -> {
            final PaymentTransactionEntryModel paymentTransactionEntry = returnRequest.getPaymentTransactionEntry();
            return paymentTransactionEntry.getCode().equals(refundReference);
        }).findAny();
    }

    private void triggerOrderProcessEvent(final PaymentTransactionType paymentTransactionType, final BusinessProcessModel businessProcessModel) {
        final String eventName = getEventName(paymentTransactionType, businessProcessModel);
        businessProcessService.triggerEvent(eventName);
    }

    private String getEventName(PaymentTransactionType paymentTransactionType, BusinessProcessModel businessProcessModels) {
        return businessProcessModels.getCode() + "_" + paymentTransactionType;
    }

    @Required
    public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }
}

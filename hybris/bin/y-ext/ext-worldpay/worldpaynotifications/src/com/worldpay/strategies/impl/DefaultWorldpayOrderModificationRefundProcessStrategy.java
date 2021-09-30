package com.worldpay.strategies.impl;

import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategies.WorldpayOrderModificationRefundProcessStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static de.hybris.platform.basecommerce.enums.ReturnStatus.CANCELED;
import static de.hybris.platform.basecommerce.enums.ReturnStatus.PAYMENT_REVERSED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CANCEL;
import static de.hybris.platform.payment.enums.PaymentTransactionType.REFUND_FOLLOW_ON;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayOrderModificationRefundProcessStrategy implements WorldpayOrderModificationRefundProcessStrategy {

    private static final Logger LOG = LogManager.getLogger(DefaultWorldpayOrderModificationRefundProcessStrategy.class);

    protected final BusinessProcessService businessProcessService;
    protected final ModelService modelService;
    protected final WorldpayPaymentTransactionService worldpayPaymentTransactionService;

    public DefaultWorldpayOrderModificationRefundProcessStrategy(final BusinessProcessService businessProcessService,
                                                                 final ModelService modelService,
                                                                 final WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.businessProcessService = businessProcessService;
        this.modelService = modelService;
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean processRefundFollowOn(final OrderModel orderModel, final OrderNotificationMessage orderNotificationMessage) {
        final String refundReference = orderNotificationMessage.getPaymentReply().getRefundReference();
        if (refundReference != null) {
            final List<ReturnRequestModel> returnRequests = orderModel.getReturnRequests();
            if (CollectionUtils.isNotEmpty(returnRequests)) {
                findMatchingReturnRequest(refundReference, returnRequests).ifPresent(matchingReturnRequest -> {
                    matchingReturnRequest.setStatus(PAYMENT_REVERSED);
                    modelService.save(matchingReturnRequest);
                    final Collection<ReturnProcessModel> returnProcesses = matchingReturnRequest.getReturnProcess();
                    // Assuming one return process per returnRequest
                    final ReturnProcessModel returnProcessModel = returnProcesses.iterator().next();
                    triggerOrderProcessEvent(returnProcessModel);
                });
            }
        }
        updateCancelNotification(orderModel.getPaymentTransactions().get(0));
        //no return request meaning notification was triggered by worldpay so we need to create transactionEntry in the refundStrategy
        LOG.warn("Refund reference is null. The refund will not be processed against any of the return requests of the order");
        return true;
    }

    /**
     * Sets the pending payment transaction to rejected due to refund notification
     *
     * @param paymentTransaction the payment transaction
     */
    private void updateCancelNotification(final PaymentTransactionModel paymentTransaction) {
        worldpayPaymentTransactionService.getPendingPaymentTransactionEntriesForType(paymentTransaction, CANCEL)
            .forEach(paymentTransactionEntryModel -> {
                paymentTransactionEntryModel.setTransactionStatus(TransactionStatus.REJECTED.name());
                paymentTransactionEntryModel.setTransactionStatusDetails(TransactionStatusDetails.PROCESSOR_DECLINE.name());
                paymentTransactionEntryModel.setPending(Boolean.FALSE);
                paymentTransactionEntryModel.setAmount(BigDecimal.ZERO);
                modelService.save(paymentTransactionEntryModel);
            });
    }

    protected Optional<ReturnRequestModel> findMatchingReturnRequest(final String refundReference, final List<ReturnRequestModel> returnRequests) {
        return returnRequests.stream().filter(returnRequest -> !CANCELED.equals(returnRequest.getStatus())).filter(returnRequest -> {
            final PaymentTransactionEntryModel paymentTransactionEntry = returnRequest.getPaymentTransactionEntry();
            return paymentTransactionEntry.getCode().equals(refundReference);
        }).findAny();
    }

    private void triggerOrderProcessEvent(final BusinessProcessModel businessProcessModel) {
        final String eventName = businessProcessModel.getCode() + "_" + REFUND_FOLLOW_ON;
        businessProcessService.triggerEvent(eventName);
    }
}

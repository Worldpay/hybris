package com.worldpay.notification.processors.impl;

import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.transaction.support.TransactionOperations;

import java.util.List;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.REFUND_FOLLOW_ON;

/**
 * The implementation of {@link OrderNotificationProcessorStrategy} that processes Refund notifications.
 */
public class DefaultRefundedOrderNotificationProcessorStrategy implements OrderNotificationProcessorStrategy {

    protected final TransactionOperations transactionTemplate;
    protected final ModelService modelService;
    protected final WorldpayPaymentTransactionService worldpayPaymentTransactionService;

    public DefaultRefundedOrderNotificationProcessorStrategy(final TransactionOperations transactionTemplate,
                                                             final ModelService modelService,
                                                             final WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.transactionTemplate = transactionTemplate;
        this.modelService = modelService;
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }

    /**
     * {@inheritDoc}
     *
     * @see OrderNotificationProcessorStrategy#processNotificationMessage(PaymentTransactionModel, OrderNotificationMessage)
     */
    @Override
    public void processNotificationMessage(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage) {
        final List<PaymentTransactionEntryModel> paymentTransactionEntries = worldpayPaymentTransactionService.getPendingPaymentTransactionEntriesForType(paymentTransactionModel, REFUND_FOLLOW_ON);
        if (paymentTransactionEntries.isEmpty()) {
            transactionTemplate.execute(transactionStatus -> {
                worldpayPaymentTransactionService.createRefundedPaymentTransactionEntry(paymentTransactionModel, orderNotificationMessage);
                return null;
            });
        } else {
            transactionTemplate.execute(transactionStatus -> {
                updatePaymentTransactionEntry(paymentTransactionModel, paymentTransactionEntries, ACCEPTED.name());
                worldpayPaymentTransactionService.updateEntriesAmount(paymentTransactionEntries, orderNotificationMessage.getPaymentReply().getAmount());
                return null;
            });
        }
    }

    protected void updatePaymentTransactionEntry(final PaymentTransactionModel transactionModel, final List<PaymentTransactionEntryModel> paymentTransactionEntries, final String transactionStatus) {
        worldpayPaymentTransactionService.updateEntriesStatus(paymentTransactionEntries, transactionStatus);
        modelService.save(transactionModel);
    }
}

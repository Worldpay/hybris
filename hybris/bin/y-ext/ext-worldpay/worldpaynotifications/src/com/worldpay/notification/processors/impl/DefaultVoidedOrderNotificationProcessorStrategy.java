package com.worldpay.notification.processors.impl;

import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.transaction.support.TransactionOperations;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;

/**
 * The implementation of {@link OrderNotificationProcessorStrategy} that processes Refund notifications.
 */
public class DefaultVoidedOrderNotificationProcessorStrategy implements OrderNotificationProcessorStrategy {

    protected final TransactionOperations transactionTemplate;
    protected final ModelService modelService;
    protected final WorldpayPaymentTransactionService worldpayPaymentTransactionService;

    public DefaultVoidedOrderNotificationProcessorStrategy(final TransactionOperations transactionTemplate,
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
        transactionTemplate.execute(transactionStatus -> {
            worldpayPaymentTransactionService.updateEntriesStatus(paymentTransactionModel.getEntries(), ACCEPTED.name());
            worldpayPaymentTransactionService.updateEntriesAmount(paymentTransactionModel.getEntries(), orderNotificationMessage.getPaymentReply().getAmount());
            modelService.save(paymentTransactionModel);
            return null;
        });
    }
}

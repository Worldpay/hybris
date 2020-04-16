package com.worldpay.notification.processors.impl;

import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.transaction.support.TransactionOperations;

import java.util.List;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;

/**
 * The implementation of {@link OrderNotificationProcessorStrategy} that processes Captured notifications.
 */
public class DefaultCapturedOrderNotificationProcessorStrategy implements OrderNotificationProcessorStrategy {

    private final TransactionOperations transactionTemplate;
    private final ModelService modelService;
    private final WorldpayPaymentTransactionService worldpayPaymentTransactionService;

    public DefaultCapturedOrderNotificationProcessorStrategy(final TransactionOperations transactionTemplate, final ModelService modelService, final WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
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
        final PaymentInfoModel paymentInfoModel = paymentTransactionModel.getInfo();
        final String paymentTypeCode = orderNotificationMessage.getPaymentReply().getMethodCode();
        if (shouldCreateCapturePaymentTransactionEntry(paymentInfoModel, paymentTypeCode)) {
            transactionTemplate.execute(transactionStatus -> {
                worldpayPaymentTransactionService.createCapturedPaymentTransactionEntry(paymentTransactionModel, orderNotificationMessage);
                return null;
            });
        } else {
            final List<PaymentTransactionEntryModel> paymentTransactionEntries = worldpayPaymentTransactionService.getPendingPaymentTransactionEntriesForType(paymentTransactionModel, CAPTURE);
            transactionTemplate.execute(transactionStatus -> {
                updatePaymentTransactionEntry(paymentTransactionModel, paymentTransactionEntries, ACCEPTED.name());
                worldpayPaymentTransactionService.updateEntriesAmount(paymentTransactionEntries, orderNotificationMessage.getPaymentReply().getAmount());
                return null;
            });
        }
    }

    private boolean shouldCreateCapturePaymentTransactionEntry(final PaymentInfoModel paymentInfoModel, String paymentTypeCode) {
        return paymentInfoModel != null && paymentInfoModel.getIsApm() && !paymentTypeCode.equals(PaymentType.KLARNASSL.getMethodCode());
    }

    protected void updatePaymentTransactionEntry(final PaymentTransactionModel transactionModel, final List<PaymentTransactionEntryModel> paymentTransactionEntries, final String transactionStatus) {
        worldpayPaymentTransactionService.updateEntriesStatus(paymentTransactionEntries, transactionStatus);
        modelService.save(transactionModel);
    }

}

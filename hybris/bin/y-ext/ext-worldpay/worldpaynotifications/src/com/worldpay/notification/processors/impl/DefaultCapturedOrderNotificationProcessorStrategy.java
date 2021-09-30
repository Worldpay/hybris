package com.worldpay.notification.processors.impl;

import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.payment.WorldpayKlarnaService;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.transaction.support.TransactionOperations;

import java.util.List;
import java.util.Objects;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;

/**
 * The implementation of {@link OrderNotificationProcessorStrategy} that processes Captured notifications.
 */
public class DefaultCapturedOrderNotificationProcessorStrategy implements OrderNotificationProcessorStrategy {

    protected final TransactionOperations transactionTemplate;
    protected final ModelService modelService;
    protected final WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    protected final WorldpayKlarnaService worldpayKlarnaService;

    public DefaultCapturedOrderNotificationProcessorStrategy(final TransactionOperations transactionTemplate,
                                                             final ModelService modelService,
                                                             final WorldpayPaymentTransactionService worldpayPaymentTransactionService,
                                                             final WorldpayKlarnaService worldpayKlarnaService) {
        this.transactionTemplate = transactionTemplate;
        this.modelService = modelService;
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
        this.worldpayKlarnaService = worldpayKlarnaService;
    }

    /**
     * {@inheritDoc}
     *
     * @see OrderNotificationProcessorStrategy#processNotificationMessage(PaymentTransactionModel, OrderNotificationMessage)
     */
    @Override
    public void processNotificationMessage(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage) {
        final PaymentInfoModel paymentInfoModel = paymentTransactionModel.getInfo();
        final String paymentTypeCode = orderNotificationMessage.getPaymentReply().getPaymentMethodCode();
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

    private boolean shouldCreateCapturePaymentTransactionEntry(final PaymentInfoModel paymentInfoModel, final String paymentTypeCode) {
        return Objects.nonNull(paymentInfoModel) && paymentInfoModel.getIsApm() && !(worldpayKlarnaService.isKlarnaPaymentType(paymentTypeCode));
    }

    protected void updatePaymentTransactionEntry(final PaymentTransactionModel transactionModel, final List<PaymentTransactionEntryModel> paymentTransactionEntries, final String transactionStatus) {
        worldpayPaymentTransactionService.updateEntriesStatus(paymentTransactionEntries, transactionStatus);
        modelService.save(transactionModel);
    }

}

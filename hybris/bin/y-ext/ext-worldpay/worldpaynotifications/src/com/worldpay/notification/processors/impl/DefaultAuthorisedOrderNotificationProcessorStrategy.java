package com.worldpay.notification.processors.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.springframework.transaction.support.TransactionOperations;

import java.math.BigDecimal;
import java.util.List;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static java.text.MessageFormat.format;

/**
 * The implementation of {@link OrderNotificationProcessorStrategy} that processes Authorised notifications.
 */
public class DefaultAuthorisedOrderNotificationProcessorStrategy implements OrderNotificationProcessorStrategy {

    private static final Logger LOG = Logger.getLogger(DefaultAuthorisedOrderNotificationProcessorStrategy.class);

    private final ModelService modelService;
    private final TransactionOperations transactionTemplate;
    private final WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    private final WorldpayPaymentInfoService worldpayPaymentInfoService;
    private final WorldpayOrderService worldpayOrderService;

    public DefaultAuthorisedOrderNotificationProcessorStrategy(final ModelService modelService,
                                                               final TransactionOperations transactionTemplate,
                                                               final WorldpayPaymentTransactionService worldpayPaymentTransactionService,
                                                               final WorldpayPaymentInfoService worldpayPaymentInfoService,
                                                               final WorldpayOrderService worldpayOrderService) {
        this.modelService = modelService;
        this.transactionTemplate = transactionTemplate;
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
        this.worldpayPaymentInfoService = worldpayPaymentInfoService;
        this.worldpayOrderService = worldpayOrderService;
    }

    /**
     * {@inheritDoc}
     *
     * @see OrderNotificationProcessorStrategy#processNotificationMessage(PaymentTransactionModel, OrderNotificationMessage)
     */
    @Override
    public void processNotificationMessage(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage) {
        LOG.debug(format("Message for order having code {0} is a success, saving card and changing transaction status to not pending.", orderNotificationMessage.getOrderCode()));

        final BigDecimal plannedAmount = worldpayOrderService.convertAmount(orderNotificationMessage.getPaymentReply().getAmount());
        paymentTransactionModel.setPlannedAmount(plannedAmount);
        final AbstractOrderModel orderModel = paymentTransactionModel.getOrder();

        final List<PaymentTransactionEntryModel> paymentTransactionEntries = worldpayPaymentTransactionService.getPendingPaymentTransactionEntriesForType(paymentTransactionModel, AUTHORIZATION);
        transactionTemplate.execute(transactionStatus -> {
            updatePaymentTransactionEntry(paymentTransactionModel, orderNotificationMessage, paymentTransactionEntries, ACCEPTED.name());
            try {
                worldpayPaymentInfoService.setPaymentInfoModel(paymentTransactionModel, orderModel, orderNotificationMessage);
            } catch (final WorldpayConfigurationException e) {
                LOG.info("Could not set the paymentInfo to the order.", e);
                return null;
            }
            return null;
        });
    }

    protected void updatePaymentTransactionEntry(PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage,
                                                 final List<PaymentTransactionEntryModel> paymentTransactionEntries, final String transactionStatus) {
        final PaymentReply paymentReply = orderNotificationMessage.getPaymentReply();
        worldpayPaymentTransactionService.addRiskScore(paymentTransactionModel, paymentReply);
        paymentTransactionModel.setApmOpen(Boolean.FALSE);

        paymentTransactionEntries.forEach(paymentTransactionEntryModel -> worldpayPaymentTransactionService.addAavFields(paymentTransactionEntryModel, paymentReply));
        worldpayPaymentTransactionService.updateEntriesStatus(paymentTransactionEntries, transactionStatus);
        worldpayPaymentTransactionService.updateEntriesAmount(paymentTransactionEntries, orderNotificationMessage.getPaymentReply().getAmount());
        modelService.save(paymentTransactionModel);
    }
}

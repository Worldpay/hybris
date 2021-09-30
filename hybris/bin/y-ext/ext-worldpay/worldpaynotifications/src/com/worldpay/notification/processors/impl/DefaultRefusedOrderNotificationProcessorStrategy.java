package com.worldpay.notification.processors.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionOperations;

import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_PENDING;
import static de.hybris.platform.payment.dto.TransactionStatus.REJECTED;

/**
 * The implementation of {@link OrderNotificationProcessorStrategy} that processes Refused notifications.
 */
public class DefaultRefusedOrderNotificationProcessorStrategy implements OrderNotificationProcessorStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRefusedOrderNotificationProcessorStrategy.class);

    protected final TransactionOperations transactionTemplate;
    protected final WorldpayPaymentInfoService worldpayPaymentInfoService;
    protected final WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    protected final ModelService modelService;

    public DefaultRefusedOrderNotificationProcessorStrategy(final TransactionOperations transactionTemplate,
                                                            final WorldpayPaymentInfoService worldpayPaymentInfoService,
                                                            final WorldpayPaymentTransactionService worldpayPaymentTransactionService,
                                                            final ModelService modelService) {
        this.transactionTemplate = transactionTemplate;
        this.worldpayPaymentInfoService = worldpayPaymentInfoService;
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
        this.modelService = modelService;
    }

    /**
     * {@inheritDoc}
     *
     * @see OrderNotificationProcessorStrategy#processNotificationMessage(PaymentTransactionModel, OrderNotificationMessage)
     */
    @Override
    public void processNotificationMessage(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage) throws WorldpayConfigurationException {
        final AbstractOrderModel orderModel = paymentTransactionModel.getOrder();
        if (isOrderRefusable(orderModel)) {
            worldpayPaymentInfoService.setPaymentInfoModel(paymentTransactionModel, orderModel, orderNotificationMessage);
            transactionTemplate.execute(transactionStatus -> {
                worldpayPaymentTransactionService.updateEntriesStatus(paymentTransactionModel.getEntries(), REJECTED.name());
                worldpayPaymentTransactionService.updateEntriesAmount(paymentTransactionModel.getEntries(), orderNotificationMessage.getPaymentReply().getAmount());
                modelService.save(paymentTransactionModel);
                return null;
            });
        } else {
            LOG.warn("The order [{}] is a cart or not in PAYMENT_PENDING status.", orderModel.getCode());
        }
    }

    protected boolean isOrderRefusable(final AbstractOrderModel orderModel) {
        return orderModel instanceof OrderModel && PAYMENT_PENDING.equals(orderModel.getStatus());
    }
}

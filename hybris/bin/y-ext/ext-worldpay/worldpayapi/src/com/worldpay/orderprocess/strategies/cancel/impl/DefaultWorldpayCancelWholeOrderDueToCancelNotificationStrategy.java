package com.worldpay.orderprocess.strategies.cancel.impl;

import com.worldpay.orderprocess.strategies.cancel.WorldpayCancelWholeOrderDueToCancelNotificationStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayCancelWholeOrderDueToCancelNotificationStrategy implements WorldpayCancelWholeOrderDueToCancelNotificationStrategy {

    private static final Logger LOG = LogManager.getLogger(DefaultWorldpayCancelWholeOrderDueToCancelNotificationStrategy.class);
    protected final WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    protected final ModelService modelService;

    public DefaultWorldpayCancelWholeOrderDueToCancelNotificationStrategy(final WorldpayPaymentTransactionService worldpayPaymentTransactionService,
                                                                          final ModelService modelService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
        this.modelService = modelService;
    }

    /**
     * {@inheritDoc}
     * In this particular implementation the method sets the order status to cancel and creates a cancel paymentTransactionEntry
     */
    @Override
    public void cancelOrder(final OrderProcessModel orderProcessModel) {
        final OrderModel orderModel = orderProcessModel.getOrder();

        LOG.debug("The order {} is being cancelled.", orderModel::getCode);

        final PaymentTransactionModel paymentTransaction = worldpayPaymentTransactionService.getPaymentTransactionFromCode(orderModel.getWorldpayOrderCode());
        if (paymentTransaction != null) {
            createAndSaveCancelTransactionEntry(paymentTransaction);
                            orderModel.setStatus(OrderStatus.CANCELLED);
        } else {
            orderModel.setStatus(OrderStatus.PROCESSING_ERROR);
        }
        modelService.save(orderModel);
    }

    protected void createAndSaveCancelTransactionEntry(final PaymentTransactionModel paymentTransactionModel) {
        final PaymentTransactionEntryModel cancelTransactionEntry = worldpayPaymentTransactionService
                .createNotPendingCancelOrderTransactionEntry(paymentTransactionModel);

        cancelTransactionEntry.setPaymentTransaction(paymentTransactionModel);
        modelService.save(cancelTransactionEntry);
    }
}

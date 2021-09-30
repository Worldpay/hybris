package com.worldpay.ordercancel.impl.denialstrategies;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.ordercancel.OrderCancelDenialReason;
import de.hybris.platform.ordercancel.OrderCancelDenialStrategy;
import de.hybris.platform.ordercancel.impl.denialstrategies.AbstractCancelDenialStrategy;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;

/**
 * Implementation of a OrderCancelDenialStrategy taking into consideration the conditions for Worldpay
 */
public class WorldpayOrderCancelDenialStrategy extends AbstractCancelDenialStrategy implements OrderCancelDenialStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(WorldpayOrderCancelDenialStrategy.class);

    /**
     * Implementation of getCancelDenialReason that checks if the order has a confirmed captured paymentTransactionEntry.
     *
     * @param orderCancelConfigModel
     * @param orderModel
     * @param principalModel
     * @param partialCancel
     * @param partialEntryCancel
     * @return Order cancel denial reason if exist.
     */
    @Override
    public OrderCancelDenialReason getCancelDenialReason(final OrderCancelConfigModel orderCancelConfigModel, final OrderModel orderModel,
                                                         final PrincipalModel principalModel, boolean partialCancel, boolean partialEntryCancel) {
        final List<PaymentTransactionModel> paymentTransactions = orderModel.getPaymentTransactions();

        if (paymentTransactions != null) {
            return findReason(orderModel, paymentTransactions);
        }
        return null;
    }

    private OrderCancelDenialReason findReason(final OrderModel orderModel, final List<PaymentTransactionModel> paymentTransactions) {
        for (final PaymentTransactionModel paymentTransaction : paymentTransactions) {
            final List<PaymentTransactionEntryModel> entries = paymentTransaction.getEntries();
            for (final PaymentTransactionEntryModel entry : entries) {
                if (shouldReturnDenialReason(entry)) {
                    LOG.warn("The order {} cannot be cancelled because the transaction entry with requestId {} is captured", orderModel.getCode(), entry.getRequestId());
                    return getReason();
                }
            }
        }
        return null;
    }

    protected boolean shouldReturnDenialReason(final PaymentTransactionEntryModel entry) {
        return !entry.getPending() && entry.getType().equals(CAPTURE);
    }
}

package com.worldpay.worldpayoms.fulfilmentprocess.actions.order;

import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.SUCCESFULL;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static org.fest.util.Collections.isEmpty;

/**
 * Action that cancels the payment in Worldpay
 */
public class WorldpayCancelPaymentAction extends AbstractSimpleDecisionAction<OrderProcessModel> {

    private PaymentService paymentService;
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;

    @Override
    public Transition executeAction(final OrderProcessModel orderProcessModel) {
        final OrderModel order = orderProcessModel.getOrder();
        final List<PaymentTransactionModel> paymentTransactions = order.getPaymentTransactions();
        if (firstElementExists(paymentTransactions)) {
            final List<PaymentTransactionEntryModel> paymentTransactionEntryModels = worldpayPaymentTransactionService.filterPaymentTransactionEntriesOfType(paymentTransactions.get(0), AUTHORIZATION);
            if (firstElementExists(paymentTransactionEntryModels)) {
                final PaymentTransactionEntryModel cancelledPaymentTransactionEntry = paymentService.cancel(paymentTransactionEntryModels.get(0));
                if (isSuccessful(cancelledPaymentTransactionEntry)) {
                    return Transition.OK;
                }
            }
        }
        return Transition.NOK;
    }

    protected boolean firstElementExists(List elements) {
        return !isEmpty(elements) && elements.get(0) != null;
    }

    protected boolean isSuccessful(PaymentTransactionEntryModel cancelledPaymentTransaction) {
        return cancelledPaymentTransaction.getTransactionStatus().equalsIgnoreCase(ACCEPTED.name()) && cancelledPaymentTransaction.getTransactionStatusDetails()
                .equalsIgnoreCase(SUCCESFULL.name());
    }

    @Required
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Required
    public void setWorldpayPaymentTransactionService(WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }


}

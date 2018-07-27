package com.worldpay.fulfilmentprocess.actions.order;

import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractAction;
import org.springframework.beans.factory.annotation.Required;

import java.util.Set;

import static de.hybris.platform.core.enums.OrderStatus.*;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;

/**
 * Action to check authorization state on order payment
 */
public class WorldpayCheckAuthorizeOrderPaymentAction extends WorldpayAbstractOrderAction<OrderProcessModel> {

    private static final String OK = "OK";
    private static final String NOK = "NOK";
    private static final String WAIT = "WAIT";

    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;

    @Override
    public Set<String> getTransitions() {
        return AbstractAction.createTransitions(OK, NOK, WAIT);
    }

    @Override
    public String execute(final OrderProcessModel process) {
        final OrderModel order = process.getOrder();

        String returnTransition = null;

        if (order != null) {
            if (order.getPaymentInfo() instanceof InvoicePaymentInfoModel) {
                returnTransition = OK;
            } else {

                if (!worldpayPaymentTransactionService.areAllPaymentTransactionsAcceptedForType(order, AUTHORIZATION)) {
                    setOrderStatus(order, PAYMENT_NOT_AUTHORIZED);
                    return NOK;
                }

                if (!worldpayPaymentTransactionService.isAuthorisedAmountCorrect(order)) {
                    setOrderStatus(order, CHECKED_INVALID);
                    createOrderHistoryEntry("Order total did not match authorised amount", order);
                    return NOK;
                }

                if (isAuthorizationPending(order)) {
                    setOrderStatus(order, PAYMENT_PENDING);
                    returnTransition = WAIT;
                } else {
                    setOrderStatus(order, PAYMENT_AUTHORIZED);
                    returnTransition = OK;
                }
            }
        }
        return returnTransition;
    }

    private boolean isAuthorizationPending(final OrderModel order) {
        boolean waitForAuthorisation = false;
        for (final PaymentTransactionModel paymentTransaction : order.getPaymentTransactions()) {
            if (worldpayPaymentTransactionService.isPaymentTransactionPending(paymentTransaction, AUTHORIZATION)) {
                waitForAuthorisation = true;
                break;
            }
        }
        return waitForAuthorisation;
    }

    protected void createOrderHistoryEntry(final String description, final OrderModel order) {
        final OrderHistoryEntryModel entry = createHistoryLog(description, order);
        modelService.save(entry);
    }

    @Required
    public void setPaymentTransactionService(final WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }

}

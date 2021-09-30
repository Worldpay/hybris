package com.worldpay.fulfilmentprocess.actions.order;

import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_CAPTURED;
import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_NOT_CAPTURED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;

/**
 * Action that handles payment transactions and update order status
 */
public class WorldpayTakePaymentAction extends AbstractAction<OrderProcessModel> {
    private static final Logger LOG = LoggerFactory.getLogger(WorldpayTakePaymentAction.class);

    private static final String OK = "OK";
    private static final String NOK = "NOK";
    private static final String WAIT = "WAIT";

    private PaymentService paymentService;
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;

    @Override
    public Set<String> getTransitions() {
        return AbstractAction.createTransitions(OK, NOK, WAIT);
    }

    //assumption that there is only ever one capture per transaction
    @Override
    public String execute(final OrderProcessModel process) {
        final OrderModel order = process.getOrder();
        for (final PaymentTransactionModel paymentTransaction : order.getPaymentTransactions()) {
            final List<PaymentTransactionEntryModel> entries = worldpayPaymentTransactionService.filterPaymentTransactionEntriesOfType(paymentTransaction, CAPTURE);
            if (entries.isEmpty()) {
                return handleEmptyTransactionEntries(paymentTransaction);
            } else if (entries.size() != 1) {
                return handleMultipleTransactionEntries(process, order, paymentTransaction, entries);
            }
            if (checkForNotAccepted(order, paymentTransaction)) {
                return NOK;
            }
            if (checkForPendingStatus(order, paymentTransaction)) {
                return WAIT;
            }
        }
        setOrderStatus(order, PAYMENT_CAPTURED);
        return OK;
    }

    protected String handleMultipleTransactionEntries(final OrderProcessModel process, final OrderModel order, final PaymentTransactionModel paymentTransaction, final List<PaymentTransactionEntryModel> entries) {
        LOG.error("Found {} PaymentTransactionEntries with " +
                "type CAPTURE for PaymentTransaction with code {} and orderProcess " +
                "with code {}. There should be exactly 1", entries.size(), paymentTransaction.getCode(), process.getCode());
        setOrderStatus(order, PAYMENT_NOT_CAPTURED);
        return NOK;
    }

    protected String handleEmptyTransactionEntries(final PaymentTransactionModel paymentTransaction) {
        final PaymentInfoModel paymentTransactionInfo = paymentTransaction.getInfo();
        if (shouldIssueCaptureRequest(paymentTransactionInfo)) {
            paymentService.capture(paymentTransaction);
        }
        // Always go back to wait-state, as the notification will not have been received at this point.
        return WAIT;
    }

    protected boolean shouldIssueCaptureRequest(final PaymentInfoModel paymentTransactionInfo) {
        return paymentTransactionInfo == null || isKlarnaPayment(paymentTransactionInfo) || !paymentTransactionInfo.getIsApm();
    }

    protected boolean checkForPendingStatus(final OrderModel order, final PaymentTransactionModel txn) {
        // wait if any capture entries are still pending
        if (worldpayPaymentTransactionService.isPaymentTransactionPending(txn, CAPTURE)) {
            LOG.info("Still waiting for capture on order: {} ", order);
            return true;
        }
        return false;
    }

    protected boolean checkForNotAccepted(final OrderModel order, final PaymentTransactionModel transaction) {
        // NOK if all capture entries have not been accepted
        if (!worldpayPaymentTransactionService.areAllPaymentTransactionsAcceptedForType(order, CAPTURE)) {
            LOG.error("The payment transaction capture has failed. Order: {}. Transaction: {}" , order.getCode(), transaction.getCode());
            setOrderStatus(order, PAYMENT_NOT_CAPTURED);
            return true;
        }
        return false;
    }

    protected boolean isKlarnaPayment(final PaymentInfoModel paymentInfoModel){
        if (paymentInfoModel instanceof WorldpayAPMPaymentInfoModel) {
            final WorldpayAPMPaymentInfoModel worldpayAPMPaymentInfoModel = (WorldpayAPMPaymentInfoModel) paymentInfoModel;
            return worldpayAPMPaymentInfoModel.isKlarnaPaymentType();
        }
        return false;
    }

    public void setPaymentService(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void setWorldpayPaymentTransactionService(final WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }
}


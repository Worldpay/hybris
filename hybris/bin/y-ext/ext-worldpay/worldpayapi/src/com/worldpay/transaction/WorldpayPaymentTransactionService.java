package com.worldpay.transaction;

import com.worldpay.data.Amount;
import com.worldpay.data.PaymentReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.math.BigDecimal;
import java.util.List;

/**
 * Definition of the methods used to handle the PaymentTransactions
 */
public interface WorldpayPaymentTransactionService {

    /**
     * Checks that all existing transactions in the order {@param order} have received or processed
     * the corresponding asynchronous notification of acceptance.
     *
     * @param order                  The current order {@link OrderModel}
     * @param paymentTransactionType The payment paymentTransaction type to check {@link PaymentTransactionType}
     * @return true when all paymentTransactions are accepted for the type {@param paymentTransactionType}
     */
    boolean areAllPaymentTransactionsAcceptedForType(OrderModel order, PaymentTransactionType paymentTransactionType);

    /**
     * Checks if the order contains a paymentTransaction with the flag apmOpen true, which means the asynchronous
     * notification has not been received or processed.
     *
     * @param order he current order {@link OrderModel}
     * @return true when there are transactions with the apmOpen flag, false otherwise.
     */
    boolean isAnyPaymentTransactionApmOpenForOrder(OrderModel order);

    /**
     * Checks if any of the PaymentTransactionEntries {@link PaymentTransactionEntryModel} in the {@param paymentTransaction} has the pending flag.
     *
     * @param paymentTransaction     The current {@link PaymentTransactionModel}
     * @param paymentTransactionType The payment paymentTransaction type to check {@link PaymentTransactionType}
     * @return true if any of the entries is pending. False otherwise.
     */
    boolean isPaymentTransactionPending(PaymentTransactionModel paymentTransaction, PaymentTransactionType paymentTransactionType);

    /**
     * Returns a list with the PaymentTransactionEntries {@link PaymentTransactionEntryModel} of the type {@param paymentTransactionType} {@link PaymentTransactionType}
     *
     * @param paymentTransaction     The current {@link PaymentTransactionModel}
     * @param paymentTransactionType The payment paymentTransaction type to check {@link PaymentTransactionType}
     * @return an empty list if no PaymentTransactionEntries {@link PaymentTransactionEntryModel} of the
     * type {@param paymentTransactionType} are found, a list of entries of the type {@param paymentTransactionType} otherwise.
     */
    List<PaymentTransactionEntryModel> filterPaymentTransactionEntriesOfType(PaymentTransactionModel paymentTransaction, PaymentTransactionType paymentTransactionType);

    /**
     * Returns a list with the PaymentTransactionEntries {@link PaymentTransactionEntryModel} of the type {@param paymentTransactionType} {@link PaymentTransactionType} in pending status
     *
     * @param paymentTransactionModel The {@link PaymentTransactionModel} to look for pending PaymentTransactionEntries
     * @param paymentTransactionType  The type {@link PaymentTransactionType} of the transaction to filter
     * @return an empty list if no PaymentTransactionEntries {@link PaymentTransactionEntryModel} in pending status of the
     * type {@param paymentTransactionType} are found, a list of entries of the type {@param paymentTransactionType} in pending status otherwise.
     */
    List<PaymentTransactionEntryModel> getPendingPaymentTransactionEntriesForType(PaymentTransactionModel paymentTransactionModel, PaymentTransactionType paymentTransactionType);


    /**
     * Returns a list with the PaymentTransactionEntries {@link PaymentTransactionEntryModel} of the type {@param paymentTransactionType} {@link PaymentTransactionType}
     *
     * @param paymentTransactionModel The {@link PaymentTransactionModel} to look for pending PaymentTransactionEntries
     * @param paymentTransactionType  The type {@link PaymentTransactionType} of the transaction to filter
     * @return an empty list if no PaymentTransactionEntries {@link PaymentTransactionEntryModel} of the
     * type {@param paymentTransactionType} are found, a list of entries of the type {@param paymentTransactionType} otherwise.
     */
    List<PaymentTransactionEntryModel> getNotPendingPaymentTransactionEntriesForType(PaymentTransactionModel paymentTransactionModel, PaymentTransactionType paymentTransactionType);

    /**
     * Checks if there are incomplete dependencies between the different transactions types, for example, Capture depends on Authorization.
     *
     * @param worldpayOrderCode      The orderCode of the paymentTransaction to check
     * @param paymentTransactionType The {@link PaymentTransactionType} to find the depending transactions
     * @param orderModel             The {@link OrderModel} to process the transactions from
     * @return true if the dependant paymentTransaction type is completed, false otherwise.
     * If the current paymentTransaction of the type {@param paymentTransactionType} has no dependencies, returns true
     */
    boolean isPreviousTransactionCompleted(String worldpayOrderCode, PaymentTransactionType paymentTransactionType, OrderModel orderModel);

    /**
     * Creates a new PaymentTransactionEntry of the type {@link PaymentTransactionType#CAPTURE}
     * associated to {@param paymentTransaction} {@link PaymentTransactionModel#getEntries()}
     *
     * @param paymentTransaction       The {@link PaymentTransactionModel} to add the new Captured {@link PaymentTransactionType#CAPTURE} paymentTransactionEntry
     * @param orderNotificationMessage The {@link OrderNotificationMessage} to get the information from
     * @return the new {@link PaymentTransactionType#CAPTURE} PaymentTransactionEntry created
     */
    PaymentTransactionEntryModel createCapturedPaymentTransactionEntry(PaymentTransactionModel paymentTransaction, OrderNotificationMessage orderNotificationMessage);

    /**
     * Creates a new PaymentTransactionEntry of the type {@link PaymentTransactionType#REFUND_FOLLOW_ON}
     * associated to {@param paymentTransaction} {@link PaymentTransactionModel#getEntries()}
     *
     * @param paymentTransaction       The {@link PaymentTransactionModel} to add the new Refund {@link PaymentTransactionType#REFUND_FOLLOW_ON} paymentTransactionEntry
     * @param orderNotificationMessage The {@link OrderNotificationMessage} to get the information from
     * @return the new {@link PaymentTransactionType#REFUND_FOLLOW_ON} PaymentTransactionEntry created
     */
    PaymentTransactionEntryModel createRefundedPaymentTransactionEntry(PaymentTransactionModel paymentTransaction, OrderNotificationMessage orderNotificationMessage);

    /**
     * Creates a new PaymentTransactionEntry {@link PaymentTransactionEntryModel} of the type {@link PaymentTransactionType#AUTHORIZATION}
     * associated to {@param paymentTransaction} with {@link PaymentTransactionEntryModel#getPending()} = true
     *
     * @param paymentTransaction The {@link PaymentTransactionModel} to associate the paymentTransactionEntry to
     * @param merchantCode       The merchantCode used in the transaction with Worldpay
     * @param cartModel          The {@link CartModel} to get the amount and currency information from
     * @param authorisedAmount   The authorisedAmount
     * @return the new {@link PaymentTransactionType#AUTHORIZATION} PaymentTransactionEntry created
     */
    PaymentTransactionEntryModel createPendingAuthorisePaymentTransactionEntry(PaymentTransactionModel paymentTransaction, String merchantCode,
                                                                               CartModel cartModel, BigDecimal authorisedAmount);

    /**
     * Creates a new PaymentTransactionEntry {@link PaymentTransactionEntryModel} of the type {@link PaymentTransactionType#AUTHORIZATION}
     * associated to {@param paymentTransaction} with {@link PaymentTransactionEntryModel#getPending()} = false
     *
     * @param paymentTransaction The {@link PaymentTransactionModel} to associate the paymentTransactionEntry to
     * @param merchantCode       The merchantCode used in the transaction with Worldpay
     * @param abstractOrderModel The cart or order to get the amount and currency information from
     * @param authorisedAmount   The authorisedAmount
     * @return the new {@link PaymentTransactionType#AUTHORIZATION} PaymentTransactionEntry created
     */
    PaymentTransactionEntryModel createNonPendingAuthorisePaymentTransactionEntry(PaymentTransactionModel paymentTransaction, String merchantCode,
                                                                                  AbstractOrderModel abstractOrderModel, BigDecimal authorisedAmount);

    /**
     * Creates a new PaymentTransactionEntry {@link PaymentTransactionEntryModel} of the type {@link PaymentTransactionType#AUTHORIZATION}
     * associated to {@param paymentTransaction} with {@link PaymentTransactionEntryModel#getPending()} = false
     *
     * @param paymentTransactionModel  The {@link PaymentTransactionModel} to associate the paymentTransactionEntry to
     * @param orderNotificationMessage The {@link OrderNotificationMessage} to get the information from
     * @return the new {@link PaymentTransactionType#SETTLED} PaymentTransactionEntry created
     */
    PaymentTransactionEntryModel createNotPendingSettledPaymentTransactionEntry(PaymentTransactionModel paymentTransactionModel, OrderNotificationMessage orderNotificationMessage);

    /**
     * Creates a new paymentTransaction in the cart once the authorization result has been received, after the HOP.
     *
     * @param apmOpen                   boolean that marks if the order was placed using an APM and the response was 'open'
     * @param merchantCode              The merchantCode used in the authorization
     * @param commerceCheckoutParameter
     * @return the created {@link PaymentTransactionModel}
     */
    PaymentTransactionModel createPaymentTransaction(boolean apmOpen, String merchantCode, CommerceCheckoutParameter commerceCheckoutParameter);

    /**
     * Returns the PaymentTransactionModel {@link PaymentTransactionModel} corresponding to the {@param worldpayOrderCode}
     *
     * @param worldpayOrderCode The worldpayOrderCode to look for associated to a PaymentTransactionModel
     * @return The PaymentTransactionModel associated to the worldpayOrderCode if found, null otherwise
     */
    PaymentTransactionModel getPaymentTransactionFromCode(String worldpayOrderCode);

    /**
     * Updates each one of the {@param paymentTransactionEntries} to the status {@param transactionStatus}
     *
     * @param paymentTransactionEntries
     * @param transactionStatus
     */
    void updateEntriesStatus(List<PaymentTransactionEntryModel> paymentTransactionEntries, String transactionStatus);

    /**
     * Set the riskscore on a paymentTransactionEntryModel
     *
     * @param paymentTransactionModel
     * @param paymentReply
     */
    void addRiskScore(PaymentTransactionModel paymentTransactionModel, PaymentReply paymentReply);

    /**
     * Set the aav fields on a paymentTransactionEntryModel
     *
     * @param paymentTransactionEntryModel
     * @param paymentReply
     */
    void addAavFields(PaymentTransactionEntryModel paymentTransactionEntryModel, PaymentReply paymentReply);

    /**
     * Updates the amount received from the OrderNotificationMessage
     *
     * @param transactionEntries transactionEntries to change the amount to.
     * @param amount             amount to set in the transactionEntries
     */
    void updateEntriesAmount(List<PaymentTransactionEntryModel> transactionEntries, Amount amount);

    /**
     * Matching order total against sum of amounts from PaymentTransactionEntries with
     * type {@link PaymentTransactionType#AUTHORIZATION}.
     *
     * @param order The current order {@link OrderModel}
     * @return true if sum of amounts are equal to order total. Otherwise false.
     */
    boolean isAuthorisedAmountCorrect(OrderModel order);

    /**
     * Creates a new PaymentTransactionEntry {@link PaymentTransactionEntryModel} of the type {@link PaymentTransactionType#CANCEL}
     * associated to {@param paymentTransaction} with {@link PaymentTransactionEntryModel#getPending()} = false
     *
     * @param paymentTransactionModel The {@link PaymentTransactionModel} to associate the paymentTransactionEntry to
     * @return the new {@link PaymentTransactionType#SETTLED} PaymentTransactionEntry created
     */
    PaymentTransactionEntryModel createNotPendingCancelOrderTransactionEntry(PaymentTransactionModel paymentTransactionModel);

    void addFraudSightToPaymentTransaction(PaymentTransactionModel paymentTransaction, PaymentReply paymentReply);
}

package com.worldpay.worldpaytests.orders;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.data.JournalReply;
import com.worldpay.data.PaymentReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;

public class WorldpayPaymentTransactionTestData {

    protected static final String MERCHANT_CODE = "merchantCode";

    protected final ModelService modelService;
    protected final OrderNotificationService orderNotificationService;

    public WorldpayPaymentTransactionTestData(final ModelService modelService,
                                              final OrderNotificationService orderNotificationService) {
        this.modelService = modelService;
        this.orderNotificationService = orderNotificationService;
    }

    public void setRequestIdsAndCreateOrderModifications(final CustomerModel user) {
        user.getOrders().forEach(orderModel -> orderModel.getPaymentTransactions().forEach(paymentTransactionModel -> {
            final String requestId = "pt_" + orderModel.getCode();
            paymentTransactionModel.setRequestId(requestId);
            paymentTransactionModel.getEntries().forEach(paymentTransactionEntryModel -> {
                paymentTransactionEntryModel.setRequestId(requestId);
                createOrderModification(requestId);
                modelService.save(paymentTransactionEntryModel);
            });
            modelService.save(paymentTransactionModel);
        }));
    }

    protected void createOrderModification(final String requestId) {
        final WorldpayOrderModificationModel orderModification = modelService.create(WorldpayOrderModificationModel.class);
        orderModification.setWorldpayOrderCode(requestId);
        orderModification.setType(AUTHORIZATION);

        final OrderNotificationMessage orderNotificationMessage = new OrderNotificationMessage();
        orderNotificationMessage.setOrderCode(requestId);
        orderNotificationMessage.setMerchantCode(MERCHANT_CODE);
        final JournalReply journalReply = new JournalReply();
        journalReply.setJournalType(AUTHORISED);

        orderNotificationMessage.setJournalReply(journalReply);
        final PaymentReply paymentReply = new PaymentReply();
        orderNotificationMessage.setPaymentReply(paymentReply);

        orderModification.setOrderNotificationMessage(orderNotificationService.serialiseNotification(orderNotificationMessage));
        modelService.save(orderModification);
    }

}

package com.worldpay.worldpaynotifications.listener;

import com.worldpay.core.event.OrderModificationEvent;
import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.worldpay.enums.order.AuthorisedStatus.REFUSED;

/**
 * Listener for order modifications events - the event is publish when received from Worldpay
 *
 * @see OrderModificationController
 * <p>
 * The listener save the order notification message for later processing
 */
public class WorldpayOrderModificationListener extends AbstractEventListener<OrderModificationEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(WorldpayOrderModificationListener.class);

    protected final ModelService modelService;
    protected final WorldpayCartService worldpayCartService;
    protected final Map<AuthorisedStatus, PaymentTransactionType> paymentTransactionTypeMap;
    protected final OrderNotificationService orderNotificationService;

    public WorldpayOrderModificationListener(final ModelService modelService,
                                             final WorldpayCartService worldpayCartService,
                                             final Map<AuthorisedStatus, PaymentTransactionType> paymentTransactionTypeMap,
                                             final OrderNotificationService orderNotificationService) {
        this.modelService = modelService;
        this.worldpayCartService = worldpayCartService;
        this.paymentTransactionTypeMap = paymentTransactionTypeMap;
        this.orderNotificationService = orderNotificationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onEvent(final OrderModificationEvent orderModificationEvent) {
        final OrderNotificationMessage orderNotificationMessage = orderModificationEvent.getOrderNotificationMessage();
        final AuthorisedStatus journalType = orderNotificationMessage.getJournalReply().getJournalType();
        if (journalType.equals(REFUSED)) {
            setDeclineCodeInCart(orderNotificationMessage);
        }
        final PaymentTransactionType transactionType = paymentTransactionTypeMap.get(journalType);

        if (transactionType != null) {
            saveOrderModification(orderNotificationMessage, transactionType);
        } else {
            LOG.warn("Transaction type {} not supported. Ignoring event.", journalType);
        }
    }

    protected void setDeclineCodeInCart(final OrderNotificationMessage orderNotificationMessage) {
        final String returnCode = orderNotificationMessage.getPaymentReply().getReturnCode();
        if (!"0".equalsIgnoreCase(returnCode)) {
            worldpayCartService.setWorldpayDeclineCodeOnCart(orderNotificationMessage.getOrderCode(), returnCode);
        }
    }

    protected void saveOrderModification(final OrderNotificationMessage orderNotificationMessage, final PaymentTransactionType transactionType) {
        final String worldpayOrderCode = orderNotificationMessage.getOrderCode();
        LOG.info("Saving worldpayOrderModificationModel for worldpay order code: {}", worldpayOrderCode);
        final WorldpayOrderModificationModel worldpayOrderModificationModel = modelService.create(WorldpayOrderModificationModel.class);
        worldpayOrderModificationModel.setType(transactionType);
        worldpayOrderModificationModel.setWorldpayOrderCode(worldpayOrderCode);
        worldpayOrderModificationModel.setOrderNotificationMessage(orderNotificationService.serialiseNotification(orderNotificationMessage));
        modelService.save(worldpayOrderModificationModel);
    }
}

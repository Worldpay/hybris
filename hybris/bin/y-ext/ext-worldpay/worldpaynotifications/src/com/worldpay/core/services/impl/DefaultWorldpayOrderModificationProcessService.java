package com.worldpay.core.services.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.dao.OrderModificationDao;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.notification.processors.WorldpayOrderNotificationHandler;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.core.services.WorldpayOrderModificationProcessService;
import com.worldpay.strategies.WorldpayPlaceOrderFromNotificationStrategy;
import com.worldpay.strategies.paymenttransaction.WorldpayPaymentTransactionTypeStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import com.worldpay.util.OrderModificationSerialiser;
import com.worldpay.worldpaynotifications.enums.DefectiveReason;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.worldpay.worldpaynotifications.enums.DefectiveReason.NO_WORLDPAY_CODE_MATCHED;
import static com.worldpay.worldpaynotifications.enums.DefectiveReason.PROCESSING_ERROR;
import static de.hybris.platform.payment.enums.PaymentTransactionType.*;
import static java.text.MessageFormat.format;

/**
 * Default implementation of the {@link WorldpayOrderModificationProcessService} interface.
 * <p>
 * For each unprocessed {@link WorldpayOrderModificationModel} a check is performed if the previous transaction was completed.
 * If it was, the order modification is processed.
 * In case of an error, a defective {@link WorldpayOrderModificationModel} is saved.
 * </p>
 */
public class DefaultWorldpayOrderModificationProcessService implements WorldpayOrderModificationProcessService {

    private static final Logger LOG = LogManager.getLogger(DefaultWorldpayOrderModificationProcessService.class);

    protected final OrderModificationDao orderModificationDao;
    protected final OrderModificationSerialiser orderModificationSerialiser;
    protected final WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    protected final WorldpayCartService worldpayCartService;
    protected final WorldpayOrderNotificationHandler worldpayOrderNotificationHandler;
    protected final WorldpayPlaceOrderFromNotificationStrategy worldpayPlaceOrderFromNotificationStrategy;
    protected final Map<PaymentTransactionType, WorldpayPaymentTransactionTypeStrategy> worldpayPaymentTransactionTypeStrategiesMap;

    public DefaultWorldpayOrderModificationProcessService(final OrderModificationDao orderModificationDao,
                                                          final OrderModificationSerialiser orderModificationSerialiser,
                                                          final WorldpayPaymentTransactionService worldpayPaymentTransactionService,
                                                          final WorldpayCartService worldpayCartService,
                                                          final WorldpayOrderNotificationHandler worldpayOrderNotificationHandler,
                                                          final WorldpayPlaceOrderFromNotificationStrategy worldpayPlaceOrderFromNotificationStrategy,
                                                          final Map<PaymentTransactionType, WorldpayPaymentTransactionTypeStrategy> worldpayPaymentTransactionTypeStrategiesMap) {
        this.orderModificationDao = orderModificationDao;
        this.orderModificationSerialiser = orderModificationSerialiser;
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
        this.worldpayCartService = worldpayCartService;
        this.worldpayOrderNotificationHandler = worldpayOrderNotificationHandler;
        this.worldpayPlaceOrderFromNotificationStrategy = worldpayPlaceOrderFromNotificationStrategy;
        this.worldpayPaymentTransactionTypeStrategiesMap = worldpayPaymentTransactionTypeStrategiesMap;
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayOrderModificationProcessService#processOrderModificationMessages(PaymentTransactionType)
     */
    @Override
    public boolean processOrderModificationMessages(final PaymentTransactionType paymentTransactionType) {
        boolean success = true;
        final List<WorldpayOrderModificationModel> orderModificationsByType = orderModificationDao.findUnprocessedOrderModificationsByType(paymentTransactionType);

        for (final WorldpayOrderModificationModel orderModificationModel : orderModificationsByType) {
            final String worldpayOrderCode = orderModificationModel.getWorldpayOrderCode();
            if (REFUSED.equals(paymentTransactionType)) {
                worldpayPaymentTransactionTypeStrategiesMap.get(REFUSED).processModificationMessage(null, orderModificationModel);
            } else {
                success = processNotRefusedNotifications(paymentTransactionType, orderModificationModel, worldpayOrderCode);
            }
        }
        return success;
    }

    protected boolean processNotRefusedNotifications(final PaymentTransactionType paymentTransactionType, final WorldpayOrderModificationModel orderModificationModel, final String worldpayOrderCode) {
        boolean success = true;
        final Optional<PaymentTransactionModel> paymentTransactionModel = getPaymentTransactionFromCode(worldpayOrderCode);
        if (paymentTransactionModel.isPresent()) {
            final PaymentTransactionModel paymentTransaction = paymentTransactionModel.get();
            if (AUTHORIZATION.equals(paymentTransactionType)) {
                worldpayPaymentTransactionTypeStrategiesMap.get(AUTHORIZATION).processModificationMessage((OrderModel) paymentTransaction.getOrder(), orderModificationModel);
            }
            final AbstractOrderModel abstractOrderModel = paymentTransaction.getOrder();
            if (abstractOrderModel instanceof OrderModel) {
                success = processOrderModificationNotification(paymentTransactionType, orderModificationModel, worldpayOrderCode, (OrderModel) abstractOrderModel);
            } else if (abstractOrderModel instanceof CartModel) {
                LOG.warn(format("Worldpay Order Code [{0}] related to a Cart. Skipping processing modification message.", worldpayOrderCode));
            }
        } else {
            final Optional<CartModel> cart = getCartByWorldpayOrderCode(worldpayOrderCode);
            if (cart.isPresent()) {
                worldpayPlaceOrderFromNotificationStrategy.placeOrderFromNotification(orderModificationModel, cart.get());
            } else {
                setDefectiveNotificationAndReason(orderModificationModel, NO_WORLDPAY_CODE_MATCHED, null, false);
            }
        }
        return success;
    }

    protected boolean processOrderModificationNotification(final PaymentTransactionType paymentTransactionType, final WorldpayOrderModificationModel orderModificationModel,
                                                           final String worldpayOrderCode, final OrderModel abstractOrderModel) {
        boolean success = true;
        LOG.info(format("Found order for Worldpay Order Code [{0}]. Processing modification message.", worldpayOrderCode));
        try {
            if (worldpayPaymentTransactionService.isPreviousTransactionCompleted(worldpayOrderCode, paymentTransactionType, abstractOrderModel)) {
                processMessage(paymentTransactionType, orderModificationModel, abstractOrderModel);
            } else {
                LOG.info(format("The previous transaction for [{0}] is still pending in worldpayOrder [{1}]", paymentTransactionType, worldpayOrderCode));
            }
        } catch (final Exception exception) {
            setDefectiveNotificationAndReason(orderModificationModel, PROCESSING_ERROR, exception, true);
            success = false;
        }
        return success;
    }

    protected void processMessage(final PaymentTransactionType paymentTransactionTypeFromCronJob, final WorldpayOrderModificationModel orderModificationModel, final OrderModel orderModel) throws WorldpayConfigurationException {
        final OrderNotificationMessage notificationMessage = orderModificationSerialiser.deserialise(orderModificationModel.getOrderNotificationMessage());
        if (REFUND_FOLLOW_ON.equals(paymentTransactionTypeFromCronJob)) {
            //pass transaction here
            worldpayPaymentTransactionTypeStrategiesMap.get(REFUND_FOLLOW_ON).processModificationMessage(orderModel, orderModificationModel);
        } else if (SETTLED.equals(paymentTransactionTypeFromCronJob)) {
            worldpayPaymentTransactionTypeStrategiesMap.get(SETTLED).processModificationMessage(orderModel, orderModificationModel);
        } else {
            PaymentTransactionType paymentTransactionType = paymentTransactionTypeFromCronJob;
            if (REFUSED.equals(paymentTransactionTypeFromCronJob)) {
                // Rejected payments expect the order to be in a waitFor_AUTHORIZATION state
                paymentTransactionType = AUTHORIZATION;
            }
            worldpayOrderNotificationHandler.handleNotificationBusinessProcess(paymentTransactionType, orderModificationModel, orderModel, notificationMessage);
        }
    }

    private void setDefectiveNotificationAndReason(WorldpayOrderModificationModel orderModificationModel, DefectiveReason invalidAuthenticatedShopperId, Exception exception, boolean processed) {
        worldpayOrderNotificationHandler.setDefectiveReason(orderModificationModel, invalidAuthenticatedShopperId);
        worldpayOrderNotificationHandler.setDefectiveModification(orderModificationModel, exception, processed);
    }

    protected Optional<CartModel> getCartByWorldpayOrderCode(final String worldpayOrderCode) {
        try {
            return Optional.ofNullable(worldpayCartService.findCartByWorldpayOrderCode(worldpayOrderCode));
        } catch (ModelNotFoundException | AmbiguousIdentifierException e) {
            LOG.warn(MessageFormat.format("No cart related to worldpay order code [{0}]", worldpayOrderCode), e);
            return Optional.empty();
        }
    }

    protected Optional<PaymentTransactionModel> getPaymentTransactionFromCode(final String worldpayOrderCode) {
        try {
            return Optional.ofNullable(worldpayPaymentTransactionService.getPaymentTransactionFromCode(worldpayOrderCode));
        } catch (ModelNotFoundException | AmbiguousIdentifierException e) {
            LOG.warn(MessageFormat.format("No payment transaction related to worldpay order code [{0}]", worldpayOrderCode), e);
            return Optional.empty();
        }
    }
}

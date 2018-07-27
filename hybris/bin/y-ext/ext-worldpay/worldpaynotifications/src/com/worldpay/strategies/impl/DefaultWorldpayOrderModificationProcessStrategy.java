package com.worldpay.strategies.impl;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.dao.OrderModificationDao;
import com.worldpay.dao.ProcessDefinitionDao;
import com.worldpay.notification.processors.WorldpayOrderNotificationHandler;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategies.WorldpayOrderModificationProcessStrategy;
import com.worldpay.strategies.WorldpayOrderModificationRefundProcessStrategy;
import com.worldpay.strategies.WorldpayPlaceOrderFromNotificationStrategy;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import com.worldpay.util.OrderModificationSerialiser;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.worldpay.worldpaynotifications.enums.DefectiveReason.*;
import static de.hybris.platform.payment.enums.PaymentTransactionType.*;
import static java.text.MessageFormat.format;

/**
 * Default implementation of the {@link WorldpayOrderModificationProcessStrategy} interface.
 * <p>
 * For each unprocessed {@link WorldpayOrderModificationModel} a check is performed if the previous transaction was completed.
 * If it was, the order modification is processed.
 * In case of an error, a defective {@link WorldpayOrderModificationModel} is saved.
 * </p>
 */
public class DefaultWorldpayOrderModificationProcessStrategy implements WorldpayOrderModificationProcessStrategy {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayOrderModificationProcessStrategy.class);

    private OrderModificationDao orderModificationDao;
    private ProcessDefinitionDao processDefinitionDao;
    private BusinessProcessService businessProcessService;
    private OrderNotificationService orderNotificationService;
    private OrderModificationSerialiser orderModificationSerialiser;
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    private Set<OrderStatus> nonTriggeringOrderStatuses;
    private WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy;
    private WorldpayOrderModificationRefundProcessStrategy worldpayOrderModificationRefundProcessStrategy;
    private WorldpayCartService worldpayCartService;
    private WorldpayOrderNotificationHandler worldpayOrderNotificationHandler;
    private WorldpayPlaceOrderFromNotificationStrategy worldpayPlaceOrderFromNotificationStrategy;

    /**
     * {@inheritDoc}
     *
     * @see WorldpayOrderModificationProcessStrategy#processOrderModificationMessages(PaymentTransactionType)
     */
    @Override
    public boolean processOrderModificationMessages(final PaymentTransactionType paymentTransactionType) {
        boolean success = true;
        final List<WorldpayOrderModificationModel> orderModificationsByType = orderModificationDao.findUnprocessedOrderModificationsByType(paymentTransactionType);

        for (final WorldpayOrderModificationModel orderModificationModel : orderModificationsByType) {
            final String worldpayOrderCode = orderModificationModel.getWorldpayOrderCode();
            if (CANCEL.equals(paymentTransactionType)) {
                LOG.info(format("Marking order modification with [{0}] transaction for refused worldpayOrder [{1}] as processed", paymentTransactionType, worldpayOrderCode));
                worldpayOrderNotificationHandler.setNonDefectiveAndProcessed(orderModificationModel);
            } else {
                success = precessOrderModificationsMessagesNotCanceled(paymentTransactionType, orderModificationModel, worldpayOrderCode);
            }
        }
        return success;
    }

    protected boolean precessOrderModificationsMessagesNotCanceled(final PaymentTransactionType paymentTransactionType, final WorldpayOrderModificationModel orderModificationModel, final String worldpayOrderCode) {
        boolean success = true;
        final Optional<PaymentTransactionModel> paymentTransactionModel = getPaymentTransactionFromCode(worldpayOrderCode);
        if (paymentTransactionModel.isPresent()) {
            final PaymentTransactionModel paymentTransaction = paymentTransactionModel.get();
            if (AUTHORIZATION.equals(paymentTransactionType)) {
                markAsProcessedIfEntryIsNotPending(paymentTransactionType, orderModificationModel, paymentTransaction);
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
                worldpayOrderNotificationHandler.setDefectiveReason(orderModificationModel, NO_WORLDPAY_CODE_MATCHED);
                worldpayOrderNotificationHandler.setDefectiveModification(orderModificationModel, null, false);
            }
        }
        return success;
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
            worldpayOrderNotificationHandler.setDefectiveReason(orderModificationModel, PROCESSING_ERROR);
            worldpayOrderNotificationHandler.setDefectiveModification(orderModificationModel, exception, true);
            success = false;
        }
        return success;
    }

    protected void markAsProcessedIfEntryIsNotPending(final PaymentTransactionType paymentTransactionType,
                                                      final WorldpayOrderModificationModel orderModificationModel,
                                                      final PaymentTransactionModel paymentTransactionModel) {
        worldpayPaymentTransactionService.getNotPendingPaymentTransactionEntriesForType(paymentTransactionModel, paymentTransactionType)
                .forEach(paymentTransactionEntryModel -> worldpayOrderNotificationHandler.setNonDefectiveAndProcessed(orderModificationModel));
    }


    protected void processMessage(final PaymentTransactionType paymentTransactionTypeFromCronJob, final WorldpayOrderModificationModel orderModificationModel, final OrderModel orderModel) {
        final OrderNotificationMessage notificationMessage = orderModificationSerialiser.deserialise(orderModificationModel.getOrderNotificationMessage());
        if (REFUND_FOLLOW_ON.equals(paymentTransactionTypeFromCronJob)) {
            if (worldpayOrderModificationRefundProcessStrategy.processRefundFollowOn(orderModel, notificationMessage)) {
                processOrderModification(orderModificationModel, notificationMessage);
            } else {
                worldpayOrderNotificationHandler.setDefectiveModification(orderModificationModel, null, true);
            }
        } else if (SETTLED.equals(paymentTransactionTypeFromCronJob)) {
            processOrderModification(orderModificationModel, notificationMessage);
        } else {
            processNotification(paymentTransactionTypeFromCronJob, orderModificationModel, orderModel, notificationMessage);
        }
    }

    protected void processNotification(final PaymentTransactionType paymentTransactionTypeFromCronJob,
                                       final WorldpayOrderModificationModel orderModificationModel,
                                       final OrderModel orderModel,
                                       final OrderNotificationMessage notificationMessage) {
        PaymentTransactionType paymentTransactionType = paymentTransactionTypeFromCronJob;
        if (CANCEL.equals(paymentTransactionTypeFromCronJob)) {
            // Rejected payments expect the order to be in a waitFor_AUTHORIZATION state
            paymentTransactionType = AUTHORIZATION;
        }
        final List<BusinessProcessModel> businessProcessModels = processDefinitionDao.findWaitingOrderProcesses(orderModel.getCode(), paymentTransactionType);
        if (businessProcessModels.size() == 1) {
            if (notificationIsValid(notificationMessage, orderModel)) {
                processOrderModification(orderModificationModel, notificationMessage);
                if (!nonTriggeringOrderStatuses.contains(orderModel.getStatus())) {
                    triggerOrderProcessEvent(paymentTransactionType, businessProcessModels.get(0));
                }
            } else {
                worldpayOrderNotificationHandler.setDefectiveReason(orderModificationModel, INVALID_AUTHENTICATED_SHOPPER_ID);
                LOG.error(format("Received modification with invalid shopperId. The Modification has been marked as defective. worldpayOrderCode = [{0}], type=[{1}]",
                        orderModificationModel.getWorldpayOrderCode(), orderModificationModel.getType()));
                worldpayOrderNotificationHandler.setDefectiveModification(orderModificationModel, null, true);
            }
        } else if (businessProcessModels.size() > 1) {
            LOG.error(format("Must only be one businessProcess found, number found: [{0}] " +
                    "for order [{1}] and transactionType [{2}]", businessProcessModels.size(), orderModel.getCode(), paymentTransactionType));
        }
    }

    protected boolean notificationIsValid(final OrderNotificationMessage notificationMessage, final AbstractOrderModel orderModel) {
        final TokenReply tokenReply = notificationMessage.getTokenReply();
        return (tokenReply != null && authenticatedShopperIdMatches(orderModel, tokenReply.getAuthenticatedShopperID())) || tokenReply == null;
    }

    protected boolean authenticatedShopperIdMatches(final AbstractOrderModel orderModel, final String tokenAuthenticatedShopperId) {
        return tokenAuthenticatedShopperId == null || tokenAuthenticatedShopperId.equals(worldpayAuthenticatedShopperIdStrategy.getAuthenticatedShopperId(orderModel.getUser()));
    }

    protected void processOrderModification(final WorldpayOrderModificationModel orderModificationModel, final OrderNotificationMessage notificationMessage) {
        orderNotificationService.processOrderNotificationMessage(notificationMessage);
        worldpayOrderNotificationHandler.setNonDefectiveAndProcessed(orderModificationModel);
    }

    private void triggerOrderProcessEvent(final PaymentTransactionType paymentTransactionType, final BusinessProcessModel businessProcessModel) {
        final String eventName = getEventName(paymentTransactionType, businessProcessModel);
        businessProcessService.triggerEvent(eventName);
    }

    private String getEventName(PaymentTransactionType paymentTransactionType, BusinessProcessModel businessProcessModels) {
        return businessProcessModels.getCode() + "_" + paymentTransactionType;
    }

    @Required
    public void setOrderModificationDao(OrderModificationDao orderModificationDao) {
        this.orderModificationDao = orderModificationDao;
    }

    @Required
    public void setBusinessProcessService(BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    @Required
    public void setProcessDefinitionDao(ProcessDefinitionDao processDefinitionDao) {
        this.processDefinitionDao = processDefinitionDao;
    }

    @Required
    public void setOrderNotificationService(OrderNotificationService orderNotificationService) {
        this.orderNotificationService = orderNotificationService;
    }

    @Required
    public void setOrderModificationSerialiser(OrderModificationSerialiser orderModificationSerialiser) {
        this.orderModificationSerialiser = orderModificationSerialiser;
    }

    @Required
    public void setWorldpayPaymentTransactionService(WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }

    @Required
    public void setNonTriggeringOrderStatuses(Set<OrderStatus> nonTriggeringOrderStatuses) {
        this.nonTriggeringOrderStatuses = nonTriggeringOrderStatuses;
    }

    @Required
    public void setWorldpayAuthenticatedShopperIdStrategy(final WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy) {
        this.worldpayAuthenticatedShopperIdStrategy = worldpayAuthenticatedShopperIdStrategy;
    }

    @Required
    public void setWorldpayOrderModificationRefundProcessStrategy(final WorldpayOrderModificationRefundProcessStrategy worldpayOrderModificationRefundProcessStrategy) {
        this.worldpayOrderModificationRefundProcessStrategy = worldpayOrderModificationRefundProcessStrategy;
    }

    @Required
    public void setWorldpayCartService(final WorldpayCartService worldpayCartService) {
        this.worldpayCartService = worldpayCartService;
    }

    @Required
    public void setWorldpayOrderNotificationHandler(final WorldpayOrderNotificationHandler worldpayOrderNotificationHandler) {
        this.worldpayOrderNotificationHandler = worldpayOrderNotificationHandler;
    }

    @Required
    public void setWorldpayPlaceOrderFromNotificationStrategy(final WorldpayPlaceOrderFromNotificationStrategy worldpayPlaceOrderFromNotificationStrategy) {
        this.worldpayPlaceOrderFromNotificationStrategy = worldpayPlaceOrderFromNotificationStrategy;
    }
}

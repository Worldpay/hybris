package com.worldpay.core.services.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.dao.OrderModificationDao;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.data.token.TokenReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.worldpaynotifications.enums.DefectiveReason;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of {@link OrderNotificationService}
 */
public class DefaultOrderNotificationService implements OrderNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultOrderNotificationService.class);

    protected final WorldpayPaymentTransactionDao worldpayPaymentTransactionDao;
    protected final Map<AuthorisedStatus, OrderNotificationProcessorStrategy> journalTypeToNotificationProcessorStrategyMap;
    protected final WorldpayCartService worldpayCartService;
    protected final OrderModificationDao orderModificationDao;
    protected final ModelService modelService;

    public DefaultOrderNotificationService(final WorldpayPaymentTransactionDao worldpayPaymentTransactionDao,
                                           final Map<AuthorisedStatus, OrderNotificationProcessorStrategy> journalTypeToNotificationProcessorStrategyMap,
                                           final WorldpayCartService worldpayCartService,
                                           final OrderModificationDao orderModificationDao,
                                           final ModelService modelService) {
        this.worldpayPaymentTransactionDao = worldpayPaymentTransactionDao;
        this.journalTypeToNotificationProcessorStrategyMap = journalTypeToNotificationProcessorStrategyMap;
        this.worldpayCartService = worldpayCartService;
        this.orderModificationDao = orderModificationDao;
        this.modelService = modelService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processOrderNotificationMessage(final OrderNotificationMessage orderNotificationMessage, final WorldpayOrderModificationModel worldpayOrderModification) throws WorldpayConfigurationException {
        final AuthorisedStatus journalTypeCode = orderNotificationMessage.getJournalReply().getJournalType();
        final OrderNotificationProcessorStrategy orderNotificationProcessorStrategy = journalTypeToNotificationProcessorStrategyMap.get(journalTypeCode);

        if (orderNotificationProcessorStrategy != null) {
            final PaymentTransactionModel paymentTransactionModel = worldpayPaymentTransactionDao.findPaymentTransactionByRequestIdFromOrdersOnly(orderNotificationMessage.getOrderCode());
            orderNotificationProcessorStrategy.processNotificationMessage(paymentTransactionModel, orderNotificationMessage);
        } else {
            final String journalTypeCodeName = journalTypeCode.name();
            LOG.warn("Could not find notification processor for journal type code [{}]. It's either an unsupported journal type or there is a configuration problem.", journalTypeCodeName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNotificationValid(final OrderNotificationMessage notificationMessage, final AbstractOrderModel orderModel) {
        final TokenReply tokenReply = notificationMessage.getTokenReply();
        return tokenReply == null || authenticatedShopperIdMatches(orderModel, tokenReply.getAuthenticatedShopperID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WorldpayOrderModificationModel> getExistingModifications(final WorldpayOrderModificationModel orderModificationModel) {
        return orderModificationDao.findExistingModifications(orderModificationModel);
    }

    private boolean authenticatedShopperIdMatches(final AbstractOrderModel orderModel, final String tokenAuthenticatedShopperId) {
        return tokenAuthenticatedShopperId == null || tokenAuthenticatedShopperId.equals(worldpayCartService.getAuthenticatedShopperId(orderModel));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String serialiseNotification(final OrderNotificationMessage orderNotificationMessage) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(orderNotificationMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderNotificationMessage deserialiseNotification(final String json) {
        final Gson gson = new Gson();
        final Type type = new TypeToken<OrderNotificationMessage>() {/**/
        }.getType();
        return gson.fromJson(json, type);
    }

    @Override
    public List<WorldpayOrderModificationModel> getUnprocessedOrderModificationsByType(final PaymentTransactionType paymentTransactionType) {
        return orderModificationDao.findUnprocessedOrderModificationsByType(paymentTransactionType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefectiveModification(final WorldpayOrderModificationModel orderModificationModel, final Exception exception, final boolean processed) {
        orderModificationModel.setDefective(Boolean.TRUE);
        orderModificationModel.setProcessed(processed);
        modelService.save(orderModificationModel);
        if (exception != null) {
            LOG.error("There was an error processing message [{}]. Reason: [{}]", orderModificationModel.getPk(), exception.getMessage(), exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefectiveReason(final WorldpayOrderModificationModel orderModificationModel, final DefectiveReason defectiveReason) {
        orderModificationModel.setDefectiveReason(defectiveReason);
        final List<WorldpayOrderModificationModel> existingModifications = getExistingModifications(orderModificationModel);

        int defectiveCounter = getDefectiveCounter(orderModificationModel) + existingModifications.stream()
            .mapToInt(this::getDefectiveCounter)
            .sum();
        existingModifications.forEach(modelService::remove);

        orderModificationModel.setDefectiveCounter(defectiveCounter + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNonDefectiveAndProcessed(final WorldpayOrderModificationModel modification) {
        modification.setProcessed(Boolean.TRUE);
        modification.setDefective(Boolean.FALSE);
        modelService.save(modification);
    }

    private int getDefectiveCounter(final WorldpayOrderModificationModel modification) {
        return Optional.ofNullable(modification.getDefectiveCounter()).orElse(0);
    }
}

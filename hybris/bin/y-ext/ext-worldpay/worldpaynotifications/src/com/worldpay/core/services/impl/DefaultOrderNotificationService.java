package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.dao.OrderModificationDao;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;

/**
 * Default implementation of {@link OrderNotificationService}
 */
public class DefaultOrderNotificationService implements OrderNotificationService {

    private static final Logger LOG = Logger.getLogger(DefaultOrderNotificationService.class);

    protected final WorldpayPaymentTransactionDao worldpayPaymentTransactionDao;
    protected final Map<AuthorisedStatus, OrderNotificationProcessorStrategy> journalTypeToNotificationProcessorStrategyMap;
    protected final WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy;
    protected final OrderModificationDao orderModificationDao;

    public DefaultOrderNotificationService(final WorldpayPaymentTransactionDao worldpayPaymentTransactionDao,
                                           final Map<AuthorisedStatus, OrderNotificationProcessorStrategy> journalTypeToNotificationProcessorStrategyMap,
                                           final WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy,
                                           final OrderModificationDao orderModificationDao) {
        this.worldpayPaymentTransactionDao = worldpayPaymentTransactionDao;
        this.journalTypeToNotificationProcessorStrategyMap = journalTypeToNotificationProcessorStrategyMap;
        this.worldpayAuthenticatedShopperIdStrategy = worldpayAuthenticatedShopperIdStrategy;
        this.orderModificationDao = orderModificationDao;
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
            LOG.warn(format("Could not find notification processor for journal type code [{0}]. " +
                "It's either an unsupported journal type or there is a configuration problem.", journalTypeCode.name()));
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
        return tokenAuthenticatedShopperId == null || tokenAuthenticatedShopperId.equals(worldpayAuthenticatedShopperIdStrategy.getAuthenticatedShopperId(orderModel.getUser()));
    }
}

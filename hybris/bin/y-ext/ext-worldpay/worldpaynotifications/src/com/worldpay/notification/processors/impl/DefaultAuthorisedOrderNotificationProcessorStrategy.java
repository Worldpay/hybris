package com.worldpay.notification.processors.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.data.PaymentReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.payment.WorldpayExemptionStrategy;
import com.worldpay.service.payment.WorldpayFraudSightStrategy;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.support.TransactionOperations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;

/**
 * The implementation of {@link OrderNotificationProcessorStrategy} that processes Authorised notifications.
 */
public class DefaultAuthorisedOrderNotificationProcessorStrategy implements OrderNotificationProcessorStrategy {

    private static final Logger LOG = LogManager.getLogger(DefaultAuthorisedOrderNotificationProcessorStrategy.class);

    protected final ModelService modelService;
    protected final TransactionOperations transactionTemplate;
    protected final WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    protected final WorldpayPaymentInfoService worldpayPaymentInfoService;
    protected final WorldpayOrderService worldpayOrderService;
    protected final WorldpayFraudSightStrategy worldpayFraudSightStrategy;
    protected final WorldpayExemptionStrategy worldpayExemptionStrategy;

    public DefaultAuthorisedOrderNotificationProcessorStrategy(final ModelService modelService,
                                                               final TransactionOperations transactionTemplate,
                                                               final WorldpayPaymentTransactionService worldpayPaymentTransactionService,
                                                               final WorldpayPaymentInfoService worldpayPaymentInfoService,
                                                               final WorldpayOrderService worldpayOrderService,
                                                               final WorldpayFraudSightStrategy worldpayFraudSightStrategy,
                                                               final WorldpayExemptionStrategy worldpayExemptionStrategy) {
        this.modelService = modelService;
        this.transactionTemplate = transactionTemplate;
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
        this.worldpayPaymentInfoService = worldpayPaymentInfoService;
        this.worldpayOrderService = worldpayOrderService;
        this.worldpayFraudSightStrategy = worldpayFraudSightStrategy;
        this.worldpayExemptionStrategy = worldpayExemptionStrategy;
    }

    /**
     * {@inheritDoc}
     *
     * @see OrderNotificationProcessorStrategy#processNotificationMessage(PaymentTransactionModel, OrderNotificationMessage)
     */
    @Override
    public void processNotificationMessage(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage) {
        LOG.debug("Message for order having code {} is a success, saving card and changing transaction status to not pending.", orderNotificationMessage.getOrderCode());

        final BigDecimal plannedAmount = worldpayOrderService.convertAmount(orderNotificationMessage.getPaymentReply().getAmount());
        paymentTransactionModel.setPlannedAmount(plannedAmount);
        final AbstractOrderModel orderModel = paymentTransactionModel.getOrder();

        final List<PaymentTransactionEntryModel> paymentTransactionEntries = worldpayPaymentTransactionService.getPendingPaymentTransactionEntriesForType(paymentTransactionModel, AUTHORIZATION);
        transactionTemplate.execute(transactionStatus -> {
            updatePaymentTransactionEntry(paymentTransactionModel, orderNotificationMessage, paymentTransactionEntries, ACCEPTED.name());
            try {
                worldpayPaymentInfoService.setPaymentInfoModel(paymentTransactionModel, orderModel, orderNotificationMessage);
            } catch (final WorldpayConfigurationException e) {
                LOG.info("Could not set the paymentInfo to the order.", e);
                return null;
            }
            return null;
        });
    }

    protected void updatePaymentTransactionEntry(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage,
                                                 final List<PaymentTransactionEntryModel> paymentTransactionEntries, final String transactionStatus) {
        final PaymentReply paymentReply = orderNotificationMessage.getPaymentReply();
        worldpayPaymentTransactionService.addRiskScore(paymentTransactionModel, paymentReply);

        addSiteRelatedAttributesToPaymentTransaction(paymentTransactionModel, paymentReply);

        paymentTransactionModel.setApmOpen(Boolean.FALSE);

        paymentTransactionEntries.forEach(paymentTransactionEntryModel -> worldpayPaymentTransactionService.addAavFields(paymentTransactionEntryModel, paymentReply));
        worldpayPaymentTransactionService.updateEntriesStatus(paymentTransactionEntries, transactionStatus);
        worldpayPaymentTransactionService.updateEntriesAmount(paymentTransactionEntries, orderNotificationMessage.getPaymentReply().getAmount());
        modelService.save(paymentTransactionModel);
    }

    protected void addSiteRelatedAttributesToPaymentTransaction(final PaymentTransactionModel paymentTransactionModel, final PaymentReply paymentReply) {
        Optional.ofNullable(paymentTransactionModel)
            .map(PaymentTransactionModel::getOrder)
            .map(AbstractOrderModel::getSite)
            .ifPresent(site -> addSiteAttributes(site, paymentTransactionModel, paymentReply));

    }

    protected void addSiteAttributes(final BaseSiteModel site, PaymentTransactionModel paymentTransactionModel, final PaymentReply paymentReply) {
        if (worldpayFraudSightStrategy.isFraudSightEnabled(site)) {
            worldpayFraudSightStrategy.addFraudSight(paymentTransactionModel, paymentReply);
        }

        if (worldpayExemptionStrategy.isExemptionEnabled(site)) {
            worldpayExemptionStrategy.addExemptionResponse(paymentTransactionModel, paymentReply);
        }
    }

}

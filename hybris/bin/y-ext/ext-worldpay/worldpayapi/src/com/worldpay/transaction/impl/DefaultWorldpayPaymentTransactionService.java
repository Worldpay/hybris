package com.worldpay.transaction.impl;

import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import com.worldpay.core.services.WorldpayBankConfigurationLookupService;
import com.worldpay.model.WorldpayAavResponseModel;
import com.worldpay.model.WorldpayBankConfigurationModel;
import com.worldpay.model.WorldpayRiskScoreModel;
import com.worldpay.data.Amount;
import com.worldpay.data.PaymentReply;
import com.worldpay.data.RiskScore;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.payment.WorldpayFraudSightStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import com.worldpay.transaction.WorldpayPaymentTransactionUtils;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.SUCCESFULL;
import static de.hybris.platform.payment.enums.PaymentTransactionType.*;
import static java.text.MessageFormat.format;

/**
 * {@inheritDoc}
 */
/* There is no obvious way of reducing the dependencies, the service is model oriented and the business logic enforces us
to inject those services and reverse populators */
@SuppressWarnings("java:S107")
public class DefaultWorldpayPaymentTransactionService implements WorldpayPaymentTransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpayPaymentTransactionService.class);

    protected final ModelService modelService;
    protected final WorldpayPaymentTransactionDao worldpayPaymentTransactionDao;
    protected final Converter<RiskScore, WorldpayRiskScoreModel> worldpayRiskScoreConverter;
    protected final Populator<PaymentReply, WorldpayAavResponseModel> worldpayAavResponsePopulator;
    protected final WorldpayBankConfigurationLookupService worldpayBankConfigurationService;
    protected final WorldpayPaymentTransactionUtils worldpayPaymentTransactionUtils;
    protected final WorldpayFraudSightStrategy worldpayFraudSightStrategy;

    public DefaultWorldpayPaymentTransactionService(final ModelService modelService,
                                                    final WorldpayPaymentTransactionDao worldpayPaymentTransactionDao,
                                                    final Converter<RiskScore, WorldpayRiskScoreModel> worldpayRiskScoreConverter,
                                                    final Populator<PaymentReply, WorldpayAavResponseModel> worldpayAavResponsePopulator,
                                                    final WorldpayBankConfigurationLookupService worldpayBankConfigurationService,
                                                    final WorldpayPaymentTransactionUtils worldpayPaymentTransactionUtils,
                                                    final WorldpayFraudSightStrategy worldpayFraudSightStrategy) {
        this.modelService = modelService;
        this.worldpayPaymentTransactionDao = worldpayPaymentTransactionDao;
        this.worldpayRiskScoreConverter = worldpayRiskScoreConverter;
        this.worldpayAavResponsePopulator = worldpayAavResponsePopulator;
        this.worldpayBankConfigurationService = worldpayBankConfigurationService;
        this.worldpayPaymentTransactionUtils = worldpayPaymentTransactionUtils;
        this.worldpayFraudSightStrategy = worldpayFraudSightStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean areAllPaymentTransactionsAcceptedForType(final OrderModel order, final PaymentTransactionType paymentTransactionType) {
        boolean typeFound = false;
        for (final PaymentTransactionModel paymentTransaction : order.getPaymentTransactions()) {
            for (final PaymentTransactionEntryModel paymentTransactionEntry : paymentTransaction.getEntries()) {
                if (paymentTransactionEntry.getType().equals(paymentTransactionType)) {
                    typeFound = true;
                }
                if (!TransactionStatus.ACCEPTED.name().equals(paymentTransactionEntry.getTransactionStatus()) && paymentTransactionEntry.getType().equals(paymentTransactionType)) {
                    return false;
                }
            }
        }
        return typeFound;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAnyPaymentTransactionApmOpenForOrder(final OrderModel order) {
        return order.getPaymentTransactions().stream()
            .filter(Objects::nonNull)
            .anyMatch(PaymentTransactionModel::getApmOpen);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPaymentTransactionPending(final PaymentTransactionModel paymentTransaction, final PaymentTransactionType paymentTransactionType) {
        return paymentTransaction.getEntries().stream()
            .filter(paymentTransactionEntry -> paymentTransactionType.equals(paymentTransactionEntry.getType()))
            .anyMatch(PaymentTransactionEntryModel::getPending);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PaymentTransactionEntryModel> filterPaymentTransactionEntriesOfType(final PaymentTransactionModel paymentTransaction,
                                                                                    final PaymentTransactionType paymentTransactionType) {
        return paymentTransaction.getEntries().stream()
            .filter(entry -> entry.getType().equals(paymentTransactionType))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PaymentTransactionEntryModel> getPendingPaymentTransactionEntriesForType(final PaymentTransactionModel paymentTransactionModel,
                                                                                         final PaymentTransactionType paymentTransactionType) {
        return filterPaymentTransactionEntriesOfType(paymentTransactionModel, paymentTransactionType).stream()
            .filter(PaymentTransactionEntryModel::getPending)
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PaymentTransactionEntryModel> getNotPendingPaymentTransactionEntriesForType(final PaymentTransactionModel paymentTransactionModel,
                                                                                            final PaymentTransactionType paymentTransactionType) {
        return filterPaymentTransactionEntriesOfType(paymentTransactionModel, paymentTransactionType).stream()
            .filter(entry -> !entry.getPending())
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPreviousTransactionCompleted(final String worldpayOrderCode, final PaymentTransactionType paymentTransactionType, final OrderModel orderModel) {
        final PaymentTransactionType dependingTransactionType = worldpayPaymentTransactionUtils.getPaymentTransactionDependency().get(paymentTransactionType);
        if (dependingTransactionType == null) {
            return true;
        }
        boolean completed = false;
        for (final PaymentTransactionModel paymentTransactionModel : orderModel.getPaymentTransactions()) {
            if (paymentTransactionModel.getRequestId().equals(worldpayOrderCode)) {
                completed = !getNotPendingPaymentTransactionEntriesForType(paymentTransactionModel, dependingTransactionType).isEmpty()
                    || isRefundForVoidOrder(paymentTransactionType, paymentTransactionModel);
            }
        }
        return completed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentTransactionEntryModel createCapturedPaymentTransactionEntry(final PaymentTransactionModel paymentTransaction, final OrderNotificationMessage orderNotificationMessage) {
        final PaymentTransactionEntryModel transactionEntryModel = modelService.create(PaymentTransactionEntryModel.class);
        transactionEntryModel.setType(CAPTURE);
        return setCommonFieldsForPaymentTransactionEntries(paymentTransaction, orderNotificationMessage, transactionEntryModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentTransactionEntryModel createRefundedPaymentTransactionEntry(final PaymentTransactionModel paymentTransaction, final OrderNotificationMessage orderNotificationMessage) {
        final PaymentTransactionEntryModel transactionEntryModel = modelService.create(PaymentTransactionEntryModel.class);
        transactionEntryModel.setType(REFUND_FOLLOW_ON);
        return setCommonFieldsForPaymentTransactionEntries(paymentTransaction, orderNotificationMessage, transactionEntryModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentTransactionEntryModel createPendingAuthorisePaymentTransactionEntry(final PaymentTransactionModel paymentTransaction,
                                                                                      final String merchantCode,
                                                                                      final CartModel cartModel,
                                                                                      final BigDecimal authorisedAmount) {
        final PaymentTransactionEntryModel transactionEntryModel = createAuthorizationPaymentTransactionEntryModel(paymentTransaction, merchantCode, cartModel, authorisedAmount);

        modelService.save(transactionEntryModel);
        modelService.refresh(paymentTransaction);

        return transactionEntryModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentTransactionEntryModel createNonPendingAuthorisePaymentTransactionEntry(final PaymentTransactionModel paymentTransaction,
                                                                                         final String merchantCode,
                                                                                         final AbstractOrderModel abstractOrderModel,
                                                                                         final BigDecimal authorisedAmount) {
        final PaymentTransactionEntryModel transactionEntryModel = createAuthorizationPaymentTransactionEntryModel(paymentTransaction, merchantCode, abstractOrderModel, authorisedAmount);
        transactionEntryModel.setPending(Boolean.FALSE);

        modelService.save(transactionEntryModel);
        modelService.refresh(paymentTransaction);

        return transactionEntryModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentTransactionModel createPaymentTransaction(final boolean apmOpen, final String merchantCode, final CommerceCheckoutParameter commerceCheckoutParameter) {
        final PaymentTransactionModel paymentTransactionModel = modelService.create(PaymentTransactionModel.class);
        final AbstractOrderModel abstractOrderModel = commerceCheckoutParameter.getCart() == null ? commerceCheckoutParameter.getOrder() : commerceCheckoutParameter.getCart();
        final String worldpayOrderCode = abstractOrderModel.getWorldpayOrderCode();
        paymentTransactionModel.setCode(worldpayOrderCode);
        paymentTransactionModel.setRequestId(worldpayOrderCode);
        paymentTransactionModel.setRequestToken(merchantCode);
        paymentTransactionModel.setPaymentProvider(commerceCheckoutParameter.getPaymentProvider());
        paymentTransactionModel.setOrder(abstractOrderModel);
        paymentTransactionModel.setCurrency(abstractOrderModel.getCurrency());
        paymentTransactionModel.setInfo(commerceCheckoutParameter.getPaymentInfo());
        paymentTransactionModel.setApmOpen(apmOpen);
        paymentTransactionModel.setPlannedAmount(commerceCheckoutParameter.getAuthorizationAmount());
        if (abstractOrderModel instanceof CartModel) {
            final String shopperBankCode = ((CartModel) abstractOrderModel).getShopperBankCode();
            final WorldpayBankConfigurationModel activeBankConfigurationsForCode = worldpayBankConfigurationService.getBankConfigurationForBankCode(shopperBankCode);
            paymentTransactionModel.setWorldpayBank(activeBankConfigurationsForCode);
        }
        modelService.save(paymentTransactionModel);
        return paymentTransactionModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentTransactionModel getPaymentTransactionFromCode(final String transactionCode) {
        try {
            return worldpayPaymentTransactionDao.findPaymentTransactionByRequestId(transactionCode);
        } catch (final ModelNotFoundException e) {
            LOG.error(format("Error finding paymentTransaction with code [{0}]", transactionCode), e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEntriesStatus(final List<PaymentTransactionEntryModel> paymentTransactionEntries, final String transactionStatus) {
        for (final PaymentTransactionEntryModel entry : paymentTransactionEntries) {
            entry.setTransactionStatus(transactionStatus);
            entry.setPending(Boolean.FALSE);
            LOG.debug("Setting pending flag of PaymentTransactionEntry with code [{}] to false", entry.getCode());
        }
        modelService.saveAll(paymentTransactionEntries);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRiskScore(final PaymentTransactionModel paymentTransactionModel, final PaymentReply paymentReply) {
        Optional.ofNullable(paymentReply.getRiskScore())
            .map(worldpayRiskScoreConverter::convert)
            .map(riskScore -> {
                paymentTransactionModel.setRiskScore(riskScore);
                return paymentTransactionModel;
            })
            .ifPresent(modelService::save);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAavFields(final PaymentTransactionEntryModel paymentTransactionEntryModel, final PaymentReply paymentReply) {
        final WorldpayAavResponseModel aavResponse = modelService.create(WorldpayAavResponseModel.class);
        worldpayAavResponsePopulator.populate(paymentReply, aavResponse);
        paymentTransactionEntryModel.setAavResponse(aavResponse);

        modelService.save(paymentTransactionEntryModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEntriesAmount(final List<PaymentTransactionEntryModel> transactionEntries, final Amount amount) {
        for (final PaymentTransactionEntryModel entry : transactionEntries) {
            final BigDecimal amountValue = worldpayPaymentTransactionUtils.convertAmount(amount);
            checkAmountChanges(entry, amountValue);
            entry.setAmount(amountValue);
            entry.setCurrency(worldpayPaymentTransactionUtils.getCurrencyFromAmount(amount));
            LOG.debug("Updating amount received value");
        }
        modelService.saveAll(transactionEntries);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthorisedAmountCorrect(final OrderModel order) {
        BigDecimal authorisedAmount = BigDecimal.ZERO;

        for (final PaymentTransactionModel paymentTransaction : order.getPaymentTransactions()) {
            for (final PaymentTransactionEntryModel entry : paymentTransaction.getEntries()) {
                if (AUTHORIZATION.equals(entry.getType()) && entry.getAmount() == null) {
                    //To handle HOP response without authorised amount in response
                    return true;
                }
                if (AUTHORIZATION.equals(entry.getType())) {
                    authorisedAmount = authorisedAmount.add(entry.getAmount());
                }
            }
        }

        final double tolerance = worldpayPaymentTransactionUtils.getAuthoriseAmountToleranceFromConfig();

        return Math.abs(order.getTotalPrice() - authorisedAmount.doubleValue()) <= tolerance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentTransactionEntryModel createNotPendingCancelOrderTransactionEntry(final PaymentTransactionModel paymentTransactionModel) {
        final PaymentTransactionEntryModel paymentTransactionEntryModel = modelService.create(PaymentTransactionEntryModel.class);
        final AbstractOrderModel orderModel = paymentTransactionModel.getOrder();

        paymentTransactionEntryModel.setCode(worldpayPaymentTransactionUtils.generatePaymentTransactionCode(paymentTransactionModel));
        paymentTransactionEntryModel.setType(CANCEL);
        paymentTransactionEntryModel.setPaymentTransaction(paymentTransactionModel);
        paymentTransactionEntryModel.setTime(Date.from(Instant.now()));
        paymentTransactionEntryModel.setRequestId(orderModel.getWorldpayOrderCode());
        paymentTransactionEntryModel.setAmount(BigDecimal.valueOf(orderModel.getTotalPrice()));
        paymentTransactionEntryModel.setRequestToken(paymentTransactionModel.getRequestToken());
        paymentTransactionEntryModel.setTransactionStatus(TransactionStatus.REJECTED.name());
        paymentTransactionEntryModel.setTransactionStatusDetails(TransactionStatusDetails.PROCESSOR_DECLINE.name());
        paymentTransactionEntryModel.setCurrency(orderModel.getCurrency());
        paymentTransactionEntryModel.setPending(false);
        modelService.save(paymentTransactionEntryModel);

        return paymentTransactionEntryModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFraudSightToPaymentTransaction(final PaymentTransactionModel paymentTransaction, final PaymentReply paymentReply) {
        if (worldpayFraudSightStrategy.isFraudSightEnabled()) {
            worldpayFraudSightStrategy.addFraudSight(paymentTransaction, paymentReply);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentTransactionEntryModel createNotPendingSettledPaymentTransactionEntry(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage) {
        final PaymentTransactionEntryModel transactionEntryModel = modelService.create(PaymentTransactionEntryModel.class);
        transactionEntryModel.setType(SETTLED);
        return setCommonFieldsForPaymentTransactionEntries(paymentTransactionModel, orderNotificationMessage, transactionEntryModel);
    }

    protected PaymentTransactionEntryModel setCommonFieldsForPaymentTransactionEntries(final PaymentTransactionModel paymentTransactionModel,
                                                                                       final OrderNotificationMessage orderNotificationMessage,
                                                                                       final PaymentTransactionEntryModel transactionEntryModel) {
        transactionEntryModel.setPaymentTransaction(paymentTransactionModel);
        transactionEntryModel.setRequestId(orderNotificationMessage.getOrderCode());
        transactionEntryModel.setRequestToken(orderNotificationMessage.getMerchantCode());
        transactionEntryModel.setTransactionStatus(ACCEPTED.name());
        transactionEntryModel.setTransactionStatusDetails(SUCCESFULL.name());
        transactionEntryModel.setCode(worldpayPaymentTransactionUtils.generatePaymentTransactionCode(paymentTransactionModel));
        transactionEntryModel.setTime(Date.from(Instant.now()));
        transactionEntryModel.setPending(Boolean.FALSE);

        final Amount amount = orderNotificationMessage.getPaymentReply().getAmount();

        final BigDecimal amountValue = worldpayPaymentTransactionUtils.convertAmount(amount);
        transactionEntryModel.setAmount(amountValue);
        transactionEntryModel.setCurrency(worldpayPaymentTransactionUtils.getCurrencyFromAmount(amount));

        modelService.save(transactionEntryModel);
        return transactionEntryModel;
    }

    private void checkAmountChanges(final PaymentTransactionEntryModel entry, final BigDecimal amountValue) {
        if (entry.getAmount() != null && entry.getAmount().compareTo(amountValue) != 0) {
            logAmountChanged(entry, amountValue);
        }
    }

    protected void logAmountChanged(final PaymentTransactionEntryModel entry, final BigDecimal amountValue) {
        LOG.warn("The amount for the transaction entry [{}] has changed from [{}] to [{}] during [{}]", entry.getCode(), entry.getAmount(), amountValue, entry.getType());
    }

    private PaymentTransactionEntryModel createAuthorizationPaymentTransactionEntryModel(final PaymentTransactionModel paymentTransaction,
                                                                                         final String merchantCode,
                                                                                         final AbstractOrderModel abstractOrderModel,
                                                                                         final BigDecimal authorisedAmount) {
        final PaymentTransactionEntryModel transactionEntryModel = modelService.create(PaymentTransactionEntryModel.class);

        transactionEntryModel.setType(AUTHORIZATION);
        transactionEntryModel.setPaymentTransaction(paymentTransaction);
        transactionEntryModel.setRequestId(abstractOrderModel.getWorldpayOrderCode());
        transactionEntryModel.setRequestToken(merchantCode);
        transactionEntryModel.setCode(worldpayPaymentTransactionUtils.generatePaymentTransactionCode(paymentTransaction));
        transactionEntryModel.setTime(Date.from(Instant.now()));
        transactionEntryModel.setTransactionStatus(ACCEPTED.name());
        transactionEntryModel.setTransactionStatusDetails(SUCCESFULL.name());
        transactionEntryModel.setAmount(authorisedAmount);
        transactionEntryModel.setCurrency(abstractOrderModel.getCurrency());
        return transactionEntryModel;
    }

    private boolean isRefundForVoidOrder(final PaymentTransactionType paymentTransactionType, final PaymentTransactionModel paymentTransactionModel) {
        final List<PaymentTransactionEntryModel> pendingCancelPaymentTransactionEntries = getPendingPaymentTransactionEntriesForType(paymentTransactionModel, CANCEL);
        return REFUND_FOLLOW_ON.equals(paymentTransactionType) && !pendingCancelPaymentTransactionEntries.isEmpty();
    }
}

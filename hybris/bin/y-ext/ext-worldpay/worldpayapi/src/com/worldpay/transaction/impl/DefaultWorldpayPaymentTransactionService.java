package com.worldpay.transaction.impl;

import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import com.worldpay.model.WorldpayAavResponseModel;
import com.worldpay.model.WorldpayRiskScoreModel;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.RiskScore;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.EntryCodeStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.SUCCESFULL;
import static de.hybris.platform.payment.enums.PaymentTransactionType.*;
import static java.text.MessageFormat.format;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayPaymentTransactionService implements WorldpayPaymentTransactionService {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayPaymentTransactionService.class);

    private ModelService modelService;
    private ConfigurationService configurationService;
    private CommonI18NService commonI18NService;
    private EntryCodeStrategy entryCodeStrategy;
    private Map<PaymentTransactionType, PaymentTransactionType> paymentTransactionDependency;
    private WorldpayPaymentTransactionDao worldpayPaymentTransactionDao;
    private Converter<RiskScore, WorldpayRiskScoreModel> worldpayRiskScoreConverter;
    private Populator<PaymentReply, WorldpayAavResponseModel> worldpayAavResponsePopulator;

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
        return order.getPaymentTransactions().stream().filter(Objects::nonNull).anyMatch(PaymentTransactionModel::getApmOpen);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPaymentTransactionPending(final PaymentTransactionModel paymentTransaction, final PaymentTransactionType paymentTransactionType) {
        return paymentTransaction.getEntries().stream()
                .anyMatch(paymentTransactionEntry -> paymentTransactionType.equals(paymentTransactionEntry.getType()) && paymentTransactionEntry.getPending());
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
        final PaymentTransactionType dependingTransactionType = paymentTransactionDependency.get(paymentTransactionType);
        if (dependingTransactionType == null) {
            return true;
        }
        boolean completed = false;
        for (final PaymentTransactionModel paymentTransactionModel : orderModel.getPaymentTransactions()) {
            if (paymentTransactionModel.getRequestId().equals(worldpayOrderCode)) {
                completed = !getNotPendingPaymentTransactionEntriesForType(paymentTransactionModel, dependingTransactionType).isEmpty();
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
        String worldpayOrderCode = abstractOrderModel.getWorldpayOrderCode();
        paymentTransactionModel.setCode(worldpayOrderCode);
        paymentTransactionModel.setRequestId(worldpayOrderCode);
        paymentTransactionModel.setRequestToken(merchantCode);
        paymentTransactionModel.setPaymentProvider(commerceCheckoutParameter.getPaymentProvider());
        paymentTransactionModel.setOrder(abstractOrderModel);
        paymentTransactionModel.setCurrency(abstractOrderModel.getCurrency());
        paymentTransactionModel.setInfo(commerceCheckoutParameter.getPaymentInfo());
        paymentTransactionModel.setApmOpen(apmOpen);
        paymentTransactionModel.setPlannedAmount(commerceCheckoutParameter.getAuthorizationAmount());

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
        } catch (ModelNotFoundException e) {
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
            LOG.debug(format("Setting pending flag of PaymentTransactionEntry with code [{0}] to false", entry.getCode()));
        }
        modelService.saveAll(paymentTransactionEntries);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRiskScore(final PaymentTransactionModel paymentTransactionModel, final PaymentReply paymentReply) {
        final RiskScore riskScore = paymentReply.getRiskScore();
        if (riskScore != null) {
            final WorldpayRiskScoreModel worldpayRiskScoreModel = worldpayRiskScoreConverter.convert(riskScore);
            paymentTransactionModel.setRiskScore(worldpayRiskScoreModel);
            modelService.save(paymentTransactionModel);
        }
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
            final BigDecimal amountValue = convertAmount(amount);
            checkAmountChanges(entry, amountValue);
            entry.setAmount(amountValue);
            entry.setCurrency(commonI18NService.getCurrency(amount.getCurrencyCode()));
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

        double tolerance = configurationService.getConfiguration().getDouble("worldpayapi.authoriseamount.validation.tolerance");

        return Math.abs(order.getTotalPrice() - authorisedAmount.doubleValue()) <= tolerance;
    }

    protected BigDecimal convertAmount(final Amount amount) {
        final Currency currency = Currency.getInstance(amount.getCurrencyCode());
        return new BigDecimal(amount.getValue()).movePointLeft(currency.getDefaultFractionDigits());
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

    protected PaymentTransactionEntryModel setCommonFieldsForPaymentTransactionEntries(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage,
                                                                                       final PaymentTransactionEntryModel transactionEntryModel) {
        transactionEntryModel.setPaymentTransaction(paymentTransactionModel);
        transactionEntryModel.setRequestId(orderNotificationMessage.getOrderCode());
        transactionEntryModel.setRequestToken(orderNotificationMessage.getMerchantCode());
        transactionEntryModel.setTransactionStatus(ACCEPTED.name());
        transactionEntryModel.setTransactionStatusDetails(SUCCESFULL.name());
        transactionEntryModel.setCode(entryCodeStrategy.generateCode(paymentTransactionModel));
        transactionEntryModel.setTime(DateTime.now().toDate());
        transactionEntryModel.setPending(Boolean.FALSE);

        final Amount amount = orderNotificationMessage.getPaymentReply().getAmount();

        final BigDecimal amountValue = convertAmount(amount);
        transactionEntryModel.setAmount(amountValue);
        transactionEntryModel.setCurrency(commonI18NService.getCurrency(amount.getCurrencyCode()));

        modelService.save(transactionEntryModel);
        return transactionEntryModel;
    }

    private void checkAmountChanges(final PaymentTransactionEntryModel entry, final BigDecimal amountValue) {
        if (entry.getAmount() != null && entry.getAmount().compareTo(amountValue) != 0) {
            logAmountChanged(entry, amountValue);
        }
    }

    protected void logAmountChanged(final PaymentTransactionEntryModel entry, final BigDecimal amountValue) {
        LOG.warn(format("The amount for the transaction entry [{0}] has changed from [{1}] to [{2}] during [{3}]", entry.getCode(), entry.getAmount(), amountValue, entry.getType()));
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
        transactionEntryModel.setCode(entryCodeStrategy.generateCode(paymentTransaction));
        transactionEntryModel.setTime(DateTime.now().toDate());
        transactionEntryModel.setTransactionStatus(ACCEPTED.name());
        transactionEntryModel.setTransactionStatusDetails(SUCCESFULL.name());
        transactionEntryModel.setAmount(authorisedAmount);
        transactionEntryModel.setCurrency(abstractOrderModel.getCurrency());
        return transactionEntryModel;
    }

    @Required
    public void setPaymentTransactionDependency(Map<PaymentTransactionType, PaymentTransactionType> paymentTransactionDependency) {
        this.paymentTransactionDependency = paymentTransactionDependency;
    }

    @Required
    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setEntryCodeStrategy(EntryCodeStrategy entryCodeStrategy) {
        this.entryCodeStrategy = entryCodeStrategy;
    }

    @Required
    public void setWorldpayPaymentTransactionDao(WorldpayPaymentTransactionDao worldpayPaymentTransactionDao) {
        this.worldpayPaymentTransactionDao = worldpayPaymentTransactionDao;
    }

    @Required
    public void setWorldpayRiskScoreConverter(Converter<RiskScore, WorldpayRiskScoreModel> worldpayRiskScoreConverter) {
        this.worldpayRiskScoreConverter = worldpayRiskScoreConverter;
    }

    @Required
    public void setWorldpayAavResponsePopulator(Populator<PaymentReply, WorldpayAavResponseModel> worldpayAavResponsePopulator) {
        this.worldpayAavResponsePopulator = worldpayAavResponsePopulator;
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}

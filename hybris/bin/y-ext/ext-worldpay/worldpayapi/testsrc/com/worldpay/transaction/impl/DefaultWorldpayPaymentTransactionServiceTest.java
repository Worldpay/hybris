package com.worldpay.transaction.impl;

import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import com.worldpay.core.services.WorldpayBankConfigurationLookupService;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.model.WorldpayAavResponseModel;
import com.worldpay.model.WorldpayBankConfigurationModel;
import com.worldpay.model.WorldpayRiskScoreModel;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.RiskScore;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.transaction.EntryCodeStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatus.REJECTED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.PROCESSOR_DECLINE;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.SUCCESFULL;
import static de.hybris.platform.payment.enums.PaymentTransactionType.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayPaymentTransactionServiceTest {

    private static final String REQUEST_ID = "requestId";
    private static final String REQUEST_TOKEN = "requestToken";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String PAYMENT_PROVIDER = "paymentProvider";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String TRANSACTION_STATUS = "transactionStatus";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String TRANSACTION_ENTRY_CODE = "transactionEntryCode";
    private static final String BANK_CODE = "ING";

    private final Currency currency = Currency.getInstance(Locale.UK);
    private final Map<PaymentTransactionType, PaymentTransactionType> paymentTransactionDependency = new HashMap<>();

    @Spy
    @InjectMocks
    private DefaultWorldpayPaymentTransactionService testObj;

    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private PaymentTransactionEntryModel authorisedAndAcceptedAndNotPendingEntryMock, authorisedAndAcceptedAndPendingEntryMock, authorisedAndRejectedAndPendingEntryMock, pendingCaptureEntryMock;
    @Mock
    private PaymentTransactionEntryModel capturedEntryMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private CommonI18NService commonI18NServiceMock;
    @Mock
    private CurrencyModel currencyModelMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock
    private Amount amountMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private EntryCodeStrategy entryCodeStrategyMock;
    @Mock
    private WorldpayPaymentTransactionDao worldpayPaymentTransactionDaoMock;
    @Mock
    private PaymentTransactionModel notApmOpenPaymentTransactionModelMock, apmOpenPaymentTransactionModelMock;
    @Mock
    private RedirectAuthoriseResult redirectAuthoriseResultMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private RiskScore riskScoreMock;
    @Mock
    private Converter<RiskScore, WorldpayRiskScoreModel> worldpayRiskScoreConverterMock;
    @Mock
    private WorldpayRiskScoreModel worldpayRiskScoreModelMock;
    @Mock
    private WorldpayAavResponseModel worldpayAavResponseModelMock;
    @Mock
    private Populator<PaymentReply, WorldpayAavResponseModel> worldpayAavResponsePopulatorMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private CommerceCheckoutParameter commerceCheckoutParameterMock;
    @Mock
    private BigDecimal bigDecimalMock;
    @Mock
    private ConfigurationService configurationServiceMock;
    @Mock
    private Configuration configurationMock;
    @Mock
    private WorldpayBankConfigurationLookupService worldpayBankConfigurationServiceMock;
    @Mock
    private WorldpayBankConfigurationModel worldpayBankConfigurationMock;
    @Mock
    private AbstractOrderModel abstractOrderMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;

    @Before
    public void setUp() {
        paymentTransactionDependency.put(CAPTURE, AUTHORIZATION);
        paymentTransactionDependency.put(SETTLED, CAPTURE);
        testObj.setPaymentTransactionDependency(paymentTransactionDependency);

        when(authorisedAndAcceptedAndNotPendingEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(authorisedAndAcceptedAndNotPendingEntryMock.getTransactionStatus()).thenReturn(ACCEPTED.name());
        when(authorisedAndAcceptedAndNotPendingEntryMock.getPending()).thenReturn(Boolean.FALSE);

        when(authorisedAndAcceptedAndPendingEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(authorisedAndAcceptedAndPendingEntryMock.getTransactionStatus()).thenReturn(REJECTED.name());
        when(authorisedAndAcceptedAndPendingEntryMock.getPending()).thenReturn(Boolean.TRUE);

        when(authorisedAndRejectedAndPendingEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(authorisedAndRejectedAndPendingEntryMock.getTransactionStatus()).thenReturn(REJECTED.name());
        when(authorisedAndRejectedAndPendingEntryMock.getPending()).thenReturn(Boolean.FALSE);

        when(capturedEntryMock.getType()).thenReturn(CAPTURE);
        when(capturedEntryMock.getTransactionStatus()).thenReturn(ACCEPTED.name());
        when(capturedEntryMock.getPending()).thenReturn(Boolean.FALSE);
        when(orderModelMock.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransactionModelMock));

        when(entryCodeStrategyMock.generateCode(paymentTransactionModelMock)).thenReturn(TRANSACTION_ENTRY_CODE);

        when(modelServiceMock.create(PaymentTransactionEntryModel.class)).thenReturn(authorisedAndAcceptedAndPendingEntryMock);
        when(modelServiceMock.create(PaymentTransactionModel.class)).thenReturn(paymentTransactionModelMock);

        when(redirectAuthoriseResultMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(redirectAuthoriseResultMock.getPending()).thenReturn(Boolean.TRUE);

        when(notApmOpenPaymentTransactionModelMock.getApmOpen()).thenReturn(Boolean.FALSE);
        when(apmOpenPaymentTransactionModelMock.getApmOpen()).thenReturn(Boolean.TRUE);

        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayOrderServiceMock.convertAmount(amountMock)).thenReturn(BigDecimal.TEN);
        when(amountMock.getCurrencyCode()).thenReturn(currency.getCurrencyCode());

        when(capturedEntryMock.getAmount()).thenReturn(BigDecimal.TEN);

        when(orderNotificationMessageMock.getPaymentReply().getAmount()).thenReturn(amountMock);
        when(orderNotificationMessageMock.getOrderCode()).thenReturn(REQUEST_ID);
        when(orderNotificationMessageMock.getMerchantCode()).thenReturn(REQUEST_TOKEN);

        when(commonI18NServiceMock.getCurrency(currency.getCurrencyCode())).thenReturn(currencyModelMock);

        when(commerceCheckoutParameterMock.getAuthorizationAmount()).thenReturn(BigDecimal.TEN);
        when(commerceCheckoutParameterMock.getCart()).thenReturn(cartModelMock);
        when(commerceCheckoutParameterMock.getPaymentInfo()).thenReturn(paymentInfoModelMock);
        when(commerceCheckoutParameterMock.getPaymentProvider()).thenReturn(PAYMENT_PROVIDER);
        when(cartModelMock.getCurrency()).thenReturn(currencyModelMock);

        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(authorisedAndAcceptedAndNotPendingEntryMock));
        when(authorisedAndAcceptedAndNotPendingEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(authorisedAndAcceptedAndNotPendingEntryMock.getAmount()).thenReturn(new BigDecimal(50));
        when(configurationServiceMock.getConfiguration()).thenReturn(configurationMock);
        when(configurationMock.getDouble("worldpayapi.authoriseamount.validation.tolerance")).thenReturn(0.01);
        when(cartModelMock.getShopperBankCode()).thenReturn(BANK_CODE);
        when(worldpayBankConfigurationServiceMock.getBankConfigurationForBankCode(BANK_CODE)).thenReturn(worldpayBankConfigurationMock);
    }

    @Test
    public void allPaymentTransactionTypesAcceptedShouldReturnTrue() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(authorisedAndAcceptedAndNotPendingEntryMock));

        final boolean result = testObj.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION);

        assertTrue(result);
    }

    @Test
    public void anyPaymentTransactionTypesOfWrongTypeShouldReturnFalse() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(capturedEntryMock));

        final boolean result = testObj.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION);

        assertFalse(result);
    }

    @Test
    public void somePaymentTransactionTypesAcceptedAndSomeNotAcceptedShouldReturnFalse() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndNotPendingEntryMock, authorisedAndAcceptedAndPendingEntryMock));

        final boolean result = testObj.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION);

        assertFalse(result);
    }

    @Test
    public void ifAllPaymentTransactionsPendingThenReturnTrue() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndNotPendingEntryMock, authorisedAndAcceptedAndPendingEntryMock));

        final boolean result = testObj.isPaymentTransactionPending(paymentTransactionModelMock, AUTHORIZATION);

        assertTrue(result);
    }

    @Test
    public void ifAnyPaymentTransactionsNotPendingThenReturnTrue() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndNotPendingEntryMock, authorisedAndAcceptedAndPendingEntryMock));

        final boolean result = testObj.isPaymentTransactionPending(paymentTransactionModelMock, AUTHORIZATION);

        assertTrue(result);
    }

    @Test
    public void ifAllPaymentTransactionsNotPendingThenReturnFalse() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndNotPendingEntryMock, authorisedAndRejectedAndPendingEntryMock));

        final boolean result = testObj.isPaymentTransactionPending(paymentTransactionModelMock, AUTHORIZATION);

        assertFalse(result);
    }

    @Test
    public void isPreviousTransactionCompletedShouldReturnTrueIfTheDependingTransactionEntryIsNotPending() {
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(authorisedAndAcceptedAndNotPendingEntryMock));

        final boolean result = testObj.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock);

        assertTrue(result);
    }

    @Test
    public void isPreviousTransactionCompletedShouldReturnTrueIfTheDependingTransactionEntryIsPending() {
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(authorisedAndAcceptedAndPendingEntryMock));

        final boolean result = testObj.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock);

        assertFalse(result);
    }

    @Test
    public void isPreviousTransactionCompletedShouldReturnFalseIfThereAreNoEntriesInTheTransaction() {
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.emptyList());

        final boolean result = testObj.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock);

        assertFalse(result);
    }

    @Test
    public void isPreviousTransactionCompletedShouldReturnFalseIfThereAreNoEntriesOfDependingTypeInTheTransaction() {
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(authorisedAndAcceptedAndNotPendingEntryMock));

        final boolean result = testObj.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, SETTLED, orderModelMock);

        assertFalse(result);
    }

    @Test
    public void whenTransactionIsNonDependantOnAPreviousOneThenReturnTrue() {
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(authorisedAndAcceptedAndPendingEntryMock));

        final boolean result = testObj.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock);

        assertTrue(result);
    }

    @Test
    public void shouldCreatePendingAuthorizationPaymentTransactionEntry() {
        when(paymentTransactionModelMock.getCode()).thenReturn(TRANSACTION_ENTRY_CODE);

        final PaymentTransactionEntryModel result = testObj.createPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, REQUEST_TOKEN, cartModelMock, bigDecimalMock);

        verify(result).setType(AUTHORIZATION);
        verify(result).setRequestId(WORLDPAY_ORDER_CODE);
        verify(result).setRequestToken(REQUEST_TOKEN);
        verify(result).setTransactionStatus(ACCEPTED.name());
        verify(result).setTransactionStatusDetails(SUCCESFULL.name());
        verify(result).setCode(TRANSACTION_ENTRY_CODE);
        verify(result, never()).setPending(anyBoolean());
        verify(modelServiceMock).save(result);
        verify(modelServiceMock).refresh(paymentTransactionModelMock);
    }

    @Test
    public void shouldCreateNonPendingAuthorizationPaymentTransactionEntry() {
        when(paymentTransactionModelMock.getCode()).thenReturn(TRANSACTION_ENTRY_CODE);

        final PaymentTransactionEntryModel result = testObj.createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, REQUEST_TOKEN, cartModelMock, bigDecimalMock);

        verify(result).setType(AUTHORIZATION);
        verify(result).setRequestId(WORLDPAY_ORDER_CODE);
        verify(result).setRequestToken(REQUEST_TOKEN);
        verify(result).setTransactionStatus(ACCEPTED.name());
        verify(result).setTransactionStatusDetails(SUCCESFULL.name());
        verify(result).setCode(TRANSACTION_ENTRY_CODE);
        verify(result).setPending(false);
        verify(modelServiceMock).save(result);
        verify(modelServiceMock).refresh(paymentTransactionModelMock);
    }

    @Test
    public void shouldCreateCaptureEntry() {

        final PaymentTransactionEntryModel result = testObj.createCapturedPaymentTransactionEntry(paymentTransactionModelMock, orderNotificationMessageMock);

        verifyPaymentTransactionEntry(result, CAPTURE, false);
        verify(result).setCurrency(currencyModelMock);
    }

    @Test
    public void shouldReturnTransactionWhenTransactionIsFound() {
        when(worldpayPaymentTransactionDaoMock.findPaymentTransactionByRequestId(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);

        testObj.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE);

        verify(worldpayPaymentTransactionDaoMock).findPaymentTransactionByRequestId(WORLDPAY_ORDER_CODE);
    }

    @Test
    public void shouldReturnNullWhenTransactionIsNotFound() {
        when(worldpayPaymentTransactionDaoMock.findPaymentTransactionByRequestId(WORLDPAY_ORDER_CODE)).thenThrow(new ModelNotFoundException(EXCEPTION_MESSAGE));

        final PaymentTransactionModel result = testObj.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE);

        assertNull(result);
        verify(worldpayPaymentTransactionDaoMock).findPaymentTransactionByRequestId(WORLDPAY_ORDER_CODE);
    }

    @Test
    public void shouldCreatePaymentTransaction() {
        testObj.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock);

        verify(paymentTransactionModelMock).setCode(WORLDPAY_ORDER_CODE);
        verify(paymentTransactionModelMock).setRequestId(WORLDPAY_ORDER_CODE);
        verify(paymentTransactionModelMock).setRequestToken(MERCHANT_CODE);
        verify(paymentTransactionModelMock).setPaymentProvider(PAYMENT_PROVIDER);
        verify(paymentTransactionModelMock).setOrder(cartModelMock);
        verify(paymentTransactionModelMock).setCurrency(currencyModelMock);
        verify(paymentTransactionModelMock).setInfo(paymentInfoModelMock);
        verify(paymentTransactionModelMock).setApmOpen(true);
        verify(paymentTransactionModelMock).setPlannedAmount(BigDecimal.TEN);
        verify(paymentTransactionModelMock).setWorldpayBank(worldpayBankConfigurationMock);
        verify(modelServiceMock).save(paymentTransactionModelMock);
    }

    @Test
    public void shouldNotSetWorldpayBankIfNotACart() {
        when(commerceCheckoutParameterMock.getCart()).thenReturn(null);
        when(commerceCheckoutParameterMock.getOrder()).thenReturn(abstractOrderMock);

        testObj.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock);

        verify(paymentTransactionModelMock, never()).setWorldpayBank(any(WorldpayBankConfigurationModel.class));
    }

    @Test
    public void shouldCreateTransactionWillNullBankCodeIfMissing() {
        when(worldpayBankConfigurationServiceMock.getBankConfigurationForBankCode(null)).thenReturn(null);

        testObj.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock);

        verify(paymentTransactionModelMock).setWorldpayBank(worldpayBankConfigurationMock);
        verify(modelServiceMock).save(paymentTransactionModelMock);
    }

    @Test
    public void shouldUpdateTransactionStatus() {
        final List<PaymentTransactionEntryModel> paymentTransactionEntries = Collections.singletonList(authorisedAndAcceptedAndPendingEntryMock);

        testObj.updateEntriesStatus(paymentTransactionEntries, TRANSACTION_STATUS);

        verify(authorisedAndAcceptedAndPendingEntryMock).setTransactionStatus(TRANSACTION_STATUS);
        verify(authorisedAndAcceptedAndPendingEntryMock).setPending(false);
        verify(modelServiceMock).saveAll(paymentTransactionEntries);
    }

    @Test
    public void isAnyPaymentTransactionApmOpenForOrderReturnsTrueWhenAnyPaymentTransactionIsApmOpen() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(asList(notApmOpenPaymentTransactionModelMock, apmOpenPaymentTransactionModelMock));

        final boolean result = testObj.isAnyPaymentTransactionApmOpenForOrder(orderModelMock);

        assertTrue(result);
    }

    @Test
    public void isAnyPaymentTransactionApmOpenForOrderReturnsFalseWhenAllPaymentTransactionAreNotApmOpen() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(Collections.singletonList(notApmOpenPaymentTransactionModelMock));

        final boolean result = testObj.isAnyPaymentTransactionApmOpenForOrder(orderModelMock);

        assertFalse(result);
    }

    @Test
    public void shouldReturnPendingEntries() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndPendingEntryMock, authorisedAndAcceptedAndNotPendingEntryMock));

        final List<PaymentTransactionEntryModel> result = testObj.getPendingPaymentTransactionEntriesForType(paymentTransactionModelMock, AUTHORIZATION);

        assertTrue(result.contains(authorisedAndAcceptedAndPendingEntryMock));
        assertFalse(result.contains(authorisedAndAcceptedAndNotPendingEntryMock));
    }

    @Test
    public void shouldReturnNonPendingEntries() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndPendingEntryMock, authorisedAndAcceptedAndNotPendingEntryMock));

        final List<PaymentTransactionEntryModel> result = testObj.getNotPendingPaymentTransactionEntriesForType(paymentTransactionModelMock, AUTHORIZATION);

        assertFalse(result.contains(authorisedAndAcceptedAndPendingEntryMock));
        assertTrue(result.contains(authorisedAndAcceptedAndNotPendingEntryMock));
    }

    @Test
    public void shouldDoNothingIfRiskScoreIsNull() {
        when(paymentReplyMock.getRiskScore()).thenReturn(null);

        testObj.addRiskScore(paymentTransactionModelMock, paymentReplyMock);

        verify(paymentTransactionModelMock, never()).setRiskScore(any());
    }

    @Test
    public void shouldSaveRiskScoreOnTransaction() {
        when(paymentReplyMock.getRiskScore()).thenReturn(riskScoreMock);
        when(worldpayRiskScoreConverterMock.convert(riskScoreMock)).thenReturn(worldpayRiskScoreModelMock);

        testObj.addRiskScore(paymentTransactionModelMock, paymentReplyMock);

        verify(paymentTransactionModelMock).setRiskScore(worldpayRiskScoreModelMock);
        verify(modelServiceMock).save(paymentTransactionModelMock);
    }

    @Test
    public void shouldUpdateAavFields() {
        when(modelServiceMock.create(WorldpayAavResponseModel.class)).thenReturn(worldpayAavResponseModelMock);

        testObj.addAavFields(paymentTransactionEntryModelMock, paymentReplyMock);

        verify(worldpayAavResponsePopulatorMock).populate(paymentReplyMock, worldpayAavResponseModelMock);
        verify(modelServiceMock).save(paymentTransactionEntryModelMock);
    }

    @Test
    public void shouldUpdateTransactionEntryAmount() {
        final List<PaymentTransactionEntryModel> paymentTransactionEntries = Collections.singletonList(pendingCaptureEntryMock);

        testObj.updateEntriesAmount(paymentTransactionEntries, amountMock);

        verify(pendingCaptureEntryMock).setAmount(BigDecimal.TEN);
        verify(pendingCaptureEntryMock).setCurrency(currencyModelMock);
        verify(modelServiceMock).saveAll(paymentTransactionEntries);
    }

    @Test
    public void shouldLogWarningForDifferentAmountsUpdated() {
        final List<PaymentTransactionEntryModel> paymentTransactionEntries = Collections.singletonList(pendingCaptureEntryMock);
        when(pendingCaptureEntryMock.getAmount()).thenReturn(BigDecimal.TEN.add(BigDecimal.valueOf(50)));
        when(pendingCaptureEntryMock.getCode()).thenReturn(TRANSACTION_ENTRY_CODE);
        when(pendingCaptureEntryMock.getType()).thenReturn(CAPTURE);

        testObj.updateEntriesAmount(paymentTransactionEntries, amountMock);

        verify(testObj).logAmountChanged(pendingCaptureEntryMock, BigDecimal.TEN);
    }

    @Test
    public void shouldNotLogWarningWhenEntryAmountIsNull() {
        final List<PaymentTransactionEntryModel> paymentTransactionEntries = Collections.singletonList(pendingCaptureEntryMock);
        when(pendingCaptureEntryMock.getAmount()).thenReturn(null);

        testObj.updateEntriesAmount(paymentTransactionEntries, amountMock);

        verify(testObj, never()).logAmountChanged(any(PaymentTransactionEntryModel.class), any(BigDecimal.class));
    }

    @Test
    public void shouldCreateNonPendingSettledPaymentTransactionEntry() {
        when(paymentTransactionModelMock.getCode()).thenReturn(TRANSACTION_ENTRY_CODE);

        final PaymentTransactionEntryModel result = testObj.createNotPendingSettledPaymentTransactionEntry(paymentTransactionModelMock, orderNotificationMessageMock);

        verifyPaymentTransactionEntry(result, SETTLED, Boolean.FALSE);
    }

    @Test
    public void shouldReturnFalseWrongAuthorizedAmountDiffHigherThanTolerance() {
        when(orderModelMock.getTotalPrice()).thenReturn(50.02);
        boolean result = testObj.isAuthorisedAmountCorrect(orderModelMock);

        assertFalse(result);
    }

    @Test
    public void shouldReturnTrueWithCorrectAuthorisedAmountDiffLowerThanTolerance() {
        when(orderModelMock.getTotalPrice()).thenReturn(50.00);

        boolean result = testObj.isAuthorisedAmountCorrect(orderModelMock);

        assertTrue(result);
    }

    @Test
    public void shouldReturnTrueWithNoAmountOnPaymentTransactionEntry() {
        when(orderModelMock.getTotalPrice()).thenReturn(50.00);
        when(authorisedAndAcceptedAndNotPendingEntryMock.getAmount()).thenReturn(null);

        boolean result = testObj.isAuthorisedAmountCorrect(orderModelMock);

        assertTrue(result);
    }

    @Test
    public void shouldReturnTrueWithCorrectAuthorisedAmountDiffEqualToTolerance() {
        when(orderModelMock.getTotalPrice()).thenReturn(50.01);

        boolean result = testObj.isAuthorisedAmountCorrect(orderModelMock);

        assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWithZeroToleranceAndDiffAboveZero() {
        when(configurationMock.getDouble("worldpayapi.authoriseamount.validation.tolerance")).thenReturn(0.00);
        when(orderModelMock.getTotalPrice()).thenReturn(50.01);

        boolean result = testObj.isAuthorisedAmountCorrect(orderModelMock);

        assertFalse(result);
    }

    @Test
    public void shouldReturnTrueWithZeroToleranceAndZeroDiff() {
        when(configurationMock.getDouble("worldpayapi.authoriseamount.validation.tolerance")).thenReturn(0.00);
        when(orderModelMock.getTotalPrice()).thenReturn(50.00);

        boolean result = testObj.isAuthorisedAmountCorrect(orderModelMock);

        assertTrue(result);
    }

    @Test
    public void createNotPendingCancelOrderTransactionEntry_ShouldCreateCancelTransactionEntryWithPendingSetToFalse() {
        when(paymentTransactionModelMock.getOrder()).thenReturn(abstractOrderMock);
        when(abstractOrderMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(abstractOrderMock.getCurrency()).thenReturn(currencyModelMock);
        when(abstractOrderMock.getTotalPrice()).thenReturn(BigDecimal.TEN.doubleValue());
        when(paymentTransactionModelMock.getRequestToken()).thenReturn(REQUEST_TOKEN);
        when(modelServiceMock.create(PaymentTransactionEntryModel.class)).thenReturn(new PaymentTransactionEntryModel());

        final PaymentTransactionEntryModel result = testObj.createNotPendingCancelOrderTransactionEntry(paymentTransactionModelMock);

        assertThat(result.getCode()).isEqualTo(TRANSACTION_ENTRY_CODE);
        assertThat(result.getPaymentTransaction()).isEqualTo(paymentTransactionModelMock);
        assertThat(result.getTime()).isNotNull();
        assertThat(result.getRequestId()).isEqualTo(WORLDPAY_ORDER_CODE);
        assertThat(result.getAmount().doubleValue()).isEqualTo(BigDecimal.TEN.doubleValue());
        assertThat(result.getRequestToken()).isEqualTo(REQUEST_TOKEN);
        assertThat(result.getTransactionStatus()).isEqualTo(REJECTED.name());
        assertThat(result.getTransactionStatusDetails()).isEqualTo(PROCESSOR_DECLINE.name());
        assertThat(result.getCurrency()).isEqualTo(currencyModelMock);
        assertThat(result.getPending()).isFalse();
        assertThat(result.getType()).isEqualTo(CANCEL);
        verify(modelServiceMock).save(result);
    }

    protected void verifyPaymentTransactionEntry(final PaymentTransactionEntryModel result, final PaymentTransactionType transactionType, final Boolean pendingFlag) {
        verify(result).setType(transactionType);
        verify(result).setRequestId(REQUEST_ID);
        verify(result).setRequestToken(REQUEST_TOKEN);
        verify(result).setTransactionStatus(ACCEPTED.name());
        verify(result).setTransactionStatusDetails(SUCCESFULL.name());
        verify(result).setCode(TRANSACTION_ENTRY_CODE);
        verify(result).setAmount(BigDecimal.TEN);
        verify(result).setPending(pendingFlag);
        verify(modelServiceMock).save(result);
    }
}

package com.worldpay.transaction.impl;

import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import com.worldpay.core.services.WorldpayBankConfigurationLookupService;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.model.WorldpayAavResponseModel;
import com.worldpay.model.WorldpayBankConfigurationModel;
import com.worldpay.model.WorldpayRiskScoreModel;
import com.worldpay.data.Amount;
import com.worldpay.data.PaymentReply;
import com.worldpay.data.RiskScore;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.payment.WorldpayFraudSightStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionUtils;
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
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Spy
    @InjectMocks
    private DefaultWorldpayPaymentTransactionService testObj;

    @Mock
    private ModelService modelServiceMock;
    @Mock
    private WorldpayPaymentTransactionDao worldpayPaymentTransactionDaoMock;
    @Mock
    private Converter<RiskScore, WorldpayRiskScoreModel> worldpayRiskScoreConverterMock;
    @Mock
    private Populator<PaymentReply, WorldpayAavResponseModel> worldpayAavResponsePopulatorMock;
    @Mock
    private WorldpayPaymentTransactionUtils worldpayPaymentTransactionUtilsMock;
    @Mock
    private WorldpayFraudSightStrategy worldpayFraudSightStrategyMock;
    @Mock
    private WorldpayBankConfigurationLookupService worldpayBankConfigurationServiceMock;

    @Mock
    private PaymentTransactionEntryModel authorisedAndAcceptedAndNotPendingEntryMock, authorisedAndAcceptedAndPendingEntryMock, authorisedAndRejectedAndPendingEntryMock, pendingCaptureEntryMock, pendingCancelEntryMock;
    @Mock
    private PaymentTransactionEntryModel capturedEntryMock;
    @Mock
    private OrderModel orderModelMock;
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
    private PaymentTransactionModel notApmOpenPaymentTransactionModelMock, apmOpenPaymentTransactionModelMock, paymentTransactionModelMock;
    @Mock
    private RedirectAuthoriseResult redirectAuthoriseResultMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private RiskScore riskScoreMock;
    @Mock
    private WorldpayRiskScoreModel worldpayRiskScoreModelMock;
    @Mock
    private WorldpayAavResponseModel worldpayAavResponseModelMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private CommerceCheckoutParameter commerceCheckoutParameterMock;
    @Mock
    private BigDecimal bigDecimalMock;
    @Mock
    private WorldpayBankConfigurationModel worldpayBankConfigurationMock;
    @Mock
    private AbstractOrderModel abstractOrderMock;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(testObj, "worldpayRiskScoreConverter", worldpayRiskScoreConverterMock);
        when(authorisedAndAcceptedAndNotPendingEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(authorisedAndAcceptedAndNotPendingEntryMock.getTransactionStatus()).thenReturn(ACCEPTED.name());
        when(authorisedAndAcceptedAndNotPendingEntryMock.getPending()).thenReturn(Boolean.FALSE);

        when(authorisedAndAcceptedAndPendingEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(authorisedAndAcceptedAndPendingEntryMock.getTransactionStatus()).thenReturn(REJECTED.name());
        when(authorisedAndAcceptedAndPendingEntryMock.getPending()).thenReturn(Boolean.TRUE);

        when(authorisedAndRejectedAndPendingEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(authorisedAndRejectedAndPendingEntryMock.getPending()).thenReturn(Boolean.FALSE);

        when(capturedEntryMock.getType()).thenReturn(CAPTURE);
        when(capturedEntryMock.getTransactionStatus()).thenReturn(ACCEPTED.name());
        when(orderModelMock.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransactionModelMock));
        when(modelServiceMock.create(PaymentTransactionEntryModel.class)).thenReturn(authorisedAndAcceptedAndPendingEntryMock);
        when(modelServiceMock.create(PaymentTransactionModel.class)).thenReturn(paymentTransactionModelMock);

        when(notApmOpenPaymentTransactionModelMock.getApmOpen()).thenReturn(Boolean.FALSE);
        when(apmOpenPaymentTransactionModelMock.getApmOpen()).thenReturn(Boolean.TRUE);

        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);

        when(orderNotificationMessageMock.getPaymentReply().getAmount()).thenReturn(amountMock);
        when(orderNotificationMessageMock.getOrderCode()).thenReturn(REQUEST_ID);
        when(orderNotificationMessageMock.getMerchantCode()).thenReturn(REQUEST_TOKEN);


        when(commerceCheckoutParameterMock.getAuthorizationAmount()).thenReturn(BigDecimal.TEN);
        when(commerceCheckoutParameterMock.getCart()).thenReturn(cartModelMock);
        when(commerceCheckoutParameterMock.getPaymentInfo()).thenReturn(paymentInfoModelMock);
        when(commerceCheckoutParameterMock.getPaymentProvider()).thenReturn(PAYMENT_PROVIDER);
        when(cartModelMock.getCurrency()).thenReturn(currencyModelMock);

        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(authorisedAndAcceptedAndNotPendingEntryMock));
        when(authorisedAndAcceptedAndNotPendingEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(authorisedAndAcceptedAndNotPendingEntryMock.getAmount()).thenReturn(new BigDecimal(50));

        when(cartModelMock.getShopperBankCode()).thenReturn(BANK_CODE);
        when(worldpayBankConfigurationServiceMock.getBankConfigurationForBankCode(BANK_CODE)).thenReturn(worldpayBankConfigurationMock);

        when(worldpayPaymentTransactionUtilsMock.generatePaymentTransactionCode(paymentTransactionModelMock)).thenReturn(TRANSACTION_ENTRY_CODE);
        when(worldpayPaymentTransactionUtilsMock.convertAmount(amountMock)).thenReturn(BigDecimal.TEN);
        when(worldpayPaymentTransactionUtilsMock.getCurrencyFromAmount(amountMock)).thenReturn(currencyModelMock);
        when(worldpayPaymentTransactionUtilsMock.getAuthoriseAmountToleranceFromConfig()).thenReturn(0.01);
        when(worldpayPaymentTransactionUtilsMock.getPaymentTransactionDependency()).thenReturn(Map.of(
            CAPTURE, AUTHORIZATION,
            SETTLED, CAPTURE
        ));
    }

    @Test
    public void allPaymentTransactionTypesAcceptedForType_ShouldReturnTrue() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(authorisedAndAcceptedAndNotPendingEntryMock));

        final boolean result = testObj.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION);

        assertTrue(result);
    }

    @Test
    public void areAllPaymentTransactionsAcceptedForType_WhenAnyPaymentTransactionTypesOfWrongType_ShouldReturnFalse() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(capturedEntryMock));

        final boolean result = testObj.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION);

        assertFalse(result);
    }

    @Test
    public void areAllPaymentTransactionsAcceptedForType_WhenSomePaymentTransactionTypesAcceptedAndSomeNotAccepted_ShouldReturnFalse() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndNotPendingEntryMock, authorisedAndAcceptedAndPendingEntryMock));

        final boolean result = testObj.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION);

        assertFalse(result);
    }

    @Test
    public void isPaymentTransactionPending_WhenAllPaymentTransactionsPending_ShouldReturnTrue() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndNotPendingEntryMock, authorisedAndAcceptedAndPendingEntryMock));

        final boolean result = testObj.isPaymentTransactionPending(paymentTransactionModelMock, AUTHORIZATION);

        assertTrue(result);
    }

    @Test
    public void isPaymentTransactionPending_WhenAnyPaymentTransactionsNotPending_ShouldReturnTrue() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndNotPendingEntryMock, authorisedAndAcceptedAndPendingEntryMock));

        final boolean result = testObj.isPaymentTransactionPending(paymentTransactionModelMock, AUTHORIZATION);

        assertTrue(result);
    }

    @Test
    public void isPaymentTransactionPending_WhenAllPaymentTransactionsNotPending_ShouldReturnFalse() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndNotPendingEntryMock, authorisedAndRejectedAndPendingEntryMock));

        final boolean result = testObj.isPaymentTransactionPending(paymentTransactionModelMock, AUTHORIZATION);

        assertFalse(result);
    }

    @Test
    public void isPreviousTransactionCompleted_WhenDependingTransactionEntryIsNotPending_ShouldReturnTrue() {
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(authorisedAndAcceptedAndNotPendingEntryMock));

        final boolean result = testObj.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock);

        assertTrue(result);
    }

    @Test
    public void isPreviousTransactionCompleted_WhenDependingTransactionEntryIsPending_ShouldReturnTrue() {
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(authorisedAndAcceptedAndPendingEntryMock));

        final boolean result = testObj.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock);

        assertFalse(result);
    }

    @Test
    public void isPreviousTransactionCompleted_WhenNoEntriesInTheTransaction_ShouldReturnFalse() {
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.emptyList());

        final boolean result = testObj.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock);

        assertFalse(result);
    }

    @Test
    public void isPreviousTransactionCompleted_WhenNoEntriesOfDependingTypeInTheTransaction_ShouldReturnFalse() {
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(authorisedAndAcceptedAndNotPendingEntryMock));

        final boolean result = testObj.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, SETTLED, orderModelMock);

        assertFalse(result);
    }

    @Test
    public void isPreviousTransactionCompleted_WhenTransactionIsNonDependantOnAPreviousOne_ShouldReturnTrue() {
        final boolean result = testObj.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock);

        assertTrue(result);
    }

    @Test
    public void isPreviousTransactionCompleted_WhenTransactionRefundForVoidOrder_ShouldReturnTrue() {
        final boolean result = testObj.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock);

        assertTrue(result);
    }

    @Test
    public void createPendingAuthorisePaymentTransactionEntry_ShouldCreatePendingAuthorizationPaymentTransactionEntry() {
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
    public void createNonPendingAuthorisePaymentTransactionEntry_ShouldCreateNonPendingAuthorizationPaymentTransactionEntry() {
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
    public void createCapturedPaymentTransactionEntry_ShouldCreateCaptureEntry() {
        final PaymentTransactionEntryModel result = testObj.createCapturedPaymentTransactionEntry(paymentTransactionModelMock, orderNotificationMessageMock);

        verifyPaymentTransactionEntry(result, CAPTURE, false);
        verify(result).setCurrency(currencyModelMock);
    }

    @Test
    public void createRefundedPaymentTransactionEntry_ShouldCreateRefundTransactionEntry() {
        final PaymentTransactionEntryModel result = testObj.createRefundedPaymentTransactionEntry(paymentTransactionModelMock, orderNotificationMessageMock);

        verifyPaymentTransactionEntry(result, REFUND_FOLLOW_ON, false);
    }

    @Test
    public void getPaymentTransactionFromCode_WhenTransactionIsFound_ShouldReturnTransaction() {
        when(worldpayPaymentTransactionDaoMock.findPaymentTransactionByRequestId(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);

        testObj.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE);

        verify(worldpayPaymentTransactionDaoMock).findPaymentTransactionByRequestId(WORLDPAY_ORDER_CODE);
    }

    @Test
    public void getPaymentTransactionFromCode_WhenTransactionIsNotFound_ShouldReturnNull() {
        when(worldpayPaymentTransactionDaoMock.findPaymentTransactionByRequestId(WORLDPAY_ORDER_CODE)).thenThrow(new ModelNotFoundException(EXCEPTION_MESSAGE));

        final PaymentTransactionModel result = testObj.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE);

        assertNull(result);
        verify(worldpayPaymentTransactionDaoMock).findPaymentTransactionByRequestId(WORLDPAY_ORDER_CODE);
    }

    @Test
    public void createPaymentTransaction_ShouldCreatePaymentTransaction() {
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
    public void createPaymentTransaction_ShouldNotSetWorldpayBankIfNotACart() {
        when(commerceCheckoutParameterMock.getCart()).thenReturn(null);
        when(commerceCheckoutParameterMock.getOrder()).thenReturn(abstractOrderMock);

        testObj.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock);

        verify(paymentTransactionModelMock, never()).setWorldpayBank(any(WorldpayBankConfigurationModel.class));
    }

    @Test
    public void createPaymentTransaction_ShouldCreateTransactionWillNullBankCodeIfMissing() {
        testObj.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock);

        verify(paymentTransactionModelMock).setWorldpayBank(worldpayBankConfigurationMock);
        verify(modelServiceMock).save(paymentTransactionModelMock);
    }

    @Test
    public void updateEntriesStatus_ShouldUpdateTransactionStatus() {
        final List<PaymentTransactionEntryModel> paymentTransactionEntries = Collections.singletonList(authorisedAndAcceptedAndPendingEntryMock);

        testObj.updateEntriesStatus(paymentTransactionEntries, TRANSACTION_STATUS);

        verify(authorisedAndAcceptedAndPendingEntryMock).setTransactionStatus(TRANSACTION_STATUS);
        verify(authorisedAndAcceptedAndPendingEntryMock).setPending(false);
        verify(modelServiceMock).saveAll(paymentTransactionEntries);
    }

    @Test
    public void isAnyPaymentTransactionApmOpenForOrder_WhenAnyPaymentTransactionIsApmOpen_WhenReturnsTrue() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(asList(notApmOpenPaymentTransactionModelMock, apmOpenPaymentTransactionModelMock));

        final boolean result = testObj.isAnyPaymentTransactionApmOpenForOrder(orderModelMock);

        assertTrue(result);
    }

    @Test
    public void isAnyPaymentTransactionApmOpenForOrder_WhenAllPaymentTransactionAreNotApmOpen_ShouldReturnsFalse() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(Collections.singletonList(notApmOpenPaymentTransactionModelMock));

        final boolean result = testObj.isAnyPaymentTransactionApmOpenForOrder(orderModelMock);

        assertFalse(result);
    }

    @Test
    public void getPendingPaymentTransactionEntriesForType_ShouldReturnPendingEntries() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndPendingEntryMock, authorisedAndAcceptedAndNotPendingEntryMock));

        final List<PaymentTransactionEntryModel> result = testObj.getPendingPaymentTransactionEntriesForType(paymentTransactionModelMock, AUTHORIZATION);

        assertTrue(result.contains(authorisedAndAcceptedAndPendingEntryMock));
        assertFalse(result.contains(authorisedAndAcceptedAndNotPendingEntryMock));
    }

    @Test
    public void getNotPendingPaymentTransactionEntriesForType_ShouldReturnNonPendingEntries() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndPendingEntryMock, authorisedAndAcceptedAndNotPendingEntryMock));

        final List<PaymentTransactionEntryModel> result = testObj.getNotPendingPaymentTransactionEntriesForType(paymentTransactionModelMock, AUTHORIZATION);

        assertFalse(result.contains(authorisedAndAcceptedAndPendingEntryMock));
        assertTrue(result.contains(authorisedAndAcceptedAndNotPendingEntryMock));
    }

    @Test
    public void addRiskScore_WhenRiskScoreIsNull_ShouldDoNothing() {
        when(paymentReplyMock.getRiskScore()).thenReturn(null);

        testObj.addRiskScore(paymentTransactionModelMock, paymentReplyMock);

        verify(paymentTransactionModelMock, never()).setRiskScore(any());
    }

    @Test
    public void addRiskScore_ShouldSaveRiskScoreOnTransaction() {
        when(paymentReplyMock.getRiskScore()).thenReturn(riskScoreMock);
        when(worldpayRiskScoreConverterMock.convert(riskScoreMock)).thenReturn(worldpayRiskScoreModelMock);

        testObj.addRiskScore(paymentTransactionModelMock, paymentReplyMock);

        verify(paymentTransactionModelMock).setRiskScore(worldpayRiskScoreModelMock);
        verify(modelServiceMock).save(paymentTransactionModelMock);
    }

    @Test
    public void addAavFields_ShouldUpdateAavFields() {
        when(modelServiceMock.create(WorldpayAavResponseModel.class)).thenReturn(worldpayAavResponseModelMock);

        testObj.addAavFields(paymentTransactionEntryModelMock, paymentReplyMock);

        verify(worldpayAavResponsePopulatorMock).populate(paymentReplyMock, worldpayAavResponseModelMock);
        verify(modelServiceMock).save(paymentTransactionEntryModelMock);
    }

    @Test
    public void updateEntriesAmount_ShouldUpdateTransactionEntryAmount() {
        final List<PaymentTransactionEntryModel> paymentTransactionEntries = Collections.singletonList(pendingCaptureEntryMock);

        testObj.updateEntriesAmount(paymentTransactionEntries, amountMock);

        verify(pendingCaptureEntryMock).setAmount(BigDecimal.TEN);
        verify(pendingCaptureEntryMock).setCurrency(currencyModelMock);
        verify(modelServiceMock).saveAll(paymentTransactionEntries);
    }

    @Test
    public void updateEntriesAmount_ShouldLogWarningForDifferentAmountsUpdated() {
        final List<PaymentTransactionEntryModel> paymentTransactionEntries = Collections.singletonList(pendingCaptureEntryMock);
        when(pendingCaptureEntryMock.getAmount()).thenReturn(BigDecimal.TEN.add(BigDecimal.valueOf(50)));
        when(pendingCaptureEntryMock.getCode()).thenReturn(TRANSACTION_ENTRY_CODE);
        when(pendingCaptureEntryMock.getType()).thenReturn(CAPTURE);

        testObj.updateEntriesAmount(paymentTransactionEntries, amountMock);

        verify(testObj).logAmountChanged(pendingCaptureEntryMock, BigDecimal.TEN);
    }

    @Test
    public void updateEntriesAmount_WhenEntryAmountIsNull_ShouldNotLogWarning() {
        final List<PaymentTransactionEntryModel> paymentTransactionEntries = Collections.singletonList(pendingCaptureEntryMock);
        when(pendingCaptureEntryMock.getAmount()).thenReturn(null);

        testObj.updateEntriesAmount(paymentTransactionEntries, amountMock);

        verify(testObj, never()).logAmountChanged(any(PaymentTransactionEntryModel.class), any(BigDecimal.class));
    }

    @Test
    public void createNotPendingSettledPaymentTransactionEntry_ShouldCreateNonPendingSettledPaymentTransactionEntry() {
        final PaymentTransactionEntryModel result = testObj.createNotPendingSettledPaymentTransactionEntry(paymentTransactionModelMock, orderNotificationMessageMock);

        verifyPaymentTransactionEntry(result, SETTLED, Boolean.FALSE);
    }

    @Test
    public void isAuthorisedAmountCorrect_WhenWrongAuthorizedAmountDiffHigherThanTolerance_ShouldReturnFalse() {
        when(orderModelMock.getTotalPrice()).thenReturn(50.02);

        boolean result = testObj.isAuthorisedAmountCorrect(orderModelMock);

        assertFalse(result);
    }

    @Test
    public void isAuthorisedAmountCorrect_WhenCorrectAuthorisedAmountDiffLowerThanTolerance_ShouldReturnTrue() {
        when(orderModelMock.getTotalPrice()).thenReturn(50.00);

        boolean result = testObj.isAuthorisedAmountCorrect(orderModelMock);

        assertTrue(result);
    }

    @Test
    public void isAuthorisedAmountCorrect_WhenNoAmountOnPaymentTransactionEntry_ShouldReturnTrue() {
        when(authorisedAndAcceptedAndNotPendingEntryMock.getAmount()).thenReturn(null);

        boolean result = testObj.isAuthorisedAmountCorrect(orderModelMock);

        assertTrue(result);
    }

    @Test
    public void isAuthorisedAmountCorrect_WhenCorrectAuthorisedAmountDiffEqualToTolerance_ShouldReturnTrue() {
        when(orderModelMock.getTotalPrice()).thenReturn(50.01);

        boolean result = testObj.isAuthorisedAmountCorrect(orderModelMock);

        assertTrue(result);
    }

    @Test
    public void isAuthorisedAmountCorrect_WhenZeroToleranceAndDiffAboveZero_ShouldReturnFalse() {
        when(worldpayPaymentTransactionUtilsMock.getAuthoriseAmountToleranceFromConfig()).thenReturn(0.00);
        when(orderModelMock.getTotalPrice()).thenReturn(50.01);

        boolean result = testObj.isAuthorisedAmountCorrect(orderModelMock);

        assertFalse(result);
    }

    @Test
    public void isAuthorisedAmountCorrect_WhenZeroToleranceAndZeroDiff_ShouldReturnTrue() {
        when(worldpayPaymentTransactionUtilsMock.getAuthoriseAmountToleranceFromConfig()).thenReturn(0.00);
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
        when(modelServiceMock.create(PaymentTransactionEntryModel.class)).thenReturn(paymentTransactionEntryModelMock);

        final PaymentTransactionEntryModel result = testObj.createNotPendingCancelOrderTransactionEntry(paymentTransactionModelMock);

        assertThat(result).isEqualTo(paymentTransactionEntryModelMock);
        verify(paymentTransactionEntryModelMock).setCode(TRANSACTION_ENTRY_CODE);
        verify(paymentTransactionEntryModelMock).setPaymentTransaction(paymentTransactionModelMock);
        verify(paymentTransactionEntryModelMock).setTime(any(Date.class));
        verify(paymentTransactionEntryModelMock).setRequestId(WORLDPAY_ORDER_CODE);
        verify(paymentTransactionEntryModelMock).setAmount(BigDecimal.valueOf(BigDecimal.TEN.doubleValue()));
        verify(paymentTransactionEntryModelMock).setRequestToken(REQUEST_TOKEN);
        verify(paymentTransactionEntryModelMock).setTransactionStatus(REJECTED.name());
        verify(paymentTransactionEntryModelMock).setTransactionStatusDetails(PROCESSOR_DECLINE.name());
        verify(paymentTransactionEntryModelMock).setCurrency(currencyModelMock);
        verify(paymentTransactionEntryModelMock).setPending(false);
        verify(paymentTransactionEntryModelMock).setType(CANCEL);
        verify(modelServiceMock).save(result);
    }

    @Test
    public void addFraudSightToPaymentTransaction_WhenFraudSightEnabled_ShouldAddFraudSightToTransaction() {
        when(worldpayFraudSightStrategyMock.isFraudSightEnabled()).thenReturn(true);

        testObj.addFraudSightToPaymentTransaction(paymentTransactionModelMock, paymentReplyMock);

        verify(worldpayFraudSightStrategyMock).addFraudSight(paymentTransactionModelMock, paymentReplyMock);
    }

    @Test
    public void addFraudSightToPaymentTransaction_WhenFraudSightIsNotEnabled_ShouldNotAddFraudSightToTransaction() {
        testObj.addFraudSightToPaymentTransaction(paymentTransactionModelMock, paymentReplyMock);

        verify(worldpayFraudSightStrategyMock, never()).addFraudSight(paymentTransactionModelMock, paymentReplyMock);
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

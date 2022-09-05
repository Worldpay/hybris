package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.hop.WorldpayHOPPService;
import com.worldpay.data.Amount;
import com.worldpay.data.BasicOrderInfo;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayTokenEventReferenceCreationStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayRedirectOrderServiceTest {

    private static final double TOTAL_PRICE = 100D;
    private static final String GBP = "GBP";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final String ORDER_KEY = "orderKey";

    @Spy
    @InjectMocks
    private DefaultWorldpayRedirectOrderService testObj;

    @Mock
    private CartModel cartModelMock;
    @Mock
    private CustomerModel customerModelMock;
    @Mock
    private BasicOrderInfo basicOrderInfoMock;
    @Mock
    private RedirectAuthoriseResult redirectAuthoriseResultMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock
    private CommerceCheckoutParameter commerceCheckoutParameterMock;
    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;
    @Mock
    private WorldpayCartService worldpayCartServiceMock;
    @Mock
    private WorldpayTokenEventReferenceCreationStrategy worldpayTokenEventReferenceCreationStrategyMock;
    @Mock
    private BigDecimal bigDecimalMock;
    @Mock
    private CurrencyModel currencyModelMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private PaymentData paymentDataMock;
    @Mock
    private WorldpayHOPPService worldpayHOPPServiceMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionaInfoDataMock;

    @Before
    public void setUp() throws WorldpayException {
        when(worldpayPaymentInfoServiceMock.createPaymentInfo(cartModelMock)).thenReturn(paymentInfoModelMock);
        when(worldpayOrderServiceMock.createCheckoutParameterAndSetPaymentInfo(paymentInfoModelMock, bigDecimalMock, cartModelMock)).thenReturn(commerceCheckoutParameterMock);
        when(cartModelMock.getPaymentInfo()).thenReturn(paymentInfoModelMock);
    }

    @Test
    public void testCompleteRedirectAuthoriseAndSetSavedPaymentInfoToTrueWhenPaymentMethodIsCC() {
        setUpRedirectAuthoriseResultMock(AUTHORISED, true);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completePendingRedirectAuthorise(redirectAuthoriseResultMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(worldpayOrderServiceMock).createCheckoutParameterAndSetPaymentInfo(paymentInfoModelMock, bigDecimalMock, cartModelMock);
        verify(worldpayPaymentTransactionServiceMock).createPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, bigDecimalMock);
    }

    @Test
    public void testCompleteRedirectAuthoriseAndSetSavedPaymentInfoToFalseWhenPaymentMethodIsCC() {
        setUpRedirectAuthoriseResultMock(AUTHORISED, true);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completePendingRedirectAuthorise(redirectAuthoriseResultMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(worldpayOrderServiceMock).createCheckoutParameterAndSetPaymentInfo(paymentInfoModelMock, bigDecimalMock, cartModelMock);
        verify(worldpayPaymentTransactionServiceMock).createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock);
        verify(worldpayPaymentTransactionServiceMock).createPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, bigDecimalMock);
    }

    @Test
    public void testCompleteRedirectAuthoriseAndSetSavedPaymentInfoToTrueWhenPaymentMethodIsAPM() {
        when(cartModelMock.getPaymentInfo()).thenReturn(null);
        setUpRedirectAuthoriseResultMock(AUTHORISED, true);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completePendingRedirectAuthorise(redirectAuthoriseResultMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(worldpayOrderServiceMock).createCheckoutParameterAndSetPaymentInfo(paymentInfoModelMock, bigDecimalMock, cartModelMock);
        verify(worldpayPaymentTransactionServiceMock).createPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, bigDecimalMock);
    }

    @Test
    public void testCompleteRedirectAuthoriseAndSetSavedPaymentInfoToFalseWhenPaymentMethodIsAPM() {
        when(cartModelMock.getPaymentInfo()).thenReturn(null);
        setUpRedirectAuthoriseResultMock(AUTHORISED, true);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completePendingRedirectAuthorise(redirectAuthoriseResultMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(worldpayOrderServiceMock).createCheckoutParameterAndSetPaymentInfo(paymentInfoModelMock, bigDecimalMock, cartModelMock);
        verify(worldpayPaymentTransactionServiceMock).createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock);
        verify(worldpayPaymentTransactionServiceMock).createPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, bigDecimalMock);
    }

    @Test
    public void testCompleteRedirectAuthoriseWithNoPendingPaymentTransactionEntryAndSetSavedPaymentInfoToTrue() {
        setUpRedirectAuthoriseResultMock(AUTHORISED, false);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completeConfirmedRedirectAuthorise(bigDecimalMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(worldpayOrderServiceMock).createCheckoutParameterAndSetPaymentInfo(paymentInfoModelMock, bigDecimalMock, cartModelMock);
        verify(worldpayPaymentTransactionServiceMock).createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock);
        verify(worldpayPaymentTransactionServiceMock).createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, bigDecimalMock);
    }

    @Test
    public void testCompleteRedirectAuthoriseWithNoPendingPaymentTransactionEntryAndSetSavedPaymentInfoToFalse() {
        setUpRedirectAuthoriseResultMock(AUTHORISED, false);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completeConfirmedRedirectAuthorise(bigDecimalMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(worldpayOrderServiceMock).createCheckoutParameterAndSetPaymentInfo(paymentInfoModelMock, bigDecimalMock, cartModelMock);
        verify(worldpayPaymentTransactionServiceMock).createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock);
        verify(worldpayPaymentTransactionServiceMock).createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, bigDecimalMock);
    }

    @Test
    public void redirectAuthorise_shouldReturnWorldpayHOPServiceReturnedObject() throws WorldpayException {
        when(worldpayHOPPServiceMock.buildHOPPageData(cartModelMock, additionalAuthInfoMock, merchantInfoMock, worldpayAdditionaInfoDataMock)).thenReturn(paymentDataMock);

        final PaymentData paymentData = testObj.redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock, worldpayAdditionaInfoDataMock);

        assertThat(paymentData).isEqualTo(paymentDataMock);
    }

    private void setUpRedirectAuthoriseResultMock(final AuthorisedStatus paymentStatus, final boolean pending) {
        when(redirectAuthoriseResultMock.getPending()).thenReturn(pending);
        when(redirectAuthoriseResultMock.getPaymentAmount()).thenReturn(bigDecimalMock);
    }
}

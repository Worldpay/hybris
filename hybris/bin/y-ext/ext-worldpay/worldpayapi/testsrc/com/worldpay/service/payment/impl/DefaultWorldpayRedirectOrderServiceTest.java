package com.worldpay.service.payment.impl;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.MerchantInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.hop.WorldpayHOPPService;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayRedirectOrderServiceTest {

    private static final String MERCHANT_CODE = "merchantCode";

    @Spy
    @InjectMocks
    private DefaultWorldpayRedirectOrderService testObj;

    @Mock
    private CartModel cartModelMock;
    @Mock
    private RedirectAuthoriseResult redirectAuthoriseResultMock;
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
    private BigDecimal bigDecimalMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private PaymentData paymentDataMock;
    @Mock
    private WorldpayHOPPService worldpayHOPPServiceMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;

    @Before
    public void setUp() throws WorldpayException {
        when(worldpayPaymentInfoServiceMock.createPaymentInfo(cartModelMock)).thenReturn(paymentInfoModelMock);
        when(worldpayOrderServiceMock.createCheckoutParameterAndSetPaymentInfo(paymentInfoModelMock, bigDecimalMock, cartModelMock)).thenReturn(commerceCheckoutParameterMock);
        when(cartModelMock.getPaymentInfo()).thenReturn(paymentInfoModelMock);
    }

    @Test
    public void testCompleteRedirectAuthoriseAndSetSavedPaymentInfoToTrueWhenPaymentMethodIsCC() {
        setUpRedirectAuthoriseResultMock(true);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completePendingRedirectAuthorise(redirectAuthoriseResultMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(worldpayOrderServiceMock).createCheckoutParameterAndSetPaymentInfo(paymentInfoModelMock, bigDecimalMock, cartModelMock);
        verify(worldpayPaymentTransactionServiceMock).createPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, bigDecimalMock);
    }

    @Test
    public void testCompleteRedirectAuthoriseAndSetSavedPaymentInfoToFalseWhenPaymentMethodIsCC() {
        setUpRedirectAuthoriseResultMock(true);
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
        setUpRedirectAuthoriseResultMock(true);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completePendingRedirectAuthorise(redirectAuthoriseResultMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(worldpayOrderServiceMock).createCheckoutParameterAndSetPaymentInfo(paymentInfoModelMock, bigDecimalMock, cartModelMock);
        verify(worldpayPaymentTransactionServiceMock).createPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, bigDecimalMock);
    }

    @Test
    public void testCompleteRedirectAuthoriseAndSetSavedPaymentInfoToFalseWhenPaymentMethodIsAPM() {
        when(cartModelMock.getPaymentInfo()).thenReturn(null);
        setUpRedirectAuthoriseResultMock(true);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completePendingRedirectAuthorise(redirectAuthoriseResultMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(worldpayOrderServiceMock).createCheckoutParameterAndSetPaymentInfo(paymentInfoModelMock, bigDecimalMock, cartModelMock);
        verify(worldpayPaymentTransactionServiceMock).createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock);
        verify(worldpayPaymentTransactionServiceMock).createPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, bigDecimalMock);
    }

    @Test
    public void testCompleteRedirectAuthoriseWithNoPendingPaymentTransactionEntryAndSetSavedPaymentInfoToTrue() {
        setUpRedirectAuthoriseResultMock(false);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completeConfirmedRedirectAuthorise(bigDecimalMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(worldpayOrderServiceMock).createCheckoutParameterAndSetPaymentInfo(paymentInfoModelMock, bigDecimalMock, cartModelMock);
        verify(worldpayPaymentTransactionServiceMock).createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock);
        verify(worldpayPaymentTransactionServiceMock).createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, bigDecimalMock);
    }

    @Test
    public void testCompleteRedirectAuthoriseWithNoPendingPaymentTransactionEntryAndSetSavedPaymentInfoToFalse() {
        setUpRedirectAuthoriseResultMock(false);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completeConfirmedRedirectAuthorise(bigDecimalMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(worldpayOrderServiceMock).createCheckoutParameterAndSetPaymentInfo(paymentInfoModelMock, bigDecimalMock, cartModelMock);
        verify(worldpayPaymentTransactionServiceMock).createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock);
        verify(worldpayPaymentTransactionServiceMock).createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, bigDecimalMock);
    }

    @Test
    public void redirectAuthorise_shouldReturnWorldpayHOPServiceReturnedObject() throws WorldpayException {
        when(worldpayHOPPServiceMock.buildHOPPageData(cartModelMock, additionalAuthInfoMock, merchantInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(paymentDataMock);

        final PaymentData paymentData = testObj.redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        assertThat(paymentData).isEqualTo(paymentDataMock);
    }

    private void setUpRedirectAuthoriseResultMock(final boolean pending) {
        when(redirectAuthoriseResultMock.getPending()).thenReturn(pending);
        when(redirectAuthoriseResultMock.getPaymentAmount()).thenReturn(bigDecimalMock);
    }
}

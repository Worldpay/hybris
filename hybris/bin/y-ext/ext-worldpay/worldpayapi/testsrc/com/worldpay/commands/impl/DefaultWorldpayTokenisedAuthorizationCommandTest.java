package com.worldpay.commands.impl;

import com.worldpay.data.Additional3DS2Info;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.data.*;
import com.worldpay.data.token.Token;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.request.WorldpayRequestService;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import static de.hybris.platform.payment.dto.TransactionStatus.ERROR;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.GENERAL_SYSTEM_ERROR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayTokenisedAuthorizationCommandTest {

    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final Currency CURRENCY = Currency.getInstance(Locale.UK);
    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";
    private static final String CVV = "CVV";
    private static final String REQUEST_AMOUNT = "100";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String SESSION_ID = "sessionId";
    private static final String IP_ADDRESS = "192.168.0.1";
    private static final String SECURITY_CODE = "securityCode";
    private static final String CUSTOMER_EMAIL = "customerEmail";
    private static final String WORLDPAY_ORDER_CODE = "originalOrderCode";
    private static final String UNKNOWN_MERCHANT_CODE = "unknownMerchantCode";
    private static final String BASIC_ORDER_INFO_DESCRIPTION = "basicOrderInfoDescription";

    private DefaultWorldpayTokenisedAuthorizationCommand testObj;

    @Mock
    private Converter<BillingInfo, Address> worldpayBillingInfoAddressConverterMock;
    @Mock
    private Converter<DirectAuthoriseServiceResponse, AuthorizationResult> worldpayAuthorizationResultConverterMock;
    @Mock
    private WorldpayRequestService worldpayRequestServiceMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock
    private WorldpayServiceGateway worldpayGatewayMock;


    @Mock
    private SubscriptionAuthorizationRequest subscriptionAuthorizationRequestMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private DirectAuthoriseServiceResponse directAuthoriseServiceResponseMock;
    @Captor
    private ArgumentCaptor<DirectAuthoriseServiceRequest> directAuthoriseServiceRequestCaptor;
    @Mock
    private AuthorizationResult authorizationResultMock;
    @Mock
    private Amount amountMock;
    @Mock
    private BasicOrderInfo basicOrderInfoMock;
    @Mock
    private Shopper shopperMock;
    @Mock
    private Address addressMock;
    @Mock
    private Token tokenMock;
    @Mock
    private Additional3DS2Info additional3DS2InfoMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private BillingInfo billingInfoMock;

    @Before
    public void setUp() throws WorldpayException {
        final DefaultWorldpayTokenisedAuthorizationCommand defaultWorldpayTokenisedAuthorizationCommand = new DefaultWorldpayTokenisedAuthorizationCommand(worldpayBillingInfoAddressConverterMock, worldpayAuthorizationResultConverterMock, worldpayMerchantInfoServiceMock, worldpayPaymentTransactionServiceMock, worldpayOrderServiceMock, worldpayGatewayMock, worldpayRequestServiceMock);
        testObj = spy(defaultWorldpayTokenisedAuthorizationCommand);
        doReturn(worldpayAdditionalInfoDataMock).when(testObj).getWorldpayAdditionalInfoData(CVV);
        when(worldpayAuthorizationResultConverterMock.convert(directAuthoriseServiceResponseMock)).thenReturn(authorizationResultMock);
        mockSubscriptionAuthorizationRequest();
        mockAdditionalInfo();
        when(worldpayRequestServiceMock.createToken(PAYMENT_TOKEN_ID, SECURITY_CODE)).thenReturn(tokenMock);
        when(worldpayOrderServiceMock.createAmount(CURRENCY, subscriptionAuthorizationRequestMock.getTotalAmount().doubleValue())).thenReturn(amountMock);
        when(worldpayOrderServiceMock.createBasicOrderInfo(eq(WORLDPAY_ORDER_CODE), anyString(), eq(amountMock))).thenReturn(basicOrderInfoMock);
        when(worldpayRequestServiceMock.createAuthenticatedShopper(eq(CUSTOMER_EMAIL), eq(AUTHENTICATED_SHOPPER_ID), any(Session.class), any(Browser.class))).thenReturn(shopperMock);
        when(shopperMock.getShopperEmailAddress()).thenReturn(CUSTOMER_EMAIL);
        when(basicOrderInfoMock.getAmount()).thenReturn(amountMock);
        when(basicOrderInfoMock.getDescription()).thenReturn(BASIC_ORDER_INFO_DESCRIPTION);
        when(basicOrderInfoMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(subscriptionAuthorizationRequestMock.getShippingInfo()).thenReturn(billingInfoMock);
        when(worldpayBillingInfoAddressConverterMock.convert(billingInfoMock)).thenReturn(addressMock);
        when(worldpayAdditionalInfoDataMock.getAuthenticatedShopperId()).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(worldpayAdditionalInfoDataMock.getAdditional3DS2()).thenReturn(additional3DS2InfoMock);
        when(worldpayRequestServiceMock.getDynamicInteractionType(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.ECOMMERCE);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAmount()).thenReturn(amountMock);
    }

    @Test
    public void perform_shouldReturnAuthorizationResult_whenResponseIsAuthorisedUsingCurrentSiteMerchant() throws WorldpayException {
        when(worldpayGatewayMock.directAuthorise(directAuthoriseServiceRequestCaptor.capture())).thenReturn(directAuthoriseServiceResponseMock);
        when(authorizationResultMock.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED);
        when(worldpayOrderServiceMock.convertAmount(amountMock)).thenReturn(BigDecimal.valueOf(3.30));

        testObj.perform(subscriptionAuthorizationRequestMock);

        verify(worldpayGatewayMock).directAuthorise(directAuthoriseServiceRequestCaptor.capture());
        verify(worldpayAuthorizationResultConverterMock).convert(directAuthoriseServiceResponseMock);
        verify(authorizationResultMock).setMerchantTransactionCode(WORLDPAY_ORDER_CODE);
        verify(authorizationResultMock).setRequestId(WORLDPAY_ORDER_CODE);
        verify(authorizationResultMock).setRequestToken(MERCHANT_CODE);
        verify(authorizationResultMock).setCurrency(subscriptionAuthorizationRequestMock.getCurrency());
        verify(authorizationResultMock).setAuthorizationTime(any(Date.class));
        verify(authorizationResultMock).setTotalAmount(BigDecimal.valueOf(3.30));
        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant();
        checkDirectAuthoriseServiceRequestValues();
    }

    @Test
    public void performShouldReturnErrorAuthorizationDueToMisConfigurationAndShouldNeverTryToDirectAuthorise() throws WorldpayException {
        when(worldpayGatewayMock.directAuthorise(directAuthoriseServiceRequestCaptor.capture())).thenReturn(directAuthoriseServiceResponseMock);
        when(authorizationResultMock.getCurrency()).thenReturn(CURRENCY);
        when(authorizationResultMock.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED);
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant()).thenReturn(null);

        final AuthorizationResult result = testObj.perform(subscriptionAuthorizationRequestMock);

        verify(worldpayGatewayMock, never()).directAuthorise(directAuthoriseServiceRequestCaptor.capture());
        verify(worldpayAuthorizationResultConverterMock, never()).convert(directAuthoriseServiceResponseMock);

        assertEquals(ERROR, result.getTransactionStatus());
        assertEquals(GENERAL_SYSTEM_ERROR, result.getTransactionStatusDetails());
        assertEquals(WORLDPAY_ORDER_CODE, result.getMerchantTransactionCode());
        assertEquals(WORLDPAY_ORDER_CODE, result.getRequestId());
        assertEquals(UNKNOWN_MERCHANT_CODE, result.getRequestToken());
        assertEquals(new BigDecimal(REQUEST_AMOUNT), result.getTotalAmount());
        assertEquals(Currency.getInstance(Locale.UK), result.getCurrency());
    }

    @Test
    public void performShouldReturnAuthorizationResultWhenResponseIsNotErrorAndNotAuthorised() throws WorldpayException {
        when(worldpayGatewayMock.directAuthorise(directAuthoriseServiceRequestCaptor.capture())).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply().getAuthStatus()).thenReturn(AuthorisedStatus.REFUSED);
        when(authorizationResultMock.getCurrency()).thenReturn(CURRENCY);

        testObj.perform(subscriptionAuthorizationRequestMock);

        verify(worldpayGatewayMock).directAuthorise(directAuthoriseServiceRequestCaptor.capture());
        verify(worldpayAuthorizationResultConverterMock).convert(directAuthoriseServiceResponseMock);

        verify(authorizationResultMock).setMerchantTransactionCode(WORLDPAY_ORDER_CODE);
        verify(authorizationResultMock).setRequestId(WORLDPAY_ORDER_CODE);
        verify(authorizationResultMock).setRequestToken(MERCHANT_CODE);
        verify(authorizationResultMock).setCurrency(subscriptionAuthorizationRequestMock.getCurrency());
        verify(authorizationResultMock).setAuthorizationTime(any(Date.class));
        verify(authorizationResultMock).setTotalAmount((new BigDecimal(REQUEST_AMOUNT)));

        checkDirectAuthoriseServiceRequestValues();
    }

    @Test
    public void performShouldReturnErrorAuthorizationResultWhenResponseIsNull() throws WorldpayException {
        when(worldpayGatewayMock.directAuthorise(directAuthoriseServiceRequestCaptor.capture())).thenReturn(null);

        final AuthorizationResult result = testObj.perform(subscriptionAuthorizationRequestMock);

        verify(worldpayGatewayMock).directAuthorise(directAuthoriseServiceRequestCaptor.capture());
        checkDirectAuthoriseServiceRequestValues();

        assertEquals(ERROR, result.getTransactionStatus());
        assertEquals(GENERAL_SYSTEM_ERROR, result.getTransactionStatusDetails());
        assertEquals(WORLDPAY_ORDER_CODE, result.getMerchantTransactionCode());
        assertEquals(WORLDPAY_ORDER_CODE, result.getRequestId());
        assertEquals(MERCHANT_CODE, result.getRequestToken());
        assertEquals(new BigDecimal(REQUEST_AMOUNT), result.getTotalAmount());
        assertEquals(Currency.getInstance(Locale.UK), result.getCurrency());
    }

    private void mockSubscriptionAuthorizationRequest() {
        when(subscriptionAuthorizationRequestMock.getCv2()).thenReturn(CVV);
        when(subscriptionAuthorizationRequestMock.getCurrency()).thenReturn(CURRENCY);
        when(subscriptionAuthorizationRequestMock.getMerchantTransactionCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(subscriptionAuthorizationRequestMock.getTotalAmount()).thenReturn(new BigDecimal(REQUEST_AMOUNT));
        when(subscriptionAuthorizationRequestMock.getSubscriptionID()).thenReturn(PAYMENT_TOKEN_ID);
    }

    private void checkDirectAuthoriseServiceRequestValues() {
        final DirectAuthoriseServiceRequest directAuthoriseServiceRequest = directAuthoriseServiceRequestCaptor.getValue();
        // Not checking all the mocked fields for DirectAuthoriseServiceRequest
        final Order order = directAuthoriseServiceRequest.getOrder();
        assertEquals(addressMock, order.getShippingAddress());
        final Token token = (Token) order.getPaymentDetails().getPayment();
        assertEquals(tokenMock, token);
        final Shopper shopper = order.getShopper();
        assertNotNull(shopper);
        assertEquals(CUSTOMER_EMAIL, shopper.getShopperEmailAddress());
    }

    private void mockAdditionalInfo() {
        when(worldpayAdditionalInfoDataMock.getCustomerIPAddress()).thenReturn(IP_ADDRESS);
        when(worldpayAdditionalInfoDataMock.getSessionId()).thenReturn(SESSION_ID);
        when(worldpayAdditionalInfoDataMock.isSavedCardPayment()).thenReturn(Boolean.TRUE);
        when(worldpayAdditionalInfoDataMock.getSecurityCode()).thenReturn(SECURITY_CODE);
        when(worldpayAdditionalInfoDataMock.getCustomerEmail()).thenReturn(CUSTOMER_EMAIL);
    }
}

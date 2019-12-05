package com.worldpay.facades.payment.direct.impl;

import com.worldpay.config.merchant.ThreeDSFlexJsonWebTokenCredentials;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.*;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.service.model.*;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import com.worldpay.service.payment.WorldpayJsonWebTokenService;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static com.worldpay.enums.order.AuthorisedStatus.*;
import static com.worldpay.payment.TransactionStatus.AUTHENTICATION_REQUIRED;
import static com.worldpay.payment.TransactionStatus.AUTHORISED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayDirectOrderFacadeTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String ISSUER_URL = "issuerURL";
    private static final String PA_REQUEST = "paRequest";
    private static final String PA_RESPONSE = "paResponse";
    private static final String RETURN_CODE = "A12";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String REDIRECT_URL = "redirectUrl";
    private static final String KLARNA_CONTENT_ENCODED = "a2xhcm5hQ29udGVudA==";
    private static final String KLARNA_CONTENT_DECODED = "klarnaContent";
    private static final String CVC = "cvc";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String EVENT_ORIGIN_DOMAIN = "EVENT_ORIGIN_DOMAIN";
    private static final String JWT_VALUE = "JWT_VALUE";
    private static final String THREEDS_V1 = "1";
    private static final String THREEDS_V2 = "2";
    private static final String ISSUER_PAYLOAD = "issuerPayload";
    private static final String TRANSACTION_ID_3DS = "transactionId3Ds";
    private static final String SESSION_ID = "SESSION_ID";

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private DefaultWorldpayDirectOrderFacade testObj;

    @Mock
    private CSEAdditionalAuthInfo cseAdditionalAuthInfoMock;
    @Mock
    private BankTransferAdditionalAuthInfo bankTransferAdditionAuthInfoMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private WorldpayDirectOrderService worldpayDirectOrderServiceMock;
    @Mock
    private WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategyMock;
    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private AcceleratorCheckoutFacade acceleratorCheckoutFacadeMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Spy
    private DirectAuthoriseServiceResponse directAuthoriseServiceResponseMock, directAuthoriseServiceResponse3dSecureMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private ErrorDetail errorDetailMock;
    @Mock
    private Request3DInfo request3DInfoMock;
    @Mock
    private RedirectReference redirectReferenceMock;
    @Mock
    private UserModel userModelMock;
    @Mock
    private ApplePayAdditionalAuthInfo applePayAdditionalAuthInfoMock;
    @Mock
    private GooglePayAdditionalAuthInfo googlePayAdditionalAuthInfoMock;
    @Mock
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;
    @Mock
    private WorldpayMerchantConfigData merchantConfigDataMock;
    @Mock
    private WorldpayJsonWebTokenService worldpayJsonWebTokenServiceMock;
    @Mock
    private ThreeDSFlexJsonWebTokenCredentials threeDSJsonWebTokenCredentialsMock;

    @Before
    public void setUp() throws Exception {
        when(worldpayDirectOrderServiceMock.authoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayDirectOrderServiceMock.authorise(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayDirectOrderServiceMock.authorise3DSecure(merchantInfoMock, WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(worldpayDirectOrderServiceMock.authorise3DSecureAgain(merchantInfoMock, WORLDPAY_ORDER_CODE, SESSION_ID)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getUser()).thenReturn(userModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayAuthenticatedShopperIdStrategyMock.getAuthenticatedShopperId(userModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
    }

    @Test
    public void shouldReturnOrderDataWhenAuthorisedAndOrderPlaced() throws Exception {
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(Boolean.FALSE);
        when(worldpayAdditionalInfoDataMock.getSecurityCode()).thenReturn(CVC);

        final DirectResponseData result = testObj.authorise(worldpayAdditionalInfoDataMock);

        final InOrder inOrder = inOrder(acceleratorCheckoutFacadeMock, worldpayDirectOrderServiceMock);

        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant();
        inOrder.verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock, MERCHANT_CODE);
        inOrder.verify(acceleratorCheckoutFacadeMock).placeOrder();
        assertEquals(acceleratorCheckoutFacadeMock.placeOrder(), result.getOrderData());
        assertEquals(AUTHORISED, result.getTransactionStatus());
    }

    @Test
    public void authoriseShouldReturnResponseDataWithStatusRefusedAndOrderDataNull() throws Exception {
        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUSED);
        when(paymentReplyMock.getReturnCode()).thenReturn(RETURN_CODE);

        final DirectResponseData result = testObj.authorise(worldpayAdditionalInfoDataMock);

        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant();
        verify(worldpayDirectOrderServiceMock).authorise(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(any(DirectAuthoriseServiceResponse.class), any(CartModel.class), anyString());
        assertNull(result.getOrderData());
        assertEquals(RETURN_CODE, result.getReturnCode());
        assertEquals(TransactionStatus.REFUSED, result.getTransactionStatus());
    }

    @Test
    public void authoriseShouldNotCompleteOrderWhenPaymentReplyIsCancelled() throws Exception {
        when(paymentReplyMock.getAuthStatus()).thenReturn(CANCELLED);

        final DirectResponseData authorise = testObj.authorise(worldpayAdditionalInfoDataMock);

        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant();
        verify(worldpayDirectOrderServiceMock).authorise(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(any(DirectAuthoriseServiceResponse.class), any(CartModel.class), anyString());
        assertEquals(TransactionStatus.CANCELLED, authorise.getTransactionStatus());
    }

    @Test
    public void authoriseShouldRaiseIllegalStateExceptionWhenTryingToAuthoriseWithoutCart() throws WorldpayException, InvalidCartException {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Cannot authorize payment where there is no cart");
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.FALSE);

        testObj.authorise(worldpayAdditionalInfoDataMock);
    }

    @Test(expected = WorldpayConfigurationException.class)
    public void authoriseShouldRaiseWorldpayConfigurationExceptionWhenMerchantsAreNotProperlyConfigured() throws WorldpayException, InvalidCartException {
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant()).thenThrow(new WorldpayConfigurationException(EXCEPTION_MESSAGE));

        testObj.authorise(worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock, never()).authorise(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);
    }

    @Test(expected = WorldpayException.class)
    public void authoriseShouldRaiseExceptionWhenPaymentReplayIsNullAndErrorDetailIsNotNull() throws Exception {
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(null);

        testObj.authorise(worldpayAdditionalInfoDataMock);

        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant();
        verify(worldpayDirectOrderServiceMock).authorise(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(any(DirectAuthoriseServiceResponse.class), any(CartModel.class), anyString());
    }

    @Test
    public void shouldReturnDirectResponseDataWith3DSecureInformationWhenDirectAuthoriseServiceResponseContainsRequest3DSLegacyFlowInfo() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponseMock.getRequest3DInfo()).thenReturn(request3DInfoMock);
        when(request3DInfoMock.getIssuerUrl()).thenReturn(ISSUER_URL);
        when(request3DInfoMock.getPaRequest()).thenReturn(PA_REQUEST);

        final DirectResponseData result = testObj.authorise(worldpayAdditionalInfoDataMock);

        assertThat(result.getIssuerURL()).isEqualTo(ISSUER_URL);
        assertThat(result.getPaRequest()).isEqualTo(PA_REQUEST);
        assertThat(result.getMajor3DSVersion()).isNullOrEmpty();
        assertThat(result.getTransactionId3DS()).isNullOrEmpty();
        assertThat(result.getIssuerPayload()).isNullOrEmpty();
        assertThat(result.getTransactionStatus()).isEqualTo(AUTHENTICATION_REQUIRED);
    }

    @Test
    public void shouldReturnDirectResponseDataWith3DSecureInformationWhenDirectAuthoriseServiceResponseContainsRequest3DSV2Info() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponseMock.getRequest3DInfo()).thenReturn(request3DInfoMock);
        when(request3DInfoMock.getMajor3DSVersion()).thenReturn(THREEDS_V2);
        when(request3DInfoMock.getIssuerUrl()).thenReturn(ISSUER_URL);
        when(request3DInfoMock.getIssuerPayload()).thenReturn(ISSUER_PAYLOAD);
        when(request3DInfoMock.getTransactionId3DS()).thenReturn(TRANSACTION_ID_3DS);
        when(request3DInfoMock.getPaRequest()).thenReturn(PA_REQUEST);

        final DirectResponseData result = testObj.authorise(worldpayAdditionalInfoDataMock);

        assertEquals(ISSUER_URL, result.getIssuerURL());
        assertEquals(PA_REQUEST, result.getPaRequest());
        assertEquals(THREEDS_V2, result.getMajor3DSVersion());
        assertEquals(TRANSACTION_ID_3DS, result.getTransactionId3DS());
        assertEquals(AUTHENTICATION_REQUIRED, result.getTransactionStatus());
    }

    @Test
    public void shouldReturnDirectResponseDataWith3DSecureInformationWhenDirectAuthoriseServiceResponseContainsRequest3DSV1Info() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponseMock.getRequest3DInfo()).thenReturn(request3DInfoMock);
        when(request3DInfoMock.getMajor3DSVersion()).thenReturn(THREEDS_V1);
        when(request3DInfoMock.getIssuerUrl()).thenReturn(ISSUER_URL);
        when(request3DInfoMock.getIssuerPayload()).thenReturn(ISSUER_PAYLOAD);
        when(request3DInfoMock.getTransactionId3DS()).thenReturn(TRANSACTION_ID_3DS);
        when(request3DInfoMock.getPaRequest()).thenReturn(PA_REQUEST);

        final DirectResponseData result = testObj.authorise(worldpayAdditionalInfoDataMock);

        assertEquals(ISSUER_URL, result.getIssuerURL());
        assertEquals(PA_REQUEST, result.getPaRequest());
        assertEquals(THREEDS_V1, result.getMajor3DSVersion());
        assertEquals(TRANSACTION_ID_3DS, result.getTransactionId3DS());
        assertEquals(AUTHENTICATION_REQUIRED, result.getTransactionStatus());
    }

    @Test
    public void shouldReturnDirectResponseDataWithOrderDataWhenAuthorise3DSecureIsAuthorised() throws WorldpayException, InvalidCartException {
        final DirectResponseData result = testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        final InOrder inOrder = inOrder(acceleratorCheckoutFacadeMock, worldpayDirectOrderServiceMock);
        inOrder.verify(worldpayDirectOrderServiceMock).authorise3DSecure(merchantInfoMock, WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        inOrder.verify(worldpayDirectOrderServiceMock).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock, merchantInfoMock);
        inOrder.verify(acceleratorCheckoutFacadeMock).placeOrder();
        assertEquals(AUTHORISED, result.getTransactionStatus());
        assertEquals(acceleratorCheckoutFacadeMock.placeOrder(), result.getOrderData());
    }

    @Test
    public void shouldReturnDirectResponseDataWithRefusedStatusWhenAuthorise3DSecureIsRefused() throws WorldpayException, InvalidCartException {
        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUSED);
        when(paymentReplyMock.getReturnCode()).thenReturn(RETURN_CODE);

        final DirectResponseData result = testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).authorise3DSecure(merchantInfoMock, WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        assertEquals(RETURN_CODE, result.getReturnCode());
        assertEquals(TransactionStatus.REFUSED, result.getTransactionStatus());
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock, merchantInfoMock);
    }

    @Test(expected = WorldpayException.class)
    public void authorise3DSecureShouldRaiseExceptionWhen3dAuthStatusInvalid() throws WorldpayException, InvalidCartException {
        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUNDED);

        testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).authorise3DSecure(merchantInfoMock, WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock, merchantInfoMock);
    }

    @Test(expected = WorldpayException.class)
    public void authorise3DSecureShouldRaiseExceptionWhen3dAuthError() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponse3dSecureMock.get3DSecureFlow()).thenReturn(Optional.empty());
        when(directAuthoriseServiceResponse3dSecureMock.getErrorDetail()).thenReturn(null);
        when(paymentReplyMock.getAuthStatus()).thenReturn(FAILURE);

        testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).authorise3DSecure(merchantInfoMock, WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock, merchantInfoMock);
    }

    @Test(expected = WorldpayException.class)
    public void authorise3DSecureShouldRaiseExceptionWhenPaymentReplyIsNull() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponse3dSecureMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).authorise3DSecure(merchantInfoMock, WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock, merchantInfoMock);
    }

    @Test
    public void testExecuteSecondPaymentAuthorisation3DSecureAuthorised() throws WorldpayException, InvalidCartException {
        final DirectResponseData result = testObj.executeSecondPaymentAuthorisation3DSecure(SESSION_ID);

        final InOrder inOrder = inOrder(acceleratorCheckoutFacadeMock, worldpayDirectOrderServiceMock);
        inOrder.verify(worldpayDirectOrderServiceMock).authorise3DSecureAgain(merchantInfoMock, WORLDPAY_ORDER_CODE, SESSION_ID);
        inOrder.verify(worldpayDirectOrderServiceMock).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock, merchantInfoMock);
        inOrder.verify(acceleratorCheckoutFacadeMock).placeOrder();
        assertEquals(AUTHORISED, result.getTransactionStatus());
        assertEquals(acceleratorCheckoutFacadeMock.placeOrder(), result.getOrderData());
    }

    @Test
    public void testExecuteSecondPaymentAuthorisation3DSecureRefused() throws WorldpayException, InvalidCartException {
        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUSED);
        when(paymentReplyMock.getReturnCode()).thenReturn(RETURN_CODE);

        final DirectResponseData result = testObj.executeSecondPaymentAuthorisation3DSecure(SESSION_ID);

        verify(worldpayDirectOrderServiceMock).authorise3DSecureAgain(merchantInfoMock, WORLDPAY_ORDER_CODE, SESSION_ID);
        assertEquals(RETURN_CODE, result.getReturnCode());
        assertEquals(TransactionStatus.REFUSED, result.getTransactionStatus());
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock, merchantInfoMock);
    }

    @Test(expected = WorldpayException.class)
    public void testExecuteSecondPaymentAuthorisation3DSecureStatusInvalid() throws WorldpayException, InvalidCartException {
        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUNDED);

        testObj.executeSecondPaymentAuthorisation3DSecure(SESSION_ID);

        verify(worldpayDirectOrderServiceMock).authorise3DSecureAgain(merchantInfoMock, WORLDPAY_ORDER_CODE, SESSION_ID);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock, merchantInfoMock);
    }

    @Test(expected = WorldpayException.class)
    public void testExecuteSecondPaymentAuthorisation3DSecureWhenPaymentReplyIsNull() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponse3dSecureMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        testObj.executeSecondPaymentAuthorisation3DSecure(SESSION_ID);

        verify(worldpayDirectOrderServiceMock).authorise3DSecureAgain(merchantInfoMock, WORLDPAY_ORDER_CODE, SESSION_ID);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock, merchantInfoMock);
    }

    @Test(expected = WorldpayException.class)
    public void testExecuteSecondPaymentAuthorisation3DSecureWhen3dAuthError() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponse3dSecureMock.get3DSecureFlow()).thenReturn(Optional.empty());
        when(directAuthoriseServiceResponse3dSecureMock.getErrorDetail()).thenReturn(null);
        when(paymentReplyMock.getAuthStatus()).thenReturn(FAILURE);

        testObj.executeSecondPaymentAuthorisation3DSecure(SESSION_ID);

        verify(worldpayDirectOrderServiceMock).authorise3DSecureAgain(merchantInfoMock, WORLDPAY_ORDER_CODE, SESSION_ID);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock, merchantInfoMock);
    }

    @Test(expected = WorldpayException.class)
    public void testExecuteSecondPaymentAuthorisation3DSecureWhenNoSessionCart() throws WorldpayException, InvalidCartException {
        when(cartServiceMock.hasSessionCart()).thenReturn(false);

        testObj.executeSecondPaymentAuthorisation3DSecure(SESSION_ID);

        verify(worldpayDirectOrderServiceMock, never()).authorise3DSecureAgain(merchantInfoMock, WORLDPAY_ORDER_CODE, SESSION_ID);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock, merchantInfoMock);
    }

    @Test
    public void authoriseShouldGenerateAuthenticatedShopperId() throws InvalidCartException, WorldpayException {

        testObj.authorise(worldpayAdditionalInfoDataMock);

        verify(worldpayAdditionalInfoDataMock).setAuthenticatedShopperId(AUTHENTICATED_SHOPPER_ID);
    }

    @Test
    public void authorise3dSecureShouldGenerateAuthenticatedShopperId() throws InvalidCartException, WorldpayException {

        testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        verify(worldpayAdditionalInfoDataMock).setAuthenticatedShopperId(AUTHENTICATED_SHOPPER_ID);
    }

    @Test
    public void shouldReturnBankURLWhenAuthorisedAndOrderPlacedWithBankTransfer() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authoriseBankTransfer(merchantInfoMock, cartModelMock, bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getRedirectReference()).thenReturn(redirectReferenceMock);
        when(redirectReferenceMock.getValue()).thenReturn(REDIRECT_URL);

        final String result = testObj.authoriseBankTransferRedirect(bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant();

        assertEquals(REDIRECT_URL, result);
    }

    @Test(expected = WorldpayException.class)
    public void shouldRaiseExceptionWhenErrorOccurredAuthorisingRedirectWithBankTransfer() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authoriseBankTransfer(merchantInfoMock, cartModelMock, bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        testObj.authoriseBankTransferRedirect(bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant();
    }

    @Test(expected = WorldpayException.class)
    public void shouldRaiseExceptionWhenNoErrorNoRedirectReferenceWasReturnedOccurredAuthorisingRedirectWithBankTransfer() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authoriseBankTransfer(merchantInfoMock, cartModelMock, bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(null);

        testObj.authoriseBankTransferRedirect(bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant();
    }

    @Test
    public void shouldAuthoriseRecurringPayment() throws WorldpayException, InvalidCartException {

        final DirectResponseData result = testObj.authoriseRecurringPayment(worldpayAdditionalInfoDataMock);

        verify(worldpayAdditionalInfoDataMock).setAuthenticatedShopperId(AUTHENTICATED_SHOPPER_ID);
        verify(worldpayDirectOrderServiceMock).authoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        final InOrder inOrder = inOrder(acceleratorCheckoutFacadeMock, worldpayDirectOrderServiceMock);
        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant();
        verify(worldpayDirectOrderServiceMock).authoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);
        inOrder.verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock, MERCHANT_CODE);
        inOrder.verify(acceleratorCheckoutFacadeMock).placeOrder();
        assertEquals(acceleratorCheckoutFacadeMock.placeOrder(), result.getOrderData());
        assertEquals(AUTHORISED, result.getTransactionStatus());
    }

    @Test
    public void shouldRedirectTo3DSecureV1OnRecurringPayment() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(null);
        when(request3DInfoMock.getMajor3DSVersion()).thenReturn(THREEDS_V1);
        when(directAuthoriseServiceResponseMock.getRequest3DInfo()).thenReturn(request3DInfoMock);
        when(request3DInfoMock.getIssuerUrl()).thenReturn(ISSUER_URL);
        when(request3DInfoMock.getPaRequest()).thenReturn(PA_REQUEST);

        final DirectResponseData result = testObj.authoriseRecurringPayment(worldpayAdditionalInfoDataMock);

        verify(worldpayAdditionalInfoDataMock).setAuthenticatedShopperId(AUTHENTICATED_SHOPPER_ID);
        assertEquals(ISSUER_URL, result.getIssuerURL());
        assertEquals(PA_REQUEST, result.getPaRequest());
        assertEquals(AUTHENTICATION_REQUIRED, result.getTransactionStatus());
    }

    @Test
    public void shouldReturnKlarnaURLWhenAuthorisedAndOrderPlacedWithKlarna() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authoriseKlarna(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getRedirectReference()).thenReturn(redirectReferenceMock);
        when(redirectReferenceMock.getValue()).thenReturn(KLARNA_CONTENT_ENCODED);

        final String result = testObj.authoriseKlarnaRedirect(worldpayAdditionalInfoDataMock, additionalAuthInfoMock);

        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant();

        assertEquals(KLARNA_CONTENT_DECODED, result);
    }

    @Test(expected = WorldpayException.class)
    public void shouldRaiseExceptionWhenErrorOccurredAuthorisingRedirectWithKlarna() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authoriseKlarna(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        testObj.authoriseKlarnaRedirect(worldpayAdditionalInfoDataMock, additionalAuthInfoMock);

        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant();
    }

    @Test(expected = WorldpayException.class)
    public void shouldRaiseExceptionWhenNoErrorNoRedirectReferenceWasReturnedOccurredAuthorisingRedirectWithKlarna() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authoriseKlarna(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(null);

        testObj.authoriseKlarnaRedirect(worldpayAdditionalInfoDataMock, additionalAuthInfoMock);

        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant();
    }

    @Test
    public void shouldAuthoriseGooglePayRequest() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authoriseGooglePay(merchantInfoMock, cartModelMock, googlePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectResponseData result = testObj.authoriseGooglePayDirect(googlePayAdditionalAuthInfoMock);

        verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock, MERCHANT_CODE);
        assertThat(result.getTransactionStatus()).isEqualTo(AUTHORISED);
    }

    @Test
    public void shouldCreateGooglePayPaymentInfoWhenTransactionIsAuthorised() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authoriseGooglePay(merchantInfoMock, cartModelMock, googlePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply().getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);

        testObj.authoriseGooglePayDirect(googlePayAdditionalAuthInfoMock);

        verify(worldpayPaymentInfoServiceMock).createPaymentInfoGooglePay(cartModelMock, googlePayAdditionalAuthInfoMock);
    }

    @Test
    public void shouldNotCreateGooglePayPaymentInfoWhenTransactionIsNotAuthorised() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authoriseGooglePay(merchantInfoMock, cartModelMock, googlePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply().getAuthStatus()).thenReturn(REFUSED);

        testObj.authoriseGooglePayDirect(googlePayAdditionalAuthInfoMock);

        verify(worldpayPaymentInfoServiceMock, never()).createPaymentInfoGooglePay(any(CartModel.class), any(GooglePayAdditionalAuthInfo.class));
    }

    @Test(expected = WorldpayConfigurationException.class)
    public void authoriseGooglePayDirectShouldRaiseWorldpayConfigurationExceptionWhenMerchantsAreNotConfigured() throws WorldpayException, InvalidCartException {
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant()).thenThrow(new WorldpayConfigurationException(EXCEPTION_MESSAGE));

        testObj.authoriseGooglePayDirect(googlePayAdditionalAuthInfoMock);

        verify(worldpayDirectOrderServiceMock, never()).authorise(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);
    }

    @Test
    public void shouldAuthoriseApplePayRequest() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authoriseApplePay(merchantInfoMock, cartModelMock, applePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectResponseData result = testObj.authoriseApplePayDirect(applePayAdditionalAuthInfoMock);

        verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock, MERCHANT_CODE);
        assertThat(result.getTransactionStatus()).isEqualTo(AUTHORISED);
    }

    @Test
    public void shouldCreateApplePayPaymentInfoWhenTransactionIsAuthorised() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authoriseApplePay(merchantInfoMock, cartModelMock, applePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply().getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);

        testObj.authoriseApplePayDirect(applePayAdditionalAuthInfoMock);

        verify(worldpayPaymentInfoServiceMock).createPaymentInfoApplePay(cartModelMock, applePayAdditionalAuthInfoMock);
    }

    @Test
    public void shouldNotCreateApplePayPaymentInfoWhenTransactionIsNotAuthorised() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authoriseApplePay(merchantInfoMock, cartModelMock, applePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply().getAuthStatus()).thenReturn(REFUSED);

        testObj.authoriseApplePayDirect(applePayAdditionalAuthInfoMock);

        verify(worldpayPaymentInfoServiceMock, never()).createPaymentInfoApplePay(any(CartModel.class), any(ApplePayAdditionalAuthInfo.class));
    }

    @Test(expected = WorldpayConfigurationException.class)
    public void authoriseAppleDirectShouldRaiseWorldpayConfigurationExceptionWhenMerchantsAreNotConfigured() throws WorldpayException, InvalidCartException {
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant()).thenThrow(new WorldpayConfigurationException(EXCEPTION_MESSAGE));

        testObj.authoriseApplePayDirect(applePayAdditionalAuthInfoMock);

        verify(worldpayDirectOrderServiceMock, never()).authorise(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);
    }

    @Test
    public void createJsonWebTokenForDDCShouldReturnAValidJsonWebToken() {
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(merchantConfigDataMock);
        when(worldpayJsonWebTokenServiceMock.createJsonWebTokenForDDC(merchantConfigDataMock)).thenReturn(JWT_VALUE);

        final String result = testObj.createJsonWebTokenForDDC();

        assertThat(result).isEqualTo(JWT_VALUE);
    }

    @Test
    public void getOriginEventDomain() {
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(merchantConfigDataMock);
        when(merchantConfigDataMock.getThreeDSFlexJsonWebTokenSettings()).thenReturn(threeDSJsonWebTokenCredentialsMock);
        when(threeDSJsonWebTokenCredentialsMock.getEventOriginDomain()).thenReturn(EVENT_ORIGIN_DOMAIN);

        final String result = testObj.getEventOriginDomainForDDC();

        assertThat(result).isEqualTo(EVENT_ORIGIN_DOMAIN);
    }
}

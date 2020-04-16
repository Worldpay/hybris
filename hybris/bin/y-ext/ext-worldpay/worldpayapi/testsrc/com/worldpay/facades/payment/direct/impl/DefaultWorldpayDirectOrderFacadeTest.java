package com.worldpay.facades.payment.direct.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.*;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.service.model.ErrorDetail;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.RedirectReference;
import com.worldpay.service.model.Request3DInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
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
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayDirectOrderFacadeTest {

    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String ISSUER_URL = "issuerURL";
    private static final String PA_REQUEST = "paRequest";
    private static final String PA_RESPONSE = "paResponse";
    private static final String RETURN_CODE = "A12";
    private static final String REDIRECT_URL = "redirectUrl";
    private static final String KLARNA_CONTENT_ENCODED = "a2xhcm5hQ29udGVudA==";
    private static final String KLARNA_CONTENT_DECODED = "klarnaContent";
    private static final String CVC = "cvc";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String THREEDS_V1 = "1";
    private static final String THREEDS_V2 = "2";
    private static final String ISSUER_PAYLOAD = "issuerPayload";
    private static final String TRANSACTION_ID_3DS = "transactionId3Ds";

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
    private AcceleratorCheckoutFacade acceleratorCheckoutFacadeMock;
    @Mock
    private CartModel cartModelMock;
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
    private OrderData orderDataMock;

    @Before
    public void setUp() throws Exception {
        when(worldpayDirectOrderServiceMock.authoriseRecurringPayment(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayDirectOrderServiceMock.authorise(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayDirectOrderServiceMock.authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(worldpayDirectOrderServiceMock.authorise3DSecureAgain(WORLDPAY_ORDER_CODE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getUser()).thenReturn(userModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayAuthenticatedShopperIdStrategyMock.getAuthenticatedShopperId(userModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(worldpayDirectOrderServiceMock.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
    }

    @Test
    public void shouldReturnOrderDataWhenAuthorisedAndOrderPlaced() throws Exception {
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(Boolean.FALSE);
        when(worldpayAdditionalInfoDataMock.getSecurityCode()).thenReturn(CVC);

        final DirectResponseData result = testObj.authorise(worldpayAdditionalInfoDataMock);

        final InOrder inOrder = inOrder(acceleratorCheckoutFacadeMock, worldpayDirectOrderServiceMock);

        inOrder.verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);
        inOrder.verify(acceleratorCheckoutFacadeMock).placeOrder();
        assertEquals(acceleratorCheckoutFacadeMock.placeOrder(), result.getOrderData());
        assertEquals(AUTHORISED, result.getTransactionStatus());
    }

    @Test
    public void authoriseShouldReturnResponseDataWithStatusRefusedAndOrderDataNull() throws Exception {
        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUSED);
        when(paymentReplyMock.getReturnCode()).thenReturn(RETURN_CODE);

        final DirectResponseData result = testObj.authorise(worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).authorise(cartModelMock, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(any(DirectAuthoriseServiceResponse.class), any(CartModel.class));
        assertNull(result.getOrderData());
        assertEquals(RETURN_CODE, result.getReturnCode());
        assertEquals(TransactionStatus.REFUSED, result.getTransactionStatus());
    }

    @Test
    public void authoriseShouldNotCompleteOrderWhenPaymentReplyIsCancelled() throws Exception {
        when(paymentReplyMock.getAuthStatus()).thenReturn(CANCELLED);

        final DirectResponseData authorise = testObj.authorise(worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).authorise(cartModelMock, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(any(DirectAuthoriseServiceResponse.class), any(CartModel.class));
        assertEquals(TransactionStatus.CANCELLED, authorise.getTransactionStatus());
    }

    @Test
    public void authoriseShouldRaiseIllegalStateExceptionWhenTryingToAuthoriseWithoutCart() throws WorldpayException, InvalidCartException {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Cannot authorize payment where there is no cart");
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.FALSE);

        testObj.authorise(worldpayAdditionalInfoDataMock);
    }

    @Test(expected = WorldpayException.class)
    public void authoriseShouldRaiseExceptionWhenPaymentReplayIsNullAndErrorDetailIsNotNull() throws Exception {
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(null);

        testObj.authorise(worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).authorise(cartModelMock, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(any(DirectAuthoriseServiceResponse.class), any(CartModel.class));
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
        inOrder.verify(worldpayDirectOrderServiceMock).authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        inOrder.verify(worldpayDirectOrderServiceMock).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock);
        inOrder.verify(acceleratorCheckoutFacadeMock).placeOrder();
        assertEquals(AUTHORISED, result.getTransactionStatus());
        assertEquals(acceleratorCheckoutFacadeMock.placeOrder(), result.getOrderData());
    }

    @Test
    public void shouldReturnDirectResponseDataWithRefusedStatusWhenAuthorise3DSecureIsRefused() throws WorldpayException, InvalidCartException {
        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUSED);
        when(paymentReplyMock.getReturnCode()).thenReturn(RETURN_CODE);

        final DirectResponseData result = testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock);
        assertEquals(RETURN_CODE, result.getReturnCode());
        assertEquals(TransactionStatus.REFUSED, result.getTransactionStatus());
    }

    @Test(expected = WorldpayException.class)
    public void authorise3DSecureShouldRaiseExceptionWhen3dAuthStatusInvalid() throws WorldpayException, InvalidCartException {
        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUNDED);

        testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock);
    }

    @Test(expected = WorldpayException.class)
    public void authorise3DSecureShouldRaiseExceptionWhen3dAuthError() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponse3dSecureMock.get3DSecureFlow()).thenReturn(Optional.empty());
        when(directAuthoriseServiceResponse3dSecureMock.getErrorDetail()).thenReturn(null);
        when(paymentReplyMock.getAuthStatus()).thenReturn(FAILURE);

        testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock);
    }

    @Test(expected = WorldpayException.class)
    public void authorise3DSecureShouldRaiseExceptionWhenPaymentReplyIsNull() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponse3dSecureMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock);
    }

    @Test
    public void testExecuteSecondPaymentAuthorisation3DSecureAuthorised() throws WorldpayException, InvalidCartException {
        final DirectResponseData result = testObj.executeSecondPaymentAuthorisation3DSecure();

        final InOrder inOrder = inOrder(acceleratorCheckoutFacadeMock, worldpayDirectOrderServiceMock);
        inOrder.verify(worldpayDirectOrderServiceMock).authorise3DSecureAgain(WORLDPAY_ORDER_CODE);
        inOrder.verify(worldpayDirectOrderServiceMock).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock);
        inOrder.verify(acceleratorCheckoutFacadeMock).placeOrder();
        assertEquals(AUTHORISED, result.getTransactionStatus());
        assertEquals(acceleratorCheckoutFacadeMock.placeOrder(), result.getOrderData());
    }

    @Test
    public void testExecuteSecondPaymentAuthorisation3DSecureRefused() throws WorldpayException, InvalidCartException {
        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUSED);
        when(paymentReplyMock.getReturnCode()).thenReturn(RETURN_CODE);

        final DirectResponseData result = testObj.executeSecondPaymentAuthorisation3DSecure();

        verify(worldpayDirectOrderServiceMock).authorise3DSecureAgain(WORLDPAY_ORDER_CODE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock);
        assertEquals(RETURN_CODE, result.getReturnCode());
        assertEquals(TransactionStatus.REFUSED, result.getTransactionStatus());
    }

    @Test(expected = WorldpayException.class)
    public void testExecuteSecondPaymentAuthorisation3DSecureStatusInvalid() throws WorldpayException, InvalidCartException {
        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUNDED);

        testObj.executeSecondPaymentAuthorisation3DSecure();

        verify(worldpayDirectOrderServiceMock).authorise3DSecureAgain(WORLDPAY_ORDER_CODE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock);
    }

    @Test(expected = WorldpayException.class)
    public void testExecuteSecondPaymentAuthorisation3DSecureWhenPaymentReplyIsNull() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponse3dSecureMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        testObj.executeSecondPaymentAuthorisation3DSecure();

        verify(worldpayDirectOrderServiceMock).authorise3DSecureAgain(WORLDPAY_ORDER_CODE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock);
    }

    @Test(expected = WorldpayException.class)
    public void testExecuteSecondPaymentAuthorisation3DSecureWhen3dAuthError() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponse3dSecureMock.get3DSecureFlow()).thenReturn(Optional.empty());
        when(directAuthoriseServiceResponse3dSecureMock.getErrorDetail()).thenReturn(null);
        when(paymentReplyMock.getAuthStatus()).thenReturn(FAILURE);

        testObj.executeSecondPaymentAuthorisation3DSecure();

        verify(worldpayDirectOrderServiceMock).authorise3DSecureAgain(WORLDPAY_ORDER_CODE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock);
    }

    @Test(expected = WorldpayException.class)
    public void testExecuteSecondPaymentAuthorisation3DSecureWhenNoSessionCart() throws WorldpayException, InvalidCartException {
        when(cartServiceMock.hasSessionCart()).thenReturn(false);

        testObj.executeSecondPaymentAuthorisation3DSecure();

        verify(worldpayDirectOrderServiceMock, never()).authorise3DSecureAgain(WORLDPAY_ORDER_CODE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponse3dSecureMock);
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
        when(worldpayDirectOrderServiceMock.authoriseBankTransfer(cartModelMock, bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getRedirectReference()).thenReturn(redirectReferenceMock);
        when(redirectReferenceMock.getValue()).thenReturn(REDIRECT_URL);

        final String result = testObj.authoriseBankTransferRedirect(bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock);


        assertEquals(REDIRECT_URL, result);
    }

    @Test(expected = WorldpayException.class)
    public void shouldRaiseExceptionWhenErrorOccurredAuthorisingRedirectWithBankTransfer() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authoriseBankTransfer(cartModelMock, bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        testObj.authoriseBankTransferRedirect(bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock);

    }

    @Test(expected = WorldpayException.class)
    public void shouldRaiseExceptionWhenNoErrorNoRedirectReferenceWasReturnedOccurredAuthorisingRedirectWithBankTransfer() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authoriseBankTransfer(cartModelMock, bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(null);

        testObj.authoriseBankTransferRedirect(bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock);

    }

    @Test
    public void shouldAuthoriseRecurringPayment() throws WorldpayException, InvalidCartException {

        final DirectResponseData result = testObj.authoriseRecurringPayment(worldpayAdditionalInfoDataMock);

        verify(worldpayAdditionalInfoDataMock).setAuthenticatedShopperId(AUTHENTICATED_SHOPPER_ID);
        verify(worldpayDirectOrderServiceMock).authoriseRecurringPayment(cartModelMock, worldpayAdditionalInfoDataMock);

        final InOrder inOrder = inOrder(acceleratorCheckoutFacadeMock, worldpayDirectOrderServiceMock);
        verify(worldpayDirectOrderServiceMock).authoriseRecurringPayment(cartModelMock, worldpayAdditionalInfoDataMock);
        inOrder.verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);
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
        when(worldpayDirectOrderServiceMock.authoriseKlarna(cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getRedirectReference()).thenReturn(redirectReferenceMock);
        when(redirectReferenceMock.getValue()).thenReturn(KLARNA_CONTENT_ENCODED);

        final String result = testObj.authoriseKlarnaRedirect(worldpayAdditionalInfoDataMock, additionalAuthInfoMock);

        assertEquals(KLARNA_CONTENT_DECODED, result);
    }

    @Test(expected = WorldpayException.class)
    public void shouldRaiseExceptionWhenErrorOccurredAuthorisingRedirectWithKlarna() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authoriseKlarna(cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        testObj.authoriseKlarnaRedirect(worldpayAdditionalInfoDataMock, additionalAuthInfoMock);
    }

    @Test(expected = WorldpayException.class)
    public void shouldRaiseExceptionWhenNoErrorNoRedirectReferenceWasReturnedOccurredAuthorisingRedirectWithKlarna() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authoriseKlarna(cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(null);

        testObj.authoriseKlarnaRedirect(worldpayAdditionalInfoDataMock, additionalAuthInfoMock);
    }

    @Test
    public void shouldAuthoriseGooglePayRequest() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authoriseGooglePay(cartModelMock, googlePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectResponseData result = testObj.authoriseGooglePayDirect(googlePayAdditionalAuthInfoMock);

        verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);
        assertThat(result.getTransactionStatus()).isEqualTo(AUTHORISED);
    }

    @Test
    public void shouldCreateGooglePayPaymentInfoWhenTransactionIsAuthorised() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authoriseGooglePay(cartModelMock, googlePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply().getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);

        testObj.authoriseGooglePayDirect(googlePayAdditionalAuthInfoMock);

        verify(worldpayPaymentInfoServiceMock).createPaymentInfoGooglePay(cartModelMock, googlePayAdditionalAuthInfoMock);
    }

    @Test
    public void shouldNotCreateGooglePayPaymentInfoWhenTransactionIsNotAuthorised() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authoriseGooglePay(cartModelMock, googlePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply().getAuthStatus()).thenReturn(REFUSED);

        testObj.authoriseGooglePayDirect(googlePayAdditionalAuthInfoMock);

        verify(worldpayPaymentInfoServiceMock, never()).createPaymentInfoGooglePay(any(CartModel.class), any(GooglePayAdditionalAuthInfo.class));
    }

    @Test
    public void shouldAuthoriseApplePayRequest() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authoriseApplePay(cartModelMock, applePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectResponseData result = testObj.authoriseApplePayDirect(applePayAdditionalAuthInfoMock);

        verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);
        assertThat(result.getTransactionStatus()).isEqualTo(AUTHORISED);
    }

    @Test
    public void shouldCreateApplePayPaymentInfoWhenTransactionIsAuthorised() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authoriseApplePay(cartModelMock, applePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply().getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);

        testObj.authoriseApplePayDirect(applePayAdditionalAuthInfoMock);

        verify(worldpayPaymentInfoServiceMock).createPaymentInfoApplePay(cartModelMock, applePayAdditionalAuthInfoMock);
    }

    @Test
    public void shouldNotCreateApplePayPaymentInfoWhenTransactionIsNotAuthorised() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authoriseApplePay(cartModelMock, applePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply().getAuthStatus()).thenReturn(REFUSED);

        testObj.authoriseApplePayDirect(applePayAdditionalAuthInfoMock);

        verify(worldpayPaymentInfoServiceMock, never()).createPaymentInfoApplePay(any(CartModel.class), any(ApplePayAdditionalAuthInfo.class));
    }

    @Test(expected = WorldpayException.class)
    public void internalTokenizeAndAuthorise_ShouldThrowAnException_WhenWorldpayDirectOrderServiceThrowIt() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenThrow(new WorldpayException("Worldpay Exception"));

        testObj.internalTokenizeAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);
    }

    @Test(expected = WorldpayException.class)
    public void internalTokenizeAndAuthorise_ShouldThrowAnException_WhenHandleDirectResponseMethodThrowsIt() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(null);

        testObj.internalTokenizeAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);
    }

    @Test(expected = InvalidCartException.class)
    public void internalTokenizeAndAuthorise_ShouldThrowInvalidCartException_WhenHandleDirectResponseMethodThrowsIt() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        doThrow(InvalidCartException.class).when(acceleratorCheckoutFacadeMock).placeOrder();

        testObj.internalTokenizeAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);
    }

    @Test(expected = InvalidCartException.class)
    public void authoriseAndTokenize_ShouldReturnThrowInvalidCartException_WhenInternalTokenizeAndAuthoriseMethodThrowsIt() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        doThrow(InvalidCartException.class).when(acceleratorCheckoutFacadeMock).placeOrder();

        testObj.authoriseAndTokenize(worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);
    }

    @Test(expected = WorldpayException.class)
    public void authoriseAndTokenize_ShouldReturnThrowWorldpayException_WhenInternalTokenizeAndAuthoriseMethodThrowsIt() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(null);

        testObj.authoriseAndTokenize(worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);
    }

    @Test
    public void internalTokenizeAndAuthorise_ShouldReturnADirectResponseDataAuthorised_WhenAResponseIsReceivedByDirectOrderServiceIsAuthorised() throws WorldpayException, InvalidCartException {
        when(acceleratorCheckoutFacadeMock.placeOrder()).thenReturn(orderDataMock);
        final DirectResponseData directResponseData = testObj.internalTokenizeAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);

        assertEquals(orderDataMock, directResponseData.getOrderData());
        assertEquals(AUTHORISED, directResponseData.getTransactionStatus());
        verify(worldpayDirectOrderServiceMock).createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);
    }

    @Test
    public void internalTokenizeAndAuthorise_ShouldReturnADirectResponseDataRefused_WhenAResponseIsReceivedByDirectOrderServiceIsRefused() throws WorldpayException, InvalidCartException {
        when(acceleratorCheckoutFacadeMock.placeOrder()).thenReturn(orderDataMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUSED);
        when(paymentReplyMock.getReturnCode()).thenReturn(RETURN_CODE);

        final DirectResponseData directResponseData = testObj.internalTokenizeAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);

        assertNull(directResponseData.getOrderData());
        assertEquals(TransactionStatus.REFUSED, directResponseData.getTransactionStatus());
        assertEquals(RETURN_CODE, directResponseData.getReturnCode());
    }

    @Test
    public void internalTokenizeAndAuthorise_ShouldReturnADirectResponseDataCancelled_WhenAResponseIsReceivedByDirectOrderServiceIsCancelled() throws WorldpayException, InvalidCartException {
        when(acceleratorCheckoutFacadeMock.placeOrder()).thenReturn(orderDataMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(CANCELLED);

        final DirectResponseData directResponseData = testObj.internalTokenizeAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);

        assertEquals(TransactionStatus.CANCELLED, directResponseData.getTransactionStatus());
        assertNull(directResponseData.getOrderData());
        assertNull(directResponseData.getReturnCode());
        verify(worldpayDirectOrderServiceMock).createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);
    }

    @Test(expected = IllegalStateException.class)
    public void tokenize_ShouldRiseAnException_WhenThereIsNotSessionCart() throws WorldpayException, IllegalStateException {
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.FALSE);

        testObj.tokenize(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);
    }

    @Test(expected = WorldpayException.class)
    public void tokenize_ShouldRiseAnException_WhenTokeniseWithCartParameterRiseAWorldpayException() throws WorldpayException, IllegalStateException {
        doThrow(new WorldpayException("Token creation error")).when(worldpayDirectOrderServiceMock).createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        testObj.tokenize(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);
    }

    @Test
    public void tokenize_ShouldCreateToken_WhenThereIsCartInSession() throws WorldpayException, IllegalStateException {
        testObj.tokenize(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);
    }

    @Test(expected = IllegalStateException.class)
    public void tokenize_ShouldRiseAnException_WhenCartParameterIsNull() throws WorldpayException, IllegalStateException {
        cartModelMock = null;

        testObj.tokenize(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);
    }

    @Test(expected = WorldpayException.class)
    public void tokenize_ShouldRiseAnException_WhenCreateTokenRiseAWorldpayException() throws WorldpayException, IllegalStateException {
        doThrow(new WorldpayException("Token creation error")).when(worldpayDirectOrderServiceMock).createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        testObj.tokenize(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);
    }

    @Test
    public void tokenize_ShouldCreateTokenHavingCartParameter_WhenThereIsCartInSession() throws WorldpayException, IllegalStateException {
        testObj.tokenize(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);
    }
}

package com.worldpay.facades.payment.direct.impl;

import java.util.Optional;

import static com.worldpay.enums.order.AuthorisedStatus.CANCELLED;
import static com.worldpay.enums.order.AuthorisedStatus.REFUNDED;
import static com.worldpay.enums.order.AuthorisedStatus.REFUSED;
import static com.worldpay.enums.order.AuthorisedStatus.SENT_FOR_AUTHORISATION;
import static com.worldpay.payment.TransactionStatus.AUTHENTICATION_REQUIRED;
import static com.worldpay.payment.TransactionStatus.AUTHORISED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.ACHDirectDebitAdditionalAuthInfo;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.ApplePayAdditionalAuthInfo;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.data.Date;
import com.worldpay.data.ErrorDetail;
import com.worldpay.data.GooglePayAdditionalAuthInfo;
import com.worldpay.data.PaymentReply;
import com.worldpay.data.RedirectReference;
import com.worldpay.data.Request3DInfo;
import com.worldpay.data.payment.Card;
import com.worldpay.data.token.TokenDetails;
import com.worldpay.data.token.TokenReply;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.model.GooglePayPaymentInfoModel;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
public class DefaultWorldpayDirectOrderFacadeTest {

    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String ISSUER_URL = "issuerURL";
    private static final String PA_REQUEST = "paRequest";
    private static final String PA_RESPONSE = "paResponse";
    private static final String RETURN_CODE = "A12";
    private static final String REDIRECT_URL = "redirectUrl";
    private static final String KLARNA_CONTENT_ENCODED = "a2xhcm5hQ29udGVudA==";
    private static final String KLARNA_CONTENT_DECODED = "klarnaContent";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String THREEDS_V1 = "1";
    private static final String THREEDS_V2 = "2";
    private static final String ISSUER_PAYLOAD = "issuerPayload";
    private static final String TRANSACTION_ID_3DS = "transactionId3Ds";
    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";
    private static final String MONTH = "01";
    private static final String YEAR = "2025";

    Date expiryDate;

    @Spy
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
    private WorldpayCartService worldpayCartServiceMock;
    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;
    @Mock
    private AcceleratorCheckoutFacade acceleratorCheckoutFacadeMock;
    @Mock
    private Populator<WorldpayAPMPaymentInfoModel, CCPaymentInfoData> apmPaymentInfoPopulatorMock;

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
    private ApplePayAdditionalAuthInfo applePayAdditionalAuthInfoMock;
    @Mock
    private GooglePayAdditionalAuthInfo googlePayAdditionalAuthInfoMock;
    @Mock
    private OrderData orderDataMock;
    @Mock
    private TokenReply tokenReplyMock;
    @Mock
    private TokenDetails tokenDetailsMock;
    @Mock
    private Card cardDetailsMock;
    @Mock
    private GooglePayPaymentInfoModel googlePayPaymentInfoMock;
    @Mock
    private CCPaymentInfoData ccPaymentInfoData;
    @Mock
    private DirectResponseData directResponseDataMock;
    @Captor
    private ArgumentCaptor<DirectResponseData> directResponseDataArgumentCaptor;
    @Mock
    private ACHDirectDebitAdditionalAuthInfo achAdditionalAuthInfoMock;

    @BeforeEach
    void setUp() {
        final Date date = new Date();
        date.setMonth(MONTH);
        date.setYear(YEAR);
        this.expiryDate = date;
    }

    @Test
    public void shouldReturnOrderDataWhenAuthorisedAndOrderPlaced() throws Exception {
        when(worldpayDirectOrderServiceMock.authorise(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

        final DirectResponseData result = testObj.authorise(worldpayAdditionalInfoDataMock);

        final InOrder inOrder = inOrder(acceleratorCheckoutFacadeMock, worldpayDirectOrderServiceMock);

        inOrder.verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);
        inOrder.verify(acceleratorCheckoutFacadeMock).placeOrder();
        assertEquals(acceleratorCheckoutFacadeMock.placeOrder(), result.getOrderData());
        assertEquals(AUTHORISED, result.getTransactionStatus());
    }

    @Test
    public void authoriseShouldReturnResponseDataWithStatusRefusedAndOrderDataNull() throws Exception {
        when(worldpayDirectOrderServiceMock.authorise(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

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
        when(worldpayDirectOrderServiceMock.authorise(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

        when(paymentReplyMock.getAuthStatus()).thenReturn(CANCELLED);

        final DirectResponseData authorise = testObj.authorise(worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).authorise(cartModelMock, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(any(DirectAuthoriseServiceResponse.class), any(CartModel.class));
        assertEquals(TransactionStatus.CANCELLED, authorise.getTransactionStatus());
    }

    @Test
    public void authoriseShouldRaiseIllegalStateExceptionWhenTryingToAuthoriseWithoutCart() {
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.FALSE);

        assertThatThrownBy(() -> testObj.authorise(worldpayAdditionalInfoDataMock))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot authorize payment where there is no cart");
    }

    @Test
    public void authoriseShouldRaiseExceptionWhenPaymentReplayIsNullAndErrorDetailIsNotNull() throws Exception {
        when(worldpayDirectOrderServiceMock.authorise(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(null);

        assertThatThrownBy(() -> testObj.authorise(worldpayAdditionalInfoDataMock))
                .isInstanceOf(WorldpayException.class)
                .hasMessage("There was an error in the service gateway: [errorMessage]");

        verify(worldpayDirectOrderServiceMock).authorise(cartModelMock, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(any(DirectAuthoriseServiceResponse.class), any(CartModel.class));
    }

    @Test
    public void shouldReturnDirectResponseDataWith3DSecureInformationWhenDirectAuthoriseServiceResponseContainsRequest3DSLegacyFlowInfo() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authorise(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

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
        when(worldpayDirectOrderServiceMock.authorise(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

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
        when(worldpayDirectOrderServiceMock.authorise(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

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
        when(worldpayDirectOrderServiceMock.authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);

        final DirectResponseData result = testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        final InOrder inOrder = inOrder(acceleratorCheckoutFacadeMock, worldpayDirectOrderServiceMock);
        inOrder.verify(worldpayDirectOrderServiceMock).authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        inOrder.verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponse3dSecureMock, cartModelMock);
        inOrder.verify(acceleratorCheckoutFacadeMock).placeOrder();
        assertEquals(AUTHORISED, result.getTransactionStatus());
        assertEquals(acceleratorCheckoutFacadeMock.placeOrder(), result.getOrderData());
    }

    @Test
    public void shouldReturnDirectResponseDataWithRefusedStatusWhenAuthorise3DSecureIsRefused() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(null);
        when(worldpayDirectOrderServiceMock.authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUSED);
        when(paymentReplyMock.getReturnCode()).thenReturn(RETURN_CODE);

        final DirectResponseData result = testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(directAuthoriseServiceResponse3dSecureMock, cartModelMock);
        assertEquals(RETURN_CODE, result.getReturnCode());
        assertEquals(TransactionStatus.REFUSED, result.getTransactionStatus());
    }

    @Test
    public void authorise3DSecureShouldRaiseExceptionWhen3dAuthStatusInvalid() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUNDED);

        assertThatThrownBy(() -> testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock))
                .isInstanceOf(WorldpayException.class)
                .hasMessage("There was a problem authorising the order with worldpayOrderCode [null]");

        verify(worldpayDirectOrderServiceMock).authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(directAuthoriseServiceResponse3dSecureMock, cartModelMock);
    }

    @Test
    public void authorise3DSecureShouldRaiseExceptionWhen3dAuthError() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponse3dSecureMock.getErrorDetail()).thenReturn(null);

        assertThatThrownBy(() -> testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock))
                .isInstanceOf(WorldpayException.class)
                .hasMessage("There was an error communicating with Worldpay");

        verify(worldpayDirectOrderServiceMock).authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(directAuthoriseServiceResponse3dSecureMock, cartModelMock);
    }

    @Test
    public void authorise3DSecureShouldRaiseExceptionWhenPaymentReplyIsNull() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponse3dSecureMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        assertThatThrownBy(() -> testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock))
                .isInstanceOf(WorldpayException.class)
                .hasMessage("There was an error in the service gateway: [errorMessage]");

        verify(worldpayDirectOrderServiceMock).authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(directAuthoriseServiceResponse3dSecureMock, cartModelMock);
    }

    @Test
    public void testExecuteSecondPaymentAuthorisation3DSecureAuthorised() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authorise3DSecureAgain(WORLDPAY_ORDER_CODE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);

        final DirectResponseData result = testObj.executeSecondPaymentAuthorisation3DSecure();

        final InOrder inOrder = inOrder(acceleratorCheckoutFacadeMock, worldpayDirectOrderServiceMock);
        inOrder.verify(worldpayDirectOrderServiceMock).authorise3DSecureAgain(WORLDPAY_ORDER_CODE);
        inOrder.verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponse3dSecureMock, cartModelMock);
        inOrder.verify(acceleratorCheckoutFacadeMock).placeOrder();
        assertEquals(AUTHORISED, result.getTransactionStatus());
        assertEquals(acceleratorCheckoutFacadeMock.placeOrder(), result.getOrderData());
    }

    @Test
    public void testExecuteSecondPaymentAuthorisation3DSecureRefused() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authorise3DSecureAgain(WORLDPAY_ORDER_CODE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(paymentReplyMock);

        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUSED);
        when(paymentReplyMock.getReturnCode()).thenReturn(RETURN_CODE);

        final DirectResponseData result = testObj.executeSecondPaymentAuthorisation3DSecure();

        verify(worldpayDirectOrderServiceMock).authorise3DSecureAgain(WORLDPAY_ORDER_CODE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(directAuthoriseServiceResponse3dSecureMock, cartModelMock);
        assertEquals(RETURN_CODE, result.getReturnCode());
        assertEquals(TransactionStatus.REFUSED, result.getTransactionStatus());
    }

    @Test
    public void testExecuteSecondPaymentAuthorisation3DSecureStatusInvalid() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authorise3DSecureAgain(WORLDPAY_ORDER_CODE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUNDED);

        assertThatThrownBy(() -> testObj.executeSecondPaymentAuthorisation3DSecure())
                .isInstanceOf(WorldpayException.class)
                .hasMessage("There was a problem authorising the order with worldpayOrderCode [null]");

        verify(worldpayDirectOrderServiceMock).authorise3DSecureAgain(WORLDPAY_ORDER_CODE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(directAuthoriseServiceResponse3dSecureMock, cartModelMock);
    }

    @Test
    public void testExecuteSecondPaymentAuthorisation3DSecureWhenPaymentReplyIsNull() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authorise3DSecureAgain(WORLDPAY_ORDER_CODE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponse3dSecureMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        assertThatThrownBy(() -> testObj.executeSecondPaymentAuthorisation3DSecure())
                .isInstanceOf(WorldpayException.class)
                .hasMessage("There was an error in the service gateway: [errorMessage]");

        verify(worldpayDirectOrderServiceMock).authorise3DSecureAgain(WORLDPAY_ORDER_CODE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(directAuthoriseServiceResponse3dSecureMock, cartModelMock);
    }

    @Test
    public void testExecuteSecondPaymentAuthorisation3DSecureWhen3dAuthError() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authorise3DSecureAgain(WORLDPAY_ORDER_CODE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponse3dSecureMock.get3DSecureFlow()).thenReturn(Optional.empty());
        when(directAuthoriseServiceResponse3dSecureMock.getErrorDetail()).thenReturn(null);

        assertThatThrownBy(() -> testObj.executeSecondPaymentAuthorisation3DSecure())
                .isInstanceOf(WorldpayException.class)
                .hasMessage("There was an error communicating with Worldpay");

        verify(worldpayDirectOrderServiceMock).authorise3DSecureAgain(WORLDPAY_ORDER_CODE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(directAuthoriseServiceResponse3dSecureMock, cartModelMock);
    }

    @Test
    public void testExecuteSecondPaymentAuthorisation3DSecureWhenNoSessionCart() throws WorldpayException {
        when(cartServiceMock.hasSessionCart()).thenReturn(false);

        assertThatThrownBy(() -> testObj.executeSecondPaymentAuthorisation3DSecure())
                .isInstanceOf(WorldpayException.class)
                .hasMessage("The session has not a valid cart");

        verify(worldpayDirectOrderServiceMock, never()).authorise3DSecureAgain(WORLDPAY_ORDER_CODE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(directAuthoriseServiceResponse3dSecureMock, cartModelMock);
    }

    @Test
    public void authoriseShouldGenerateAuthenticatedShopperId() throws InvalidCartException, WorldpayException {
        when(worldpayDirectOrderServiceMock.authorise(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

        testObj.authorise(worldpayAdditionalInfoDataMock);

        verify(worldpayAdditionalInfoDataMock).setAuthenticatedShopperId(AUTHENTICATED_SHOPPER_ID);
    }

    @Test
    public void authorise3dSecureShouldGenerateAuthenticatedShopperId() throws InvalidCartException, WorldpayException {
        when(worldpayDirectOrderServiceMock.authorise3DSecure(WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

        testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        verify(worldpayAdditionalInfoDataMock).setAuthenticatedShopperId(AUTHENTICATED_SHOPPER_ID);
    }

    @Test
    public void shouldReturnBankURLWhenAuthorisedAndOrderPlacedWithBankTransfer() throws WorldpayException {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayDirectOrderServiceMock.authoriseBankTransfer(cartModelMock, bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getRedirectReference()).thenReturn(redirectReferenceMock);
        when(redirectReferenceMock.getValue()).thenReturn(REDIRECT_URL);

        final String result = testObj.authoriseBankTransferRedirect(bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock);

        assertEquals(REDIRECT_URL, result);
    }

    @Test
    public void shouldRaiseExceptionWhenErrorOccurredAuthorisingRedirectWithBankTransfer() throws WorldpayException {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayDirectOrderServiceMock.authoriseBankTransfer(cartModelMock, bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        assertThatThrownBy(() -> testObj.authoriseBankTransferRedirect(bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock))
                .isInstanceOf(WorldpayException.class)
                .hasMessage("There was an error in the service gateway: [errorMessage]");
    }

    @Test
    public void shouldRaiseExceptionWhenNoErrorNoRedirectReferenceWasReturnedOccurredAuthorisingRedirectWithBankTransfer() throws WorldpayException {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayDirectOrderServiceMock.authoriseBankTransfer(cartModelMock, bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(null);

        assertThatThrownBy(() -> testObj.authoriseBankTransferRedirect(bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock))
                .isInstanceOf(WorldpayException.class)
                .hasMessage("There was an error communicating with Worldpay");
    }

    @Test
    public void shouldAuthoriseRecurringPayment() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authoriseRecurringPayment(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

        final DirectResponseData result = testObj.authoriseRecurringPayment(worldpayAdditionalInfoDataMock);

        verify(worldpayAdditionalInfoDataMock).setAuthenticatedShopperId(AUTHENTICATED_SHOPPER_ID);
        verify(worldpayDirectOrderServiceMock).authoriseRecurringPayment(cartModelMock, worldpayAdditionalInfoDataMock);

        final InOrder inOrder = inOrder(acceleratorCheckoutFacadeMock, worldpayDirectOrderServiceMock);
        inOrder.verify(worldpayDirectOrderServiceMock).authoriseRecurringPayment(cartModelMock, worldpayAdditionalInfoDataMock);
        inOrder.verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);
        inOrder.verify(acceleratorCheckoutFacadeMock).placeOrder();
        assertEquals(acceleratorCheckoutFacadeMock.placeOrder(), result.getOrderData());
        assertEquals(AUTHORISED, result.getTransactionStatus());
    }

    @Test
    public void shouldRedirectTo3DSecureV1OnRecurringPayment() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authoriseRecurringPayment(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

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
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayDirectOrderServiceMock.authoriseKlarna(cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getRedirectReference()).thenReturn(redirectReferenceMock);
        when(redirectReferenceMock.getValue()).thenReturn(KLARNA_CONTENT_ENCODED);

        final String result = testObj.authoriseKlarnaRedirect(worldpayAdditionalInfoDataMock, additionalAuthInfoMock);

        assertEquals(KLARNA_CONTENT_DECODED, result);
    }

    @Test
    public void shouldRaiseExceptionWhenErrorOccurredAuthorisingRedirectWithKlarna() throws WorldpayException {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayDirectOrderServiceMock.authoriseKlarna(cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        assertThatThrownBy(() -> testObj.authoriseKlarnaRedirect(worldpayAdditionalInfoDataMock, additionalAuthInfoMock))
                .isInstanceOf(WorldpayException.class)
                .hasMessage("There was an error in the service gateway: [errorMessage]");
    }

    @Test
    public void shouldRaiseExceptionWhenNoErrorNoRedirectReferenceWasReturnedOccurredAuthorisingRedirectWithKlarna() throws WorldpayException {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayDirectOrderServiceMock.authoriseKlarna(cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(null);

        assertThatThrownBy(() -> testObj.authoriseKlarnaRedirect(worldpayAdditionalInfoDataMock, additionalAuthInfoMock))
                .isInstanceOf(WorldpayException.class)
                .hasMessage("There was an error communicating with Worldpay");
    }

    @Test
    public void shouldAuthoriseGooglePayRequest() throws WorldpayException, InvalidCartException {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayDirectOrderServiceMock.authoriseGooglePay(cartModelMock, googlePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectResponseData result = testObj.authoriseGooglePayDirect(googlePayAdditionalAuthInfoMock);

        verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);
        assertThat(result.getTransactionStatus()).isEqualTo(AUTHORISED);
    }

    @Test
    public void shouldCreateGooglePayPaymentInfoWhenTransactionIsAuthorised() throws WorldpayException, InvalidCartException {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(paymentReplyMock.getCardDetails()).thenReturn(cardDetailsMock);
        when(worldpayDirectOrderServiceMock.authoriseGooglePay(cartModelMock, googlePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getTokenDetails()).thenReturn(tokenDetailsMock);
        when(tokenDetailsMock.getPaymentTokenID()).thenReturn(PAYMENT_TOKEN_ID);

        testObj.authoriseGooglePayDirect(googlePayAdditionalAuthInfoMock);

        verify(worldpayPaymentInfoServiceMock).createPaymentInfoGooglePay(cartModelMock, googlePayAdditionalAuthInfoMock, PAYMENT_TOKEN_ID, cardDetailsMock);
    }

    @Test
    public void shouldNotCreateGooglePayPaymentInfoWhenTransactionIsNotAuthorised() throws WorldpayException, InvalidCartException {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayDirectOrderServiceMock.authoriseGooglePay(cartModelMock, googlePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUSED);

        testObj.authoriseGooglePayDirect(googlePayAdditionalAuthInfoMock);

        verify(worldpayPaymentInfoServiceMock, never()).createPaymentInfoGooglePay(any(CartModel.class), any(GooglePayAdditionalAuthInfo.class), any(String.class), any(Card.class));
    }

    @Test
    public void shouldAuthoriseApplePayRequest() throws WorldpayException, InvalidCartException {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayDirectOrderServiceMock.authoriseApplePay(cartModelMock, applePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectResponseData result = testObj.authoriseApplePayDirect(applePayAdditionalAuthInfoMock);

        verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);
        assertThat(result.getTransactionStatus()).isEqualTo(AUTHORISED);
    }

    @Test
    public void shouldCreateApplePayPaymentInfoWhenTransactionIsAuthorised() throws WorldpayException, InvalidCartException {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayDirectOrderServiceMock.authoriseApplePay(cartModelMock, applePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply().getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);

        testObj.authoriseApplePayDirect(applePayAdditionalAuthInfoMock);

        verify(worldpayPaymentInfoServiceMock).createPaymentInfoApplePay(cartModelMock, applePayAdditionalAuthInfoMock);
    }

    @Test
    public void shouldNotCreateApplePayPaymentInfoWhenTransactionIsNotAuthorised() throws WorldpayException, InvalidCartException {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayDirectOrderServiceMock.authoriseApplePay(cartModelMock, applePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUSED);

        testObj.authoriseApplePayDirect(applePayAdditionalAuthInfoMock);

        verify(worldpayPaymentInfoServiceMock, never()).createPaymentInfoApplePay(any(CartModel.class), any(ApplePayAdditionalAuthInfo.class));
    }

    @Test
    public void internalTokenizeAndAuthorise_ShouldThrowAnException_WhenWorldpayDirectOrderServiceThrowIt() throws WorldpayException {
        doThrow(new WorldpayException("Worldpay Exception")).when(worldpayDirectOrderServiceMock).createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);

        assertThatThrownBy(() -> testObj.internalTokenizeAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock))
                .isInstanceOf(WorldpayException.class)
                .hasMessage("Worldpay Exception");
    }

    @Test
    public void internalTokenizeAndAuthorise_ShouldThrowAnException_WhenHandleDirectResponseMethodThrowsIt() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayDirectOrderServiceMock.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(null);

        assertThatThrownBy(() -> testObj.internalTokenizeAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock))
                .isInstanceOf(WorldpayException.class)
                .hasMessage("There was an error communicating with Worldpay");
    }

    @Test
    public void internalTokenizeAndAuthorise_ShouldThrowInvalidCartException_WhenHandleDirectResponseMethodThrowsIt() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayDirectOrderServiceMock.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayDirectOrderServiceMock.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);

        when(worldpayDirectOrderServiceMock.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        doThrow(new InvalidCartException("InvalidCartException")).when(acceleratorCheckoutFacadeMock).placeOrder();

        assertThatThrownBy(() -> testObj.internalTokenizeAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock))
                .isInstanceOf(InvalidCartException.class)
                .hasMessage("InvalidCartException");
    }

    @Test
    public void authoriseAndTokenize_ShouldReturnThrowInvalidCartException_WhenInternalTokenizeAndAuthoriseMethodThrowsIt() throws InvalidCartException, WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayDirectOrderServiceMock.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);

        doThrow(new InvalidCartException("InvalidCartException")).when(acceleratorCheckoutFacadeMock).placeOrder();

        assertThatThrownBy(() -> testObj.internalTokenizeAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock))
                .isInstanceOf(InvalidCartException.class)
                .hasMessage("InvalidCartException");
    }

    @Test
    public void authoriseAndTokenize_ShouldReturnThrowWorldpayException_WhenInternalTokenizeAndAuthoriseMethodThrowsIt() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authorise(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(null);

        assertThatThrownBy(() -> testObj.authoriseAndTokenize(worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock))
                .isInstanceOf(WorldpayException.class)
                .hasMessage("There was an error communicating with Worldpay");
    }

    @Test
    public void internalTokenizeAndAuthorise_ShouldReturnADirectResponseDataAuthorised_WhenAResponseIsReceivedByDirectOrderServiceIsAuthorised() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(acceleratorCheckoutFacadeMock.placeOrder()).thenReturn(orderDataMock);

        final DirectResponseData directResponseData = testObj.internalTokenizeAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);

        assertEquals(orderDataMock, directResponseData.getOrderData());
        assertEquals(AUTHORISED, directResponseData.getTransactionStatus());
        verify(worldpayDirectOrderServiceMock).createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);
    }

    @Test
    public void internalTokenizeAndAuthorise_ShouldReturnADirectResponseDataRefused_WhenAResponseIsReceivedByDirectOrderServiceIsRefused() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);

        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUSED);
        when(paymentReplyMock.getReturnCode()).thenReturn(RETURN_CODE);

        final DirectResponseData directResponseData = testObj.internalTokenizeAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);

        assertNull(directResponseData.getOrderData());
        assertEquals(TransactionStatus.REFUSED, directResponseData.getTransactionStatus());
        assertEquals(RETURN_CODE, directResponseData.getReturnCode());
    }

    @Test
    public void internalTokenizeAndAuthorise_ShouldReturnADirectResponseDataCancelled_WhenAResponseIsReceivedByDirectOrderServiceIsCancelled() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);

        when(paymentReplyMock.getAuthStatus()).thenReturn(CANCELLED);

        final DirectResponseData directResponseData = testObj.internalTokenizeAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);

        assertEquals(TransactionStatus.CANCELLED, directResponseData.getTransactionStatus());
        assertNull(directResponseData.getOrderData());
        assertNull(directResponseData.getReturnCode());
        verify(worldpayDirectOrderServiceMock).createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);
    }

    @Test
    public void tokenize_ShouldRiseAnException_WhenThereIsNotSessionCart() {
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.FALSE);

        assertThatThrownBy(() -> testObj.tokenize(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot authorize payment where there is no cart");

    }

    @Test
    public void tokenize_ShouldRiseAnException_WhenTokeniseWithCartParameterRiseAWorldpayException() throws WorldpayException, IllegalStateException {
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

        doThrow(new WorldpayException("Token creation error")).when(worldpayDirectOrderServiceMock).createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        assertThatThrownBy(() -> testObj.tokenize(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock))
                .isInstanceOf(WorldpayException.class)
                .hasMessage("Token creation error");
    }

    @Test
    public void tokenize_ShouldCreateToken_WhenThereIsCartInSession() throws WorldpayException, IllegalStateException {
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

        testObj.tokenize(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);
    }

    @Test
    public void tokenize_ShouldRiseAnException_WhenCartParameterIsNull() throws IllegalStateException {
        assertThatThrownBy(() -> testObj.tokenize(null, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot tokenize where there is no cart");
    }

    @Test
    public void tokenize_ShouldRaiseAnException_WhenCreateTokenRiseAWorldpayException() throws WorldpayException, IllegalStateException {
        doThrow(new WorldpayException("Token creation error")).when(worldpayDirectOrderServiceMock).createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        assertThatThrownBy(() -> testObj.tokenize(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock))
                .isInstanceOf(WorldpayException.class)
                .hasMessage("Token creation error");
    }

    @Test
    public void executeFirstPaymentAuthorisation3DSecure_shouldAuthorizeRecurringPayment_whenUsingExistingSavedCard() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authoriseRecurringPayment(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

        // cseToken (encrypted data) is not set because subscriptionId is given
        when(cseAdditionalAuthInfoMock.getEncryptedData()).thenReturn(null);

        testObj.executeFirstPaymentAuthorisation3DSecure(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        // then
        verify(worldpayDirectOrderServiceMock).authoriseRecurringPayment(cartModelMock, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);
        verifyNoMoreInteractions(worldpayDirectOrderServiceMock);
    }

    @Test
    public void executeFirstPaymentAuthorisation3DSecure_shouldAuthorizeAndTokenize_whenUsingNewSavedCard() throws WorldpayException, InvalidCartException {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayDirectOrderServiceMock.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(worldpayDirectOrderServiceMock.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceResponseMock);

        // cseToken (encrypted data) is set for the first time a saved card is used
        when(cseAdditionalAuthInfoMock.getEncryptedData()).thenReturn("cse-token");
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(true);

        testObj.executeFirstPaymentAuthorisation3DSecure(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        // then
        verify(worldpayDirectOrderServiceMock).createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);
        verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);

        verifyNoMoreInteractions(worldpayDirectOrderServiceMock);
    }

    @Test
    public void executeFirstPaymentAuthorisation3DSecure_shouldAuthorizeAndTokenize_whenUsingNewCard() throws WorldpayException, InvalidCartException {
        when(worldpayDirectOrderServiceMock.authorise(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);

        // cseToken (encrypted data) is given for non-saved cards
        when(cseAdditionalAuthInfoMock.getEncryptedData()).thenReturn("cse-token");
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(false);

        testObj.executeFirstPaymentAuthorisation3DSecure(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        // then
        verify(worldpayDirectOrderServiceMock).createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock).authorise(cartModelMock, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);

        verifyNoMoreInteractions(worldpayDirectOrderServiceMock);
    }

    @Test
    public void processDirectResponse_whenGooglePayPaymentInfoAuthorisation_ShouldPopulateApmAttributes() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);

        when(cartModelMock.getPaymentInfo()).thenReturn(googlePayPaymentInfoMock);
        when(acceleratorCheckoutFacadeMock.placeOrder()).thenReturn(orderDataMock);

        testObj.processDirectResponse(directAuthoriseServiceResponseMock, cartModelMock);

        verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);
        verify(testObj).populateApmAttributes(eq(cartModelMock), any(DirectResponseData.class));
    }

    @Test
    public void processDirectResponse_whenPaymentStatusIsPending_ShouldPopulateApmAttributes() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(SENT_FOR_AUTHORISATION);
        when(cartModelMock.getPaymentInfo()).thenReturn(googlePayPaymentInfoMock);
        when(acceleratorCheckoutFacadeMock.placeOrder()).thenReturn(orderDataMock);

        testObj.processDirectResponse(directAuthoriseServiceResponseMock, cartModelMock);

        verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);
        verify(testObj).populateApmAttributes(eq(cartModelMock), directResponseDataArgumentCaptor.capture());
        verify(testObj).handleAuthorisedResponse(directResponseDataArgumentCaptor.capture());

        final DirectResponseData directResponseData = directResponseDataArgumentCaptor.getValue();
        assertThat(directResponseData.getTransactionStatus()).isEqualTo(AUTHORISED);
    }

    @Test
    public void populateApmAttributes_ShouldPopulateApmAttributes() {
        final DirectResponseData responseData = new DirectResponseData();
        responseData.setOrderData(orderDataMock);
        when(orderDataMock.getPaymentInfo()).thenReturn(ccPaymentInfoData);
        when(cartModelMock.getPaymentInfo()).thenReturn(googlePayPaymentInfoMock);

        testObj.populateApmAttributes(cartModelMock, responseData);

        verify(apmPaymentInfoPopulatorMock).populate(googlePayPaymentInfoMock, ccPaymentInfoData);
    }

    @Test
    public void executeSecondPaymentAuthorisation3DSecure_ShouldReturnDirectResponseData_WhenShouldProcessResponseIsTrue() throws Exception {
        doReturn(true).when(testObj).shouldProcessResponse(directAuthoriseServiceResponseMock);
        doReturn(directAuthoriseServiceResponseMock).when(worldpayDirectOrderServiceMock).authorise3DSecureAgain(WORLDPAY_ORDER_CODE);
        when(worldpayCartServiceMock.findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE)).thenReturn(cartModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        doReturn(directResponseDataMock).when(testObj).processDirectResponseData(eq(cartModelMock), eq(directAuthoriseServiceResponseMock), any(DirectResponseData.class));

        final DirectResponseData result = testObj.executeSecondPaymentAuthorisation3DSecure(WORLDPAY_ORDER_CODE);

        assertSame(directResponseDataMock, result);
        verify(cartServiceMock).setSessionCart(cartModelMock);
    }

    @Test
    public void executeSecondPaymentAuthorisation3DSecure_ShouldThrowWorldpayException_WhenNoErrorDetailAndNotProcessed() throws Exception {
        when(worldpayDirectOrderServiceMock.authorise3DSecureAgain(WORLDPAY_ORDER_CODE)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayCartServiceMock.findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE)).thenReturn(cartModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        doReturn(false).when(testObj).shouldProcessResponse(directAuthoriseServiceResponseMock);
        doReturn(null).when(directAuthoriseServiceResponseMock).getErrorDetail();

        assertThatThrownBy(() -> testObj.executeSecondPaymentAuthorisation3DSecure(WORLDPAY_ORDER_CODE))
                .isInstanceOf(WorldpayException.class)
                .hasMessage("There was an error communicating with Worldpay");

        verify(cartServiceMock).setSessionCart(cartModelMock);
    }

    @Test
    public void authoriseACHDirectDebit_ShouldReturnDirectResponseData() throws Exception {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayDirectOrderServiceMock.authoriseACHDirectDebit(cartModelMock, achAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        final DirectResponseData expectedResponse = new DirectResponseData();
        doReturn(expectedResponse).when(testObj).handleACHDirectDebitResponse(directAuthoriseServiceResponseMock, cartModelMock);

        final DirectResponseData result = testObj.authoriseACHDirectDebit(worldpayAdditionalInfoDataMock, achAdditionalAuthInfoMock);

        assertSame(expectedResponse, result);
        verify(worldpayDirectOrderServiceMock).authoriseACHDirectDebit(cartModelMock, achAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);
    }

    @Test
    public void authoriseACHDirectDebit_ShouldThrowWorldpayConfigurationException() throws InvalidCartException, WorldpayException {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(worldpayDirectOrderServiceMock.authoriseACHDirectDebit(cartModelMock, achAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock))
                .thenThrow(new WorldpayConfigurationException("No config"));

        assertThatThrownBy(() -> testObj.authoriseACHDirectDebit(worldpayAdditionalInfoDataMock, achAdditionalAuthInfoMock))
                .isInstanceOf(WorldpayConfigurationException.class)
                .hasMessage("No config");
    }

    @Test
    public void authoriseACHDirectDebit_ShouldThrowInvalidCartException() throws Exception {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getCode()).thenReturn("014124");
        doThrow(new InvalidCartException("Invalid cart")).when(worldpayDirectOrderServiceMock).authoriseACHDirectDebit(cartModelMock, achAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        assertThatThrownBy(() -> testObj.authoriseACHDirectDebit(worldpayAdditionalInfoDataMock, achAdditionalAuthInfoMock))
                .isInstanceOf(InvalidCartException.class)
                .hasMessage("There was an error placing the order for cart [014124]");
    }

    @Test
    public void handleACHDirectDebitResponse_ShouldProcessACHResponse() throws Exception {
        final DirectResponseData expected = new DirectResponseData();
        doReturn(true).when(testObj).shouldProcessACHResponse(directAuthoriseServiceResponseMock);
        doReturn(expected).when(testObj).processACHDirectDebitDirectResponse(directAuthoriseServiceResponseMock, cartModelMock);

        final DirectResponseData result = testObj.handleACHDirectDebitResponse(directAuthoriseServiceResponseMock, cartModelMock);

        assertSame(expected, result);
    }

    @Test
    public void handleACHDirectDebitResponse_ShouldHandleErrorOnServiceResponse() throws Exception {
        final DirectResponseData expected = new DirectResponseData();
        doReturn(false).when(testObj).shouldProcessACHResponse(directAuthoriseServiceResponseMock);
        doReturn(expected).when(testObj).handleErrorOnServiceResponse(directAuthoriseServiceResponseMock);

        final DirectResponseData result = testObj.handleACHDirectDebitResponse(directAuthoriseServiceResponseMock, cartModelMock);

        assertSame(expected, result);
    }
}

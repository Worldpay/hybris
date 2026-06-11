package com.worldpay.service.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.Amount;
import com.worldpay.data.ApplePayAdditionalAuthInfo;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.data.ErrorDetail;
import com.worldpay.data.GooglePayAdditionalAuthInfo;
import com.worldpay.data.MerchantInfo;
import com.worldpay.data.Order;
import com.worldpay.data.PaymentReply;
import com.worldpay.data.SchemeResponse;
import com.worldpay.data.token.TokenDetails;
import com.worldpay.data.token.TokenReply;
import com.worldpay.enums.token.TokenEvent;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpaySessionService;
import com.worldpay.service.payment.request.WorldpayRequestFactory;
import com.worldpay.service.payment.request.WorldpayRequestRetryStrategy;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.DeleteTokenServiceRequest;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.request.SecondThreeDSecurePaymentRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.DeleteTokenResponse;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.service.response.UpdateTokenResponse;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class DefaultWorldpayDirectOrderServiceTest {

    private static final String ORDER_CODE = "orderCode";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String PA_RESPONSE = "paResponse";
    private static final String COOKIE = "cookie";
    private static final String THREE_D_SECURE_ECHO_DATA_PARAM = "3DSecureEchoData";
    private static final String THREE_D_SECURE_COOKIE_PARAM = "3DSecureCookie";
    private static final String THREE_D_SECURE_WINDOW_SIZE = "challengeWindowSize";
    private static final String TRANSACTION_IDENTIFIER = "transactionIdentifier";
    private static final String SESSION_ID = "sessionId";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";

    @Spy
    @InjectMocks
    private DefaultWorldpayDirectOrderService testObj;

    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoService;
    @Spy
    private List<WorldpayRequestRetryStrategy> worldpayRequestRetryStrategyMock = new ArrayList<>();
    @Mock
    private WorldpayRequestRetryStrategy worldpayRequestRetryExemptionStrategyMock;
    @Mock
    private WorldpayRequestRetryStrategy worldpayRequestRetryEFTPOSStrategyMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private CSEAdditionalAuthInfo cseAdditionalAuthInfoMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private GooglePayAdditionalAuthInfo googlePayAdditionalAuthInfoMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private DirectAuthoriseServiceResponse directAuthoriseServiceResponseMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private DirectAuthoriseServiceRequest directAuthoriseServiceRequestMock, directAuthoriseForRedirectServiceRequestMock, directAuthoriseForApplePayServiceRequestMock;
    @Mock
    private SecondThreeDSecurePaymentRequest secondThreeDSecurePaymentRequest;
    @Mock
    private AddressModel paymentAddressModelMock;
    @Mock
    private WorldpayServiceGateway worldpayServiceGatewayMock;
    @Mock
    private CommerceCheckoutParameter commerceCheckoutParameterMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private CreateTokenServiceRequest createTokenServiceRequestMock;
    @Mock
    private DeleteTokenServiceRequest deleteTokenServiceRequestMock;
    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoModelMock, newlyCreatedCreditCardPaymentInfoModel;
    @Mock
    private PaymentInfoModel paymentInfoMock;
    @Mock
    private CreateTokenResponse createTokenResponseMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private DeleteTokenResponse deleteTokenResponseMock;
    @Mock
    private WorldpayRequestFactory worldpayRequestFactoryMock;
    @Mock
    private BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfoMock;
    @Mock
    private ErrorDetail errorDetailMock;
    @Mock
    private Amount amountMock;
    @Mock
    private UpdateTokenServiceRequest updateTokenServiceRequestMock;
    @Mock
    private UpdateTokenResponse updateTokenResponseMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private ApplePayAdditionalAuthInfo applePayAdditionalAuthInfoMock;
    @Mock
    private WorldpaySessionService worldpaySessionServiceMock;
    @Mock
    private SchemeResponse schemeResponseMock;
    @Mock
    private TokenReply tokenReplyMock;
    @Mock
    private TokenDetails tokenDetailsMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private Order orderMock;

    private final Boolean saveCard = Boolean.FALSE;

    @Test
    void authorise_ShouldNotStoreCookieAndEchoDataInSession_WhenItReceivesAnAuthRequestWithTokenForCSE() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);

        testObj.authorise(cartModelMock, worldpayAdditionalInfoDataMock);

        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_COOKIE_PARAM), anyString());
        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_ECHO_DATA_PARAM), anyString());
        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_WINDOW_SIZE), anyString());
    }

    @Test
    void authoriseBankTransfer_ShouldDoAAuthoriseRequestThroughGateway_WhenItReceivesARequestWithBankTransferPaymentMethod() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayRequestFactoryMock.buildDirectAuthoriseBankTransferRequest(merchantInfoMock, cartModelMock, bankTransferAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseForRedirectServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseForRedirectServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = testObj.authoriseBankTransfer(cartModelMock, bankTransferAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        assertEquals(directAuthoriseServiceResponseMock, directAuthoriseServiceResponse);
    }

    @Test
    void authorise_ShouldStoreCookieAndEchoDataInSession_WhenThreeDSecureFlowIsLegacy() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);

        testObj.authorise(cartModelMock, worldpayAdditionalInfoDataMock);

        verify(worldpaySessionServiceMock).setSessionAttributesFor3DSecure(directAuthoriseServiceResponseMock, worldpayAdditionalInfoDataMock);
    }

    @Test
    void authorise_ShouldStoreCookieAndEchoDataInSessionWhenThreeDSecureFlowIsThreeDS2_WhenItReceivesAn3DsFlexAuthRequest() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);

        testObj.authorise(cartModelMock, worldpayAdditionalInfoDataMock);

        verify(worldpaySessionServiceMock).setSessionAttributesFor3DSecure(directAuthoriseServiceResponseMock, worldpayAdditionalInfoDataMock);
    }

    @Test
    void authorise_ShouldCallDirectAuthoriseWithGateway_WhenItReceivesACSEAuthRequest() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);

        final DirectAuthoriseServiceResponse result = testObj.authorise(cartModelMock, worldpayAdditionalInfoDataMock);

        verify(worldpayServiceGatewayMock).directAuthorise(directAuthoriseServiceRequestMock);
        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    void authorise_ShouldCallDirectAuthoriseTwice_WhenDirectRequestWithExemptionIsToBeRetriedWithoutExemption() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        worldpayRequestRetryStrategyMock.add(worldpayRequestRetryEFTPOSStrategyMock);
        worldpayRequestRetryStrategyMock.add(worldpayRequestRetryExemptionStrategyMock);
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(worldpayRequestRetryExemptionStrategyMock.isRequestToBeRetried(directAuthoriseServiceRequestMock, directAuthoriseServiceResponseMock)).thenReturn(true);
        when(worldpayRequestRetryExemptionStrategyMock.getDirectAuthoriseServiceRequestToRetry(directAuthoriseServiceRequestMock, directAuthoriseServiceResponseMock))
                .thenReturn(directAuthoriseServiceRequestMock);
        when(directAuthoriseServiceRequestMock.getOrder()).thenReturn(orderMock);

        final DirectAuthoriseServiceResponse result = testObj.authorise(cartModelMock, worldpayAdditionalInfoDataMock);

        verify(worldpayServiceGatewayMock, times(2)).directAuthorise(directAuthoriseServiceRequestMock);
        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    void authorise_ShouldCallDirectAuthoriseTwice_WhenDirectRequestWithRoutingMIDIsToBeRetriedWithoutRoutingMID() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        worldpayRequestRetryStrategyMock.add(worldpayRequestRetryEFTPOSStrategyMock);
        worldpayRequestRetryStrategyMock.add(worldpayRequestRetryExemptionStrategyMock);
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(worldpayRequestRetryEFTPOSStrategyMock.isRequestToBeRetried(directAuthoriseServiceRequestMock, directAuthoriseServiceResponseMock)).thenReturn(true);
        when(worldpayRequestRetryEFTPOSStrategyMock.getDirectAuthoriseServiceRequestToRetry(directAuthoriseServiceRequestMock, directAuthoriseServiceResponseMock))
                .thenReturn(directAuthoriseServiceRequestMock);
        when(directAuthoriseServiceRequestMock.getOrder()).thenReturn(orderMock);

        final DirectAuthoriseServiceResponse result = testObj.authorise(cartModelMock, worldpayAdditionalInfoDataMock);

        verify(worldpayServiceGatewayMock, times(2)).directAuthorise(directAuthoriseServiceRequestMock);
        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    void authorise_ShouldCallDirectAuthoriseThrice_WhenDirectRequestWithExemptionAndRoutingMIDIsToBeRetriedWithoutExemptionAndRoutingMID() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        worldpayRequestRetryStrategyMock.add(worldpayRequestRetryEFTPOSStrategyMock);
        worldpayRequestRetryStrategyMock.add(worldpayRequestRetryExemptionStrategyMock);
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(worldpayRequestRetryEFTPOSStrategyMock.isRequestToBeRetried(directAuthoriseServiceRequestMock, directAuthoriseServiceResponseMock)).thenReturn(true);
        when(worldpayRequestRetryEFTPOSStrategyMock.getDirectAuthoriseServiceRequestToRetry(directAuthoriseServiceRequestMock, directAuthoriseServiceResponseMock))
                .thenReturn(directAuthoriseServiceRequestMock);
        when(worldpayRequestRetryExemptionStrategyMock.isRequestToBeRetried(directAuthoriseServiceRequestMock, directAuthoriseServiceResponseMock)).thenReturn(true);
        when(worldpayRequestRetryExemptionStrategyMock.getDirectAuthoriseServiceRequestToRetry(directAuthoriseServiceRequestMock, directAuthoriseServiceResponseMock))
                .thenReturn(directAuthoriseServiceRequestMock);
        when(directAuthoriseServiceRequestMock.getOrder()).thenReturn(orderMock);

        final DirectAuthoriseServiceResponse result = testObj.authorise(cartModelMock, worldpayAdditionalInfoDataMock);

        verify(worldpayServiceGatewayMock, times(3)).directAuthorise(directAuthoriseServiceRequestMock);
        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    void authorise3DSecure_ShouldRecoverEchoDataAndCookieFromSessionAndAddTo3dRequest_WhenItReceivesA3DsAuthRequest() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpaySessionServiceMock.getAndRemoveThreeDSecureCookie()).thenReturn(COOKIE);
        when(worldpayRequestFactoryMock.build3dDirectAuthoriseRequest(merchantInfoMock, ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE, COOKIE)).thenReturn(directAuthoriseServiceRequestMock);

        testObj.authorise3DSecure(ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);

        verify(worldpayServiceGatewayMock).directAuthorise(directAuthoriseServiceRequestMock);
        verify(worldpaySessionServiceMock).getAndRemoveThreeDSecureCookie();
    }

    @Test
    void createToken_shouldCreateToken_WhenHavingASuccessCreateTokenResponse() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayRequestFactoryMock.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(createTokenServiceRequestMock);
        when(createTokenResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getTokenDetails()).thenReturn(tokenDetailsMock);
        when(tokenDetailsMock.getTokenEvent()).thenReturn(TokenEvent.CONFLICT.name());
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponseMock);
        when(tokenDetailsMock.getTokenEvent()).thenReturn(TokenEvent.NEW.name());

        testObj.createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayPaymentInfoServiceMock).createCreditCardPaymentInfo(cartModelMock, tokenReplyMock, cseAdditionalAuthInfoMock.getSaveCard(), MERCHANT_CODE);
    }

    @Test
    void deleteToken_ShouldCallDeleteTokenOnWorldpayServiceGateway_WhenItIsCalled() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(deleteTokenResponseMock.isError()).thenReturn(false);
        when(worldpayRequestFactoryMock.buildTokenDeleteRequest(merchantInfoMock, creditCardPaymentInfoModelMock, SUBSCRIPTION_ID)).thenReturn(deleteTokenServiceRequestMock);
        when(worldpayServiceGatewayMock.deleteToken(deleteTokenServiceRequestMock)).thenReturn(deleteTokenResponseMock);

        testObj.deleteToken(merchantInfoMock, creditCardPaymentInfoModelMock, SUBSCRIPTION_ID);

        verify(worldpayServiceGatewayMock).deleteToken(any(DeleteTokenServiceRequest.class));
    }

    @Test
    void deleteToken_ShouldThrowException_WhenResponseHasError() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(deleteTokenResponseMock.isError()).thenReturn(false);
        when(worldpayRequestFactoryMock.buildTokenDeleteRequest(merchantInfoMock, creditCardPaymentInfoModelMock, SUBSCRIPTION_ID)).thenReturn(deleteTokenServiceRequestMock);
        when(worldpayServiceGatewayMock.deleteToken(deleteTokenServiceRequestMock)).thenReturn(deleteTokenResponseMock);
        when(deleteTokenResponseMock.isError()).thenReturn(true);

        assertThatThrownBy(() -> testObj.deleteToken(merchantInfoMock, creditCardPaymentInfoModelMock, SUBSCRIPTION_ID))
                .isInstanceOf(WorldpayException.class);
    }

    @Test
    void createToken_shouldRaiseError_WhenCreateTokenRequestContainsError() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayRequestFactoryMock.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(createTokenServiceRequestMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponseMock);
        when(createTokenResponseMock.isError()).thenReturn(true);
        when(createTokenResponseMock.getErrorDetail()).thenReturn(errorDetailMock);

        assertThatThrownBy(() -> testObj.createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock))
                .isInstanceOf(WorldpayException.class);

        verify(worldpayPaymentInfoServiceMock, never()).createCreditCardPaymentInfo(cartModelMock, tokenReplyMock, cseAdditionalAuthInfoMock.getSaveCard(), MERCHANT_CODE);
        verify(worldpayServiceGatewayMock).createToken(createTokenServiceRequestMock);
    }

    @Test
    void createToken_ShouldCreateCreditCard_WhenItGetsASuccessfulTokenResponse() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayRequestFactoryMock.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(createTokenServiceRequestMock);
        when(createTokenResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getTokenDetails()).thenReturn(tokenDetailsMock);
        when(tokenDetailsMock.getTokenEvent()).thenReturn(TokenEvent.CONFLICT.name());
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponseMock);
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(false);
        when(worldpayPaymentInfoServiceMock.createCreditCardPaymentInfo(cartModelMock, tokenReplyMock, false, MERCHANT_CODE)).thenReturn(creditCardPaymentInfoModelMock);
        when(tokenDetailsMock.getTokenEvent()).thenReturn(TokenEvent.NEW.name());

        testObj.createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayPaymentInfoServiceMock, never()).updateCreditCardPaymentInfo(eq(cartModelMock), any(UpdateTokenServiceRequest.class), eq(Boolean.FALSE));
        verify(worldpayPaymentInfoServiceMock).setPaymentInfoOnCart(cartModelMock, creditCardPaymentInfoModelMock);
    }

    @Test
    void createToken_ShouldUpdateToken_WhenThereIsAConflict() throws WorldpayException {
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayServiceGatewayMock.updateToken(updateTokenServiceRequestMock)).thenReturn(updateTokenResponseMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayRequestFactoryMock.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(createTokenServiceRequestMock);
        when(createTokenResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getTokenDetails()).thenReturn(tokenDetailsMock);
        when(tokenDetailsMock.getTokenEvent()).thenReturn(TokenEvent.CONFLICT.name());
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponseMock);
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(false);
        when(worldpayRequestFactoryMock.buildTokenUpdateRequest(merchantInfoMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock, paymentAddressModelMock, createTokenResponseMock)).thenReturn(updateTokenServiceRequestMock);
        when(worldpayPaymentInfoServiceMock.updateCreditCardPaymentInfo(cartModelMock, updateTokenServiceRequestMock, saveCard)).thenReturn(Optional.of(creditCardPaymentInfoModelMock));

        testObj.createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayPaymentInfoServiceMock, never()).createCreditCardPaymentInfo(cartModelMock, tokenReplyMock, false, MERCHANT_CODE);
        verify(worldpayServiceGatewayMock).updateToken(updateTokenServiceRequestMock);
        verify(worldpayPaymentInfoServiceMock).setPaymentInfoOnCart(cartModelMock, creditCardPaymentInfoModelMock);
    }

    @Test
    void createToken_ShouldCreateNewPaymentInfo_WhenNoCardIsUpdatedOnConflict() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayServiceGatewayMock.updateToken(updateTokenServiceRequestMock)).thenReturn(updateTokenResponseMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayRequestFactoryMock.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(createTokenServiceRequestMock);
        when(createTokenResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getTokenDetails()).thenReturn(tokenDetailsMock);
        when(tokenDetailsMock.getTokenEvent()).thenReturn(TokenEvent.CONFLICT.name());
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);

        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponseMock);
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(false);
        when(createTokenResponseMock.getToken().getTokenDetails().getTokenEvent()).thenReturn(TokenEvent.CONFLICT.name());
        when(worldpayRequestFactoryMock.buildTokenUpdateRequest(merchantInfoMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock, paymentAddressModelMock, createTokenResponseMock)).thenReturn(updateTokenServiceRequestMock);
        when(worldpayPaymentInfoServiceMock.updateCreditCardPaymentInfo(cartModelMock, updateTokenServiceRequestMock, saveCard)).thenReturn(Optional.empty());
        when(worldpayPaymentInfoServiceMock.createCreditCardPaymentInfo(cartModelMock, tokenReplyMock, false, MERCHANT_CODE)).thenReturn(newlyCreatedCreditCardPaymentInfoModel);

        testObj.createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayPaymentInfoServiceMock).createCreditCardPaymentInfo(cartModelMock, tokenReplyMock, false, MERCHANT_CODE);
        verify(worldpayServiceGatewayMock).updateToken(updateTokenServiceRequestMock);
        verify(worldpayPaymentInfoServiceMock).setPaymentInfoOnCart(cartModelMock, newlyCreatedCreditCardPaymentInfoModel);
    }

    @Test
    void createToken_ShouldThrowException_WhenThereIsAnErrorDuringUpdateResponse() throws WorldpayException {
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayServiceGatewayMock.updateToken(updateTokenServiceRequestMock)).thenReturn(updateTokenResponseMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayRequestFactoryMock.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(createTokenServiceRequestMock);
        when(createTokenResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getTokenDetails()).thenReturn(tokenDetailsMock);
        when(tokenDetailsMock.getTokenEvent()).thenReturn(TokenEvent.CONFLICT.name());
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);

        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponseMock);
        when(createTokenResponseMock.getToken().getTokenDetails().getTokenEvent()).thenReturn(TokenEvent.CONFLICT.name());
        when(worldpayRequestFactoryMock.buildTokenUpdateRequest(merchantInfoMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock, paymentAddressModelMock, createTokenResponseMock)).thenReturn(updateTokenServiceRequestMock);
        when(updateTokenResponseMock.isError()).thenReturn(true);
        when(updateTokenResponseMock.getErrorDetail()).thenReturn(errorDetailMock);

        assertThatThrownBy(() -> testObj.createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock))
                .isInstanceOf(WorldpayException.class);

        verify(worldpayPaymentInfoServiceMock, never()).createCreditCardPaymentInfo(any(), any(), anyBoolean(), any());
    }

    @Test
    void completeAuthorise_ShouldCreateNonPendingAuthorisePaymentTransactionEntry_WhenAuthorising() throws WorldpayException {
        when(worldpayOrderServiceMock.createCheckoutParameterAndSetPaymentInfo(creditCardPaymentInfoModelMock, BigDecimal.TEN, cartModelMock)).thenReturn(commerceCheckoutParameterMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAmount()).thenReturn(amountMock);
        when(worldpayOrderServiceMock.convertAmount(amountMock)).thenReturn(BigDecimal.TEN);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);
        when(worldpayPaymentTransactionServiceMock.createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, BigDecimal.TEN)).thenReturn(paymentTransactionEntryModelMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);

        when(cartModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(directAuthoriseServiceResponseMock.getToken()).thenReturn(tokenReplyMock);

        testObj.completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);

        verify(worldpayPaymentTransactionServiceMock).createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, BigDecimal.TEN);
        verify(worldpayPaymentInfoServiceMock).updateAndAttachPaymentInfoModel(paymentTransactionModelMock, cartModelMock, creditCardPaymentInfoModelMock);
        verify(worldpayPaymentTransactionServiceMock).addAavFields(paymentTransactionEntryModelMock, paymentReplyMock);
        verify(worldpayPaymentTransactionServiceMock).addRiskScore(paymentTransactionModelMock, paymentReplyMock);
        verify(worldpayPaymentTransactionServiceMock).addExemptionResponseToPaymentTransaction(paymentTransactionModelMock, paymentReplyMock);
    }

    @Test
    void completeAuthorise_ShouldCreateNonPendingAuthorisePaymentTransactionEntry_WhenAuthorisingOrderModel() throws WorldpayException {
        when(worldpayOrderServiceMock.createCommerceCheckoutParameter(orderModelMock, creditCardPaymentInfoModelMock, BigDecimal.TEN)).thenReturn(commerceCheckoutParameterMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAmount()).thenReturn(amountMock);
        when(worldpayOrderServiceMock.convertAmount(amountMock)).thenReturn(BigDecimal.TEN);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);
        when(worldpayPaymentTransactionServiceMock.createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, orderModelMock, BigDecimal.TEN)).thenReturn(paymentTransactionEntryModelMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);

        when(orderModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(directAuthoriseServiceResponseMock.getToken()).thenReturn(tokenReplyMock);

        testObj.completeAuthorise(directAuthoriseServiceResponseMock, orderModelMock);

        verify(worldpayOrderServiceMock).createCommerceCheckoutParameter(orderModelMock, creditCardPaymentInfoModelMock, BigDecimal.TEN);
    }

    @Test
    void authoriseRecurringPayment_shouldGetADirectAuthoriseResponse_WhenPaymentMethodIsRecurringPayment() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);

        final DirectAuthoriseServiceResponse result = testObj.authoriseRecurringPayment(cartModelMock, worldpayAdditionalInfoDataMock);

        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_COOKIE_PARAM), anyString());
        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_ECHO_DATA_PARAM), anyString());
        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_WINDOW_SIZE), anyString());
        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    void authoriseRecurringPayment_ShouldAddSessionAttributes_WhenResponseContainsRequest3DInfoOnRecurringPayment() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);

        final DirectAuthoriseServiceResponse result = testObj.authoriseRecurringPayment(cartModelMock, worldpayAdditionalInfoDataMock);

        verify(worldpaySessionServiceMock).setSessionAttributesFor3DSecure(directAuthoriseServiceResponseMock, worldpayAdditionalInfoDataMock);
        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    void authoriseKlarna_ShouldReturnADirectAuthoriseResponse_WhenPaymentMethodIsKlarna() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayRequestFactoryMock.buildDirectAuthoriseKlarnaRequest(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock)).thenReturn(directAuthoriseForRedirectServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseForRedirectServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectAuthoriseServiceResponse result = testObj.authoriseKlarna(cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock);

        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    void authoriseGooglePay_ShouldReturnADirectAuthoriseResponse_WhenPaymentMethodIsGooglePay() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayRequestFactoryMock.buildDirectAuthoriseGooglePayRequest(merchantInfoMock, cartModelMock, googlePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseForRedirectServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseForRedirectServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectAuthoriseServiceResponse result = testObj.authoriseGooglePay(cartModelMock, googlePayAdditionalAuthInfoMock);

        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    void authoriseApplePay_ShouldReturnADirectAuthoriseResponse_WhenPaymentMethodIsApplePay() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayRequestFactoryMock.buildApplePayDirectAuthorisationRequest(merchantInfoMock, cartModelMock, applePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseForApplePayServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseForApplePayServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = testObj.authoriseApplePay(cartModelMock, applePayAdditionalAuthInfoMock);

        assertEquals(directAuthoriseServiceResponseMock, directAuthoriseServiceResponse);
    }

    @Test
    void completeAuthorise_ShouldSetTransactionIdentifierOnPaymentInfo_WhenTransactionIdentifierIsOnDirectAuthoriseServiceResponse() throws WorldpayConfigurationException {
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(directAuthoriseServiceResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getTokenDetails()).thenReturn(tokenDetailsMock);
        when(tokenDetailsMock.getTokenEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);
        when(paymentReplyMock.getSchemeResponse()).thenReturn(schemeResponseMock);
        when(schemeResponseMock.getTransactionIdentifier()).thenReturn(TRANSACTION_IDENTIFIER);
        when(worldpayPaymentInfoServiceMock.createCreditCardPaymentInfo(cartModelMock, tokenReplyMock, true, MERCHANT_CODE)).thenReturn(creditCardPaymentInfoModelMock);

        testObj.completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);

        verify(worldpayPaymentInfoServiceMock).setTransactionIdentifierOnPaymentInfo(creditCardPaymentInfoModelMock, TRANSACTION_IDENTIFIER);
    }

    @Test
    void createTokenAndAuthorise_ShouldThrowAWorldpayException_WhenWorldpayServiceGateWayThrowsIt() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayRequestFactoryMock.buildDirectTokenAndAuthorise(merchantInfoMock, cartModelMock,
                worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenThrow(new WorldpayException("Error during authorise"));

        assertThatThrownBy(() -> testObj.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock))
                .isInstanceOf(WorldpayException.class);
    }

    @Test
    void createTokenAndAuthorise_ShouldReturnADirectAuthoriseServiceResponse_WhenItIsCall() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayRequestFactoryMock.buildDirectTokenAndAuthorise(merchantInfoMock, cartModelMock,
                worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);

        testObj.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);

        verify(worldpayRequestFactoryMock).buildDirectTokenAndAuthorise(merchantInfoMock, cartModelMock,
                worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);
        verify(worldpayServiceGatewayMock).directAuthorise(directAuthoriseServiceRequestMock);
    }

    @Test
    void createTokenAndAuthorise_ShouldThrowException_WhenAuthServiceResponseHasError() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(directAuthoriseServiceResponseMock.getErrorDetail().getMessage()).thenReturn("something went wrong");
        when(worldpayRequestFactoryMock.buildDirectTokenAndAuthorise(merchantInfoMock, cartModelMock,
                worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);

        when(directAuthoriseServiceResponseMock.isError()).thenReturn(true);

        assertThatThrownBy(() -> testObj.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock))
                .isInstanceOf(WorldpayException.class);
    }

    @Test
    void authorise3DSecureAgain_ShouldGetSessionIdAndCookieValueFromSessionStorage() throws WorldpayException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpaySessionServiceMock.getAndRemoveAdditionalDataSessionId()).thenReturn(SESSION_ID);
        when(worldpaySessionServiceMock.getAndRemoveThreeDSecureCookie()).thenReturn(COOKIE);
        when(worldpayRequestFactoryMock.buildSecondThreeDSecurePaymentRequest(merchantInfoMock, ORDER_CODE, SESSION_ID, COOKIE)).thenReturn(secondThreeDSecurePaymentRequest);

        testObj.authorise3DSecureAgain(ORDER_CODE);

        verify(worldpayServiceGatewayMock).sendSecondThreeDSecurePayment(secondThreeDSecurePaymentRequest);
    }

    @Test
    void completeAuthoriseGooglePay_ShouldCreatePaymentTransaction() {
        when(cartModelMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAmount()).thenReturn(amountMock);
        when(worldpayOrderServiceMock.convertAmount(amountMock)).thenReturn(BigDecimal.TEN);
        when(worldpayOrderServiceMock.createCheckoutParameterAndSetPaymentInfo(paymentInfoMock, BigDecimal.TEN, cartModelMock)).thenReturn(commerceCheckoutParameterMock);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completeAuthoriseGooglePay(directAuthoriseServiceResponseMock, cartModelMock, MERCHANT_CODE);

        final InOrder inOrder = inOrder(worldpayPaymentInfoServiceMock, worldpayPaymentTransactionServiceMock);
        inOrder.verify(worldpayPaymentInfoServiceMock).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoMock);
        inOrder.verify(worldpayPaymentTransactionServiceMock).createPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, BigDecimal.TEN);
        inOrder.verify(worldpayPaymentInfoServiceMock).updateAndAttachPaymentInfoModel(paymentTransactionModelMock, cartModelMock, paymentInfoMock);
    }

    @Test
    void completeAuthoriseGooglePay_ShouldDoNothing_WhenOrderModel() {
        when(orderModelMock.getPaymentInfo()).thenReturn(paymentInfoMock);

        testObj.completeAuthoriseGooglePay(directAuthoriseServiceResponseMock, orderModelMock, MERCHANT_CODE);

        verifyNoInteractions(worldpayPaymentTransactionServiceMock);
        verifyNoInteractions(worldpayPaymentInfoServiceMock);
    }
}

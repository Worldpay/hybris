package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.*;
import com.worldpay.data.payment.Card;
import com.worldpay.data.token.TokenDetails;
import com.worldpay.data.token.TokenReply;
import com.worldpay.enums.order.ThreeDSecureFlowEnum;
import com.worldpay.enums.token.TokenEvent;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpaySessionService;
import com.worldpay.service.payment.request.WorldpayRequestFactory;
import com.worldpay.service.payment.request.WorldpayRequestService;
import com.worldpay.service.request.*;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.DeleteTokenResponse;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.service.response.UpdateTokenResponse;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;
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
import de.hybris.platform.servicelayer.user.AddressService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayDirectOrderServiceTest {

    private static final String SHOPPER_EMAIL_ADDRESS = "shopperEmailAddress";
    private static final String STATEMENT_NARRATIVE = "statementNarrative";
    private static final String ORDER_CODE = "orderCode";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String PA_RESPONSE = "paResponse";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String BANK_TRANSFER_PAYMENT_METHOD = "bankTransferPaymentMethod";
    private static final String BANK_CODE = "bankCode";
    private static final String ECHO_DATA = "echoData";
    private static final String COOKIE = "cookie";
    private static final String WINDOW_SIZE = ChallengeWindowSizeEnum.R_250_400.toString();
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
    @Mock
    private WorldpayRequestService worldpayRequestServiceMock;

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
    @Mock
    private Session sessionMock;
    @Mock
    private Browser browserMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private DirectAuthoriseServiceResponse directAuthoriseServiceResponseMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private DirectAuthoriseServiceRequest directAuthoriseServiceRequestMock, directAuthoriseForRedirectServiceRequestMock, directAuthoriseForApplePayServiceRequestMock;
    @Mock
    private SecondThreeDSecurePaymentRequest secondThreeDSecurePaymentRequest;
    @Mock
    private AddressModel deliveryAddressModelMock, paymentAddressModelMock;
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
    private Shopper shopperMock;
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
    private AddressService addressServiceMock;
    @Mock
    private WorldpaySessionService worldpaySessionServiceMock;
    @Mock
    private SchemeResponse schemeResponseMock;
    @Mock
    private TokenReply tokenReplyMock;
    @Mock
    private TokenDetails tokenDetailsMock;
    @Mock
    private Card cardMock;
    @Mock
    private Additional3DS2Info aditional3DSInfoMock;
    @Mock
    private OrderModel orderMock;

    private Boolean saveCard = Boolean.FALSE;

    @Before
    public void setUp() throws WorldpayException {
        when(worldpayOrderServiceMock.createCheckoutParameterAndSetPaymentInfo(creditCardPaymentInfoModelMock, BigDecimal.TEN, cartModelMock)).thenReturn(commerceCheckoutParameterMock);
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAmount()).thenReturn(amountMock);
        when(worldpayOrderServiceMock.convertAmount(amountMock)).thenReturn(BigDecimal.TEN);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayServiceGatewayMock.updateToken(updateTokenServiceRequestMock)).thenReturn(updateTokenResponseMock);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);
        when(worldpayPaymentTransactionServiceMock.createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, BigDecimal.TEN)).thenReturn(paymentTransactionEntryModelMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayRequestFactoryMock.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(createTokenServiceRequestMock);
        when(createTokenResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getTokenDetails()).thenReturn(tokenDetailsMock);
        when(tokenDetailsMock.getTokenEvent()).thenReturn(TokenEvent.CONFLICT.name());
        when(worldpayMerchantInfoService.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(deleteTokenResponseMock.isError()).thenReturn(false);
        when(directAuthoriseServiceResponseMock.getErrorDetail().getMessage()).thenReturn("something went wrong");
    }

    @Test
    public void authorise_ShouldNotStoreCookieAndEchoDataInSession_WhenItReceivesAnAuthRequestWithTokenForCSE() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);

        testObj.authorise(cartModelMock, worldpayAdditionalInfoDataMock);

        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_COOKIE_PARAM), anyString());
        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_ECHO_DATA_PARAM), anyString());
        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_WINDOW_SIZE), anyString());
    }

    @Test
    public void authoriseBankTransfer_ShouldDoAAuthoriseRequestThroughGateway_WhenItReceivesARequestWithBankTransferPaymentMethod() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseBankTransferRequest(merchantInfoMock, cartModelMock, bankTransferAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseForRedirectServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseForRedirectServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = testObj.authoriseBankTransfer(cartModelMock, bankTransferAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        assertEquals(directAuthoriseServiceResponseMock, directAuthoriseServiceResponse);
    }

    @Test
    public void authorise_ShouldStoreCookieAndEchoDataInSession_WhenThreeDSecureFlowIsLegacy() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(directAuthoriseServiceResponseMock.get3DSecureFlow()).thenReturn(Optional.of(ThreeDSecureFlowEnum.LEGACY_FLOW));

        testObj.authorise(cartModelMock, worldpayAdditionalInfoDataMock);

        verify(worldpaySessionServiceMock).setSessionAttributesFor3DSecure(directAuthoriseServiceResponseMock, worldpayAdditionalInfoDataMock);
    }

    @Test
    public void authorise_ShouldStoreCookieAndEchoDataInSessionWhenThreeDSecureFlowIsThreeDS2_WhenItReceivesAn3DsFlexAuthRequest() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(directAuthoriseServiceResponseMock.get3DSecureFlow()).thenReturn(Optional.of(ThreeDSecureFlowEnum.THREEDSFLEX_FLOW));

        testObj.authorise(cartModelMock, worldpayAdditionalInfoDataMock);

        verify(worldpaySessionServiceMock).setSessionAttributesFor3DSecure(directAuthoriseServiceResponseMock, worldpayAdditionalInfoDataMock);
    }

    @Test
    public void authorise_ShouldCallDirectAuthoriseWithGateway_WhenItReceivesACSEAuthRequest() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);

        final DirectAuthoriseServiceResponse result = testObj.authorise(cartModelMock, worldpayAdditionalInfoDataMock);

        verify(worldpayServiceGatewayMock).directAuthorise(directAuthoriseServiceRequestMock);
        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    public void authorise3DSecure_ShouldRecoverEchoDataAndCookieFromSessionAndAddTo3dRequest_WhenItReceivesA3DsAuthRequest() throws WorldpayException {
        when(worldpaySessionServiceMock.getAndRemoveThreeDSecureCookie()).thenReturn(COOKIE);
        when(worldpayRequestFactoryMock.build3dDirectAuthoriseRequest(merchantInfoMock, ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE, COOKIE)).thenReturn(directAuthoriseServiceRequestMock);

        testObj.authorise3DSecure(ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);

        verify(worldpayServiceGatewayMock).directAuthorise(directAuthoriseServiceRequestMock);
        verify(worldpaySessionServiceMock).getAndRemoveThreeDSecureCookie();
    }

    @Test
    public void createToken_shouldCreateToken_WhenHavingASuccessCreateTokenResponse() throws WorldpayException {
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponseMock);
        when(tokenDetailsMock.getTokenEvent()).thenReturn(TokenEvent.NEW.name());

        testObj.createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayPaymentInfoServiceMock).createCreditCardPaymentInfo(cartModelMock, tokenReplyMock, cseAdditionalAuthInfoMock.getSaveCard(), MERCHANT_CODE);
    }

    @Test
    public void deleteToken_ShouldCallDeleteTokenOnWorldpayServiceGateway_WhenItIsCalled() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildTokenDeleteRequest(merchantInfoMock, creditCardPaymentInfoModelMock, SUBSCRIPTION_ID)).thenReturn(deleteTokenServiceRequestMock);
        when(worldpayServiceGatewayMock.deleteToken(deleteTokenServiceRequestMock)).thenReturn(deleteTokenResponseMock);

        testObj.deleteToken(merchantInfoMock, creditCardPaymentInfoModelMock, SUBSCRIPTION_ID);

        verify(worldpayServiceGatewayMock).deleteToken(any(DeleteTokenServiceRequest.class));
    }


    @Test(expected = WorldpayException.class)
    public void deleteToken_ShouldThrowException_WhenResponseHasError() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildTokenDeleteRequest(merchantInfoMock, creditCardPaymentInfoModelMock, SUBSCRIPTION_ID)).thenReturn(deleteTokenServiceRequestMock);
        when(worldpayServiceGatewayMock.deleteToken(deleteTokenServiceRequestMock)).thenReturn(deleteTokenResponseMock);
        when(deleteTokenResponseMock.isError()).thenReturn(true);

        testObj.deleteToken(merchantInfoMock, creditCardPaymentInfoModelMock, SUBSCRIPTION_ID);
    }

    @Test(expected = WorldpayException.class)
    public void createToken_shouldRaiseError_WhenCreateTokenRequestContainsError() throws WorldpayException {
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponseMock);
        when(createTokenResponseMock.isError()).thenReturn(true);
        when(createTokenResponseMock.getErrorDetail()).thenReturn(errorDetailMock);

        testObj.createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayPaymentInfoServiceMock, never()).createCreditCardPaymentInfo(cartModelMock, tokenReplyMock, cseAdditionalAuthInfoMock.getSaveCard(), MERCHANT_CODE);
        verify(worldpayServiceGatewayMock);
    }

    @Test
    public void createToken_ShouldCreateCreditCard_WhenItGetsASuccessfulTokenResponse() throws WorldpayException {
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponseMock);
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(false);
        when(worldpayPaymentInfoServiceMock.createCreditCardPaymentInfo(cartModelMock, tokenReplyMock, false, MERCHANT_CODE)).thenReturn(creditCardPaymentInfoModelMock);
        when(tokenDetailsMock.getTokenEvent()).thenReturn(TokenEvent.NEW.name());

        testObj.createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayPaymentInfoServiceMock, never()).updateCreditCardPaymentInfo(eq(cartModelMock), any(UpdateTokenServiceRequest.class), eq(Boolean.FALSE));
        verify(worldpayPaymentInfoServiceMock).setPaymentInfoOnCart(cartModelMock, creditCardPaymentInfoModelMock);
    }

    @Test
    public void createToken_ShouldUpdateToken_WhenThereIsAConflict() throws WorldpayException {
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
    public void createToken_ShouldCreateNewPaymentInfo_WhenNoCardIsUpdatedOnConflict() throws WorldpayException {
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

    @Test(expected = WorldpayException.class)
    public void createToken_ShouldThrowException_WhenThereIsAnErrorDuringUpdateResponse() throws WorldpayException {
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponseMock);
        when(createTokenResponseMock.getToken().getTokenDetails().getTokenEvent()).thenReturn(TokenEvent.CONFLICT.name());
        when(worldpayRequestFactoryMock.buildTokenUpdateRequest(merchantInfoMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock, paymentAddressModelMock, createTokenResponseMock)).thenReturn(updateTokenServiceRequestMock);
        when(updateTokenResponseMock.isError()).thenReturn(true);
        when(updateTokenResponseMock.getErrorDetail()).thenReturn(errorDetailMock);

        testObj.createToken(cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verifyNoInteractions(worldpayPaymentInfoServiceMock.createCreditCardPaymentInfo(any(), any(), any(), any()));
    }

    @Test
    public void completeAuthorise_ShouldCreateNonPendingAuthorisePaymentTransactionEntry_WhenAuthorising() throws WorldpayConfigurationException {
        when(cartModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(directAuthoriseServiceResponseMock.getToken()).thenReturn(tokenReplyMock);

        testObj.completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);

        verify(worldpayPaymentTransactionServiceMock).createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, BigDecimal.TEN);
        verify(worldpayPaymentInfoServiceMock).updateAndAttachPaymentInfoModel(paymentTransactionModelMock, cartModelMock, creditCardPaymentInfoModelMock);
        verify(worldpayPaymentTransactionServiceMock).addAavFields(paymentTransactionEntryModelMock, paymentReplyMock);
        verify(worldpayPaymentTransactionServiceMock).addRiskScore(paymentTransactionModelMock, paymentReplyMock);
    }

    @Test
    public void completeAuthorise_ShouldCreateNonPendingAuthorisePaymentTransactionEntry_WhenAuthorisingOrderModel() throws WorldpayConfigurationException {
        when(orderMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(directAuthoriseServiceResponseMock.getToken()).thenReturn(tokenReplyMock);

        testObj.completeAuthorise(directAuthoriseServiceResponseMock, orderMock);

        verify(worldpayOrderServiceMock).createCommerceCheckoutParameter(orderMock, creditCardPaymentInfoModelMock, BigDecimal.TEN);
    }

    @Test
    public void completeAuthorise3DSecure_ShouldCompleteAuthorise_WhenComplete3DAuthorise() throws WorldpayConfigurationException {
        when(directAuthoriseServiceRequestMock.getMerchantInfo().getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayPaymentTransactionServiceMock.createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, BigDecimal.TEN)).thenReturn(paymentTransactionEntryModelMock);
        when(worldpayPaymentInfoServiceMock.createCreditCardPaymentInfo(cartModelMock, tokenReplyMock, false, MERCHANT_CODE)).thenReturn(creditCardPaymentInfoModelMock);
        when(directAuthoriseServiceResponseMock.getToken()).thenReturn(tokenReplyMock);

        testObj.completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponseMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, creditCardPaymentInfoModelMock);
        verify(worldpayOrderServiceMock).createCheckoutParameterAndSetPaymentInfo(creditCardPaymentInfoModelMock, BigDecimal.TEN, cartModelMock);
        verify(worldpayPaymentInfoServiceMock).updateAndAttachPaymentInfoModel(paymentTransactionModelMock, cartModelMock, creditCardPaymentInfoModelMock);
        verify(worldpayPaymentTransactionServiceMock).addAavFields(paymentTransactionEntryModelMock, paymentReplyMock);
        verify(worldpayPaymentTransactionServiceMock).addRiskScore(paymentTransactionModelMock, paymentReplyMock);
    }

    @Test
    public void authoriseRecurringPayment_shouldGetADirectAuthoriseResponse_WhenPaymentMethodIsRecurringPayment() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);

        final DirectAuthoriseServiceResponse result = testObj.authoriseRecurringPayment(cartModelMock, worldpayAdditionalInfoDataMock);

        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_COOKIE_PARAM), anyString());
        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_ECHO_DATA_PARAM), anyString());
        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_WINDOW_SIZE), anyString());
        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    public void authoriseRecurringPayment_ShouldAddSessionAttributes_WhenResponseContainsRequest3DInfoOnRecurringPayment() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(directAuthoriseServiceResponseMock.get3DSecureFlow()).thenReturn(Optional.of(ThreeDSecureFlowEnum.LEGACY_FLOW));

        final DirectAuthoriseServiceResponse result = testObj.authoriseRecurringPayment(cartModelMock, worldpayAdditionalInfoDataMock);

        verify(worldpaySessionServiceMock).setSessionAttributesFor3DSecure(directAuthoriseServiceResponseMock, worldpayAdditionalInfoDataMock);
        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    public void authoriseKlarna_ShouldReturnADirectAuthoriseResponse_WhenPaymentMethodIsKlarna() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseKlarnaRequest(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock)).thenReturn(directAuthoriseForRedirectServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseForRedirectServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectAuthoriseServiceResponse result = testObj.authoriseKlarna(cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock);

        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    public void authoriseGooglePay_ShouldReturnADirectAuthoriseResponse_WhenPaymentMethodIsGooglePay() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseGooglePayRequest(merchantInfoMock, cartModelMock, googlePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseForRedirectServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseForRedirectServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectAuthoriseServiceResponse result = testObj.authoriseGooglePay(cartModelMock, googlePayAdditionalAuthInfoMock);

        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    public void authoriseApplePay_ShouldReturnADirectAuthoriseResponse_WhenPaymentMethodIsApplePay() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildApplePayDirectAuthorisationRequest(merchantInfoMock, cartModelMock, applePayAdditionalAuthInfoMock)).thenReturn(directAuthoriseForApplePayServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseForApplePayServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = testObj.authoriseApplePay(cartModelMock, applePayAdditionalAuthInfoMock);

        assertEquals(directAuthoriseServiceResponseMock, directAuthoriseServiceResponse);
    }

    @Test
    public void completeAuthorise_ShouldSetTransactionIdentifierOnPaymentInfo_WhenTransactionIdentifierIsOnDirectAuthoriseServiceResponse() throws WorldpayConfigurationException {
        when(paymentReplyMock.getSchemeResponse()).thenReturn(schemeResponseMock);
        when(schemeResponseMock.getTransactionIdentifier()).thenReturn(TRANSACTION_IDENTIFIER);
        when(worldpayPaymentInfoServiceMock.createCreditCardPaymentInfo(cartModelMock, tokenReplyMock, true, MERCHANT_CODE)).thenReturn(creditCardPaymentInfoModelMock);
        when(directAuthoriseServiceResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getTokenDetails()).thenReturn(tokenDetailsMock);
        when(tokenDetailsMock.getTokenEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);

        testObj.completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock);

        verify(worldpayPaymentInfoServiceMock).setTransactionIdentifierOnPaymentInfo(creditCardPaymentInfoModelMock, TRANSACTION_IDENTIFIER);
    }

    @Test(expected = WorldpayException.class)
    public void createTokenAndAuthorise_ShouldThrowAWorldpayException_WhenWorldpayServiceGateWayThrowsIt() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectTokenAndAuthorise(merchantInfoMock, cartModelMock,
            worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenThrow(new WorldpayException("Error during authorise"));

        testObj.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);
    }

    @Test
    public void createTokenAndAuthorise_ShouldReturnADirectAuthoriseServiceResponse_WhenItIsCall() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectTokenAndAuthorise(merchantInfoMock, cartModelMock,
            worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);

        testObj.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);

        verify(worldpayRequestFactoryMock).buildDirectTokenAndAuthorise(merchantInfoMock, cartModelMock,
            worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);
        verify(worldpayServiceGatewayMock).directAuthorise(directAuthoriseServiceRequestMock);
    }

    @Test(expected = WorldpayException.class)
    public void createTokenAndAuthorise_ShouldThrowException_WhenAuthServiceResponseHasError() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectTokenAndAuthorise(merchantInfoMock, cartModelMock,
            worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.isError()).thenReturn(true);

        testObj.createTokenAndAuthorise(cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);
    }

    @Test
    public void authorise3DSecureAgain_ShouldGetSessionIdAndCookieValueFromSessionStorage() throws WorldpayException {
        when(worldpaySessionServiceMock.getAndRemoveAdditionalDataSessionId()).thenReturn(SESSION_ID);
        when(worldpaySessionServiceMock.getAndRemoveThreeDSecureCookie()).thenReturn(COOKIE);
        when(worldpayRequestFactoryMock.buildSecondThreeDSecurePaymentRequest(merchantInfoMock, ORDER_CODE, SESSION_ID, COOKIE)).thenReturn(secondThreeDSecurePaymentRequest);

        testObj.authorise3DSecureAgain(ORDER_CODE);

        verify(worldpayServiceGatewayMock).sendSecondThreeDSecurePayment(secondThreeDSecurePaymentRequest);
    }

    @Test
    public void completeAuthoriseGooglePay_ShouldCreatePaymentTransaction_WhenCartModel() {
        when(cartModelMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(worldpayOrderServiceMock.createCheckoutParameterAndSetPaymentInfo(paymentInfoMock, BigDecimal.TEN, cartModelMock)).thenReturn(commerceCheckoutParameterMock);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completeAuthoriseGooglePay(directAuthoriseServiceResponseMock, cartModelMock, MERCHANT_CODE);

        final InOrder inOrder = inOrder(worldpayPaymentInfoServiceMock, worldpayPaymentTransactionServiceMock);
        inOrder.verify(worldpayPaymentInfoServiceMock).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoMock);
        inOrder.verify(worldpayPaymentTransactionServiceMock).createPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, BigDecimal.TEN);
        inOrder.verify(worldpayPaymentInfoServiceMock).updateAndAttachPaymentInfoModel(paymentTransactionModelMock, cartModelMock, paymentInfoMock);
    }

    @Test
    public void completeAuthoriseGooglePay_ShouldDoNothing_WhenOrderModel() {
        when(orderMock.getPaymentInfo()).thenReturn(paymentInfoMock);

        testObj.completeAuthoriseGooglePay(directAuthoriseServiceResponseMock, orderMock, MERCHANT_CODE);

        verifyNoInteractions(worldpayPaymentTransactionServiceMock);
        verifyNoInteractions(worldpayPaymentInfoServiceMock);
    }
}

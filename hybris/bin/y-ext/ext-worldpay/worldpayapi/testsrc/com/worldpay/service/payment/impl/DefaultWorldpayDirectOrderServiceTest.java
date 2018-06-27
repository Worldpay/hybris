package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.enums.token.TokenEvent;
import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.request.WorldpayRequestFactory;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.DeleteTokenServiceRequest;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.DeleteTokenResponse;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.service.response.UpdateTokenResponse;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.AddressService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
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
    private static final String THREE_D_SECURE_ECHO_DATA_PARAM = "3DSecureEchoData";
    private static final String THREE_D_SECURE_COOKIE_PARAM = "3DSecureCookie";

    @Spy
    @InjectMocks
    private DefaultWorldpayDirectOrderService testObj;

    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private CommerceCheckoutService commerceCheckoutServiceMock;
    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private CartModel cartModelMock;
    @Mock
    private CSEAdditionalAuthInfo cseAdditionalAuthInfoMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private Session sessionMock;
    @Mock
    private Browser browserMock;
    @Mock
    private DirectAuthoriseServiceResponse directAuthoriseServiceResponseMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private DirectAuthoriseServiceRequest directAuthoriseServiceRequestMock, directAuthoriseForRedirectServiceRequestMock;
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
    private Request3DInfo request3DInfoMock;
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
    @Mock(answer = RETURNS_DEEP_STUBS)
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
    private AddressService addressServiceMock;

    @Before
    public void setUp() throws WorldpayException {
        doReturn(commerceCheckoutParameterMock).when(testObj).createCommerceCheckoutParameter(cartModelMock, creditCardPaymentInfoModelMock, BigDecimal.TEN.setScale(2, BigDecimal.ROUND_CEILING));
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);
        when(cseAdditionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AUTHORISED);
        when(paymentReplyMock.getAmount()).thenReturn(amountMock);
        when(amountMock.getCurrencyCode()).thenReturn("GBP");
        when(amountMock.getValue()).thenReturn("1000");
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayServiceGatewayMock.updateToken(updateTokenServiceRequestMock)).thenReturn(updateTokenResponseMock);
        when(worldpayOrderServiceMock.createShopper(SHOPPER_EMAIL_ADDRESS, sessionMock, browserMock)).thenReturn(shopperMock);
        when(shopperMock.getSession()).thenReturn(sessionMock);
        when(cartModelMock.getCode()).thenReturn(ORDER_CODE);
        when(addressServiceMock.cloneAddressForOwner(paymentAddressModelMock, paymentTransactionModelMock)).thenReturn(deliveryAddressModelMock);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);
        when(worldpayPaymentTransactionServiceMock.createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP))).thenReturn(paymentTransactionEntryModelMock);
        when(worldpayAdditionalInfoDataMock.getAuthenticatedShopperId()).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayRequestFactoryMock.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(createTokenServiceRequestMock);
        when(directAuthoriseServiceResponseMock.getCookie()).thenReturn(COOKIE);
        when(directAuthoriseServiceResponseMock.getEchoData()).thenReturn(ECHO_DATA);
        when(createTokenResponseMock.getToken().getPaymentInstrument().getPaymentType()).thenReturn(PaymentType.VISA);
    }

    @Test
    public void shouldNotStoreCookieAndEchoDataInSession() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequest(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(directAuthoriseServiceResponseMock.getRequest3DInfo()).thenReturn(null);

        testObj.authorise(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_COOKIE_PARAM), anyString());
        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_ECHO_DATA_PARAM), anyString());
    }

    @Test
    public void authoriseBankTransferShouldBuildRequestWithBankTransferPaymentMethod() throws WorldpayException {
        when(bankTransferAdditionalAuthInfoMock.getPaymentMethod()).thenReturn(BANK_TRANSFER_PAYMENT_METHOD);
        when(bankTransferAdditionalAuthInfoMock.getShopperBankCode()).thenReturn(BANK_CODE);
        when(bankTransferAdditionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(worldpayRequestFactoryMock.buildDirectAuthoriseBankTransferRequest(merchantInfoMock, cartModelMock, bankTransferAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseForRedirectServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseForRedirectServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = testObj.authoriseBankTransfer(merchantInfoMock, cartModelMock, bankTransferAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        assertEquals(directAuthoriseServiceResponseMock, directAuthoriseServiceResponse);
    }

    @Test
    public void shouldStoreCookieAndEchoDataInSession() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequest(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(directAuthoriseServiceResponseMock.getRequest3DInfo()).thenReturn(request3DInfoMock);

        testObj.authorise(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        verify(sessionServiceMock).setAttribute(THREE_D_SECURE_COOKIE_PARAM, COOKIE);
        verify(sessionServiceMock).setAttribute(THREE_D_SECURE_ECHO_DATA_PARAM, ECHO_DATA);
    }

    @Test
    public void shouldDirectAuthoriseWithGateway() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequest(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);

        final DirectAuthoriseServiceResponse result = testObj.authorise(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        verify(worldpayServiceGatewayMock).directAuthorise(directAuthoriseServiceRequestMock);
        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    public void shouldRecoverEchoDataAndCookieFromSessionAndAddTo3dRequest() throws WorldpayException {
        when(worldpayRequestFactoryMock.build3dDirectAuthoriseRequest(merchantInfoMock, ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE, COOKIE)).thenReturn(directAuthoriseServiceRequestMock);
        when(sessionServiceMock.getAttribute(THREE_D_SECURE_COOKIE_PARAM)).thenReturn(COOKIE);
        when(sessionServiceMock.getAttribute(THREE_D_SECURE_ECHO_DATA_PARAM)).thenReturn(ECHO_DATA);

        testObj.authorise3DSecure(merchantInfoMock, ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE);

        verify(sessionServiceMock).removeAttribute(THREE_D_SECURE_COOKIE_PARAM);
        verify(worldpayServiceGatewayMock).directAuthorise(directAuthoriseServiceRequestMock);
    }

    @Test
    public void shouldCreateToken() throws WorldpayException {
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponseMock);

        testObj.createToken(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayPaymentInfoServiceMock).createCreditCardPaymentInfo(cartModelMock, createTokenResponseMock, cseAdditionalAuthInfoMock.getSaveCard(), MERCHANT_CODE);
    }

    @Test
    public void shouldDeleteToken() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildTokenDeleteRequest(merchantInfoMock, creditCardPaymentInfoModelMock)).thenReturn(deleteTokenServiceRequestMock);
        when(worldpayServiceGatewayMock.deleteToken(deleteTokenServiceRequestMock)).thenReturn(deleteTokenResponseMock);
        when(deleteTokenResponseMock.isError()).thenReturn(false);

        testObj.deleteToken(merchantInfoMock, creditCardPaymentInfoModelMock);

        verify(worldpayServiceGatewayMock).deleteToken(any(DeleteTokenServiceRequest.class));
    }

    @Test(expected = WorldpayException.class)
    public void shouldRaiseErrorWhenCreateTokenRequestContainsError() throws WorldpayException {
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponseMock);
        when(createTokenResponseMock.isError()).thenReturn(true);
        when(createTokenResponseMock.getErrorDetail()).thenReturn(errorDetailMock);

        testObj.createToken(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayPaymentInfoServiceMock, never()).createCreditCardPaymentInfo(cartModelMock, createTokenResponseMock, cseAdditionalAuthInfoMock.getSaveCard(), MERCHANT_CODE);
        verify(worldpayServiceGatewayMock);
    }

    @Test
    public void shouldCreateCreditCardForSuccessfulTokenResponse() throws WorldpayException {
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponseMock);
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(false);
        when(worldpayPaymentInfoServiceMock.createCreditCardPaymentInfo(cartModelMock, createTokenResponseMock, false, MERCHANT_CODE)).thenReturn(creditCardPaymentInfoModelMock);

        testObj.createToken(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayPaymentInfoServiceMock, never()).updateCreditCardPaymentInfo(eq(cartModelMock), any(UpdateTokenServiceRequest.class));
        verify(cartModelMock).setPaymentInfo(creditCardPaymentInfoModelMock);
        verify(cartServiceMock).saveOrder(cartModelMock);
    }

    @Test
    public void shouldUpdateTokenOnConflict() throws WorldpayException {
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponseMock);
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(false);
        when(createTokenResponseMock.getToken().getTokenDetails().getTokenEvent()).thenReturn(TokenEvent.CONFLICT.name());
        when(worldpayRequestFactoryMock.buildTokenUpdateRequest(merchantInfoMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock, createTokenResponseMock)).thenReturn(updateTokenServiceRequestMock);
        when(worldpayPaymentInfoServiceMock.updateCreditCardPaymentInfo(cartModelMock, updateTokenServiceRequestMock)).thenReturn(Optional.of(creditCardPaymentInfoModelMock));

        testObj.createToken(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayPaymentInfoServiceMock, never()).createCreditCardPaymentInfo(cartModelMock, createTokenResponseMock, false, MERCHANT_CODE);
        verify(worldpayServiceGatewayMock).updateToken(updateTokenServiceRequestMock);
        verify(cartModelMock).setPaymentInfo(creditCardPaymentInfoModelMock);
        verify(cartServiceMock).saveOrder(cartModelMock);
    }

    @Test
    public void shouldCreateNewPaymentInfoWhenNoCardIsUpdatedOnConflict() throws WorldpayException {
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponseMock);
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(false);
        when(createTokenResponseMock.getToken().getTokenDetails().getTokenEvent()).thenReturn(TokenEvent.CONFLICT.name());
        when(worldpayRequestFactoryMock.buildTokenUpdateRequest(merchantInfoMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock, createTokenResponseMock)).thenReturn(updateTokenServiceRequestMock);
        when(worldpayPaymentInfoServiceMock.updateCreditCardPaymentInfo(cartModelMock, updateTokenServiceRequestMock)).thenReturn(Optional.empty());
        when(worldpayPaymentInfoServiceMock.createCreditCardPaymentInfo(cartModelMock, createTokenResponseMock, false, MERCHANT_CODE)).thenReturn(newlyCreatedCreditCardPaymentInfoModel);

        testObj.createToken(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayPaymentInfoServiceMock).createCreditCardPaymentInfo(cartModelMock, createTokenResponseMock, false, MERCHANT_CODE);
        verify(worldpayServiceGatewayMock).updateToken(updateTokenServiceRequestMock);
        verify(cartModelMock).setPaymentInfo(newlyCreatedCreditCardPaymentInfoModel);
        verify(cartServiceMock).saveOrder(cartModelMock);
    }

    @Test(expected = WorldpayException.class)
    public void shouldThrowExceptionOnErrorInUpdateResponse() throws WorldpayException {
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponseMock);
        when(createTokenResponseMock.getToken().getTokenDetails().getTokenEvent()).thenReturn(TokenEvent.CONFLICT.name());
        when(worldpayRequestFactoryMock.buildTokenUpdateRequest(merchantInfoMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock, createTokenResponseMock)).thenReturn(updateTokenServiceRequestMock);
        when(updateTokenResponseMock.isError()).thenReturn(true);
        when(updateTokenResponseMock.getErrorDetail()).thenReturn(errorDetailMock);

        testObj.createToken(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verifyZeroInteractions(worldpayPaymentInfoServiceMock.createCreditCardPaymentInfo(any(), any(), any(), any()));
    }

    @Test
    public void completeAuthoriseShouldCreateNonPendingAuthorisePaymentTransactionEntry() {
        when(cartModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(addressServiceMock.cloneAddressForOwner(paymentAddressModelMock, creditCardPaymentInfoModelMock)).thenReturn(deliveryAddressModelMock);

        testObj.completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock, MERCHANT_CODE);

        verify(worldpayPaymentTransactionServiceMock).createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP));
        verify(commerceCheckoutServiceMock).setPaymentInfo(commerceCheckoutParameterMock);
        verify(worldpayPaymentInfoServiceMock).updateAndAttachPaymentInfoModel(paymentTransactionModelMock, cartModelMock, creditCardPaymentInfoModelMock);
        verify(worldpayPaymentTransactionServiceMock).addAavFields(paymentTransactionEntryModelMock, paymentReplyMock);
        verify(worldpayPaymentTransactionServiceMock).addRiskScore(paymentTransactionModelMock, paymentReplyMock);
    }

    @Test
    public void shouldCompleteAuthoriseWhenComplete3DAuthorise() {
        when(directAuthoriseServiceRequestMock.getMerchantInfo().getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayPaymentTransactionServiceMock.createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock, BigDecimal.TEN)).thenReturn(paymentTransactionEntryModelMock);
        when(cartModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(addressServiceMock.cloneAddressForOwner(paymentAddressModelMock, creditCardPaymentInfoModelMock)).thenReturn(deliveryAddressModelMock);

        testObj.completeAuthorise3DSecure(cartModelMock, directAuthoriseServiceResponseMock, merchantInfoMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, creditCardPaymentInfoModelMock);
        verify(commerceCheckoutServiceMock).setPaymentInfo(commerceCheckoutParameterMock);
        verify(worldpayPaymentInfoServiceMock).updateAndAttachPaymentInfoModel(paymentTransactionModelMock, cartModelMock, creditCardPaymentInfoModelMock);
        verify(worldpayPaymentTransactionServiceMock).addAavFields(paymentTransactionEntryModelMock, paymentReplyMock);
        verify(worldpayPaymentTransactionServiceMock).addRiskScore(paymentTransactionModelMock, paymentReplyMock);
    }

    @Test
    public void shouldAuthoriseRecurringPayment() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);

        final DirectAuthoriseServiceResponse result = testObj.authoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_COOKIE_PARAM), anyString());
        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_ECHO_DATA_PARAM), anyString());
        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    public void shouldAddSessionAttributesWhenResponseContainsRequest3DInfoOnRecurringPayment() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(directAuthoriseServiceResponseMock.getRequest3DInfo()).thenReturn(request3DInfoMock);

        final DirectAuthoriseServiceResponse result = testObj.authoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        verify(sessionServiceMock).setAttribute(THREE_D_SECURE_COOKIE_PARAM, COOKIE);
        verify(sessionServiceMock).setAttribute(THREE_D_SECURE_ECHO_DATA_PARAM, ECHO_DATA);
        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    public void klarnaShouldBuildRequestWithKlarnaPaymentMethod() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseKlarnaRequest(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock)).thenReturn(directAuthoriseForRedirectServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseForRedirectServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = testObj.authoriseKlarna(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock);

        assertEquals(directAuthoriseServiceResponseMock, directAuthoriseServiceResponse);
    }
}

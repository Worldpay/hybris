package com.worldpay.service.payment.request.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.data.*;
import com.worldpay.data.applepay.ApplePay;
import com.worldpay.data.payment.Cse;
import com.worldpay.data.payment.PayWithGoogleSSL;
import com.worldpay.data.payment.Payment;
import com.worldpay.data.payment.StoredCredentials;
import com.worldpay.data.threeds2.Additional3DSData;
import com.worldpay.data.threeds2.RiskData;
import com.worldpay.data.token.CardDetails;
import com.worldpay.data.token.Token;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.enums.payment.storedCredentials.MerchantInitiatedReason;
import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.payment.*;
import com.worldpay.service.payment.request.WorldpayRequestService;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import com.worldpay.service.request.SecondThreeDSecurePaymentRequest;
import com.worldpay.service.response.CreateTokenResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayRequestFactoryTest {

    private static final String SHOPPER_EMAIL_ADDRESS = "shopperEmailAddress";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final double INTEGER_TOTAL = 119.12;
    private static final String PA_RESPONSE = "paResponse";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    private static final String CVC_CODE = "cvcCode";
    private static final String COOKIE = "cookie";
    private static final String STATEMENT_NARRATIVE = "statementNarrative";
    private static final String SHOPPER_BANK_CODE = "shopperBankCode";
    private static final String PAYMENT_METHOD = "paymentMethod";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String CARD_HOLDER_NAME = "cardHolderName";
    private static final String EXPIRY_MONTH = "expiryMonth";
    private static final String EXPIRY_YEAR = "expiryYear";
    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";
    private static final String ORDER_CODE = "ORDER_CODE";
    private static final String PWD = "PWD";
    private static final String MERCHANT_CODE = "MERCHANT_CODE";
    private static final String TOKEN_UPDATED = "Token updated ";
    private static final String TOKEN_DELETED = "Token deleted ";
    private static final String SESSION_ID = "SESSION_ID";
    private static final String TRANSACTION_IDENTIFIER = "transactionIdentifier";
    private static final String INSTALLATION_ID = "installationId";
    private static final String KLARNA_V2_SSL = "KLARNA_V2-SSL";

    @Spy
    @InjectMocks
    private DefaultWorldpayRequestFactory testObj;

    @Mock
    private WorldpayTokenEventReferenceCreationStrategy worldpayTokenEventReferenceCreationStrategyMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock
    private WorldpayKlarnaService worldpayKlarnaServiceMock;
    @Mock
    private WorldpayRiskDataService worldpayRiskDataServiceMock;
    @Mock
    private WorldpayCartService worldpayCartServiceMock;
    @Mock
    private WorldpayRequestService worldpayRequestServiceMock;
    @Mock
    private WorldpayAdditionalRequestDataService worldpayAdditionalRequestDataServiceMock;

    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private AbstractOrderModel abstractOrderModelMock;
    @Mock
    private CSEAdditionalAuthInfo cseAdditionalAuthInfoMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private BasicOrderInfo basicOrderInfoMock;
    @Mock
    private Cse csePaymentMock;
    @Mock
    private Session sessionMock;
    @Mock
    private Browser browserMock;
    @Mock
    private Address shippingAddressMock, billingAddressMock;
    @Mock
    private CurrencyModel currencyModelMock;
    @Mock
    private DirectAuthoriseServiceRequest directAuthoriseServiceRequestMock, directTokenisedAuthoriseServiceRequestMock, directGooglePayAuthoriseServiceRequestMock, applePayAuthoriseServiceRequestMock;
    @Mock
    private Amount amountMock;
    @Mock
    private AddressModel deliveryAddressModelMock, paymentAddressModelMock;
    @Mock
    private CustomerModel customerModelMock;
    @Mock
    private TokenRequest tokenRequestMockWithReasonNull, tokenRequestMockWithReason;
    @Mock
    private Token tokenMock;
    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoModelMock;
    @Mock
    private BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfoMock;
    @Mock
    private ApplePayAdditionalAuthInfo applePayAdditionalAuthInfoMock;
    @Mock
    private Payment paymentMock;
    @Mock
    private ApplePay applePayPaymentMock;
    @Mock
    private Shopper shopperMock, shopperWithoutSessionNorBrowserMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CreateTokenResponse createTokenResponseMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private LanguageModel languageModelMock;
    @Mock
    private OrderLines orderLinesMock;
    @Mock
    private PayWithGoogleSSL googlePayMock;
    @Mock
    private GooglePayAdditionalAuthInfo googlePayAdditionalAuthInfoMock;
    @Mock
    private Additional3DSData additional3DSDataMock;
    @Mock
    private RiskData riskDataMock;
    @Mock
    private StoredCredentials storedCredentialsMock;
    @Mock
    private List<PaymentType> includedPTsMock;
    @Mock
    private RedirectAuthoriseServiceRequest redirectAuthoriseRequestMock;
    @Mock
    private WorldpayAPMPaymentInfoModel wordpayPaymentInfoModelMock;
    @Mock
    private CardDetails cartDetailsMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AuthoriseRequestParameters.AuthoriseRequestParametersCreator authoriseRequestParametersCreatorMock;
    @Mock
    private AlternativeShippingAddress alternativeShippingAddressMock;

    @Captor
    private ArgumentCaptor<AuthoriseRequestParameters> authoriseRequestParametersArgumentCaptor;
    @Captor
    private ArgumentCaptor<AuthoriseRequestParameters> authoriseRequestParameterArgumentCaptor;

    @Before
    public void setUp() throws WorldpayException {
        when(cartModelMock.getCurrency()).thenReturn(currencyModelMock);
        when(cartModelMock.getTotalPrice()).thenReturn(INTEGER_TOTAL);
        when(cartModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(cartModelMock.getUser()).thenReturn(customerModelMock);
        when(worldpayTokenEventReferenceCreationStrategyMock.createTokenEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);

        when(abstractOrderModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(worldpayOrderServiceMock.createAmount(currencyModelMock, INTEGER_TOTAL)).thenReturn(amountMock);
        when(worldpayOrderServiceMock.createBasicOrderInfo(WORLDPAY_ORDER_CODE, WORLDPAY_ORDER_CODE, amountMock)).thenReturn(basicOrderInfoMock);
        when(worldpayOrderServiceMock.createBasicOrderInfo(WORLDPAY_ORDER_CODE, WORLDPAY_ORDER_CODE, null)).thenReturn(basicOrderInfoMock);
        when(creditCardPaymentInfoModelMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);
        when(worldpayAdditionalInfoDataMock.getSecurityCode()).thenReturn(CVC_CODE);
        when(worldpayAdditionalInfoDataMock.getAuthenticatedShopperId()).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(bankTransferAdditionalAuthInfoMock.getPaymentMethod()).thenReturn(PAYMENT_METHOD);
        when(bankTransferAdditionalAuthInfoMock.getShopperBankCode()).thenReturn(SHOPPER_BANK_CODE);
        when(bankTransferAdditionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(worldpayRiskDataServiceMock.createRiskData(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(riskDataMock);
        when(abstractOrderModelMock.getPaymentInfo().getTransactionIdentifier()).thenReturn(TRANSACTION_IDENTIFIER);
        when(worldpayOrderServiceMock.generateWorldpayOrderCode(cartModelMock)).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayOrderServiceMock.createKlarnaPayment(COUNTRY_CODE, languageModelMock, null, KLARNA_V2_SSL)).thenReturn(paymentMock);
        when(worldpayRequestServiceMock.createToken(SUBSCRIPTION_ID, CVC_CODE)).thenReturn(tokenMock);
        when(worldpayRequestServiceMock.createBrowser(worldpayAdditionalInfoDataMock)).thenReturn(browserMock);
        when(worldpayRequestServiceMock.createSession(worldpayAdditionalInfoDataMock)).thenReturn(sessionMock);
        when(worldpayRequestServiceMock.createAuthenticatedShopper(SHOPPER_EMAIL_ADDRESS, AUTHENTICATED_SHOPPER_ID, sessionMock, browserMock)).thenReturn(shopperMock);
        when(worldpayRequestServiceMock.createTokenRequest(TOKEN_EVENT_REFERENCE, null)).thenReturn(tokenRequestMockWithReasonNull);
        when(worldpayRequestServiceMock.createBankPayment(WORLDPAY_ORDER_CODE, PAYMENT_METHOD, SHOPPER_BANK_CODE)).thenReturn(paymentMock);
        when(worldpayRequestServiceMock.createShopper(SHOPPER_EMAIL_ADDRESS, sessionMock, browserMock)).thenReturn(shopperMock);
        when(worldpayRequestServiceMock.createShopper(SHOPPER_EMAIL_ADDRESS, null, null)).thenReturn(shopperWithoutSessionNorBrowserMock);
        when(worldpayRequestServiceMock.createAdditional3DSData(worldpayAdditionalInfoDataMock)).thenReturn(additional3DSDataMock);
        when(worldpayOrderServiceMock.createCsePayment(cseAdditionalAuthInfoMock, billingAddressMock)).thenReturn(csePaymentMock);
        when(worldpayRequestServiceMock.createStoredCredentials(Usage.USED, null, null)).thenReturn(storedCredentialsMock);
        when(worldpayCartServiceMock.getBillingAddress(cartModelMock, cseAdditionalAuthInfoMock)).thenReturn(billingAddressMock);
        when(worldpayCartServiceMock.getBillingAddress(cartModelMock, additionalAuthInfoMock)).thenReturn(billingAddressMock);
        when(worldpayCartServiceMock.getEmailForCustomer(cartModelMock)).thenReturn(SHOPPER_EMAIL_ADDRESS);
        when(worldpayCartServiceMock.getAddressFromCart(cartModelMock, true)).thenReturn(shippingAddressMock);
        when(worldpayCartServiceMock.getAddressFromCart(cartModelMock, false)).thenReturn(billingAddressMock);
        when(worldpayRequestServiceMock.getDynamicInteractionType(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.ECOMMERCE);
        when(worldpayRequestServiceMock.createCardDetails(cseAdditionalAuthInfoMock, paymentAddressModelMock)).thenReturn(cartDetailsMock);
        when(createTokenResponseMock.getToken().getTokenDetails().getPaymentTokenID()).thenReturn(PAYMENT_TOKEN_ID);
    }

    @Test
    public void shouldCreateTokenRequest() {
        testObj.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayRequestServiceMock).createTokenServiceRequest(merchantInfoMock, AUTHENTICATED_SHOPPER_ID, csePaymentMock, tokenRequestMockWithReasonNull);
    }

    @Test
    public void shouldUseBillingAddressWhenNoShippingAddress() { // User has selected "pick up in store" only
        testObj.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayRequestServiceMock).createTokenServiceRequest(merchantInfoMock, AUTHENTICATED_SHOPPER_ID, csePaymentMock, tokenRequestMockWithReasonNull);
    }

    @Test
    public void buildTokenRequest_ShouldUseShippingAsBilling_WhenUsingShippingAsBillingFlagIsTrue() {
        when(worldpayCartServiceMock.getBillingAddress(cartModelMock, cseAdditionalAuthInfoMock)).thenReturn(shippingAddressMock);

        testObj.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayOrderServiceMock).createCsePayment(cseAdditionalAuthInfoMock, shippingAddressMock);
    }

    @Test
    public void buildDirectAuthoriseRequestWithTokenForCSE_WhenItIsCalledAndCurrentSiteFsIsEnabled_ShouldCreateTokenisedDirectAuthoriseRequest() {
        when(worldpayRequestServiceMock.createAdditional3DSData(worldpayAdditionalInfoDataMock)).thenReturn(additional3DSDataMock);
        when(worldpayRiskDataServiceMock.createRiskData(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(riskDataMock);

        final DirectAuthoriseServiceRequest result = testObj.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createTokenisedDirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());
        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertEquals(merchantInfoMock, result.getMerchantInfo());
        assertEquals(basicOrderInfoMock, requestParameters.getOrderInfo());
        assertEquals(tokenMock, requestParameters.getPayment());
        assertEquals(shopperMock, requestParameters.getShopper());
        assertEquals(shippingAddressMock, requestParameters.getShippingAddress());
        assertEquals(DynamicInteractionType.ECOMMERCE, requestParameters.getDynamicInteractionType());
        assertEquals(additional3DSDataMock, requestParameters.getAdditional3DSData());
        assertEquals(riskDataMock, requestParameters.getRiskData());
    }

    @Test
    public void buildDirectAuthoriseRequestWithTokenForCSE_WhenItIsCalledAndCurrentSiteFSIsDisabled_ShouldReturnADirectAuthoriseServiceRequest() {
        when(worldpayRequestServiceMock.createAdditional3DSData(worldpayAdditionalInfoDataMock)).thenReturn(additional3DSDataMock);
        when(worldpayRiskDataServiceMock.createRiskData(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(riskDataMock);
        testObj.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createTokenisedDirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());
        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertEquals(merchantInfoMock, requestParameters.getMerchantInfo());
        assertEquals(basicOrderInfoMock, requestParameters.getOrderInfo());
        assertEquals(tokenMock, requestParameters.getPayment());
        assertEquals(shopperMock, requestParameters.getShopper());
        assertEquals(shippingAddressMock, requestParameters.getShippingAddress());
        assertEquals(DynamicInteractionType.ECOMMERCE, requestParameters.getDynamicInteractionType());
        assertEquals(additional3DSDataMock, requestParameters.getAdditional3DSData());
        assertEquals(riskDataMock, requestParameters.getRiskData());
    }

    @Test
    public void shouldCreateApplePayDirectAuthoriseRequest() {
        doReturn(applePayAuthoriseServiceRequestMock).when(testObj).createApplePayDirectAuthoriseRequest(Mockito.any(AuthoriseRequestParameters.class));
        when(worldpayOrderServiceMock.createApplePayPayment(applePayAdditionalAuthInfoMock)).thenReturn(applePayPaymentMock);

        testObj.buildApplePayDirectAuthorisationRequest(merchantInfoMock, cartModelMock, applePayAdditionalAuthInfoMock);

        verify(testObj).createApplePayDirectAuthoriseRequest(Mockito.any(AuthoriseRequestParameters.class));
    }

    @Test
    public void shouldCreate3DSecureAuthoriseRequest() {
        doReturn(directAuthoriseServiceRequestMock).when(testObj).createDirect3DAuthoriseRequest(Mockito.any(AuthoriseRequestParameters.class));
        when(worldpayRequestServiceMock.createShopper(null, sessionMock, null)).thenReturn(shopperMock);

        final DirectAuthoriseServiceRequest result = testObj.build3dDirectAuthoriseRequest(merchantInfoMock, WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE, COOKIE);

        assertEquals(directAuthoriseServiceRequestMock, result);
        verify(directAuthoriseServiceRequestMock).setCookie(COOKIE);
    }

    @Test
    public void shouldCreateDirectAuthoriseBankTransferRequest() throws WorldpayConfigurationException {
        doReturn(directAuthoriseServiceRequestMock).when(testObj).createBankTransferAuthoriseRequest(Mockito.any(AuthoriseRequestParameters.class));

        final DirectAuthoriseServiceRequest result = testObj.buildDirectAuthoriseBankTransferRequest(merchantInfoMock, cartModelMock, bankTransferAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        assertEquals(directAuthoriseServiceRequestMock, result);
    }

    @Test
    public void shouldCreateDirectAuthoriseKlarnaRequest() throws WorldpayConfigurationException {
        when(billingAddressMock.getCountryCode()).thenReturn(COUNTRY_CODE);
        when(customerModelMock.getSessionLanguage()).thenReturn(languageModelMock);
        when(additionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(worldpayKlarnaServiceMock.createOrderLines(cartModelMock)).thenReturn(orderLinesMock);

        doReturn(directAuthoriseServiceRequestMock).when(testObj).createKlarnaDirectAuthoriseRequest(Mockito.any(AuthoriseRequestParameters.class));

        final DirectAuthoriseServiceRequest result = testObj.buildDirectAuthoriseKlarnaRequest(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock);

        assertEquals(directAuthoriseServiceRequestMock, result);
    }

    @Test
    public void shouldBuildDirectAuthorisationWithGooglePay() {
        when(googlePayAdditionalAuthInfoMock.getProtocolVersion()).thenReturn("protocolVersion");
        when(googlePayAdditionalAuthInfoMock.getSignature()).thenReturn("signature");
        when(googlePayAdditionalAuthInfoMock.getSignedMessage()).thenReturn("signedMessage");
        when(worldpayOrderServiceMock.createGooglePayPayment("protocolVersion", "signature", "signedMessage")).thenReturn(googlePayMock);
        doReturn(directGooglePayAuthoriseServiceRequestMock).when(testObj).createGooglePayDirectAuthoriseRequest(authoriseRequestParameterArgumentCaptor.capture());

        final DirectAuthoriseServiceRequest result = testObj.buildDirectAuthoriseGooglePayRequest(merchantInfoMock, cartModelMock, googlePayAdditionalAuthInfoMock);

        verify(worldpayRequestServiceMock).createShopper(SHOPPER_EMAIL_ADDRESS, null, null);
        assertThat(result).isEqualTo(directGooglePayAuthoriseServiceRequestMock);
        final AuthoriseRequestParameters authoriseRequestParameters = authoriseRequestParameterArgumentCaptor.getValue();
        assertThat(authoriseRequestParameters.getBillingAddress()).isEqualTo(billingAddressMock);
        assertThat(authoriseRequestParameters.getPayment()).isInstanceOf(PayWithGoogleSSL.class);
    }

    @Test
    public void buildDirectAuthoriseGooglePayRequest_shouldBuildDirectAuthorisationWithGooglePay() {
        when(googlePayAdditionalAuthInfoMock.getSaveCard()).thenReturn(Boolean.TRUE);
        when(googlePayAdditionalAuthInfoMock.getProtocolVersion()).thenReturn("protocolVersion");
        when(googlePayAdditionalAuthInfoMock.getSignature()).thenReturn("signature");
        when(googlePayAdditionalAuthInfoMock.getSignedMessage()).thenReturn("signedMessage");
        when(worldpayOrderServiceMock.createGooglePayPayment("protocolVersion", "signature", "signedMessage")).thenReturn(googlePayMock);
        when(worldpayRequestServiceMock.createStoredCredentials(Usage.FIRST, null, null)).thenReturn(storedCredentialsMock);
        doReturn(directGooglePayAuthoriseServiceRequestMock).when(testObj).createGooglePayDirectAuthoriseRequest(authoriseRequestParameterArgumentCaptor.capture());

        final DirectAuthoriseServiceRequest result = testObj.buildDirectAuthoriseGooglePayRequest(merchantInfoMock, cartModelMock, googlePayAdditionalAuthInfoMock);

        verify(worldpayRequestServiceMock).createAuthenticatedShopper(SHOPPER_EMAIL_ADDRESS, null, null, null);
        assertThat(result).isEqualTo(directGooglePayAuthoriseServiceRequestMock);
        final AuthoriseRequestParameters authoriseRequestParameters = authoriseRequestParameterArgumentCaptor.getValue();
        assertThat(authoriseRequestParameters.getBillingAddress()).isEqualTo(billingAddressMock);
        assertThat(authoriseRequestParameters.getPayment()).isInstanceOf(PayWithGoogleSSL.class);
        assertThat(authoriseRequestParameters.getTokenRequest()).isEqualTo(tokenRequestMockWithReasonNull);
        assertEquals(authoriseRequestParameters.getStoredCredentials(), storedCredentialsMock);
    }

    @Test
    public void buildTokenUpdateRequest_shouldCreateUpdateTokenRequest() {
        when(worldpayRequestServiceMock.createTokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_UPDATED + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))).thenReturn(tokenRequestMockWithReason);

        testObj.buildTokenUpdateRequest(merchantInfoMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock, paymentAddressModelMock, createTokenResponseMock);

        verify(worldpayRequestServiceMock).createUpdateTokenServiceRequest(merchantInfoMock, worldpayAdditionalInfoDataMock, tokenRequestMockWithReason, PAYMENT_TOKEN_ID, cartDetailsMock);
    }

    @Test
    public void buildTokenUpdateRequest_WhenAddressIsNull_ShouldCreateUpdateTokenRequestWithoutAddress() {
        when(worldpayRequestServiceMock.createTokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_UPDATED + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))).thenReturn(tokenRequestMockWithReason);

        testObj.buildTokenUpdateRequest(merchantInfoMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock, paymentAddressModelMock, createTokenResponseMock);

        verify(worldpayRequestServiceMock).createUpdateTokenServiceRequest(merchantInfoMock, worldpayAdditionalInfoDataMock, tokenRequestMockWithReason, PAYMENT_TOKEN_ID, cartDetailsMock);
    }

    @Test
    public void buildTokenDeleteRequest_ShouldCreateDeleteTokenRequest() {
        when(worldpayRequestServiceMock.createTokenRequestForDeletion(TOKEN_EVENT_REFERENCE, TOKEN_DELETED + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE), AUTHENTICATED_SHOPPER_ID)).
            thenReturn(tokenRequestMockWithReason);
        when(creditCardPaymentInfoModelMock.getEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);
        when(creditCardPaymentInfoModelMock.getAuthenticatedShopperID()).thenReturn(AUTHENTICATED_SHOPPER_ID);

        testObj.buildTokenDeleteRequest(merchantInfoMock, creditCardPaymentInfoModelMock, SUBSCRIPTION_ID);

        verify(testObj).createDeleteTokenServiceRequest(eq(merchantInfoMock), eq(AUTHENTICATED_SHOPPER_ID), eq(SUBSCRIPTION_ID), eq(tokenRequestMockWithReason));
    }

    @Test
    public void buildDirectAuthoriseRecurringPayment_WhenDynamicIntereactionTypeIsCommerceAndFSEnabled_ShouldCreateAuthoriseRecurringPaymentRequestAndSetDeviceSession() {
        when(worldpayRequestServiceMock.getDynamicInteractionType(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.ECOMMERCE);

        testObj.buildDirectAuthoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createTokenisedDirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());
        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertEquals(merchantInfoMock, requestParameters.getMerchantInfo());
        assertEquals(basicOrderInfoMock, requestParameters.getOrderInfo());
        assertEquals(tokenMock, requestParameters.getPayment());
        assertEquals(shopperMock, requestParameters.getShopper());
        assertEquals(shippingAddressMock, requestParameters.getShippingAddress());
        assertEquals(billingAddressMock, requestParameters.getBillingAddress());
        assertNull(requestParameters.getStatementNarrative());
        assertEquals(additional3DSDataMock, requestParameters.getAdditional3DSData());
        assertEquals(storedCredentialsMock, requestParameters.getStoredCredentials());

    }

    @Test
    public void buildDirectAuthoriseRecurringPayment_WhenDynamicIntereactionTypeIsMotoAndFSDisabled_ShouldCreateAuthoriseRecurringPaymentRequestAndNotSetDeviceSession() {
        when(worldpayRequestServiceMock.getDynamicInteractionType(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.MOTO);

        testObj.buildDirectAuthoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createTokenisedDirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());
        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertEquals(merchantInfoMock, requestParameters.getMerchantInfo());
        assertEquals(basicOrderInfoMock, requestParameters.getOrderInfo());
        assertEquals(tokenMock, requestParameters.getPayment());
        assertEquals(shopperMock, requestParameters.getShopper());
        assertEquals(shippingAddressMock, requestParameters.getShippingAddress());
        assertEquals(billingAddressMock, requestParameters.getBillingAddress());
        assertNull(requestParameters.getStatementNarrative());
        assertEquals(additional3DSDataMock, requestParameters.getAdditional3DSData());
        assertEquals(storedCredentialsMock, requestParameters.getStoredCredentials());
        assertNull(requestParameters.getAction());
    }

    @Test
    public void buildDirectAuthoriseRecurringPayment_shouldCreateAuthoriseRecurringPaymentRequest_WhenDynamicInteractionTypeIsRecurring() {
        when(worldpayRequestServiceMock.getDynamicInteractionType(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.RECURRING);
        when(worldpayRequestServiceMock.createStoredCredentials(Usage.USED, MerchantInitiatedReason.RECURRING, TRANSACTION_IDENTIFIER)).thenReturn(storedCredentialsMock);

        testObj.buildDirectAuthoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createTokenisedDirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());
        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertEquals(merchantInfoMock, requestParameters.getMerchantInfo());
        assertEquals(basicOrderInfoMock, requestParameters.getOrderInfo());
        assertEquals(tokenMock, requestParameters.getPayment());
        assertEquals(shopperMock, requestParameters.getShopper());
        assertEquals(shippingAddressMock, requestParameters.getShippingAddress());
        assertEquals(billingAddressMock, requestParameters.getBillingAddress());
        assertNull(requestParameters.getStatementNarrative());
        assertEquals(additional3DSDataMock, requestParameters.getAdditional3DSData());
        assertEquals(storedCredentialsMock, requestParameters.getStoredCredentials());
        assertNull(requestParameters.getAction());
    }

    @Test
    public void testBuildSecondThreeDSecurePaymentRequest() {
        final SecondThreeDSecurePaymentRequest secondThreeDSecurePaymentRequestMock = testObj.buildSecondThreeDSecurePaymentRequest(merchantInfoMock, WORLDPAY_ORDER_CODE, SESSION_ID, COOKIE);

        assertEquals(COOKIE, secondThreeDSecurePaymentRequestMock.getCookie());
        assertEquals(merchantInfoMock, secondThreeDSecurePaymentRequestMock.getMerchantInfo());
        assertEquals(SESSION_ID, secondThreeDSecurePaymentRequestMock.getSessionId());
        assertEquals(WORLDPAY_ORDER_CODE, secondThreeDSecurePaymentRequestMock.getOrderCode());
    }

    @Test
    public void buildDirectAuthoriseRequestWithToken_WhenItIsCalledAndCurrentSite_ShouldReturnADirectAuthoriseServiceRequestAndNotSetDeviceSession() {
        when(cseAdditionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(worldpayRequestServiceMock.createStoredCredentials(Usage.FIRST, null, null)).thenReturn(storedCredentialsMock);
        when(worldpayRequestServiceMock.createAdditional3DSData(worldpayAdditionalInfoDataMock)).thenReturn(additional3DSDataMock);
        when(worldpayRiskDataServiceMock.createRiskData(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(riskDataMock);
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(true);

        testObj.buildDirectTokenAndAuthorise(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);

        verify(testObj).buildDirectTokenAndAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());
        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertEquals(merchantInfoMock, requestParameters.getMerchantInfo());
        assertEquals(basicOrderInfoMock, requestParameters.getOrderInfo());
        assertEquals(csePaymentMock, requestParameters.getPayment());
        assertEquals(shopperMock, requestParameters.getShopper());
        assertEquals(shippingAddressMock, requestParameters.getShippingAddress());
        assertEquals(billingAddressMock, requestParameters.getBillingAddress());
        assertEquals(STATEMENT_NARRATIVE, requestParameters.getStatementNarrative());
        assertEquals(DynamicInteractionType.ECOMMERCE, requestParameters.getDynamicInteractionType());
        assertEquals(AUTHENTICATED_SHOPPER_ID, requestParameters.getAuthenticatedShopperId());
        assertEquals(tokenRequestMockWithReasonNull, requestParameters.getTokenRequest());
        assertEquals(additional3DSDataMock, requestParameters.getAdditional3DSData());
        assertEquals(riskDataMock, requestParameters.getRiskData());
        assertEquals(storedCredentialsMock, requestParameters.getStoredCredentials());
    }

    @Test
    public void buildRedirectAuthoriseRequest_WhenThePaymentMethodsIsRequestedToBeSaved_ShouldReturnADirectAuthoriseServiceRequestWithStoredCredentials() throws WorldpayConfigurationException {
        doReturn(redirectAuthoriseRequestMock).when(testObj).createRedirectAuthoriseRequest(any(AuthoriseRequestParameters.class));
        doReturn(includedPTsMock).when(testObj).getIncludedPaymentTypeList(additionalAuthInfoMock);
        when(additionalAuthInfoMock.getSaveCard()).thenReturn(Boolean.TRUE);
        when(additionalAuthInfoMock.getInstallationId()).thenReturn(INSTALLATION_ID);
        when(worldpayRequestServiceMock.createStoredCredentials(Usage.FIRST, null, null)).thenReturn(storedCredentialsMock);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(worldpayRequestServiceMock.createAuthenticatedShopper(SHOPPER_EMAIL_ADDRESS, AUTHENTICATED_SHOPPER_ID, null, null)).thenReturn(shopperWithoutSessionNorBrowserMock);

        testObj.buildRedirectAuthoriseRequest(merchantInfoMock, cartModelMock, additionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createRedirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());
        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();

        assertThat(requestParameters.getInstallationId()).isEqualTo(INSTALLATION_ID);
        assertEquals(basicOrderInfoMock, requestParameters.getOrderInfo());
        assertEquals(shippingAddressMock, requestParameters.getShippingAddress());
        assertEquals(billingAddressMock, requestParameters.getBillingAddress());
        assertEquals(merchantInfoMock, requestParameters.getMerchantInfo());
        assertEquals(includedPTsMock, requestParameters.getIncludedPTs());
        assertEquals(storedCredentialsMock, requestParameters.getStoredCredentials());
        assertEquals(shopperWithoutSessionNorBrowserMock, requestParameters.getShopper());
        assertEquals(tokenRequestMockWithReasonNull, requestParameters.getTokenRequest());
        assertEquals(riskDataMock, requestParameters.getRiskData());
    }

    @Test
    public void buildRedirectAuthoriseRequest_WhenPaymentMethodIsNotPaypalExpressAnd_ShouldNotIncludePaymentAttributes() throws WorldpayConfigurationException {
        doReturn(redirectAuthoriseRequestMock).when(testObj).createRedirectAuthoriseRequest(any(AuthoriseRequestParameters.class));
        doReturn(includedPTsMock).when(testObj).getIncludedPaymentTypeList(additionalAuthInfoMock);
        when(additionalAuthInfoMock.getSaveCard()).thenReturn(Boolean.TRUE);
        when(additionalAuthInfoMock.getInstallationId()).thenReturn(INSTALLATION_ID);
        when(additionalAuthInfoMock.getPaymentMethod()).thenReturn(PaymentType.ONLINE.getMethodCode());

        testObj.buildRedirectAuthoriseRequest(merchantInfoMock, cartModelMock, additionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createRedirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());
        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();

        assertThat(requestParameters.getPaymentMethodAttributes()).isNullOrEmpty();
    }

    @Test
    public void buildRedirectAuthoriseRequest_WhenThePaymentMethodsIsNotRequestedToBeSaved_ShouldReturnADirectAuthoriseServiceRequestWithoutStoredCredentials() throws WorldpayConfigurationException {
        doReturn(includedPTsMock).when(testObj).getIncludedPaymentTypeList(additionalAuthInfoMock);
        when(worldpayRequestServiceMock.createShopper(SHOPPER_EMAIL_ADDRESS, null, null)).thenReturn(shopperMock);

        testObj.buildRedirectAuthoriseRequest(merchantInfoMock, cartModelMock, additionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createRedirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());
        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();

        assertEquals(basicOrderInfoMock, requestParameters.getOrderInfo());
        assertEquals(shippingAddressMock, requestParameters.getShippingAddress());
        assertEquals(billingAddressMock, requestParameters.getBillingAddress());
        assertEquals(merchantInfoMock, requestParameters.getMerchantInfo());
        assertEquals(includedPTsMock, requestParameters.getIncludedPTs());
        assertNull(requestParameters.getStoredCredentials());
        assertEquals(shopperMock, requestParameters.getShopper());
        assertNull(requestParameters.getTokenRequest());
        assertEquals(riskDataMock, requestParameters.getRiskData());
    }

    @Test
    public void getSubscriptionId_ShouldObtainSubscriptionIdFromCreditCardPaymentInfo_WhenPaymentInfoIsInstanceOfCreditCardPaymentInfo() {
        when(abstractOrderModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(creditCardPaymentInfoModelMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);

        final String result = testObj.getSubscriptionId(abstractOrderModelMock);

        verify(creditCardPaymentInfoModelMock).getSubscriptionId();

        assertEquals(SUBSCRIPTION_ID, result);
    }

    @Test
    public void getSubscriptionId_ShouldObtainSubscriptionIdFromWorldpayAPMPaymentInfo_WhenPaymentInfoIsInstanceOfWorldpayAPMPaymentInfo() {
        when(abstractOrderModelMock.getPaymentInfo()).thenReturn(wordpayPaymentInfoModelMock);
        when(wordpayPaymentInfoModelMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);

        final String result = testObj.getSubscriptionId(abstractOrderModelMock);

        verify(wordpayPaymentInfoModelMock).getSubscriptionId();

        assertEquals(SUBSCRIPTION_ID, result);
    }

    @Test
    public void internalGetRedirectAuthoriseServiceRequestForKlarna_ShouldReturnRedirectAuthoriseServiceRequest() throws WorldpayConfigurationException {
        when(additionalAuthInfoMock.getPaymentMethod()).thenReturn(KLARNA_V2_SSL);
        when(billingAddressMock.getCountryCode()).thenReturn(COUNTRY_CODE);
        when(customerModelMock.getSessionLanguage()).thenReturn(languageModelMock);
        when(additionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(worldpayKlarnaServiceMock.createOrderLines(cartModelMock)).thenReturn(orderLinesMock);
        when(worldpayRequestServiceMock.createAlternativeShippingAddress()).thenReturn(alternativeShippingAddressMock);
        doReturn(authoriseRequestParametersCreatorMock).when(authoriseRequestParametersCreatorMock).withPayment(paymentMock);
        doReturn(authoriseRequestParametersCreatorMock).when(authoriseRequestParametersCreatorMock).withShopper(shopperMock);
        doReturn(authoriseRequestParametersCreatorMock).when(authoriseRequestParametersCreatorMock).withStatementNarrative(STATEMENT_NARRATIVE);
        doReturn(authoriseRequestParametersCreatorMock).when(authoriseRequestParametersCreatorMock).withDynamicInteractionType(DynamicInteractionType.ECOMMERCE);
        doReturn(authoriseRequestParametersCreatorMock).when(authoriseRequestParametersCreatorMock).withOrderLines(orderLinesMock);
        doReturn(authoriseRequestParametersCreatorMock).when(authoriseRequestParametersCreatorMock).withAlternativeShippingAddress(alternativeShippingAddressMock);
        when(worldpayRequestServiceMock.createShopper(SHOPPER_EMAIL_ADDRESS, null, null)).thenReturn(shopperMock);
        doReturn(getAuthoriseRequestParameters()).when(authoriseRequestParametersCreatorMock).build();

        final RedirectAuthoriseServiceRequest result = testObj.internalGetRedirectAuthoriseServiceRequestForKlarna(cartModelMock, additionalAuthInfoMock, authoriseRequestParametersCreatorMock);

        verify(authoriseRequestParametersCreatorMock).withPayment(paymentMock);
        verify(authoriseRequestParametersCreatorMock).withShopper(shopperMock);
        verify(authoriseRequestParametersCreatorMock).withStatementNarrative(STATEMENT_NARRATIVE);
        verify(authoriseRequestParametersCreatorMock).withDynamicInteractionType(DynamicInteractionType.ECOMMERCE);
        verify(authoriseRequestParametersCreatorMock).withOrderLines(orderLinesMock);
        verify(authoriseRequestParametersCreatorMock).withAlternativeShippingAddress(alternativeShippingAddressMock);
        assertThat(result).isNotNull();
    }

    protected AuthoriseRequestParameters getAuthoriseRequestParameters() {
        return AuthoriseRequestParameters.AuthoriseRequestParametersBuilder.getInstance()
            .withMerchantInfo(merchantInfoMock)
            .withOrderInfo(basicOrderInfoMock)
            .withPayment(tokenMock)
            .withShopper(shopperMock)
            .withShippingAddress(shippingAddressMock)
            .withBillingAddress(null)
            .withStatementNarrative(null)
            .withDynamicInteractionType(DynamicInteractionType.ECOMMERCE)
            .withAdditional3DSData(additional3DSDataMock)
            .build();
    }
}

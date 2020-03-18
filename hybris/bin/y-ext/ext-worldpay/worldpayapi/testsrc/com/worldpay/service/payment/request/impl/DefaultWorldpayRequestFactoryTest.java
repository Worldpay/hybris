package com.worldpay.service.payment.request.impl;

import com.worldpay.core.services.strategies.RecurringGenerateMerchantTransactionCodeStrategy;
import com.worldpay.data.*;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.enums.payment.storedCredentials.MerchantInitiatedReason;
import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.interaction.WorldpayDynamicInteractionResolverService;
import com.worldpay.service.model.*;
import com.worldpay.service.model.applepay.ApplePay;
import com.worldpay.service.model.payment.*;
import com.worldpay.service.model.threeds2.Additional3DSData;
import com.worldpay.service.model.threeds2.RiskData;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.Token;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.payment.WorldpayKlarnaStrategy;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayRiskDataService;
import com.worldpay.service.payment.WorldpayTokenEventReferenceCreationStrategy;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import com.worldpay.service.request.SecondThreeDSecurePaymentRequest;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import com.worldpay.strategy.WorldpayDeliveryAddressStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
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

    @Spy
    @InjectMocks
    private DefaultWorldpayRequestFactory testObj;

    @Mock
    private WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategyMock;
    @Mock
    private Converter<AddressModel, Address> worldpayAddressConverterMock;
    @Mock
    private CustomerEmailResolutionService customerEmailResolutionServiceMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
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
    private WorldpayTokenEventReferenceCreationStrategy worldpayTokenEventReferenceCreationStrategyMock;
    @Mock
    private RecurringGenerateMerchantTransactionCodeStrategy recurringGenerateMerchantTransactionCodeStrategyMock;
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
    @Mock(answer = RETURNS_DEEP_STUBS)
    private CreateTokenResponse createTokenResponseMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private LanguageModel languageModelMock;
    @Mock
    private CommerceCommonI18NService commerceCommonI18NServiceMock;
    @Mock
    private OrderLines orderLinesMock;
    @Mock
    private WorldpayKlarnaStrategy worldpayKlarnaStrategyMock;
    @Mock
    private WorldpayDynamicInteractionResolverService worldpayDynamicInteractionResolverService;
    @Mock
    private PayWithGoogleSSL googlePayMock;
    @Mock
    private GooglePayAdditionalAuthInfo googlePayAdditionalAuthInfoMock;
    @Mock
    private Additional3DSData additional3DSDataMock;
    @Mock
    private WorldpayRiskDataService worldpayRiskDataServiceMock;
    @Mock
    private RiskData riskDataMock;
    @Captor
    private ArgumentCaptor<AuthoriseRequestParameters> authoriseRequestParameterArgumentCaptor;
    @Mock
    private StoredCredentials storedCredentialsMock;
    @Captor
    private ArgumentCaptor<AuthoriseRequestParameters> authoriseRequestParametersArgumentCaptor;
    @Captor
    private ArgumentCaptor<CardDetails> cardDetailsCaptor;
    @Mock
    private List<PaymentType> includedPTsMock;
    @Mock
    private WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategyMock;
    @Mock
    private RedirectAuthoriseServiceRequest redirectAuthoriseRequestMock;

    @Before
    public void setUp() throws WorldpayException {
        doReturn(csePaymentMock).when(testObj).createCsePayment(cseAdditionalAuthInfoMock, billingAddressMock);

        when(worldpayAddressConverterMock.convert(deliveryAddressModelMock)).thenReturn(shippingAddressMock);
        when(worldpayAddressConverterMock.convert(paymentAddressModelMock)).thenReturn(billingAddressMock);

        when(cartModelMock.getDeliveryAddress()).thenReturn(deliveryAddressModelMock);
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);
        when(cartModelMock.getCurrency()).thenReturn(currencyModelMock);
        when(cartModelMock.getTotalPrice()).thenReturn(INTEGER_TOTAL);
        when(cartModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(cartModelMock.getUser()).thenReturn(customerModelMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(recurringGenerateMerchantTransactionCodeStrategyMock.generateCode(cartModelMock)).thenReturn(WORLDPAY_ORDER_CODE);

        when(abstractOrderModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(abstractOrderModelMock.getUser()).thenReturn(customerModelMock);
        when(recurringGenerateMerchantTransactionCodeStrategyMock.generateCode(abstractOrderModelMock)).thenReturn(WORLDPAY_ORDER_CODE);

        when(worldpayTokenEventReferenceCreationStrategyMock.createTokenEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);
        when(worldpayOrderServiceMock.createTokenRequest(TOKEN_EVENT_REFERENCE, null)).thenReturn(tokenRequestMockWithReasonNull);
        when(worldpayOrderServiceMock.createAmount(currencyModelMock, INTEGER_TOTAL)).thenReturn(amountMock);
        when(worldpayOrderServiceMock.createBasicOrderInfo(WORLDPAY_ORDER_CODE, WORLDPAY_ORDER_CODE, amountMock)).thenReturn(basicOrderInfoMock);
        when(worldpayOrderServiceMock.createBasicOrderInfo(WORLDPAY_ORDER_CODE, WORLDPAY_ORDER_CODE, null)).thenReturn(basicOrderInfoMock);
        when(worldpayOrderServiceMock.createToken(SUBSCRIPTION_ID, CVC_CODE)).thenReturn(tokenMock);
        when(creditCardPaymentInfoModelMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);
        when(worldpayAdditionalInfoDataMock.getSecurityCode()).thenReturn(CVC_CODE);
        when(customerEmailResolutionServiceMock.getEmailForCustomer(customerModelMock)).thenReturn(SHOPPER_EMAIL_ADDRESS);
        when(worldpayOrderServiceMock.createBrowser(worldpayAdditionalInfoDataMock)).thenReturn(browserMock);
        when(worldpayOrderServiceMock.createSession(worldpayAdditionalInfoDataMock)).thenReturn(sessionMock);
        when(worldpayOrderServiceMock.createAuthenticatedShopper(SHOPPER_EMAIL_ADDRESS, AUTHENTICATED_SHOPPER_ID, sessionMock, browserMock)).thenReturn(shopperMock);
        when(worldpayAdditionalInfoDataMock.getAuthenticatedShopperId()).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(worldpayOrderServiceMock.createBankPayment(PAYMENT_METHOD, SHOPPER_BANK_CODE)).thenReturn(paymentMock);
        when(worldpayOrderServiceMock.createShopper(SHOPPER_EMAIL_ADDRESS, sessionMock, browserMock)).thenReturn(shopperMock);
        when(worldpayOrderServiceMock.createShopper(SHOPPER_EMAIL_ADDRESS, null, null)).thenReturn(shopperWithoutSessionNorBrowserMock);
        when(bankTransferAdditionalAuthInfoMock.getPaymentMethod()).thenReturn(PAYMENT_METHOD);
        when(bankTransferAdditionalAuthInfoMock.getShopperBankCode()).thenReturn(SHOPPER_BANK_CODE);
        when(bankTransferAdditionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(worldpayDeliveryAddressStrategyMock.getDeliveryAddress(cartModelMock)).thenReturn(deliveryAddressModelMock);
        when(worldpayDeliveryAddressStrategyMock.getDeliveryAddress(abstractOrderModelMock)).thenReturn(deliveryAddressModelMock);
        when(worldpayDynamicInteractionResolverService.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.ECOMMERCE);
        when(worldpayOrderServiceMock.createAdditional3DSData(worldpayAdditionalInfoDataMock)).thenReturn(additional3DSDataMock);
        when(worldpayRiskDataServiceMock.createRiskData(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(riskDataMock);
        when(worldpayOrderServiceMock.createCsePayment(cseAdditionalAuthInfoMock, billingAddressMock)).thenReturn(csePaymentMock);
        when(worldpayOrderServiceMock.createStoredCredentials(Usage.USED, null, null)).thenReturn(storedCredentialsMock);
        when(abstractOrderModelMock.getPaymentInfo().getTransactionIdentifier()).thenReturn(TRANSACTION_IDENTIFIER);
    }

    @Test
    public void shouldCreateTokenRequest() {
        when(cseAdditionalAuthInfoMock.getUsingShippingAsBilling()).thenReturn(false);

        testObj.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayOrderServiceMock).createTokenServiceRequest(merchantInfoMock, AUTHENTICATED_SHOPPER_ID, csePaymentMock, tokenRequestMockWithReasonNull);
    }

    @Test
    public void shouldUseBillingAddressWhenNoShippingAddress() { // User has selected "pick up in store" only
        when(cartModelMock.getDeliveryAddress()).thenReturn(null);
        when(cseAdditionalAuthInfoMock.getUsingShippingAsBilling()).thenReturn(false);

        testObj.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayOrderServiceMock).createTokenServiceRequest(merchantInfoMock, AUTHENTICATED_SHOPPER_ID, csePaymentMock, tokenRequestMockWithReasonNull);
    }

    @Test
    public void buildTokenRequest_ShouldUseShippingAsBilling_WhenUsingShippingAsBillingFlagIsTrue() {
        when(cseAdditionalAuthInfoMock.getUsingShippingAsBilling()).thenReturn(true);
        doReturn(directTokenisedAuthoriseServiceRequestMock).when(testObj).createTokenisedDirectAuthoriseRequest(getAuthoriseRequestParameters());
        doReturn(csePaymentMock).when(testObj).createCsePayment(cseAdditionalAuthInfoMock, shippingAddressMock);

        testObj.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayAddressConverterMock).convert(deliveryAddressModelMock);
        verify(testObj).createCsePayment(cseAdditionalAuthInfoMock, shippingAddressMock);
    }

    @Test
    public void shouldCreateTokenisedDirectAuthoriseRequest() {
        doReturn(directTokenisedAuthoriseServiceRequestMock).when(testObj).createTokenisedDirectAuthoriseRequest(Mockito.any(AuthoriseRequestParameters.class));

        final DirectAuthoriseServiceRequest result = testObj.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        assertThat(result).isEqualTo(directTokenisedAuthoriseServiceRequestMock);
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
        when(shopperMock.getSession()).thenReturn(sessionMock);

        doReturn(directAuthoriseServiceRequestMock).when(testObj).createDirect3DAuthoriseRequest(Mockito.any(AuthoriseRequestParameters.class));
        when(worldpayOrderServiceMock.createShopper(null, sessionMock, null)).thenReturn(shopperMock);

        final DirectAuthoriseServiceRequest result = testObj.build3dDirectAuthoriseRequest(merchantInfoMock, WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE, COOKIE);

        assertEquals(directAuthoriseServiceRequestMock, result);
        verify(directAuthoriseServiceRequestMock).setCookie(COOKIE);
    }

    @Test
    public void shouldCreateDirectAuthoriseBankTransferRequest() throws WorldpayConfigurationException {
        when(basicOrderInfoMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(shippingAddressMock.getCountryCode()).thenReturn(COUNTRY_CODE);
        when(merchantInfoMock.getMerchantPassword()).thenReturn(PWD);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        doReturn(directAuthoriseServiceRequestMock).when(testObj).createBankTransferAuthoriseRequest(Mockito.any(AuthoriseRequestParameters.class));

        final DirectAuthoriseServiceRequest result = testObj.buildDirectAuthoriseBankTransferRequest(merchantInfoMock, cartModelMock, bankTransferAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        assertEquals(directAuthoriseServiceRequestMock, result);
    }

    @Test
    public void shouldCreateDirectAuthoriseKlarnaRequest() throws WorldpayConfigurationException {
        when(billingAddressMock.getCountryCode()).thenReturn(COUNTRY_CODE);
        when(customerModelMock.getSessionLanguage()).thenReturn(languageModelMock);
        when(commerceCommonI18NServiceMock.getLocaleForLanguage(languageModelMock)).thenReturn(Locale.UK);
        when(worldpayOrderServiceMock.createKlarnaPayment(COUNTRY_CODE, Locale.UK.toLanguageTag(), null)).thenReturn(paymentMock);
        when(additionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(worldpayKlarnaStrategyMock.createOrderLines(cartModelMock)).thenReturn(orderLinesMock);

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

        assertThat(result).isEqualTo(directGooglePayAuthoriseServiceRequestMock);
        final AuthoriseRequestParameters authoriseRequestParameters = authoriseRequestParameterArgumentCaptor.getValue();
        assertThat(authoriseRequestParameters.getBillingAddress()).isEqualTo(billingAddressMock);
        assertThat(authoriseRequestParameters.getPayment()).isInstanceOf(PayWithGoogleSSL.class);
    }

    @Test
    public void shouldCreateUpdateTokenRequest() {
        when(worldpayOrderServiceMock.createTokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_UPDATED + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))).thenReturn(tokenRequestMockWithReason);

        when(createTokenResponseMock.getToken().getTokenDetails().getPaymentTokenID()).thenReturn(PAYMENT_TOKEN_ID);

        when(cseAdditionalAuthInfoMock.getCardHolderName()).thenReturn(CARD_HOLDER_NAME);
        when(cseAdditionalAuthInfoMock.getExpiryMonth()).thenReturn(EXPIRY_MONTH);
        when(cseAdditionalAuthInfoMock.getExpiryYear()).thenReturn(EXPIRY_YEAR);

        testObj.buildTokenUpdateRequest(merchantInfoMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock, createTokenResponseMock);

        verify(worldpayOrderServiceMock).createUpdateTokenServiceRequest(eq(merchantInfoMock), eq(worldpayAdditionalInfoDataMock), eq(tokenRequestMockWithReason), eq(PAYMENT_TOKEN_ID), cardDetailsCaptor.capture());
        final CardDetails cardDetails = cardDetailsCaptor.getValue();
        assertEquals(CARD_HOLDER_NAME, cardDetails.getCardHolderName());
        assertEquals(EXPIRY_MONTH, cardDetails.getExpiryDate().getMonth());
        assertEquals(EXPIRY_YEAR, cardDetails.getExpiryDate().getYear());
    }

    @Test
    public void shouldCreateDeleteTokenRequest() {
        when(worldpayOrderServiceMock.createTokenRequestForDeletion(TOKEN_EVENT_REFERENCE, TOKEN_DELETED + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE), AUTHENTICATED_SHOPPER_ID)).
                thenReturn(tokenRequestMockWithReason);
        when(creditCardPaymentInfoModelMock.getEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);
        when(creditCardPaymentInfoModelMock.getAuthenticatedShopperID()).thenReturn(AUTHENTICATED_SHOPPER_ID);

        testObj.buildTokenDeleteRequest(merchantInfoMock, creditCardPaymentInfoModelMock);

        verify(testObj).createDeleteTokenServiceRequest(eq(merchantInfoMock), eq(creditCardPaymentInfoModelMock), eq(tokenRequestMockWithReason));
    }

    @Test
    public void buildDirectAuthoriseRecurringPayment_shouldCreateAuthoriseRecurringPaymentRequest_WhenDynamicIntereactionTypeIsCommerce() {
        when(worldpayDynamicInteractionResolverService.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.ECOMMERCE);

        testObj.buildDirectAuthoriseRecurringPayment(merchantInfoMock, abstractOrderModelMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createTokenisedDirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());
        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertEquals(merchantInfoMock, requestParameters.getMerchantInfo());
        assertEquals(basicOrderInfoMock, requestParameters.getOrderInfo());
        assertEquals(tokenMock, requestParameters.getPayment());
        assertEquals(shopperMock, requestParameters.getShopper());
        assertEquals(shippingAddressMock, requestParameters.getShippingAddress());
        assertNull(requestParameters.getBillingAddress());
        assertNull(requestParameters.getStatementNarrative());
        assertEquals(additional3DSDataMock, requestParameters.getAdditional3DSData());
        assertEquals(storedCredentialsMock, requestParameters.getStoredCredentials());
    }

    @Test
    public void buildDirectAuthoriseRecurringPayment_shouldCreateAuthoriseRecurringPaymentRequest_WhenDynamicIntereactionTypeIsMoto() {
        when(worldpayDynamicInteractionResolverService.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.MOTO);

        testObj.buildDirectAuthoriseRecurringPayment(merchantInfoMock, abstractOrderModelMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createTokenisedDirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());
        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertEquals(merchantInfoMock, requestParameters.getMerchantInfo());
        assertEquals(basicOrderInfoMock, requestParameters.getOrderInfo());
        assertEquals(tokenMock, requestParameters.getPayment());
        assertEquals(shopperMock, requestParameters.getShopper());
        assertEquals(shippingAddressMock, requestParameters.getShippingAddress());
        assertNull(requestParameters.getBillingAddress());
        assertNull(requestParameters.getStatementNarrative());
        assertEquals(additional3DSDataMock, requestParameters.getAdditional3DSData());
        assertEquals(storedCredentialsMock, requestParameters.getStoredCredentials());
    }

    @Test
    public void buildDirectAuthoriseRecurringPayment_shouldCreateAuthoriseRecurringPaymentRequest_WhenDynamicIntereactionTypeIsRecurring() {
        when(worldpayDynamicInteractionResolverService.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.RECURRING);
        when(worldpayOrderServiceMock.createStoredCredentials(Usage.USED, MerchantInitiatedReason.RECURRING, TRANSACTION_IDENTIFIER)).thenReturn(storedCredentialsMock);

        testObj.buildDirectAuthoriseRecurringPayment(merchantInfoMock, abstractOrderModelMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createTokenisedDirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());
        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertEquals(merchantInfoMock, requestParameters.getMerchantInfo());
        assertEquals(basicOrderInfoMock, requestParameters.getOrderInfo());
        assertEquals(tokenMock, requestParameters.getPayment());
        assertEquals(shopperMock, requestParameters.getShopper());
        assertEquals(shippingAddressMock, requestParameters.getShippingAddress());
        assertNull(requestParameters.getBillingAddress());
        assertNull(requestParameters.getStatementNarrative());
        assertEquals(additional3DSDataMock, requestParameters.getAdditional3DSData());
        assertEquals(storedCredentialsMock, requestParameters.getStoredCredentials());
    }

    @Test
    public void testBuildSecondThreeDSecurePaymentRequest() {
        when(shopperMock.getSession()).thenReturn(sessionMock);

        when(worldpayOrderServiceMock.createShopper(null, sessionMock, null)).thenReturn(shopperMock);

        final SecondThreeDSecurePaymentRequest secondThreeDSecurePaymentRequestMock = testObj.buildSecondThreeDSecurePaymentRequest(merchantInfoMock, WORLDPAY_ORDER_CODE, SESSION_ID, COOKIE);

        assertEquals(COOKIE, secondThreeDSecurePaymentRequestMock.getCookie());
        assertEquals(merchantInfoMock, secondThreeDSecurePaymentRequestMock.getMerchantInfo());
        assertEquals(SESSION_ID, secondThreeDSecurePaymentRequestMock.getSessionId());
        assertEquals(WORLDPAY_ORDER_CODE, secondThreeDSecurePaymentRequestMock.getOrderCode());
    }

    @Test
    public void buildDirectAuthoriseRequestWithToken_ShouldReturnADirectAuthoriseServiceRequest_WhenItIsCalled() {
        when(cseAdditionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(worldpayOrderServiceMock.createBasicOrderInfo(WORLDPAY_ORDER_CODE, WORLDPAY_ORDER_CODE, amountMock)).thenReturn(basicOrderInfoMock);
        when(worldpayOrderServiceMock.createStoredCredentials(Usage.FIRST, null, null)).thenReturn(storedCredentialsMock);
        when(worldpayOrderServiceMock.createAdditional3DSData(worldpayAdditionalInfoDataMock)).thenReturn(additional3DSDataMock);
        when(worldpayRiskDataServiceMock.createRiskData(cartModelMock,worldpayAdditionalInfoDataMock)).thenReturn(riskDataMock);
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
    public void buildRedirectAuthoriseRequest_ShouldReturnADirectAuthoriseServiceRequestWithStoredCredentials_WhenThePaymentMethodsIsRequestedToBeSaved() {
        doReturn(redirectAuthoriseRequestMock).when(testObj).createRedirectAuthoriseRequest(any(AuthoriseRequestParameters.class));
        doReturn(includedPTsMock).when(testObj).getIncludedPaymentTypeList(additionalAuthInfoMock);
        when(additionalAuthInfoMock.getSaveCard()).thenReturn(Boolean.TRUE);
        when(additionalAuthInfoMock.getInstallationId()).thenReturn(INSTALLATION_ID);
        when(worldpayOrderServiceMock.createStoredCredentials(Usage.FIRST, null, null)).thenReturn(storedCredentialsMock);
        when(customerModelMock.getContactEmail()).thenReturn(SHOPPER_EMAIL_ADDRESS);
        when(worldpayAuthenticatedShopperIdStrategyMock.getAuthenticatedShopperId(customerModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(worldpayOrderServiceMock.createAuthenticatedShopper(SHOPPER_EMAIL_ADDRESS, AUTHENTICATED_SHOPPER_ID, null, null)).thenReturn(shopperWithoutSessionNorBrowserMock);

        testObj.buildRedirectAuthoriseRequest(merchantInfoMock, cartModelMock, additionalAuthInfoMock);

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
    }

    @Test
    public void buildRedirectAuthoriseRequest_ShouldReturnADirectAuthoriseServiceRequestWithoutStoredCredentials_WhenThePaymentMethodsIsNotRequestedToBeSaved() {
        doReturn(includedPTsMock).when(testObj).getIncludedPaymentTypeList(additionalAuthInfoMock);
        when(worldpayOrderServiceMock.createStoredCredentials(Usage.FIRST, null, null)).thenReturn(storedCredentialsMock);
        when(worldpayOrderServiceMock.createShopper(SHOPPER_EMAIL_ADDRESS, null, null)).thenReturn(shopperMock);

        testObj.buildRedirectAuthoriseRequest(merchantInfoMock, cartModelMock, additionalAuthInfoMock);

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

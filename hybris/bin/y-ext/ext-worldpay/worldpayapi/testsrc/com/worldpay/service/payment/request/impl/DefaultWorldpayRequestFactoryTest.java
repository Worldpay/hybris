package com.worldpay.service.payment.request.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.data.*;
import com.worldpay.data.applepay.ApplePay;
import com.worldpay.data.payment.*;
import com.worldpay.data.threeds2.Additional3DSData;
import com.worldpay.data.threeds2.RiskData;
import com.worldpay.data.token.CardDetails;
import com.worldpay.data.token.Token;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.enums.payment.storedCredentials.MerchantInitiatedReason;
import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.payment.*;
import com.worldpay.service.payment.request.WorldpayRequestService;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.service.request.DeleteTokenServiceRequest;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@ExtendWith(MockitoExtension.class)
class DefaultWorldpayRequestFactoryTest {

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
    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";
    private static final String TOKEN_UPDATED = "Token updated ";
    private static final String TOKEN_DELETED = "Token deleted ";
    private static final String SESSION_ID = "SESSION_ID";
    private static final String TRANSACTION_IDENTIFIER = "transactionIdentifier";
    private static final String INSTALLATION_ID = "installationId";
    private static final String KLARNA_V2_SSL = "KLARNA_V2-SSL";
    private static final String PAYPAL_SSL = "PAYPAL-SSL";

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
    private DirectAuthoriseServiceRequest directAuthoriseServiceRequestMock, directGooglePayAuthoriseServiceRequestMock, applePayAuthoriseServiceRequestMock;
    @Mock
    private Amount amountMock;
    @Mock
    private AddressModel paymentAddressModelMock;
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
    private ACHDirectDebitAdditionalAuthInfo achDirectDebitAdditionalAuthInfoMock;
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

    @Test
    void buildTokenRequest_shouldCreateTokenRequest() {
        when(worldpayCartServiceMock.getBillingAddress(cartModelMock, cseAdditionalAuthInfoMock)).thenReturn(billingAddressMock);
        mockTokenRequestForSaveCard();
        when(worldpayOrderServiceMock.createCsePayment(cseAdditionalAuthInfoMock, billingAddressMock)).thenReturn(csePaymentMock);
        when(worldpayAdditionalInfoDataMock.getAuthenticatedShopperId()).thenReturn(AUTHENTICATED_SHOPPER_ID);

        testObj.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayRequestServiceMock).createTokenServiceRequest(
                merchantInfoMock, AUTHENTICATED_SHOPPER_ID, csePaymentMock, tokenRequestMockWithReasonNull);
    }

    @Test
    void buildTokenRequest_ShouldUseShippingAsBilling_WhenUsingShippingAsBillingFlagIsTrue() {
        when(worldpayCartServiceMock.getBillingAddress(cartModelMock, cseAdditionalAuthInfoMock)).thenReturn(shippingAddressMock);
        mockTokenRequestForSaveCard();
        when(worldpayOrderServiceMock.createCsePayment(cseAdditionalAuthInfoMock, shippingAddressMock)).thenReturn(csePaymentMock);
        when(worldpayAdditionalInfoDataMock.getAuthenticatedShopperId()).thenReturn(AUTHENTICATED_SHOPPER_ID);

        testObj.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayOrderServiceMock).createCsePayment(cseAdditionalAuthInfoMock, shippingAddressMock);
    }

    @Test
    void buildDirectAuthoriseRequestWithTokenForCSE_ShouldAssembleRequestWithTokenShopperAddressesAnd3DSData() {
        mockBasicOrderInfo();
        when(worldpayRequestServiceMock.createAdditional3DSData(worldpayAdditionalInfoDataMock)).thenReturn(additional3DSDataMock);
        when(worldpayRiskDataServiceMock.createRiskData(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(riskDataMock);
        mockTokenFromSavedCard();
        mockAuthenticatedShopperWithSessionAndBrowser();
        when(worldpayRequestServiceMock.getDynamicInteractionType(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.ECOMMERCE);
        mockCartAddresses();

        final DirectAuthoriseServiceRequest result = testObj.buildDirectAuthoriseRequestWithTokenForCSE(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createTokenisedDirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());
        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertEquals(merchantInfoMock, result.getMerchantInfo());
        assertBaseAuthoriseRequestParameters(requestParameters, tokenMock, shopperMock);
        assertEquals(DynamicInteractionType.ECOMMERCE, requestParameters.getDynamicInteractionType());
        assertEquals(additional3DSDataMock, requestParameters.getAdditional3DSData());
        assertEquals(riskDataMock, requestParameters.getRiskData());
    }

    @Test
    void buildApplePayDirectAuthorisationRequest_ShouldAssembleAuthoriseRequestAndCreateApplePayDirectRequest() {
        mockBasicOrderInfo();
        when(worldpayOrderServiceMock.createApplePayPayment(applePayAdditionalAuthInfoMock)).thenReturn(applePayPaymentMock);
        mockSimpleShopper();
        mockCartAddresses();

        doReturn(applePayAuthoriseServiceRequestMock)
                .when(testObj)
                .createApplePayDirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());

        final DirectAuthoriseServiceRequest result =
                testObj.buildApplePayDirectAuthorisationRequest(merchantInfoMock, cartModelMock, applePayAdditionalAuthInfoMock);

        assertEquals(applePayAuthoriseServiceRequestMock, result);

        final AuthoriseRequestParameters params = authoriseRequestParametersArgumentCaptor.getValue();
        assertBaseAuthoriseRequestParameters(params, applePayPaymentMock, shopperMock);
        assertEquals(DynamicInteractionType.ECOMMERCE, params.getDynamicInteractionType());
        assertNull(params.getStatementNarrative());
    }

    @Test
    void build3dDirectAuthoriseRequest_shouldCreate3DSecureAuthoriseRequest() {
        when(worldpayOrderServiceMock.createBasicOrderInfo(WORLDPAY_ORDER_CODE, WORLDPAY_ORDER_CODE, null)).thenReturn(basicOrderInfoMock);
        when(worldpayRequestServiceMock.createSession(worldpayAdditionalInfoDataMock)).thenReturn(sessionMock);
        when(worldpayRequestServiceMock.createShopper(null, sessionMock, null)).thenReturn(shopperMock);

        doReturn(directAuthoriseServiceRequestMock)
                .when(testObj)
                .createDirect3DAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());

        final DirectAuthoriseServiceRequest result = testObj.build3dDirectAuthoriseRequest(
                merchantInfoMock, WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE, COOKIE);

        assertEquals(directAuthoriseServiceRequestMock, result);
        verify(directAuthoriseServiceRequestMock).setCookie(COOKIE);
        final AuthoriseRequestParameters params = authoriseRequestParametersArgumentCaptor.getValue();
        assertEquals(merchantInfoMock, params.getMerchantInfo());
        assertEquals(basicOrderInfoMock, params.getOrderInfo());
        assertNull(params.getPayment());
        assertEquals(shopperMock, params.getShopper());
        assertNull(params.getShippingAddress());
        assertNull(params.getBillingAddress());
        assertNull(params.getStatementNarrative());
        assertNull(params.getDynamicInteractionType());
        assertEquals(PA_RESPONSE, params.getPaRes());
    }

    @Test
    void buildDirectAuthoriseBankTransferRequest_shouldCreateDirectAuthoriseBankTransferRequest() throws WorldpayConfigurationException {
        mockBasicOrderInfo();
        when(worldpayCartServiceMock.getBillingAddress(cartModelMock, bankTransferAdditionalAuthInfoMock)).thenReturn(billingAddressMock);
        when(worldpayCartServiceMock.getAddressFromCart(cartModelMock, true)).thenReturn(shippingAddressMock);
        when(bankTransferAdditionalAuthInfoMock.getPaymentMethod()).thenReturn(PAYMENT_METHOD);
        when(bankTransferAdditionalAuthInfoMock.getShopperBankCode()).thenReturn(SHOPPER_BANK_CODE);
        when(bankTransferAdditionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(worldpayRequestServiceMock.createBankPayment(WORLDPAY_ORDER_CODE, PAYMENT_METHOD, SHOPPER_BANK_CODE)).thenReturn(paymentMock);
        mockShopperWithSessionAndBrowser();
        when(worldpayRequestServiceMock.getDynamicInteractionType(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.ECOMMERCE);

        doReturn(directAuthoriseServiceRequestMock)
                .when(testObj)
                .createBankTransferAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());

        final DirectAuthoriseServiceRequest result = testObj.buildDirectAuthoriseBankTransferRequest(merchantInfoMock, cartModelMock, bankTransferAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        assertEquals(directAuthoriseServiceRequestMock, result);
        final AuthoriseRequestParameters params = authoriseRequestParametersArgumentCaptor.getValue();
        assertBaseAuthoriseRequestParameters(params, paymentMock, shopperMock);
        assertEquals(STATEMENT_NARRATIVE, params.getStatementNarrative());
        assertEquals(DynamicInteractionType.ECOMMERCE, params.getDynamicInteractionType());
    }

    @Test
    void buildDirectAuthoriseACHDirectDebitRequest_shouldCreateDirectAuthoriseACHDirectDebitRequest() {
        mockBasicOrderInfo();
        when(worldpayCartServiceMock.getBillingAddress(cartModelMock, achDirectDebitAdditionalAuthInfoMock)).thenReturn(billingAddressMock);
        mockShopperWithSessionAndBrowser();
        when(achDirectDebitAdditionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(worldpayRequestServiceMock.getDynamicInteractionType(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.ECOMMERCE);
        when(worldpayOrderServiceMock.createACHDirectDebitPayment(billingAddressMock, achDirectDebitAdditionalAuthInfoMock)).thenReturn(paymentMock);
        when(worldpayCartServiceMock.getAddressFromCart(cartModelMock, true)).thenReturn(shippingAddressMock);

        doReturn(directAuthoriseServiceRequestMock)
                .when(testObj)
                .createACHDirectDebitAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());

        final DirectAuthoriseServiceRequest result = testObj.buildDirectAuthoriseACHDirectDebitRequest(
                merchantInfoMock, cartModelMock, achDirectDebitAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        assertEquals(directAuthoriseServiceRequestMock, result);
        verify(worldpayOrderServiceMock).createACHDirectDebitPayment(billingAddressMock, achDirectDebitAdditionalAuthInfoMock);

        final AuthoriseRequestParameters params = authoriseRequestParametersArgumentCaptor.getValue();
        assertBaseAuthoriseRequestParameters(params, paymentMock, shopperMock);
        assertEquals(STATEMENT_NARRATIVE, params.getStatementNarrative());
        assertEquals(DynamicInteractionType.ECOMMERCE, params.getDynamicInteractionType());
    }

    @Test
    void buildDirectAuthoriseKlarnaRequest_shouldCreateDirectAuthoriseKlarnaRequest() throws WorldpayConfigurationException {
        mockBasicOrderInfo();
        when(worldpayCartServiceMock.getBillingAddress(cartModelMock, additionalAuthInfoMock)).thenReturn(billingAddressMock);
        when(worldpayCartServiceMock.getAddressFromCart(cartModelMock, true)).thenReturn(shippingAddressMock);
        when(cartModelMock.getUser()).thenReturn(customerModelMock);
        when(customerModelMock.getSessionLanguage()).thenReturn(languageModelMock);
        when(billingAddressMock.getCountryCode()).thenReturn(COUNTRY_CODE);
        when(additionalAuthInfoMock.getPaymentMethod()).thenReturn(KLARNA_V2_SSL);
        when(worldpayOrderServiceMock.createKlarnaPayment(COUNTRY_CODE, languageModelMock, null, KLARNA_V2_SSL)).thenReturn(paymentMock);
        when(additionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(worldpayKlarnaServiceMock.createOrderLines(cartModelMock)).thenReturn(orderLinesMock);
        mockShopperWithSessionAndBrowser();

        doReturn(directAuthoriseServiceRequestMock)
                .when(testObj)
                .createKlarnaDirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());

        final DirectAuthoriseServiceRequest result = testObj.buildDirectAuthoriseKlarnaRequest(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock);

        assertEquals(directAuthoriseServiceRequestMock, result);
        verify(worldpayAdditionalRequestDataServiceMock).populateRequestGuaranteedPayments(eq(cartModelMock), eq(worldpayAdditionalInfoDataMock), any());
        final AuthoriseRequestParameters params = authoriseRequestParametersArgumentCaptor.getValue();
        assertBaseAuthoriseRequestParameters(params, paymentMock, shopperMock);
        assertEquals(STATEMENT_NARRATIVE, params.getStatementNarrative());
        assertEquals(DynamicInteractionType.ECOMMERCE, params.getDynamicInteractionType());
        assertEquals(orderLinesMock, params.getOrderLines());
    }

    @Test
    void buildDirectAuthoriseGooglePayRequest_shouldBuildDirectAuthorisationWithGooglePay() {
        mockBasicOrderInfo();
        when(worldpayCartServiceMock.getAddressFromCart(cartModelMock,true)).thenReturn(shippingAddressMock);
        when(worldpayCartServiceMock.getAddressFromCart(cartModelMock, false)).thenReturn(billingAddressMock);
        mockSimpleShopper();
        mockGooglePayPayment();

        doReturn(directGooglePayAuthoriseServiceRequestMock)
                .when(testObj)
                .createGooglePayDirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());

        final DirectAuthoriseServiceRequest result = testObj.buildDirectAuthoriseGooglePayRequest(merchantInfoMock, cartModelMock, googlePayAdditionalAuthInfoMock);

        assertThat(result).isEqualTo(directGooglePayAuthoriseServiceRequestMock);
        verify(worldpayAdditionalRequestDataServiceMock).populateRequestGuaranteedPayments(eq(cartModelMock), any(WorldpayAdditionalInfoData.class), any());
        verify(worldpayRequestServiceMock).createShopper(SHOPPER_EMAIL_ADDRESS, null, null);
        final AuthoriseRequestParameters authoriseRequestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertBaseAuthoriseRequestParameters(authoriseRequestParameters, googlePayMock, shopperMock);
        assertThat(authoriseRequestParameters.getDynamicInteractionType()).isEqualTo(DynamicInteractionType.ECOMMERCE);
    }

    @Test
    void buildDirectAuthoriseGooglePayRequest_shouldBuildDirectAuthorisationWithGooglePay_whenSaveCard() {
        mockBasicOrderInfo();
        when(worldpayCartServiceMock.getAddressFromCart(cartModelMock,true)).thenReturn(shippingAddressMock);
        when(worldpayCartServiceMock.getAddressFromCart(cartModelMock, false)).thenReturn(billingAddressMock);
        when(worldpayCartServiceMock.getEmailForCustomer(cartModelMock)).thenReturn(SHOPPER_EMAIL_ADDRESS);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(worldpayRequestServiceMock.createAuthenticatedShopper(SHOPPER_EMAIL_ADDRESS, AUTHENTICATED_SHOPPER_ID, null, null)).thenReturn(shopperMock);
        mockTokenRequestForSaveCard();
        when(googlePayAdditionalAuthInfoMock.getSaveCard()).thenReturn(Boolean.TRUE);
        mockGooglePayPayment();
        when(worldpayRequestServiceMock.createStoredCredentials(Usage.FIRST, null, null)).thenReturn(storedCredentialsMock);

        doReturn(directGooglePayAuthoriseServiceRequestMock)
                .when(testObj)
                .createGooglePayDirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());

        final DirectAuthoriseServiceRequest result = testObj.buildDirectAuthoriseGooglePayRequest(merchantInfoMock, cartModelMock, googlePayAdditionalAuthInfoMock);

        verify(worldpayRequestServiceMock).createAuthenticatedShopper(SHOPPER_EMAIL_ADDRESS, AUTHENTICATED_SHOPPER_ID, null, null);
        assertThat(result).isEqualTo(directGooglePayAuthoriseServiceRequestMock);
        final AuthoriseRequestParameters authoriseRequestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertBaseAuthoriseRequestParameters(authoriseRequestParameters, googlePayMock, shopperMock);
        assertThat(authoriseRequestParameters.getTokenRequest()).isEqualTo(tokenRequestMockWithReasonNull);
        assertEquals(authoriseRequestParameters.getStoredCredentials(), storedCredentialsMock);
    }

    @Test
    void buildTokenUpdateRequest_shouldCreateUpdateTokenRequest() {
        when(worldpayTokenEventReferenceCreationStrategyMock.createTokenEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);
        when(createTokenResponseMock.getToken().getTokenDetails().getPaymentTokenID()).thenReturn(PAYMENT_TOKEN_ID);
        when(worldpayRequestServiceMock.createCardDetails(cseAdditionalAuthInfoMock, paymentAddressModelMock)).thenReturn(cartDetailsMock);
        when(worldpayRequestServiceMock.createTokenRequest(
                eq(TOKEN_EVENT_REFERENCE), startsWith(TOKEN_UPDATED))).thenReturn(tokenRequestMockWithReason);

        testObj.buildTokenUpdateRequest(merchantInfoMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock, paymentAddressModelMock, createTokenResponseMock);

        verify(worldpayRequestServiceMock).createUpdateTokenServiceRequest(
                merchantInfoMock,
                worldpayAdditionalInfoDataMock,
                tokenRequestMockWithReason,
                PAYMENT_TOKEN_ID,
                cartDetailsMock
        );
    }

    @Test
    void buildTokenUpdateRequest_WhenAddressIsNull_ShouldCreateUpdateTokenRequestWithoutAddress() {
        when(worldpayTokenEventReferenceCreationStrategyMock.createTokenEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);
        when(createTokenResponseMock.getToken().getTokenDetails().getPaymentTokenID()).thenReturn(PAYMENT_TOKEN_ID);
        when(worldpayRequestServiceMock.createCardDetails(cseAdditionalAuthInfoMock, null)).thenReturn(cartDetailsMock);
        when(worldpayRequestServiceMock.createTokenRequest(
                eq(TOKEN_EVENT_REFERENCE), startsWith(TOKEN_UPDATED))).thenReturn(tokenRequestMockWithReason);

        testObj.buildTokenUpdateRequest(merchantInfoMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock, null, createTokenResponseMock);

        verify(worldpayRequestServiceMock).createUpdateTokenServiceRequest(
                merchantInfoMock,
                worldpayAdditionalInfoDataMock,
                tokenRequestMockWithReason,
                PAYMENT_TOKEN_ID,
                cartDetailsMock
        );
    }

    @Test
    void buildTokenDeleteRequest_ShouldCreateDeleteTokenRequest() {
        when(creditCardPaymentInfoModelMock.getEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);
        when(creditCardPaymentInfoModelMock.getAuthenticatedShopperID()).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(worldpayRequestServiceMock.createTokenRequestForDeletion(
                eq(TOKEN_EVENT_REFERENCE),
                startsWith(TOKEN_DELETED),
                eq(AUTHENTICATED_SHOPPER_ID)
        )).thenReturn(tokenRequestMockWithReason);

        final DeleteTokenServiceRequest result = testObj.buildTokenDeleteRequest(merchantInfoMock, creditCardPaymentInfoModelMock, SUBSCRIPTION_ID);

        assertThat(result).isNotNull();
        verify(testObj).createDeleteTokenServiceRequest(merchantInfoMock, AUTHENTICATED_SHOPPER_ID, SUBSCRIPTION_ID, tokenRequestMockWithReason);
    }

    @Test
    void buildDirectAuthoriseRecurringPayment_WhenDynamicInteractionTypeIsCommerce_ShouldCreateAuthoriseRecurringPaymentRequestAndSetDeviceSession() {
        mockBasicOrderInfo();
        when(worldpayRequestServiceMock.createAdditional3DSData(worldpayAdditionalInfoDataMock)).thenReturn(additional3DSDataMock);
        mockAuthenticatedShopperWithSessionAndBrowser();
        mockTokenFromSavedCard();
        mockCartAddresses();
        when(worldpayRequestServiceMock.getDynamicInteractionType(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.ECOMMERCE);
        when(worldpayRequestServiceMock.createStoredCredentials(Usage.USED, null, null)).thenReturn(storedCredentialsMock);

        doReturn(directAuthoriseServiceRequestMock).when(testObj).createTokenisedDirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());

        final DirectAuthoriseServiceRequest result = testObj.buildDirectAuthoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        assertEquals(directAuthoriseServiceRequestMock, result);
        verify(worldpayAdditionalRequestDataServiceMock).populateDirectRequestAdditionalData(eq(cartModelMock), eq(worldpayAdditionalInfoDataMock), any());
        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertBaseAuthoriseRequestParameters(requestParameters, tokenMock, shopperMock);
        assertNull(requestParameters.getStatementNarrative());
        assertEquals(additional3DSDataMock, requestParameters.getAdditional3DSData());
        assertEquals(storedCredentialsMock, requestParameters.getStoredCredentials());
        assertEquals(DynamicInteractionType.ECOMMERCE, requestParameters.getDynamicInteractionType());
    }

    @Test
    void buildDirectAuthoriseRecurringPayment_WhenDynamicInteractionTypeIsMoto_ShouldCreateAuthoriseRecurringPaymentRequestAndNotSetDeviceSession() {
        mockBasicOrderInfo();
        when(worldpayRequestServiceMock.createAdditional3DSData(worldpayAdditionalInfoDataMock)).thenReturn(additional3DSDataMock);
        mockAuthenticatedShopperWithSessionAndBrowser();
        mockTokenFromSavedCard();
        mockCartAddresses();
        when(worldpayRequestServiceMock.getDynamicInteractionType(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.MOTO);
        when(worldpayRequestServiceMock.createStoredCredentials(Usage.USED, null, null)).thenReturn(storedCredentialsMock);

        doReturn(directAuthoriseServiceRequestMock).when(testObj).createTokenisedDirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());

        testObj.buildDirectAuthoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertBaseAuthoriseRequestParameters(requestParameters, tokenMock, shopperMock);
        assertEquals(DynamicInteractionType.MOTO, requestParameters.getDynamicInteractionType());
        assertNull(requestParameters.getStatementNarrative());
        assertEquals(additional3DSDataMock, requestParameters.getAdditional3DSData());
        assertEquals(storedCredentialsMock, requestParameters.getStoredCredentials());
        assertNull(requestParameters.getAction());
    }

    @Test
    void buildDirectAuthoriseRecurringPayment_shouldCreateAuthoriseRecurringPaymentRequest_WhenDynamicInteractionTypeIsRecurring() {
        mockBasicOrderInfo();
        when(worldpayRequestServiceMock.createAdditional3DSData(worldpayAdditionalInfoDataMock)).thenReturn(additional3DSDataMock);
        mockAuthenticatedShopperWithSessionAndBrowser();
        when(cartModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(creditCardPaymentInfoModelMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);
        when(creditCardPaymentInfoModelMock.getTransactionIdentifier()).thenReturn(TRANSACTION_IDENTIFIER);
        when(worldpayAdditionalInfoDataMock.getSecurityCode()).thenReturn(CVC_CODE);
        when(worldpayRequestServiceMock.createToken(SUBSCRIPTION_ID, CVC_CODE)).thenReturn(tokenMock);
        mockCartAddresses();
        when(worldpayRequestServiceMock.getDynamicInteractionType(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.RECURRING);
        when(worldpayRequestServiceMock.createStoredCredentials(Usage.USED, MerchantInitiatedReason.RECURRING, TRANSACTION_IDENTIFIER)).thenReturn(storedCredentialsMock);

        doReturn(directAuthoriseServiceRequestMock).when(testObj).createTokenisedDirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());

        testObj.buildDirectAuthoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertBaseAuthoriseRequestParameters(requestParameters, tokenMock, shopperMock);
        assertNull(requestParameters.getStatementNarrative());
        assertEquals(additional3DSDataMock, requestParameters.getAdditional3DSData());
        assertEquals(storedCredentialsMock, requestParameters.getStoredCredentials());
        assertEquals(DynamicInteractionType.RECURRING, requestParameters.getDynamicInteractionType());
    }

    @Test
    void buildSecondThreeDSecurePaymentRequest_shouldPopulateAllFields() {
        final SecondThreeDSecurePaymentRequest secondThreeDSecurePaymentRequestMock = testObj.buildSecondThreeDSecurePaymentRequest(merchantInfoMock, WORLDPAY_ORDER_CODE, SESSION_ID, COOKIE);

        assertEquals(COOKIE, secondThreeDSecurePaymentRequestMock.getCookie());
        assertEquals(merchantInfoMock, secondThreeDSecurePaymentRequestMock.getMerchantInfo());
        assertEquals(SESSION_ID, secondThreeDSecurePaymentRequestMock.getSessionId());
        assertEquals(WORLDPAY_ORDER_CODE, secondThreeDSecurePaymentRequestMock.getOrderCode());
    }

    @Test
    void buildDirectAuthoriseRequestWithToken_WhenItIsCalledAndCurrentSite_ShouldReturnADirectAuthoriseServiceRequestAndNotSetDeviceSession() {
        mockBasicOrderInfo();
        when(worldpayCartServiceMock.getBillingAddress(cartModelMock, cseAdditionalAuthInfoMock)).thenReturn(billingAddressMock);
        when(worldpayCartServiceMock.getAddressFromCart(cartModelMock, true)).thenReturn(shippingAddressMock);
        when(worldpayOrderServiceMock.createCsePayment(cseAdditionalAuthInfoMock, billingAddressMock)).thenReturn(csePaymentMock);
        mockAuthenticatedShopperWithSessionAndBrowser();
        when(worldpayRequestServiceMock.getDynamicInteractionType(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.ECOMMERCE);
        mockTokenRequestForSaveCard();
        when(cseAdditionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(worldpayRequestServiceMock.createStoredCredentials(Usage.FIRST, null, null)).thenReturn(storedCredentialsMock);
        when(worldpayRequestServiceMock.createAdditional3DSData(worldpayAdditionalInfoDataMock)).thenReturn(additional3DSDataMock);
        when(worldpayRiskDataServiceMock.createRiskData(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(riskDataMock);
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(true);

        doReturn(directAuthoriseServiceRequestMock).when(testObj).buildDirectTokenAndAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());

        final DirectAuthoriseServiceRequest result = testObj.buildDirectTokenAndAuthorise(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, cseAdditionalAuthInfoMock);

        assertEquals(directAuthoriseServiceRequestMock, result);
        verify(worldpayAdditionalRequestDataServiceMock).populateDirectRequestAdditionalData(eq(cartModelMock), eq(worldpayAdditionalInfoDataMock), any());

        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertBaseAuthoriseRequestParameters(requestParameters, csePaymentMock, shopperMock);
        assertEquals(STATEMENT_NARRATIVE, requestParameters.getStatementNarrative());
        assertEquals(DynamicInteractionType.ECOMMERCE, requestParameters.getDynamicInteractionType());
        assertEquals(AUTHENTICATED_SHOPPER_ID, requestParameters.getAuthenticatedShopperId());
        assertEquals(tokenRequestMockWithReasonNull, requestParameters.getTokenRequest());
        assertEquals(additional3DSDataMock, requestParameters.getAdditional3DSData());
        assertEquals(riskDataMock, requestParameters.getRiskData());
        assertEquals(storedCredentialsMock, requestParameters.getStoredCredentials());
    }

    @Test
    void buildRedirectAuthoriseRequest_WhenThePaymentMethodsIsRequestedToBeSaved_ShouldReturnADirectAuthoriseServiceRequestWithStoredCredentials() throws WorldpayConfigurationException {
        mockBasicOrderInfo();
        when(worldpayCartServiceMock.getAddressFromCart(cartModelMock, true)).thenReturn(shippingAddressMock);
        when(worldpayCartServiceMock.getBillingAddress(cartModelMock, additionalAuthInfoMock)).thenReturn(billingAddressMock);
        when(worldpayRiskDataServiceMock.createRiskData(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(riskDataMock);
        when(worldpayCartServiceMock.getEmailForCustomer(cartModelMock)).thenReturn(SHOPPER_EMAIL_ADDRESS);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(worldpayRequestServiceMock.createAuthenticatedShopper(SHOPPER_EMAIL_ADDRESS, AUTHENTICATED_SHOPPER_ID, null, null)).thenReturn(shopperWithoutSessionNorBrowserMock);
        mockTokenRequestForSaveCard();
        when(additionalAuthInfoMock.getSaveCard()).thenReturn(Boolean.TRUE);
        when(additionalAuthInfoMock.getInstallationId()).thenReturn(INSTALLATION_ID);
        when(additionalAuthInfoMock.getPaymentMethod()).thenReturn(PaymentType.ONLINE.getMethodCode());
        when(worldpayRequestServiceMock.createStoredCredentials(Usage.FIRST, null, null)).thenReturn(storedCredentialsMock);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(worldpayRequestServiceMock.createAuthenticatedShopper(SHOPPER_EMAIL_ADDRESS, AUTHENTICATED_SHOPPER_ID, null, null)).thenReturn(shopperWithoutSessionNorBrowserMock);
        doReturn(includedPTsMock).when(testObj).getIncludedPaymentTypeList(additionalAuthInfoMock);
        doReturn(redirectAuthoriseRequestMock).when(testObj).createRedirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());

        testObj.buildRedirectAuthoriseRequest(merchantInfoMock, cartModelMock, additionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertBaseAuthoriseRequestParameters(requestParameters, null, shopperWithoutSessionNorBrowserMock);
        assertThat(requestParameters.getInstallationId()).isEqualTo(INSTALLATION_ID);
        assertEquals(includedPTsMock, requestParameters.getIncludedPTs());
        assertEquals(storedCredentialsMock, requestParameters.getStoredCredentials());
        assertEquals(tokenRequestMockWithReasonNull, requestParameters.getTokenRequest());
        assertEquals(riskDataMock, requestParameters.getRiskData());
    }

    @Test
    void buildRedirectAuthoriseRequest_WhenPaymentMethodIsNotPaypalExpress_ShouldNotIncludePaymentAttributes() throws WorldpayConfigurationException {
        mockBasicOrderInfo();
        when(worldpayCartServiceMock.getAddressFromCart(cartModelMock, true)).thenReturn(shippingAddressMock);
        when(worldpayCartServiceMock.getBillingAddress(cartModelMock, additionalAuthInfoMock)).thenReturn(billingAddressMock);
        when(worldpayRiskDataServiceMock.createRiskData(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(riskDataMock);
        when(worldpayCartServiceMock.getEmailForCustomer(cartModelMock)).thenReturn(SHOPPER_EMAIL_ADDRESS);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(worldpayRequestServiceMock.createAuthenticatedShopper(SHOPPER_EMAIL_ADDRESS, AUTHENTICATED_SHOPPER_ID, null, null)).thenReturn(shopperWithoutSessionNorBrowserMock);
        mockTokenRequestForSaveCard();
        when(additionalAuthInfoMock.getSaveCard()).thenReturn(Boolean.TRUE);
        when(additionalAuthInfoMock.getInstallationId()).thenReturn(INSTALLATION_ID);
        when(additionalAuthInfoMock.getPaymentMethod()).thenReturn(PaymentType.ONLINE.getMethodCode());

        doReturn(includedPTsMock).when(testObj).getIncludedPaymentTypeList(additionalAuthInfoMock);
        doReturn(redirectAuthoriseRequestMock).when(testObj).createRedirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());

        testObj.buildRedirectAuthoriseRequest(merchantInfoMock, cartModelMock, additionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertThat(requestParameters.getPaymentMethodAttributes()).isNullOrEmpty();
    }

    @Test
    void buildRedirectAuthoriseRequest_WhenThePaymentMethodsIsNotRequestedToBeSaved_ShouldReturnADirectAuthoriseServiceRequestWithoutStoredCredentials() throws WorldpayConfigurationException {
        mockBasicOrderInfo();
        when(worldpayCartServiceMock.getAddressFromCart(cartModelMock, true)).thenReturn(shippingAddressMock);
        when(worldpayCartServiceMock.getBillingAddress(cartModelMock, additionalAuthInfoMock)).thenReturn(billingAddressMock);
        when(worldpayRiskDataServiceMock.createRiskData(cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(riskDataMock);
        when(worldpayCartServiceMock.getEmailForCustomer(cartModelMock)).thenReturn(SHOPPER_EMAIL_ADDRESS);
        when(worldpayRequestServiceMock.createShopper(SHOPPER_EMAIL_ADDRESS, null, null)).thenReturn(shopperMock);

        doReturn(includedPTsMock).when(testObj).getIncludedPaymentTypeList(additionalAuthInfoMock);
        doReturn(redirectAuthoriseRequestMock).when(testObj).createRedirectAuthoriseRequest(authoriseRequestParametersArgumentCaptor.capture());

        testObj.buildRedirectAuthoriseRequest(merchantInfoMock, cartModelMock, additionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        final AuthoriseRequestParameters requestParameters = authoriseRequestParametersArgumentCaptor.getValue();
        assertBaseAuthoriseRequestParameters(requestParameters, null, shopperMock);
        assertEquals(includedPTsMock, requestParameters.getIncludedPTs());
        assertNull(requestParameters.getStoredCredentials());
        assertNull(requestParameters.getTokenRequest());
        assertEquals(riskDataMock, requestParameters.getRiskData());
    }

    @Test
    void getSubscriptionId_ShouldObtainSubscriptionIdFromCreditCardPaymentInfo_WhenPaymentInfoIsInstanceOfCreditCardPaymentInfo() {
        when(abstractOrderModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(creditCardPaymentInfoModelMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);

        final String result = testObj.getSubscriptionId(abstractOrderModelMock);

        verify(creditCardPaymentInfoModelMock).getSubscriptionId();

        assertEquals(SUBSCRIPTION_ID, result);
    }

    @Test
    void getSubscriptionId_ShouldObtainSubscriptionIdFromWorldpayAPMPaymentInfo_WhenPaymentInfoIsInstanceOfWorldpayAPMPaymentInfo() {
        when(abstractOrderModelMock.getPaymentInfo()).thenReturn(wordpayPaymentInfoModelMock);
        when(wordpayPaymentInfoModelMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);

        final String result = testObj.getSubscriptionId(abstractOrderModelMock);

        verify(wordpayPaymentInfoModelMock).getSubscriptionId();

        assertEquals(SUBSCRIPTION_ID, result);
    }

    @Test
    void internalGetRedirectAuthoriseServiceRequestForKlarna_ShouldReturnRedirectAuthoriseServiceRequest() throws WorldpayConfigurationException {
        when(cartModelMock.getUser()).thenReturn(customerModelMock);
        when(worldpayCartServiceMock.getBillingAddress(cartModelMock, additionalAuthInfoMock)).thenReturn(billingAddressMock);
        when(worldpayCartServiceMock.getEmailForCustomer(cartModelMock)).thenReturn(SHOPPER_EMAIL_ADDRESS);
        when(worldpayOrderServiceMock.createKlarnaPayment(COUNTRY_CODE, languageModelMock, null, KLARNA_V2_SSL)).thenReturn(paymentMock);

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

    @Test
    void internalGetRedirectAuthoriseServiceRequestForAlternativePayments_ShouldReturnRedirectAuthoriseServiceRequest() throws WorldpayConfigurationException {
        when(worldpayCartServiceMock.getBillingAddress(cartModelMock, additionalAuthInfoMock)).thenReturn(billingAddressMock);
        when(worldpayCartServiceMock.getEmailForCustomer(cartModelMock)).thenReturn(SHOPPER_EMAIL_ADDRESS);
        when(worldpayOrderServiceMock.createAlternativePayment(COUNTRY_CODE, PAYPAL_SSL)).thenReturn(paymentMock);
        when(additionalAuthInfoMock.getPaymentMethod()).thenReturn(PAYPAL_SSL);
        when(billingAddressMock.getCountryCode()).thenReturn(COUNTRY_CODE);
        doReturn(authoriseRequestParametersCreatorMock).when(authoriseRequestParametersCreatorMock).withPayment(paymentMock);
        doReturn(authoriseRequestParametersCreatorMock).when(authoriseRequestParametersCreatorMock).withShopper(shopperMock);
        when(worldpayRequestServiceMock.createShopper(SHOPPER_EMAIL_ADDRESS, null, null)).thenReturn(shopperMock);
        doReturn(getAuthoriseRequestParameters()).when(authoriseRequestParametersCreatorMock).build();

        final RedirectAuthoriseServiceRequest result = testObj.internalGetRedirectAuthoriseServiceRequestForAlternativePayments(cartModelMock, additionalAuthInfoMock, authoriseRequestParametersCreatorMock, worldpayAdditionalInfoDataMock);

        verify(authoriseRequestParametersCreatorMock).withPayment(paymentMock);
        verify(authoriseRequestParametersCreatorMock).withShopper(shopperMock);
        assertThat(result).isNotNull();
    }


    private void mockBasicOrderInfo() {
        when(worldpayOrderServiceMock.generateWorldpayOrderCode(cartModelMock)).thenReturn(WORLDPAY_ORDER_CODE);
        when(cartModelMock.getCurrency()).thenReturn(currencyModelMock);
        when(cartModelMock.getTotalPrice()).thenReturn(INTEGER_TOTAL);
        when(worldpayOrderServiceMock.createAmount(currencyModelMock, INTEGER_TOTAL)).thenReturn(amountMock);
        when(worldpayOrderServiceMock.createBasicOrderInfo(WORLDPAY_ORDER_CODE, WORLDPAY_ORDER_CODE, amountMock)).thenReturn(basicOrderInfoMock);
    }

    private void mockCartAddresses() {
        when(worldpayCartServiceMock.getAddressFromCart(cartModelMock, true)).thenReturn(shippingAddressMock);
        when(worldpayCartServiceMock.getAddressFromCart(cartModelMock, false)).thenReturn(billingAddressMock);
    }

    private void mockAuthenticatedShopperWithSessionAndBrowser() {
        when(worldpayCartServiceMock.getEmailForCustomer(cartModelMock)).thenReturn(SHOPPER_EMAIL_ADDRESS);
        when(worldpayRequestServiceMock.createSession(worldpayAdditionalInfoDataMock)).thenReturn(sessionMock);
        when(worldpayRequestServiceMock.createBrowser(worldpayAdditionalInfoDataMock)).thenReturn(browserMock);
        when(worldpayAdditionalInfoDataMock.getAuthenticatedShopperId()).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(worldpayRequestServiceMock.createAuthenticatedShopper(SHOPPER_EMAIL_ADDRESS, AUTHENTICATED_SHOPPER_ID, sessionMock, browserMock)).thenReturn(shopperMock);
    }

    private void mockSimpleShopper() {
        when(worldpayCartServiceMock.getEmailForCustomer(cartModelMock)).thenReturn(SHOPPER_EMAIL_ADDRESS);
        when(worldpayRequestServiceMock.createShopper(SHOPPER_EMAIL_ADDRESS, null, null)).thenReturn(shopperMock);
    }

    private void mockShopperWithSessionAndBrowser() {
        when(worldpayCartServiceMock.getEmailForCustomer(cartModelMock)).thenReturn(SHOPPER_EMAIL_ADDRESS);
        when(worldpayRequestServiceMock.createSession(worldpayAdditionalInfoDataMock)).thenReturn(sessionMock);
        when(worldpayRequestServiceMock.createBrowser(worldpayAdditionalInfoDataMock)).thenReturn(browserMock);
        when(worldpayRequestServiceMock.createShopper(SHOPPER_EMAIL_ADDRESS, sessionMock, browserMock)).thenReturn(shopperMock);
    }

    private void mockTokenFromSavedCard() {
        when(cartModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(creditCardPaymentInfoModelMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);
        when(worldpayAdditionalInfoDataMock.getSecurityCode()).thenReturn(CVC_CODE);
        when(worldpayRequestServiceMock.createToken(SUBSCRIPTION_ID, CVC_CODE)).thenReturn(tokenMock);
    }

    private void mockTokenRequestForSaveCard() {
        when(worldpayTokenEventReferenceCreationStrategyMock.createTokenEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);
        when(worldpayRequestServiceMock.createTokenRequest(TOKEN_EVENT_REFERENCE, null)).thenReturn(tokenRequestMockWithReasonNull);
    }

    private void mockGooglePayPayment() {
        when(googlePayAdditionalAuthInfoMock.getProtocolVersion()).thenReturn("protocolVersion");
        when(googlePayAdditionalAuthInfoMock.getSignature()).thenReturn("signature");
        when(googlePayAdditionalAuthInfoMock.getSignedMessage()).thenReturn("signedMessage");
        when(worldpayOrderServiceMock.createGooglePayPayment("protocolVersion", "signature", "signedMessage")).thenReturn(googlePayMock);
    }

    private void assertBaseAuthoriseRequestParameters(final AuthoriseRequestParameters requestParameters, final Payment payment, final Shopper shopper) {
        assertEquals(merchantInfoMock, requestParameters.getMerchantInfo());
        assertEquals(basicOrderInfoMock, requestParameters.getOrderInfo());
        assertEquals(payment, requestParameters.getPayment());
        assertEquals(shopper, requestParameters.getShopper());
        assertEquals(shippingAddressMock, requestParameters.getShippingAddress());
        assertEquals(billingAddressMock, requestParameters.getBillingAddress());
    }
}

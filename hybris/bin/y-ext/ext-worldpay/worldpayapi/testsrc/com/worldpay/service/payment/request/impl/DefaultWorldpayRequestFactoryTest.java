package com.worldpay.service.payment.request.impl;

import com.worldpay.core.services.strategies.RecurringGenerateMerchantTransactionCodeStrategy;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.interaction.WorldpayDynamicInteractionResolverService;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.Cse;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.Token;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.payment.WorldpayKlarnaStrategy;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayTokenEventReferenceCreationStrategy;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.response.CreateTokenResponse;
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
import java.util.Locale;

import static com.worldpay.service.payment.request.impl.DefaultWorldpayRequestFactory.TOKEN_DELETED;
import static com.worldpay.service.payment.request.impl.DefaultWorldpayRequestFactory.TOKEN_UPDATED;
import static org.junit.Assert.assertEquals;
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
    private DirectAuthoriseServiceRequest directAuthoriseServiceRequestMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private DirectAuthoriseServiceRequest directTokenisedAuthoriseServiceRequestMock;
    @Mock
    private Amount amountMock;
    @Mock
    private AddressModel deliveryAddressModelMock, paymentAddressModelMock;
    @Mock
    private CustomerModel customerModelMock;
    @Mock
    private TokenRequest tokenRequestMockWithReasonNull, tokenRequestMockWithReason;
    @Mock
    private Shopper authenticatedShopperMock;
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
    private Payment paymentMock;
    @Mock
    private Shopper shopperMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private CreateTokenResponse createTokenResponseMock;
    @Captor
    private ArgumentCaptor<CardDetails> cardDetailsCaptor;
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
        when(worldpayOrderServiceMock.createAuthenticatedShopper(SHOPPER_EMAIL_ADDRESS, AUTHENTICATED_SHOPPER_ID, sessionMock, browserMock)).thenReturn(authenticatedShopperMock);
        when(worldpayAdditionalInfoDataMock.getAuthenticatedShopperId()).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(worldpayOrderServiceMock.createBankPayment(PAYMENT_METHOD, SHOPPER_BANK_CODE)).thenReturn(paymentMock);
        when(worldpayOrderServiceMock.createShopper(SHOPPER_EMAIL_ADDRESS, sessionMock, browserMock)).thenReturn(shopperMock);
        when(bankTransferAdditionalAuthInfoMock.getPaymentMethod()).thenReturn(PAYMENT_METHOD);
        when(bankTransferAdditionalAuthInfoMock.getShopperBankCode()).thenReturn(SHOPPER_BANK_CODE);
        when(bankTransferAdditionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(worldpayDeliveryAddressStrategyMock.getDeliveryAddress(cartModelMock)).thenReturn(deliveryAddressModelMock);
        when(worldpayDeliveryAddressStrategyMock.getDeliveryAddress(abstractOrderModelMock)).thenReturn(deliveryAddressModelMock);
        when(worldpayDynamicInteractionResolverService.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.ECOMMERCE);
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
    public void shouldUseShippingAsBilling() throws Exception {
        when(cseAdditionalAuthInfoMock.getUsingShippingAsBilling()).thenReturn(true);
        doReturn(directTokenisedAuthoriseServiceRequestMock).when(testObj).createTokenisedDirectAuthoriseRequest(merchantInfoMock, basicOrderInfoMock, tokenMock, authenticatedShopperMock, shippingAddressMock, DynamicInteractionType.ECOMMERCE);
        doReturn(csePaymentMock).when(testObj).createCsePayment(cseAdditionalAuthInfoMock, shippingAddressMock);

        testObj.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayOrderServiceMock).createTokenServiceRequest(merchantInfoMock, AUTHENTICATED_SHOPPER_ID, csePaymentMock, tokenRequestMockWithReasonNull);
    }

    @Test
    public void shouldCreateTokenisedDirectAuthoriseRequest() throws Exception {
        doReturn(directTokenisedAuthoriseServiceRequestMock).when(testObj).createTokenisedDirectAuthoriseRequest(merchantInfoMock, basicOrderInfoMock, tokenMock, authenticatedShopperMock, shippingAddressMock, DynamicInteractionType.ECOMMERCE);

        testObj.buildDirectAuthoriseRequest(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createTokenisedDirectAuthoriseRequest(merchantInfoMock, basicOrderInfoMock, tokenMock, authenticatedShopperMock, shippingAddressMock, DynamicInteractionType.ECOMMERCE);
    }

    @Test
    public void shouldCreate3DSecureAuthoriseRequest() {
        doReturn(directAuthoriseServiceRequestMock).when(testObj).createDirect3DAuthoriseRequest(merchantInfoMock, basicOrderInfoMock, sessionMock, PA_RESPONSE);

        final DirectAuthoriseServiceRequest result = testObj.build3dDirectAuthoriseRequest(merchantInfoMock, WORLDPAY_ORDER_CODE, worldpayAdditionalInfoDataMock, PA_RESPONSE, COOKIE);

        assertEquals(directAuthoriseServiceRequestMock, result);
        verify(directAuthoriseServiceRequestMock).setCookie(COOKIE);
    }

    @Test
    public void shouldCreateDirectAuthoriseBankTransferRequest() throws WorldpayConfigurationException {
        when(shippingAddressMock.getCountryCode()).thenReturn(COUNTRY_CODE);
        doReturn(directAuthoriseServiceRequestMock).when(testObj).createDirectAuthoriseRequest(merchantInfoMock, basicOrderInfoMock, paymentMock, shopperMock, shippingAddressMock, billingAddressMock, STATEMENT_NARRATIVE, DynamicInteractionType.ECOMMERCE);

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
        doReturn(directAuthoriseServiceRequestMock).when(testObj).createKlarnaDirectAuthoriseRequest(merchantInfoMock, basicOrderInfoMock, paymentMock, shopperMock, shippingAddressMock, billingAddressMock, STATEMENT_NARRATIVE, orderLinesMock, DynamicInteractionType.ECOMMERCE);

        final DirectAuthoriseServiceRequest result = testObj.buildDirectAuthoriseKlarnaRequest(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, additionalAuthInfoMock);

        assertEquals(directAuthoriseServiceRequestMock, result);
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
    public void shouldCreateAuthoriseRecurringPaymentRequest() {
        when(worldpayDynamicInteractionResolverService.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoDataMock)).thenReturn(DynamicInteractionType.CONT_AUTH);

        testObj.buildDirectAuthoriseRecurringPayment(merchantInfoMock, abstractOrderModelMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createTokenisedDirectAuthoriseRequest(merchantInfoMock, basicOrderInfoMock, tokenMock, authenticatedShopperMock, shippingAddressMock, DynamicInteractionType.CONT_AUTH);
    }
}

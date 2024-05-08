package com.worldpay.service.payment.impl;

import com.worldpay.core.services.strategies.RecurringGenerateMerchantTransactionCodeStrategy;
import com.worldpay.data.*;
import com.worldpay.data.applepay.ApplePay;
import com.worldpay.data.klarna.KlarnaPayment;
import com.worldpay.data.payment.AchDirectDebitPayment;
import com.worldpay.data.payment.AlternativePayment;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.ACHDIRECTDEBITSSL;
import com.worldpay.internal.model.Header;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.payment.WorldpayKlarnaService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static com.worldpay.enums.AchDirectDebitAccountType.CHECKING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayOrderServiceTest {

    private static final double AMOUNT = 19.3;
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String ORDER_DESCRIPTION = "orderDescription";
    private static final String GBP = "GBP";
    private static final String COUNTRY_CODE = "GB";
    private static final String EXTRA_DATA = "extraData";
    private static final String EPHEMERAL_PUBLIC_KEY = "ephemeralPublicKey";
    private static final String PUBLIC_KEY_HASH = "publicKeyHash";
    private static final String TRANSACTION_ID = "transactionId";
    private static final String DATA = "data";
    private static final String SIGNATURE = "signature";
    private static final String VERSION = "version";
    private static final String WEB = "WEB";

    private static final String KLARNA_V2_SSL = "KLARNA_V2-SSL";
    private static final String KLARNA_PAYNOW_SSL = "KLARNA_PAYNOW-SSL";
    private static final String KLARNA_SLICE_IT_SSL = "KLARNA_SLICEIT-SSL";
    private static final String KLARNA_PAYLATER_SSL = "KLARNA_PAYLATER-SSL";
    private static final String WRONG_KLARNA_PAYMMENT_METHOD = "WRONG-KLARNA_V2-SSL";
    private static final String SUCCESS_URL = "SUCCESS_URL";
    private static final String PENDING_URL = "PENDING_URL";
    private static final String FAILURE_URL = "FAILURE_URL";
    private static final String CANCEL_URL = "CANCEL_URL";
    private static final String PAYMENT_PROVIDER = "paymentProvider";
    private static final String LOCALE = "en-GB";
    private static final String PAYPAL_SSL = "PAYPAL-SSL";
    private static final String CHECK_NUMBER = "checkNumber";
    private static final String ACCOUNT_NUMBER = "accountNumber";
    private static final String ROUTING_NUMBER = "routingNumber";
    private static final String COMPANY_NAME = "test";
    private static final String CUSTOM_IDENTIFIER = "CustomIdentifier";

    @InjectMocks
    private DefaultWorldpayOrderService testObj;

    @Mock
    private CommonI18NService commonI18NServiceMock;
    @Mock
    private WorldpayUrlService worldpayUrlServiceMock;
    @Mock
    private WorldpayKlarnaService worldpayKlarnaServiceMock;
    @Mock
    private CommerceCheckoutService commerceCheckoutServiceMock;
    @Mock
    private BaseSiteService baseSiteServiceMock;
    @Mock
    private RecurringGenerateMerchantTransactionCodeStrategy recurringGenerateMerchantTransactionCodeStrategyMock;
    @Mock
    Converter<com.worldpay.data.applepay.Header, Header> internalHeaderConverter;

    @Mock
    private CurrencyModel currencyModelMock;
    @Mock
    private Amount amountMock;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AbstractOrderModel abstractOrderModelMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private CartModel cartMock;
    @Mock
    private LanguageModel languageMock;
    @Mock
    private BaseSiteModel currentBaseSiteMock;
    @Mock
    private Header internalHeaderMock;

    @Before
    public void setUp() throws Exception {
        when(worldpayKlarnaServiceMock.isKlarnaPaymentType(KLARNA_V2_SSL)).thenReturn(true);
        when(worldpayKlarnaServiceMock.isKlarnaPaymentType(KLARNA_SLICE_IT_SSL)).thenReturn(true);
        when(worldpayKlarnaServiceMock.isKlarnaPaymentType(KLARNA_PAYNOW_SSL)).thenReturn(true);
        when(worldpayKlarnaServiceMock.isKlarnaPaymentType(KLARNA_PAYLATER_SSL)).thenReturn(true);
        when(commonI18NServiceMock.getLocaleForLanguage(languageMock)).thenReturn(Locale.ENGLISH);
    }

    @Test
    public void shouldFormatValue() {
        final Currency currency = Currency.getInstance(GBP);
        when(commonI18NServiceMock.convertAndRoundCurrency(1, Math.pow(10, currency.getDefaultFractionDigits()), 0, AMOUNT)).thenReturn(1930d);

        final Amount result = testObj.createAmount(currency, AMOUNT);

        assertThat(result.getCurrencyCode()).isEqualToIgnoringCase("GBP");
        assertThat(result.getExponent()).isEqualToIgnoringCase("2");
        assertThat(result.getValue()).isEqualToIgnoringCase("1930");
    }

    @Test
    public void shouldFormatTotal() {
        when(currencyModelMock.getIsocode()).thenReturn(GBP);
        when(commonI18NServiceMock.convertAndRoundCurrency(1, Math.pow(10, 2), 0, AMOUNT)).thenReturn(1930d);

        final Amount result = testObj.createAmount(currencyModelMock, AMOUNT);

        assertThat(result.getCurrencyCode()).isEqualToIgnoringCase("GBP");
        assertThat(result.getExponent()).isEqualToIgnoringCase("2");
        assertThat(result.getValue()).isEqualToIgnoringCase("1930");
    }

    @Test
    public void shouldReturnAmountAsBigDecimalUsingFractionDigitsFromCurrency() {
        when(amountMock.getCurrencyCode()).thenReturn("GBP");
        when(amountMock.getValue()).thenReturn("1935");

        final BigDecimal result = testObj.convertAmount(amountMock);

        assertEquals(result, BigDecimal.valueOf(19.35d));
    }

    @Test
    public void shouldCreateBasicOrderInfo() {
        final BasicOrderInfo result = testObj.createBasicOrderInfo(WORLDPAY_ORDER_CODE, ORDER_DESCRIPTION, amountMock);

        assertEquals(amountMock, result.getAmount());
        assertEquals(ORDER_DESCRIPTION, result.getDescription());
        assertEquals(WORLDPAY_ORDER_CODE, result.getOrderCode());
        assertEquals(WEB, result.getOrderChannel());
    }

    @Test
    public void shouldFormatYENNoDigits() {
        final Currency currency = Currency.getInstance("JPY");

        testObj.createAmount(currency, 8500);

        verify(commonI18NServiceMock).convertAndRoundCurrency(Math.pow(10, 0), 1, 0, 8500d);
    }

    @Test
    public void shouldFormatGBPTwoDigits() {
        final Currency currency = Currency.getInstance("GBP");

        testObj.createAmount(currency, 8500);

        verify(commonI18NServiceMock).convertAndRoundCurrency(Math.pow(10, 2), 1, 2, 8500d);
    }

    @Test
    public void createKlarnaPayment_shouldCreateKlarnaPayment_WhenKlarnaPayNowPaymentIsReceived() throws WorldpayConfigurationException {
        when(worldpayUrlServiceMock.getFullSuccessURL()).thenReturn(SUCCESS_URL);
        when(worldpayUrlServiceMock.getFullCancelURL()).thenReturn(CANCEL_URL);
        when(worldpayUrlServiceMock.getFullFailureURL()).thenReturn(FAILURE_URL);
        when(worldpayUrlServiceMock.getFullPendingURL()).thenReturn(PENDING_URL);

        final KlarnaPayment result = (KlarnaPayment) testObj.createKlarnaPayment(COUNTRY_CODE, languageMock, EXTRA_DATA, KLARNA_PAYNOW_SSL);

        assertEquals(PaymentType.KLARNAPAYNOWSSL.getMethodCode(), result.getPaymentType());
        assertEquals(EXTRA_DATA, result.getExtraMerchantData());
        assertEquals(SUCCESS_URL, result.getSuccessURL());
        assertEquals(PENDING_URL, result.getPendingURL());
        assertEquals(FAILURE_URL, result.getFailureURL());
    }

    @Test
    public void createKlarnaPayment_shouldCreateKlarnaPayment_WhenKlarnaSliceITPaymentIsReceived() throws WorldpayConfigurationException {
        when(worldpayUrlServiceMock.getFullSuccessURL()).thenReturn(SUCCESS_URL);
        when(worldpayUrlServiceMock.getFullCancelURL()).thenReturn(CANCEL_URL);
        when(worldpayUrlServiceMock.getFullFailureURL()).thenReturn(FAILURE_URL);
        when(worldpayUrlServiceMock.getFullPendingURL()).thenReturn(PENDING_URL);

        final KlarnaPayment result = (KlarnaPayment) testObj.createKlarnaPayment(COUNTRY_CODE, languageMock, EXTRA_DATA, KLARNA_SLICE_IT_SSL);

        assertEquals(PaymentType.KLARNASLICESSL.getMethodCode(), result.getPaymentType());
        assertEquals(EXTRA_DATA, result.getExtraMerchantData());
        assertEquals(SUCCESS_URL, result.getSuccessURL());
        assertEquals(PENDING_URL, result.getPendingURL());
        assertEquals(FAILURE_URL, result.getFailureURL());
    }

    @Test
    public void createKlarnaPayment_shouldCreateKlarnaPayment_WhenKlarnaPayLaterPaymentIsReceived() throws WorldpayConfigurationException {
        when(worldpayUrlServiceMock.getFullSuccessURL()).thenReturn(SUCCESS_URL);
        when(worldpayUrlServiceMock.getFullCancelURL()).thenReturn(CANCEL_URL);
        when(worldpayUrlServiceMock.getFullFailureURL()).thenReturn(FAILURE_URL);
        when(worldpayUrlServiceMock.getFullPendingURL()).thenReturn(PENDING_URL);

        final KlarnaPayment result = (KlarnaPayment) testObj.createKlarnaPayment(COUNTRY_CODE, languageMock, EXTRA_DATA, KLARNA_PAYLATER_SSL);

        assertEquals(PaymentType.KLARNAPAYLATERSSL.getMethodCode(), result.getPaymentType());
        assertEquals(EXTRA_DATA, result.getExtraMerchantData());
        assertEquals(SUCCESS_URL, result.getSuccessURL());
        assertEquals(PENDING_URL, result.getPendingURL());
        assertEquals(FAILURE_URL, result.getFailureURL());
    }

    @Test
    public void createKlarnaPayment_shouldCreateKlarnaPayment_WhenKlarnaV2SSLPaymentIsReceived() throws WorldpayConfigurationException {
        when(worldpayUrlServiceMock.getFullSuccessURL()).thenReturn(SUCCESS_URL);
        when(worldpayUrlServiceMock.getFullCancelURL()).thenReturn(CANCEL_URL);
        when(worldpayUrlServiceMock.getFullFailureURL()).thenReturn(FAILURE_URL);
        when(worldpayUrlServiceMock.getFullPendingURL()).thenReturn(PENDING_URL);

        final KlarnaPayment result = (KlarnaPayment) testObj.createKlarnaPayment(COUNTRY_CODE, languageMock, EXTRA_DATA, KLARNA_V2_SSL);

        assertThat(result.getPaymentType()).isEqualTo(PaymentType.KLARNAV2SSL.getMethodCode());
        assertThat(result.getShopperCountryCode()).isEqualTo(COUNTRY_CODE);
        assertThat(result.getLocale()).isEqualTo(LOCALE);
        assertThat(result.getSuccessURL()).isEqualTo(SUCCESS_URL);
        assertThat(result.getFailureURL()).isEqualTo(FAILURE_URL);
        assertThat(result.getCancelURL()).isEqualTo(CANCEL_URL);
        assertThat(result.getPendingURL()).isEqualTo(PENDING_URL);
    }

    @Test
    public void createKlarnaPayment_shouldCreateKlarnaPaymentWithBaseSiteDefaultLanguage_WhenLanguageIsNull() throws WorldpayConfigurationException {

        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(currentBaseSiteMock);
        when(baseSiteServiceMock.getCurrentBaseSite().getDefaultLanguage()).thenReturn(languageMock);

        when(worldpayUrlServiceMock.getFullSuccessURL()).thenReturn(SUCCESS_URL);
        when(worldpayUrlServiceMock.getFullCancelURL()).thenReturn(CANCEL_URL);
        when(worldpayUrlServiceMock.getFullFailureURL()).thenReturn(FAILURE_URL);
        when(worldpayUrlServiceMock.getFullPendingURL()).thenReturn(PENDING_URL);

        final KlarnaPayment result = (KlarnaPayment) testObj.createKlarnaPayment(COUNTRY_CODE, null, EXTRA_DATA, KLARNA_V2_SSL);

        verify(commonI18NServiceMock)
            .getLocaleForLanguage(baseSiteServiceMock.getCurrentBaseSite().getDefaultLanguage());

        assertThat(result.getPaymentType()).isEqualTo(PaymentType.KLARNAV2SSL.getMethodCode());
        assertThat(result.getShopperCountryCode()).isEqualTo(COUNTRY_CODE);
        assertThat(result.getLocale()).isEqualTo(LOCALE);
        assertThat(result.getSuccessURL()).isEqualTo(SUCCESS_URL);
        assertThat(result.getFailureURL()).isEqualTo(FAILURE_URL);
        assertThat(result.getCancelURL()).isEqualTo(CANCEL_URL);
        assertThat(result.getPendingURL()).isEqualTo(PENDING_URL);

    }

    @Test(expected = WorldpayConfigurationException.class)
    public void createKlarnaPayment_shouldThrowWorldpayConfigurationException_WhenIncorrectKlarnaPaymentMethodIsReceived() throws WorldpayConfigurationException {
        testObj.createKlarnaPayment(COUNTRY_CODE, languageMock, EXTRA_DATA, WRONG_KLARNA_PAYMMENT_METHOD);
    }

    @Test(expected = WorldpayConfigurationException.class)
    public void createKlarnaPayment_shouldThrowWorldpayConfigurationException_WhenNULLPaymentMethodIsReceived() throws WorldpayConfigurationException {
        testObj.createKlarnaPayment(COUNTRY_CODE, languageMock, EXTRA_DATA, null);
    }

    @Test
    public void shouldCreateApplePayPayment() throws WorldpayModelTransformationException {


        final ApplePayHeader applePayHeader = new ApplePayHeader();
        applePayHeader.setEphemeralPublicKey(EPHEMERAL_PUBLIC_KEY);
        applePayHeader.setPublicKeyHash(PUBLIC_KEY_HASH);
        applePayHeader.setTransactionId(TRANSACTION_ID);

        final ApplePayAdditionalAuthInfo additionalInfoApplePayData = new ApplePayAdditionalAuthInfo();
        additionalInfoApplePayData.setHeader(applePayHeader);
        additionalInfoApplePayData.setData(DATA);
        additionalInfoApplePayData.setSignature(SIGNATURE);
        additionalInfoApplePayData.setVersion(VERSION);

        final ApplePay result = (ApplePay) testObj.createApplePayPayment(additionalInfoApplePayData);

        assertThat(result.getData()).isEqualTo(DATA);
        assertThat(result.getSignature()).isEqualTo(SIGNATURE);
        assertThat(result.getVersion()).isEqualTo(VERSION);

        when(internalHeaderConverter.convert(result.getHeader())).thenReturn(internalHeaderMock);
        when(internalHeaderMock.getEphemeralPublicKey()).thenReturn(EPHEMERAL_PUBLIC_KEY);
        when(internalHeaderMock.getPublicKeyHash()).thenReturn(PUBLIC_KEY_HASH);
        when(internalHeaderMock.getTransactionId()).thenReturn(TRANSACTION_ID);

        final Header header = internalHeaderConverter.convert(result.getHeader());
        assertThat(header.getEphemeralPublicKey()).isEqualTo(EPHEMERAL_PUBLIC_KEY);
        assertThat(header.getPublicKeyHash()).isEqualTo(PUBLIC_KEY_HASH);
        assertThat(header.getTransactionId()).isEqualTo(TRANSACTION_ID);

    }

    @Test
    public void createCommerceCheckoutParameter_WhenItIsCalled_shouldCreateCommerceCheckoutParameter() {
        when(commerceCheckoutServiceMock.getPaymentProvider()).thenReturn(PAYMENT_PROVIDER);

        final CommerceCheckoutParameter result = testObj.createCommerceCheckoutParameter(abstractOrderModelMock, paymentInfoModelMock, BigDecimal.ONE);

        assertTrue(result.isEnableHooks());
        assertEquals(BigDecimal.ONE, result.getAuthorizationAmount());
        assertEquals(PAYMENT_PROVIDER, result.getPaymentProvider());
        assertEquals(paymentInfoModelMock, result.getPaymentInfo());
        assertEquals(abstractOrderModelMock, result.getOrder());
    }

    @Test
    public void createCommerceCheckoutParameter_WhenInstanceofCartModel_shouldCreateCommerceCheckoutParameterWithCartParam() {
        when(commerceCheckoutServiceMock.getPaymentProvider()).thenReturn(PAYMENT_PROVIDER);

        final CommerceCheckoutParameter result = testObj.createCommerceCheckoutParameter(cartMock, paymentInfoModelMock, BigDecimal.ONE);

        assertTrue(result.isEnableHooks());
        assertEquals(BigDecimal.ONE, result.getAuthorizationAmount());
        assertEquals(PAYMENT_PROVIDER, result.getPaymentProvider());
        assertEquals(paymentInfoModelMock, result.getPaymentInfo());
        assertEquals(cartMock, result.getCart());
    }

    @Test
    public void generateWorldpayOrderCode_ShouldGenerateCode() {
        testObj.generateWorldpayOrderCode(abstractOrderModelMock);

        verify(recurringGenerateMerchantTransactionCodeStrategyMock).generateCode(abstractOrderModelMock);
    }

    @Test
    public void createAlternativePayment_shouldCreatePayPalSSLPayment_WhenPayPalSSLIsReceived() throws WorldpayConfigurationException {
        when(worldpayUrlServiceMock.getFullSuccessURL()).thenReturn(SUCCESS_URL);
        when(worldpayUrlServiceMock.getFullCancelURL()).thenReturn(CANCEL_URL);
        when(worldpayUrlServiceMock.getFullFailureURL()).thenReturn(FAILURE_URL);
        when(worldpayUrlServiceMock.getFullPendingURL()).thenReturn(PENDING_URL);

        final AlternativePayment result = (AlternativePayment) testObj.createAlternativePayment(COUNTRY_CODE, PAYPAL_SSL);

        assertEquals(PaymentType.PAYPAL_SSL.getMethodCode(), result.getPaymentType());
        assertEquals(SUCCESS_URL, result.getSuccessURL());
        assertEquals(PENDING_URL, result.getPendingURL());
        assertEquals(FAILURE_URL, result.getFailureURL());
        assertEquals(CANCEL_URL, result.getCancelURL());
        assertEquals(COUNTRY_CODE, result.getShopperCountryCode());
    }

    @Test
    public void createACHDirectDebitPayment_shouldCreateACHDirectDebitPayment_WhenPayPalSSLIsReceived() throws WorldpayConfigurationException {
        final ACHDirectDebitAdditionalAuthInfo additionalInfo = new ACHDirectDebitAdditionalAuthInfo();
        additionalInfo.setRoutingNumber(ROUTING_NUMBER);
        additionalInfo.setCheckNumber(CHECK_NUMBER);
        additionalInfo.setAccountNumber(ACCOUNT_NUMBER);
        additionalInfo.setAccountType(CHECKING);
        additionalInfo.setCompanyName(COMPANY_NAME);
        additionalInfo.setCustomIdentifier(CUSTOM_IDENTIFIER);
        additionalInfo.setPaymentMethod(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());

        final AchDirectDebitPayment result = (AchDirectDebitPayment) testObj.createACHDirectDebitPayment(new Address(), additionalInfo);

        assertEquals(PaymentType.ACHDIRECTDEBITSSL.getMethodCode(), result.getPaymentType());
        assertEquals(ACCOUNT_NUMBER, result.getAccountNumber());
        assertEquals(CHECKING, result.getAccountType());
        assertEquals(CHECK_NUMBER, result.getCheckNumber());
        assertEquals(COMPANY_NAME, result.getCompanyName());
        assertEquals(ROUTING_NUMBER, result.getRoutingNumber());
        assertEquals(CUSTOM_IDENTIFIER, result.getCustomIdentifier());
        assertNotNull(result.getAddress());
    }
}

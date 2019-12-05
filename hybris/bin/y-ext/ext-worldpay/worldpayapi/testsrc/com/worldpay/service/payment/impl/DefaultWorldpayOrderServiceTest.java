package com.worldpay.service.payment.impl;

import com.worldpay.data.Additional3DS2Info;
import com.worldpay.data.ApplePayAdditionalAuthInfo;
import com.worldpay.data.ApplePayHeader;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Header;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.model.*;
import com.worldpay.service.model.applepay.ApplePay;
import com.worldpay.service.model.klarna.KlarnaPayment;
import com.worldpay.service.model.payment.AlternativeShopperBankCodePayment;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.threeds2.Additional3DSData;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import com.worldpay.threedsecureflexenums.ChallengePreferenceEnum;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayOrderServiceTest {

    private static final double AMOUNT = 19.3;
    private static final String BANK_CODE = "bankCode";
    private static final String FULL_SUCCESS_URL = "fullSuccessURL";
    private static final String KLARNA_CHECKOUT_URL = "klarnaCheckoutURL";
    private static final String KLARNA_CONFIRMATION_URL = "klarnaConfirmationURL";
    private static final String SESSION_ID = "sessionId";
    private static final String IP_ADDRESS = "ipAddress";
    private static final String DEVICE_TYPE = "deviceType";
    private static final String ACCEPT_HEADER = "acceptHeader";
    private static final String USER_AGENT_HEADER = "userAgentHeader";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String ORDER_DESCRIPTION = "orderDescription";
    private static final String GBP = "GBP";
    private static final String CUSTOMER_EMAIL = "customerEmail";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperID";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String LANGUAGE_CODE = "languageCode";
    private static final String EXTRA_DATA = "extraData";
    private static final String WORLDPAY_MERCHANT_TOKEN_ENABLED = "worldpay.merchant.token.enabled";
    private static final String EPHEMERAL_PUBLIC_KEY = "ephemeralPublicKey";
    private static final String PUBLIC_KEY_HASH = "publicKeyHash";
    private static final String TRANSACTION_ID = "transactionId";
    private static final String DATA = "data";
    private static final String SIGNATURE = "signature";
    private static final String VERSION = "version";
    private static final String REFERENCE_ID = "referenceId";
    private static final String WINDOW_SIZE = "390x400";
    private static final String NO_PREFERENCE = "noPreference";

    @InjectMocks
    private DefaultWorldpayOrderService testObj;

    @Mock
    private CommonI18NService commonI18NServiceMock;
    @Mock
    private WorldpayUrlService worldpayUrlServiceMock;
    @Mock
    private SiteConfigService siteConfigServiceMock;

    @Mock
    private CurrencyModel currencyModelMock;
    @Mock
    private Session sessionMock;
    @Mock
    private Browser browserMock;
    @Mock
    private Amount amountMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private TokenRequest tokenRequestMock;
    @Mock
    private CardDetails cardDetailsMock;
    @Mock
    private Additional3DS2Info additional3DS2InfoMock;

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
    public void shouldCreateSession() {
        when(worldpayAdditionalInfoDataMock.getSessionId()).thenReturn(SESSION_ID);
        when(worldpayAdditionalInfoDataMock.getCustomerIPAddress()).thenReturn(IP_ADDRESS);

        final Session result = testObj.createSession(worldpayAdditionalInfoDataMock);

        assertEquals(SESSION_ID, result.getId());
        assertEquals(IP_ADDRESS, result.getShopperIPAddress());
    }

    @Test
    public void shouldCreateBrowser() {
        when(worldpayAdditionalInfoDataMock.getDeviceType()).thenReturn(DEVICE_TYPE);
        when(worldpayAdditionalInfoDataMock.getAcceptHeader()).thenReturn(ACCEPT_HEADER);
        when(worldpayAdditionalInfoDataMock.getUserAgentHeader()).thenReturn(USER_AGENT_HEADER);

        final Browser result = testObj.createBrowser(worldpayAdditionalInfoDataMock);

        assertEquals(DEVICE_TYPE, result.getDeviceType());
        assertEquals(ACCEPT_HEADER, result.getAcceptHeader());
        assertEquals(USER_AGENT_HEADER, result.getUserAgentHeader());
    }

    @Test
    public void shouldCreateBasicOrderInfo() {
        final BasicOrderInfo result = testObj.createBasicOrderInfo(WORLDPAY_ORDER_CODE, ORDER_DESCRIPTION, amountMock);

        assertEquals(amountMock, result.getAmount());
        assertEquals(ORDER_DESCRIPTION, result.getDescription());
        assertEquals(WORLDPAY_ORDER_CODE, result.getOrderCode());
    }

    @Test
    public void shouldCreateShopperWithoutAuthenticatedShopperID() {
        final Shopper result = testObj.createShopper(CUSTOMER_EMAIL, sessionMock, browserMock);
        assertEquals(CUSTOMER_EMAIL, result.getShopperEmailAddress());
        assertEquals(sessionMock, result.getSession());
        assertEquals(browserMock, result.getBrowser());
        assertNull(result.getAuthenticatedShopperID());
    }

    @Test
    public void shouldCreateShopperWithAuthenticatedShopperIDWhenMerchantTokenIsDisabled() {
        when(siteConfigServiceMock.getBoolean(WORLDPAY_MERCHANT_TOKEN_ENABLED, false)).thenReturn(false);

        final Shopper result = testObj.createAuthenticatedShopper(CUSTOMER_EMAIL, AUTHENTICATED_SHOPPER_ID, sessionMock, browserMock);

        assertEquals(CUSTOMER_EMAIL, result.getShopperEmailAddress());
        assertEquals(sessionMock, result.getSession());
        assertEquals(browserMock, result.getBrowser());
        assertEquals(AUTHENTICATED_SHOPPER_ID, result.getAuthenticatedShopperID());
    }

    @Test
    public void shouldCreateShopperWithAuthenticatedShopperIDWhenMerchantTokenIsEnabled() {
        when(siteConfigServiceMock.getBoolean(WORLDPAY_MERCHANT_TOKEN_ENABLED, false)).thenReturn(true);

        final Shopper result = testObj.createAuthenticatedShopper(CUSTOMER_EMAIL, AUTHENTICATED_SHOPPER_ID, sessionMock, browserMock);

        assertEquals(CUSTOMER_EMAIL, result.getShopperEmailAddress());
        assertEquals(sessionMock, result.getSession());
        assertEquals(browserMock, result.getBrowser());
        assertNull(result.getAuthenticatedShopperID());
    }

    @Test
    public void shouldCreateTokenRequestWithMerchantScope() {
        when(siteConfigServiceMock.getBoolean(WORLDPAY_MERCHANT_TOKEN_ENABLED, false)).thenReturn(true);

        final TokenRequest result = testObj.createTokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_REASON);

        assertEquals(TOKEN_EVENT_REFERENCE, result.getTokenEventReference());
        assertEquals(TOKEN_REASON, result.getTokenReason());
        assertTrue(result.isMerchantToken());
    }

    @Test
    public void shouldCreateTokenRequestWithoutTokenReasonWithMerchantScope() {
        when(siteConfigServiceMock.getBoolean(WORLDPAY_MERCHANT_TOKEN_ENABLED, false)).thenReturn(true);

        final TokenRequest result = testObj.createTokenRequest(TOKEN_EVENT_REFERENCE, null);

        assertEquals(TOKEN_EVENT_REFERENCE, result.getTokenEventReference());
        assertNull(result.getTokenReason());
        assertTrue(result.isMerchantToken());
    }

    @Test
    public void shouldCreateTokenRequestWithShopperScope() {
        when(siteConfigServiceMock.getBoolean(WORLDPAY_MERCHANT_TOKEN_ENABLED, false)).thenReturn(false);

        final TokenRequest result = testObj.createTokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_REASON);

        assertEquals(TOKEN_EVENT_REFERENCE, result.getTokenEventReference());
        assertEquals(TOKEN_REASON, result.getTokenReason());
        assertThat(result.isMerchantToken()).isFalse();
    }

    @Test
    public void shouldCreateTokenRequestWithoutTokenReasonWithShopperScope() {
        when(siteConfigServiceMock.getBoolean(WORLDPAY_MERCHANT_TOKEN_ENABLED, false)).thenReturn(false);

        final TokenRequest result = testObj.createTokenRequest(TOKEN_EVENT_REFERENCE, null);

        assertEquals(TOKEN_EVENT_REFERENCE, result.getTokenEventReference());
        assertNull(result.getTokenReason());
        assertThat(result.isMerchantToken()).isFalse();
    }

    @Test
    public void shouldCreatePaymentForIdealSSL() throws WorldpayConfigurationException {
        when(worldpayUrlServiceMock.getFullSuccessURL()).thenReturn(FULL_SUCCESS_URL);

        final AlternativeShopperBankCodePayment result = (AlternativeShopperBankCodePayment) testObj.createBankPayment("IDEAL-SSL", BANK_CODE);

        assertEquals(BANK_CODE, result.getShopperBankCode());
        assertEquals(PaymentType.IDEAL, result.getPaymentType());
        assertEquals(FULL_SUCCESS_URL, result.getSuccessURL());
    }

    @Test
    public void shouldReturnNullWhenPaymentTypeIsNotFound() throws WorldpayConfigurationException {
        when(worldpayUrlServiceMock.getFullSuccessURL()).thenReturn(FULL_SUCCESS_URL);

        final AlternativeShopperBankCodePayment result = (AlternativeShopperBankCodePayment) testObj.createBankPayment("notfound", BANK_CODE);

        assertThat(result).isNull();
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
    public void shouldCreateKlarnaPayment() throws WorldpayConfigurationException {
        when(worldpayUrlServiceMock.getBaseWebsiteUrlForSite()).thenReturn(KLARNA_CHECKOUT_URL);
        when(worldpayUrlServiceMock.getKlarnaConfirmationURL()).thenReturn(KLARNA_CONFIRMATION_URL);

        final KlarnaPayment result = (KlarnaPayment) testObj.createKlarnaPayment(COUNTRY_CODE, LANGUAGE_CODE, EXTRA_DATA);

        assertEquals(PaymentType.KLARNASSL, result.getPaymentType());
        assertEquals(COUNTRY_CODE, result.getPurchaseCountry());
        assertEquals(LANGUAGE_CODE, result.getShopperLocale());
        assertEquals(KLARNA_CHECKOUT_URL, result.getMerchantUrls().getCheckoutURL());
        assertEquals(KLARNA_CONFIRMATION_URL, result.getMerchantUrls().getConfirmationURL());
        assertEquals(EXTRA_DATA, result.getExtraMerchantData());
    }

    @Test
    public void shouldCreateUpdateTokenServiceRequestWithShopperScope() {
        when(siteConfigServiceMock.getBoolean(WORLDPAY_MERCHANT_TOKEN_ENABLED, false)).thenReturn(false);

        final UpdateTokenServiceRequest result = testObj.createUpdateTokenServiceRequest(merchantInfoMock, worldpayAdditionalInfoDataMock, tokenRequestMock, "paymentTokenId", cardDetailsMock);

        assertThat(result.getUpdateTokenRequest().isMerchantToken()).isFalse();
    }

    @Test
    public void shouldCreateUpdateTokenServiceRequestWithMerchantScope() {
        when(siteConfigServiceMock.getBoolean(WORLDPAY_MERCHANT_TOKEN_ENABLED, false)).thenReturn(true);

        final UpdateTokenServiceRequest result = testObj.createUpdateTokenServiceRequest(merchantInfoMock, worldpayAdditionalInfoDataMock, tokenRequestMock, "paymentTokenId", cardDetailsMock);

        assertThat(result.getUpdateTokenRequest().isMerchantToken()).isTrue();
    }

    @Test
    public void shouldCreateTokenForDeletionWithMerchantScope() {
        final TokenRequest result = testObj.createTokenRequestForDeletion(TOKEN_EVENT_REFERENCE, TOKEN_REASON, null);
        assertThat(result.isMerchantToken()).isTrue();
    }

    @Test
    public void shouldCreateTokenForDeletionWithShopperScope() {
        final TokenRequest result = testObj.createTokenRequestForDeletion(TOKEN_EVENT_REFERENCE, TOKEN_REASON, AUTHENTICATED_SHOPPER_ID);
        assertThat(result.isMerchantToken()).isFalse();
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
        final Header header = (Header) result.getHeader().transformToInternalModel();
        assertThat(header.getEphemeralPublicKey()).isEqualTo(EPHEMERAL_PUBLIC_KEY);
        assertThat(header.getPublicKeyHash()).isEqualTo(PUBLIC_KEY_HASH);
        assertThat(header.getTransactionId()).isEqualTo(TRANSACTION_ID);

    }

    @Test
    public void shouldCreateAdditional3DSData() {
        when(worldpayAdditionalInfoDataMock.getAdditional3DS2()).thenReturn(additional3DS2InfoMock);
        when(additional3DS2InfoMock.getDfReferenceId()).thenReturn(REFERENCE_ID);
        when(additional3DS2InfoMock.getChallengeWindowSize()).thenReturn(WINDOW_SIZE);
        when(additional3DS2InfoMock.getChallengePreference()).thenReturn(NO_PREFERENCE);

        final Additional3DSData result = testObj.createAdditional3DSData(worldpayAdditionalInfoDataMock);

        assertThat(result.getDfReferenceId()).isEqualTo(REFERENCE_ID);
        assertThat(result.getChallengeWindowSize()).isEqualTo(ChallengeWindowSizeEnum.getEnum(WINDOW_SIZE));
        assertThat(result.getChallengePreference()).isEqualTo(ChallengePreferenceEnum.getEnum(NO_PREFERENCE));
    }

    @Test
    public void shouldCreateAdditional3DSDataEvenIfWithChallengePreferenceAsEmptyBeingNoPreferenceAsDefault() {
        when(worldpayAdditionalInfoDataMock.getAdditional3DS2()).thenReturn(additional3DS2InfoMock);
        when(additional3DS2InfoMock.getDfReferenceId()).thenReturn(REFERENCE_ID);
        when(additional3DS2InfoMock.getChallengeWindowSize()).thenReturn(null);
        when(additional3DS2InfoMock.getChallengePreference()).thenReturn(null);

        final Additional3DSData result = testObj.createAdditional3DSData(worldpayAdditionalInfoDataMock);

        assertThat(result.getDfReferenceId()).isEqualTo(REFERENCE_ID);
        assertThat(result.getChallengeWindowSize()).isEqualTo(ChallengeWindowSizeEnum.getEnum(WINDOW_SIZE));
        assertThat(result.getChallengePreference()).isNull();
    }
}

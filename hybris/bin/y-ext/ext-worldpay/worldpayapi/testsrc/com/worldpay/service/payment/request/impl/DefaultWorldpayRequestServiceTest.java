package com.worldpay.service.payment.request.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.data.*;
import com.worldpay.data.payment.AlternativeShopperBankCodePayment;
import com.worldpay.data.payment.Payment;
import com.worldpay.data.payment.StoredCredentials;
import com.worldpay.data.threeds2.Additional3DSData;
import com.worldpay.data.token.CardDetails;
import com.worldpay.data.token.CardTokenRequest;
import com.worldpay.data.token.Token;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.enums.payment.storedCredentials.MerchantInitiatedReason;
import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.hop.WorldpayOrderCodeVerificationService;
import com.worldpay.service.interaction.WorldpayDynamicInteractionResolverService;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import com.worldpay.threedsecureflexenums.ChallengePreferenceEnum;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import org.apache.commons.beanutils.ConversionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.GeneralSecurityException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayRequestServiceTest {

    private static final String ENCRYPTED_ORDER_CODE = "/wLfcHcPCWk6BOmSxLh/fPlCjxTSrdDxd1dE205/D+1AyLJ9AWs=";
    private static final String ENCODED_ORDER_CODE = "%2FwLfcHcPCWk6BOmSxLh%2FfPlCjxTSrdDxd1dE205%2FD%2B1AyLJ9AWs%3D";
    private static final String REFERENCE_ID = "referenceId";
    private static final String WINDOW_SIZE = "390x400";
    private static final String NO_PREFERENCE = "noPreference";
    private static final String TRANSACTION_IDENTIFIER = "transactionIdentifier";
    private static final String WORLDPAY_MERCHANT_TOKEN_ENABLED = "worldpay.merchant.token.enabled";
    private static final String CUSTOMER_EMAIL = "customerEmail";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperID";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String SESSION_ID = "sessionId";
    private static final String IP_ADDRESS = "ipAddress";
    private static final String DEVICE_TYPE = "deviceType";
    private static final String ACCEPT_HEADER = "acceptHeader";
    private static final String USER_AGENT_HEADER = "userAgentHeader";
    private static final String BANK_CODE = "bankCode";
    private static final String FULL_SUCCESS_URL = "fullSuccessURL";
    private static final String WORLDPAY_ORDER_CODE = "10202255";
    private static final String EXPIRY_YEAR = "2021";
    private static final String EXPIRY_MONTH = "2";
    private static final String CARD_HOLDER_NAME = "Mr J S";
    private static final String TIME_ZONE = "timeZone";
    private static final String LANGUAGE = "language";
    private static final int SCREEN_HEIGHT = 1080;
    private static final int SCREEN_WIDTH = 1200;
    private static final int COLOR_DEPTH = 24;

    @InjectMocks
    private DefaultWorldpayRequestService testObj;

    @Mock
    private WorldpayUrlService worldpayUrlServiceMock;
    @Mock
    private SiteConfigService siteConfigServiceMock;
    @Mock
    private WorldpayDynamicInteractionResolverService worldpayDynamicInteractionResolverServiceMock;
    @Mock
    private WorldpayOrderCodeVerificationService worldpayOrderCodeVerificationServiceMock;
    @Mock
    private WorldpayCartService worldpayCartServiceMock;

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
    @Mock
    private CSEAdditionalAuthInfo cseAdditionalAuthInfoMock;
    @Mock
    private Session sessionMock;
    @Mock
    private Browser browserMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AbstractOrderModel abstractOrderModelMock;
    @Mock
    private CartModel cartMock;
    @Mock
    private AddressModel paymentAddressMock;
    @Mock
    private Address addressMock;
    @Mock
    private Payment paymentMock;

    @Before
    public void setUp() {
        when(siteConfigServiceMock.getBoolean(WORLDPAY_MERCHANT_TOKEN_ENABLED, false)).thenReturn(true);
        when(cseAdditionalAuthInfoMock.getExpiryMonth()).thenReturn(EXPIRY_MONTH);
        when(cseAdditionalAuthInfoMock.getExpiryYear()).thenReturn(EXPIRY_YEAR);
        when(cseAdditionalAuthInfoMock.getCardHolderName()).thenReturn(CARD_HOLDER_NAME);
        when(worldpayCartServiceMock.convertAddressModelToAddress(paymentAddressMock)).thenReturn(addressMock);
    }

    @Test
    public void shouldReturnNullWhenPaymentTypeIsNotFound() throws WorldpayConfigurationException {

        final AlternativeShopperBankCodePayment result = (AlternativeShopperBankCodePayment) testObj.createBankPayment(WORLDPAY_ORDER_CODE, "notfound", BANK_CODE);

        assertThat(result).isNull();
    }

    @Test
    public void createBankPayment_shouldCreatePaymentForIdealSSL() throws WorldpayConfigurationException, GeneralSecurityException {
        when(worldpayOrderCodeVerificationServiceMock.getEncryptedOrderCode(WORLDPAY_ORDER_CODE)).thenReturn(ENCRYPTED_ORDER_CODE);
        when(worldpayUrlServiceMock.getFullSuccessURL()).thenReturn(FULL_SUCCESS_URL);

        final AlternativeShopperBankCodePayment result = (AlternativeShopperBankCodePayment) testObj.createBankPayment(WORLDPAY_ORDER_CODE, "IDEAL-SSL", BANK_CODE);

        assertEquals(BANK_CODE, result.getShopperBankCode());
        assertEquals(PaymentType.IDEAL.getMethodCode(), result.getPaymentType());
        assertEquals(FULL_SUCCESS_URL + "?orderId=" + ENCODED_ORDER_CODE, result.getSuccessURL());
    }

    @Test(expected = ConversionException.class)
    public void createBankPayment_whenEncryptionFails_shouldThrowException() throws WorldpayConfigurationException, GeneralSecurityException {
        doThrow(new ConversionException("something failed")).when(worldpayOrderCodeVerificationServiceMock).getEncryptedOrderCode(WORLDPAY_ORDER_CODE);

        testObj.createBankPayment(WORLDPAY_ORDER_CODE, "IDEAL-SSL", BANK_CODE);
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
        when(worldpayAdditionalInfoDataMock.getJavascriptEnabled()).thenReturn(Boolean.TRUE);
        when(worldpayAdditionalInfoDataMock.getJavaEnabled()).thenReturn(Boolean.TRUE);
        when(worldpayAdditionalInfoDataMock.getScreenHeight()).thenReturn(SCREEN_HEIGHT);
        when(worldpayAdditionalInfoDataMock.getScreenWidth()).thenReturn(SCREEN_WIDTH);
        when(worldpayAdditionalInfoDataMock.getColorDepth()).thenReturn(COLOR_DEPTH);
        when(worldpayAdditionalInfoDataMock.getTimeZone()).thenReturn(TIME_ZONE);
        when(worldpayAdditionalInfoDataMock.getLanguage()).thenReturn(LANGUAGE);

        final Browser result = testObj.createBrowser(worldpayAdditionalInfoDataMock);

        assertEquals(DEVICE_TYPE, result.getDeviceType());
        assertEquals(ACCEPT_HEADER, result.getAcceptHeader());
        assertEquals(USER_AGENT_HEADER, result.getUserAgentHeader());
        assertTrue(result.getJavaEnabled());
        assertTrue(result.getJavascriptEnabled());
        assertEquals(TIME_ZONE, result.getTimeZone());
        assertEquals(SCREEN_HEIGHT, result.getScreenHeight().intValue());
        assertEquals(SCREEN_WIDTH, result.getScreenWidth().intValue());
        assertEquals(LANGUAGE, result.getLanguage());
        assertEquals(COLOR_DEPTH, result.getColorDepth().intValue());
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
        final Shopper result = testObj.createAuthenticatedShopper(CUSTOMER_EMAIL, AUTHENTICATED_SHOPPER_ID, sessionMock, browserMock);

        assertEquals(CUSTOMER_EMAIL, result.getShopperEmailAddress());
        assertEquals(sessionMock, result.getSession());
        assertEquals(browserMock, result.getBrowser());
        assertNull(result.getAuthenticatedShopperID());
    }

    @Test
    public void shouldCreateTokenRequestWithMerchantScope() {

        final TokenRequest result = testObj.createTokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_REASON);

        assertEquals(TOKEN_EVENT_REFERENCE, result.getTokenEventReference());
        assertEquals(TOKEN_REASON, result.getTokenReason());
        assertTrue(result.isMerchantToken());
    }

    @Test
    public void shouldCreateTokenRequestWithoutTokenReasonWithMerchantScope() {
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
    public void shouldCreateUpdateTokenServiceRequestWithShopperScope() {
        when(siteConfigServiceMock.getBoolean(WORLDPAY_MERCHANT_TOKEN_ENABLED, false)).thenReturn(false);

        final UpdateTokenServiceRequest result = testObj.createUpdateTokenServiceRequest(merchantInfoMock, worldpayAdditionalInfoDataMock, tokenRequestMock, "paymentTokenId", cardDetailsMock);

        assertThat(result.getUpdateTokenRequest().isMerchantToken()).isFalse();
    }

    @Test
    public void shouldCreateUpdateTokenServiceRequestWithMerchantScope() {
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
    public void shouldCreateAdditional3DSData() {
        when(worldpayAdditionalInfoDataMock.getAdditional3DS2()).thenReturn(additional3DS2InfoMock);
        when(additional3DS2InfoMock.getDfReferenceId()).thenReturn(REFERENCE_ID);
        when(additional3DS2InfoMock.getChallengeWindowSize()).thenReturn(WINDOW_SIZE);
        when(additional3DS2InfoMock.getChallengePreference()).thenReturn(NO_PREFERENCE);

        final Additional3DSData result = testObj.createAdditional3DSData(worldpayAdditionalInfoDataMock);

        assertThat(result.getDfReferenceId()).isEqualTo(REFERENCE_ID);
        assertThat(result.getChallengeWindowSize()).isEqualTo(ChallengeWindowSizeEnum.R_390_400.toString());
        assertThat(result.getChallengePreference()).isEqualTo(ChallengePreferenceEnum.NO_PREFERENCE.toString());
    }

    @Test
    public void shouldCreateAdditional3DSDataEvenIfWithChallengeWindowSizeAsEmptyBeing390x400AsDefault() {
        when(worldpayAdditionalInfoDataMock.getAdditional3DS2()).thenReturn(additional3DS2InfoMock);
        when(additional3DS2InfoMock.getDfReferenceId()).thenReturn(REFERENCE_ID);
        when(additional3DS2InfoMock.getChallengeWindowSize()).thenReturn(null);
        when(additional3DS2InfoMock.getChallengePreference()).thenReturn(null);

        final Additional3DSData result = testObj.createAdditional3DSData(worldpayAdditionalInfoDataMock);

        assertThat(result.getDfReferenceId()).isEqualTo(REFERENCE_ID);
        assertThat(result.getChallengeWindowSize()).isEqualTo(ChallengeWindowSizeEnum.R_390_400.toString());
        assertThat(result.getChallengePreference()).isNull();
    }

    @Test
    public void createStoreCredentials_ShouldCreateAStoredCredentials_WhenAllParametersAreProvided() {
        when(abstractOrderModelMock.getPaymentInfo().getTransactionIdentifier()).thenReturn(TRANSACTION_IDENTIFIER);

        final StoredCredentials result = testObj.createStoredCredentials(Usage.USED, MerchantInitiatedReason.RECURRING, TRANSACTION_IDENTIFIER);

        assertThat(result.getUsage()).isEqualTo(Usage.USED);
        assertThat(result.getMerchantInitiatedReason()).isEqualTo(MerchantInitiatedReason.RECURRING);
        assertThat(result.getSchemeTransactionIdentifier()).isEqualTo(TRANSACTION_IDENTIFIER);
    }

    @Test
    public void createStoreCredentials_ShouldCreateAStoredCredentials_WhenOnlyUsageParameterIsProvided() {
        when(abstractOrderModelMock.getPaymentInfo().getTransactionIdentifier()).thenReturn(TRANSACTION_IDENTIFIER);

        final StoredCredentials result = testObj.createStoredCredentials(Usage.USED, null, null);

        assertThat(result.getUsage()).isEqualTo(Usage.USED);
        assertThat(result.getMerchantInitiatedReason()).isNull();
        assertThat(result.getSchemeTransactionIdentifier()).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createStoreCredentials_ShouldThrowAnIllegalArgumentException_WhenUsageArgumentIsNull() {
        testObj.createStoredCredentials(null, null, null);
    }

    @Test
    public void getDynamicInteractionType_ShouldReturnDynamicInteractionType() {
        testObj.getDynamicInteractionType(worldpayAdditionalInfoDataMock);

        verify(worldpayDynamicInteractionResolverServiceMock).resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoDataMock);
    }

    @Test
    public void createToken_ShouldReturnTokenObject() {
        final Token result = testObj.createToken("subscriptionId", "securityCode");

        assertThat(result.getPaymentTokenID()).isEqualTo("subscriptionId");
        assertThat(result.getPaymentInstrument().getCvcNumber()).isEqualTo("securityCode");
    }

    @Test
    public void createCardDetails_WhenAddressIsNotNull() {
        final CardDetails result = testObj.createCardDetails(cseAdditionalAuthInfoMock, paymentAddressMock);

        assertThat(result.getCardHolderName()).isEqualTo(CARD_HOLDER_NAME);
        assertThat(result.getCardAddress()).isEqualTo(addressMock);
        assertThat(result.getExpiryDate().getMonth()).isEqualTo(EXPIRY_MONTH);
        assertThat(result.getExpiryDate().getYear()).isEqualTo(EXPIRY_YEAR);
    }

    @Test
    public void createCardDetails_WhenAddressIsNull() {
        final CardDetails result = testObj.createCardDetails(cseAdditionalAuthInfoMock, null);

        assertThat(result.getCardHolderName()).isEqualTo(CARD_HOLDER_NAME);
        assertThat(result.getExpiryDate().getMonth()).isEqualTo(EXPIRY_MONTH);
        assertThat(result.getExpiryDate().getYear()).isEqualTo(EXPIRY_YEAR);
        assertThat(result.getCardAddress()).isNull();
    }

    @Test
    public void createTokenServiceRequest_ShouldCreateMerchantToken_WhenMerchantIsEnabled() {

        final CreateTokenServiceRequest result = testObj.createTokenServiceRequest(merchantInfoMock, AUTHENTICATED_SHOPPER_ID, paymentMock, tokenRequestMock);

        assertThat(result.getMerchantInfo()).isEqualTo(merchantInfoMock);
        final CardTokenRequest cardTokenRequest = result.getCardTokenRequest();
        assertThat(cardTokenRequest.getPayment()).isEqualTo(paymentMock);
        assertThat(cardTokenRequest.getTokenRequest()).isEqualTo(tokenRequestMock);
        assertThat(cardTokenRequest.getAuthenticatedShopperId()).isNull();
    }

    @Test
    public void createTokenServiceRequest_ShouldCreateShopperToken_WhenMerchantIsNotEnabled() {
        when(siteConfigServiceMock.getBoolean(WORLDPAY_MERCHANT_TOKEN_ENABLED, false)).thenReturn(false);

        final CreateTokenServiceRequest result = testObj.createTokenServiceRequest(merchantInfoMock, AUTHENTICATED_SHOPPER_ID, paymentMock, tokenRequestMock);

        assertThat(result.getMerchantInfo()).isEqualTo(merchantInfoMock);
        final CardTokenRequest cardTokenRequest = result.getCardTokenRequest();
        assertThat(cardTokenRequest.getPayment()).isEqualTo(paymentMock);
        assertThat(cardTokenRequest.getTokenRequest()).isEqualTo(tokenRequestMock);
        assertThat(cardTokenRequest.getAuthenticatedShopperId()).isEqualTo(AUTHENTICATED_SHOPPER_ID);
    }

    @Test
    public void createAlternativeShippingAddress_shouldReturnNull_whenStillNotImplemented() {
        final AlternativeShippingAddress result = testObj.createAlternativeShippingAddress();

        assertThat(result).isNull();
    }
}

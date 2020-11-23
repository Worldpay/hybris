package com.worldpay.service.payment.request.impl;

import com.worldpay.data.Additional3DS2Info;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.enums.payment.storedCredentials.MerchantInitiatedReason;
import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.interaction.WorldpayDynamicInteractionResolverService;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.AlternativeShopperBankCodePayment;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.payment.StoredCredentials;
import com.worldpay.service.model.threeds2.Additional3DSData;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import com.worldpay.strategy.WorldpayDeliveryAddressStrategy;
import com.worldpay.threedsecureflexenums.ChallengePreferenceEnum;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.converter.Converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayRequestServiceTest {

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

    @InjectMocks
    private DefaultWorldpayRequestService testObj;

    @Mock
    private WorldpayUrlService worldpayUrlServiceMock;
    @Mock
    private SiteConfigService siteConfigServiceMock;
    @Mock
    private Converter<AddressModel, Address> worldpayAddressConverterMock;
    @Mock
    private WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategyMock;
    @Mock
    private WorldpayDynamicInteractionResolverService worldpayDynamicInteractionResolverServiceMock;
    @Mock
    private CustomerEmailResolutionService customerEmailResolutionServiceMock;

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
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private Session sessionMock;
    @Mock
    private Browser browserMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AbstractOrderModel abstractOrderModelMock;
    @Mock
    private CustomerModel customerMock;
    @Mock
    private AddressModel deliveryAddressMock, paymentAddressMock;
    @Mock
    private Address addressMock;
    @Mock
    private CartModel cartMock;

    @Test
    public void shouldReturnNullWhenPaymentTypeIsNotFound() throws WorldpayConfigurationException {
        when(worldpayUrlServiceMock.getFullSuccessURL()).thenReturn(FULL_SUCCESS_URL);

        final AlternativeShopperBankCodePayment result = (AlternativeShopperBankCodePayment) testObj.createBankPayment("notfound", BANK_CODE);

        assertThat(result).isNull();
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
    public void createStoreCredentials_ShouldThrowAnIlegalArgumentException_WhenUsageArgumentIsNull() {
        testObj.createStoredCredentials(null, null, null);
    }

    @Test
    public void getDynamicInteractionType_ShouldReturnDynamicInteractionType() {
        testObj.getDynamicInteractionType(worldpayAdditionalInfoDataMock);

        verify(worldpayDynamicInteractionResolverServiceMock).resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoDataMock);
    }

    @Test
    public void getEmailForCustomer_ShouldReturnCustomerEmail() {
        testObj.getEmailForCustomer(customerMock);

        verify(customerEmailResolutionServiceMock).getEmailForCustomer(customerMock);
    }

    @Test
    public void getAddressFromCart_WhenIsDeliveryAddressTrue_ShouldReturnDeliveryAddress() {
        when(worldpayDeliveryAddressStrategyMock.getDeliveryAddress(abstractOrderModelMock)).thenReturn(deliveryAddressMock);
        when(worldpayAddressConverterMock.convert(deliveryAddressMock)).thenReturn(addressMock);

        final Address result = testObj.getAddressFromCart(abstractOrderModelMock, true);

        assertThat(result).isEqualTo(addressMock);
    }

    @Test
    public void getAddressFromCart_WhenIsDeliveryAddressFalse_ShouldReturnPaymentAddress() {
        when(abstractOrderModelMock.getPaymentAddress()).thenReturn(paymentAddressMock);
        when(worldpayAddressConverterMock.convert(paymentAddressMock)).thenReturn(addressMock);

        final Address result = testObj.getAddressFromCart(abstractOrderModelMock, false);

        assertThat(result).isEqualTo(addressMock);
    }

    @Test
    public void getBillingAddress_WhenUsingShippingAsBilling_ShouldReturnBillingAddress() {
        when(cartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(additionalAuthInfoMock.getUsingShippingAsBilling()).thenReturn(Boolean.TRUE);
        when(worldpayAddressConverterMock.convert(deliveryAddressMock)).thenReturn(addressMock);

        final Address result = testObj.getBillingAddress(cartMock, additionalAuthInfoMock);

        assertThat(result).isEqualTo(addressMock);
    }

    @Test
    public void getBillingAddress_WhenUsingShippingAsBillingFalse_ShouldReturnPaymentAddress() {
        when(cartMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(cartMock.getPaymentAddress()).thenReturn(paymentAddressMock);
        when(additionalAuthInfoMock.getUsingShippingAsBilling()).thenReturn(Boolean.FALSE);
        when(worldpayAddressConverterMock.convert(paymentAddressMock)).thenReturn(addressMock);

        final Address result = testObj.getBillingAddress(cartMock, additionalAuthInfoMock);

        assertThat(result).isEqualTo(addressMock);
    }

    @Test
    public void getBillingAddress_WhenNoPaymentOrDeliveryAddress_ShouldReturnNull() {
        when(cartMock.getDeliveryAddress()).thenReturn(null);
        when(cartMock.getPaymentAddress()).thenReturn(null);

        final Address result = testObj.getBillingAddress(cartMock, additionalAuthInfoMock);

        assertThat(result).isNull();
    }
}

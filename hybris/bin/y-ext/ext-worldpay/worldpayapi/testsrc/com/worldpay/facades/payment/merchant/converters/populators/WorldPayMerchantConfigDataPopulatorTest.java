package com.worldpay.facades.payment.merchant.converters.populators;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.enums.*;
import com.worldpay.model.WorldpayApplePayConfigurationModel;
import com.worldpay.model.WorldpayGooglePayConfigurationModel;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import com.worldpay.model.WorldpayThreeDS2JsonWebTokenConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldPayMerchantConfigDataPopulatorTest {

    private static final String CARD_TYPE = "CARD";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String MAC_SECRET = "macSecret";
    private static final String MERCHANT_PASSWORD = "password";
    private static final String CSE_PUBLIC_KEY_VALUE = "csePublicKeyValue";
    private static final String INSTALLATION_ID = "installationId";
    private static final String STATEMENT_NARRATIVE_TEXT = "STATEMENT NARRATIVE TEXT";
    private static final String ORDER_CONTENT = "orderContent";
    private static final String CHALLENGE_PREFERENCE = "challengePreference";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String MERCHANT_ID = "merchantId";
    private static final String MERCHANT_NAME = "Merchant name";
    private static final String MERCHANT_NAME_VALUE = "merchant_name";
    private static final String MERCHANT_ID_VALUE = "merchant_id";
    private static final String GATEWAY_MERCHANT_ID_VALUE = "Gateway merchant id";
    private static final String ISS_VALUE = "5bd9e0e4444dce153428c940";
    private static final String JWT_MAC_KEY_VALUE = "fa2daee2-1fbb-45ff-4444-52805d5cd9e0";
    private static final String ORG_UNIT_ID_VALUE = "5bd9b55e4444761ac0af1c80";
    private static final String ALG_VALUE = "alg";
    private static final String EVENT_ORIGIN_DOMAIN = "EVENT_ORIGIN_DOMAIN";
    private static final String ISSUER_URL_VALUE = "acsURLValue";
    private static final String RETURN_URL_VALUE = "ReturnURLValue";

    @InjectMocks
    private WorldPayMerchantConfigDataPopulator testObj;

    @Mock
    private WorldpayMerchantConfigurationModel worldpayMerchantConfigurationMock;
    @Mock
    private WorldpayThreeDS2JsonWebTokenConfigurationModel threeDSFlexJsonWebTokenSettingsMock;
    @Mock
    private WorldpayGooglePayConfigurationModel googlePayConfigurationMock;
    @Mock
    private WorldpayApplePayConfigurationModel applePayConfigurationMock;

    private WorldpayMerchantConfigData targetData = new WorldpayMerchantConfigData();

    @Before
    public void setUp() {
        when(worldpayMerchantConfigurationMock.getCode()).thenReturn(MERCHANT_CODE);
        when(worldpayMerchantConfigurationMock.getPassword()).thenReturn(MERCHANT_PASSWORD);
        when(worldpayMerchantConfigurationMock.getMacSecret()).thenReturn(MAC_SECRET);
        when(worldpayMerchantConfigurationMock.getMacValidation()).thenReturn(true);
        when(worldpayMerchantConfigurationMock.getCsePublicKey()).thenReturn(CSE_PUBLIC_KEY_VALUE);
        when(worldpayMerchantConfigurationMock.getInstallationId()).thenReturn(INSTALLATION_ID);
        when(worldpayMerchantConfigurationMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE_TEXT);
        when(worldpayMerchantConfigurationMock.getOrderContent()).thenReturn(ORDER_CONTENT);
        when(worldpayMerchantConfigurationMock.getThreeDSFlexChallengePreference()).thenReturn(ChallengePreference.NOPREFERENCE);

        mockApplePayConfiguration();
        mockGooglePayConfiguration();
        mockThreeDSFlexJsonWebTokenSettings();
    }

    @Test
    public void populate_shouldPopulateTheMerchantData() {
        testObj.populate(worldpayMerchantConfigurationMock, targetData);

        assertEquals(MERCHANT_CODE, targetData.getCode());
        assertEquals(MERCHANT_PASSWORD, targetData.getPassword());
        assertEquals(MAC_SECRET, targetData.getMacSecret());
        assertEquals(true, targetData.getMacValidation());
        assertEquals(CSE_PUBLIC_KEY_VALUE, targetData.getCsePublicKey());
        assertEquals(INSTALLATION_ID, targetData.getInstallationId());
        assertEquals(STATEMENT_NARRATIVE_TEXT, targetData.getStatementNarrative());
        assertEquals(ORDER_CONTENT, targetData.getOrderContent());
        assertEquals(ChallengePreference.NOPREFERENCE.getCode(), targetData.getThreeDSFlexChallengePreference());

        assertNotNull(targetData.getApplePaySettings());
        assertEquals(COUNTRY_CODE, targetData.getApplePaySettings().getCountryCode());
        assertEquals(MERCHANT_ID, targetData.getApplePaySettings().getMerchantId());
        assertEquals(MERCHANT_NAME, targetData.getApplePaySettings().getMerchantName());
        assertTrue(targetData.getApplePaySettings().getMerchantCapabilities().contains(ApplePayMerchantCapabilities.SUPPORTS3DS.getCode()));
        assertTrue(targetData.getApplePaySettings().getSupportedNetworks().contains(ApplePaySupportedNetworks.CARTESBANCAIRES.getCode()));

        assertNotNull(targetData.getGooglePaySettings());
        assertEquals(GATEWAY_MERCHANT_ID_VALUE, targetData.getGooglePaySettings().getGatewayMerchantId());
        assertEquals(MERCHANT_ID_VALUE, targetData.getGooglePaySettings().getMerchantId());
        assertEquals(MERCHANT_NAME_VALUE, targetData.getGooglePaySettings().getMerchantName());
        assertEquals(EnvironmentType.TEST.getCode(), targetData.getGooglePaySettings().getEnvironment());
        assertTrue(targetData.getGooglePaySettings().getAllowedAuthMethods().contains(GooglePayCardAuthMethods.PAN_ONLY.getCode()));
        assertTrue(targetData.getGooglePaySettings().getAllowedCardNetworks().contains(GooglePayCardNetworks.AMEX.getCode()));

        assertNotNull(targetData.getThreeDSFlexJsonWebTokenSettings());
        assertEquals(ALG_VALUE, targetData.getThreeDSFlexJsonWebTokenSettings().getAlg());
        assertEquals(ISS_VALUE, targetData.getThreeDSFlexJsonWebTokenSettings().getIss());
        assertEquals(ORG_UNIT_ID_VALUE, targetData.getThreeDSFlexJsonWebTokenSettings().getOrgUnitId());
        assertEquals(JWT_MAC_KEY_VALUE, targetData.getThreeDSFlexJsonWebTokenSettings().getJwtMacKey());
        assertEquals(EVENT_ORIGIN_DOMAIN, targetData.getThreeDSFlexJsonWebTokenSettings().getEventOriginDomain());
        assertEquals(ISSUER_URL_VALUE, targetData.getThreeDSFlexJsonWebTokenSettings().getChallengeUrl());
        assertEquals(RETURN_URL_VALUE, targetData.getThreeDSFlexJsonWebTokenSettings().getDdcUrl());
    }

    private void mockThreeDSFlexJsonWebTokenSettings() {
        when(threeDSFlexJsonWebTokenSettingsMock.getAlg()).thenReturn(ALG_VALUE);
        when(threeDSFlexJsonWebTokenSettingsMock.getIss()).thenReturn(ISS_VALUE);
        when(threeDSFlexJsonWebTokenSettingsMock.getOrgUnitId()).thenReturn(ORG_UNIT_ID_VALUE);
        when(threeDSFlexJsonWebTokenSettingsMock.getJwtMacKey()).thenReturn(JWT_MAC_KEY_VALUE);
        when(threeDSFlexJsonWebTokenSettingsMock.getEventOriginDomain()).thenReturn(EVENT_ORIGIN_DOMAIN);
        when(threeDSFlexJsonWebTokenSettingsMock.getChallengeUrl()).thenReturn(ISSUER_URL_VALUE);
        when(threeDSFlexJsonWebTokenSettingsMock.getDdcUrl()).thenReturn(RETURN_URL_VALUE);
        when(worldpayMerchantConfigurationMock.getThreeDSFlexJsonWebTokenSettings()).thenReturn(threeDSFlexJsonWebTokenSettingsMock);
    }

    private void mockGooglePayConfiguration() {
        when(googlePayConfigurationMock.getMerchantId()).thenReturn(MERCHANT_ID_VALUE);
        when(googlePayConfigurationMock.getMerchantName()).thenReturn(MERCHANT_NAME_VALUE);
        when(googlePayConfigurationMock.getCardType()).thenReturn(CARD_TYPE);
        when(googlePayConfigurationMock.getEnvironment()).thenReturn(EnvironmentType.TEST);
        when(googlePayConfigurationMock.getGatewayMerchantId()).thenReturn(GATEWAY_MERCHANT_ID_VALUE);
        when(googlePayConfigurationMock.getAllowedAuthMethods()).thenReturn(Set.of(GooglePayCardAuthMethods.PAN_ONLY, GooglePayCardAuthMethods.CRYPTOGRAM_3DS));
        when(googlePayConfigurationMock.getAllowedCardNetworks()).thenReturn(Set.of(GooglePayCardNetworks.AMEX, GooglePayCardNetworks.DISCOVER));
        when(worldpayMerchantConfigurationMock.getGooglePayConfiguration()).thenReturn(googlePayConfigurationMock);
    }

    private void mockApplePayConfiguration() {
        when(applePayConfigurationMock.getCountryCode()).thenReturn(COUNTRY_CODE);
        when(applePayConfigurationMock.getMerchantId()).thenReturn(MERCHANT_ID);
        when(applePayConfigurationMock.getMerchantName()).thenReturn(MERCHANT_NAME);
        when(applePayConfigurationMock.getMerchantCapabilities()).thenReturn(Set.of(ApplePayMerchantCapabilities.SUPPORTS3DS, ApplePayMerchantCapabilities.SUPPORTSCREDIT));
        when(applePayConfigurationMock.getSupportedNetworks()).thenReturn(Set.of(ApplePaySupportedNetworks.MADA, ApplePaySupportedNetworks.CARTESBANCAIRES));
        when(worldpayMerchantConfigurationMock.getApplePayConfiguration()).thenReturn(applePayConfigurationMock);
    }
}

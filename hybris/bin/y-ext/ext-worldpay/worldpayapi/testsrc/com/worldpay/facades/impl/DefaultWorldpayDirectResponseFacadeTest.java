package com.worldpay.facades.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayAddonEndpointService;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.payment.WorldpayDirectResponseService;
import com.worldpay.service.payment.WorldpayJsonWebTokenService;
import com.worldpay.service.payment.WorldpaySessionService;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayDirectResponseFacadeTest {
    private static final ChallengeWindowSizeEnum WINDOW_SIZE = ChallengeWindowSizeEnum.R_250_400;
    private static final String AUTO_SUBMIT_3DS_LEGACY = "autoSubmit3DSecureLegacy";
    private static final String TERM_URL_PARAM_NAME = "termURL";
    private static final String PA_REQUEST_PARAM_NAME = "paRequest";
    private static final String ISSUER_URL_PARAM_NAME = "issuerURL";
    private static final String MERCHANT_DATA_PARAM_NAME = "merchantData";
    private static final String ISSUER_URL_VALUE = "issuerUrlValue";
    private static final String PA_REQUEST_VALUE = "paRequestValue";
    private static final String TERM_URL_VALUE = "termUrlValue";
    private static final String MERCHANT_VALUE = "merchantDataValue";
    private static final String JWT_CHALLENGE_VALUE = "JWT_CHALLENGE_VALUE";
    private static final String JWT_PARAM = "jwt";
    private static final String CHALLENGE_URL_PARAM = "challengeUrl";
    private static final String CHALLENGE_URL_VALUE = "challengeUrlValue";
    private static final String V1 = "1";
    private static final String V2 = "2";
    private static final String HEIGHT_PARAM = "height";
    private static final String WIDTH_PARAM = "width";
    private static final String H400 = "400";
    private static final String W390 = "390";
    private static final String W250 = "250";

    @InjectMocks
    private DefaultWorldpayDirectResponseFacade testObj;

    @Mock
    private WorldpayDirectResponseService worldpayDirectResponseServiceMock;
    @Mock
    private WorldpayUrlService worldpayUrlServiceMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AcceleratorCheckoutFacade acceleratorCheckoutFacadeMock;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointServiceMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DirectResponseData directResponseDataMock;
    @Mock
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WorldpayMerchantConfigData merchantConfigDataMock;
    @Mock
    private WorldpayJsonWebTokenService worldpayJsonWebTokenServiceMock;
    @Mock
    private WorldpaySessionService worldpaySessionServiceMock;


    @Test
    public void returnsIfDirectResponseIsCancelled() {
        when(worldpayDirectResponseServiceMock.isCancelled(directResponseDataMock)).thenReturn(true);

        final Boolean result = testObj.isCancelled(directResponseDataMock);

        assertThat(result).isTrue();
    }

    @Test
    public void returnsIfDirectResponseIsAuthorised() {
        when(worldpayDirectResponseServiceMock.isAuthorised(directResponseDataMock)).thenReturn(true);

        final Boolean result = testObj.isAuthorised(directResponseDataMock);

        assertThat(result).isTrue();
    }

    @Test
    public void returnsIfDirectResponseIs3DSecureLegacyFlow() {
        when(worldpayDirectResponseServiceMock.is3DSecureLegacyFlow(directResponseDataMock)).thenReturn(true);

        final Boolean result = testObj.is3DSecureLegacyFlow(directResponseDataMock);

        assertThat(result).isTrue();
    }

    @Test
    public void returnsIfDirectResponseIs3DSecureFlexFlow() {
        when(worldpayDirectResponseServiceMock.is3DSecureFlexFlow(directResponseDataMock)).thenReturn(true);

        final Boolean result = testObj.is3DSecureFlexFlow(directResponseDataMock);

        assertThat(result).isTrue();
    }

    @Test
    public void retrieveAttributesWhenResponseDataIs3dSecureLegacyFlow() throws WorldpayConfigurationException {
        when(worldpayDirectResponseServiceMock.is3DSecureLegacyFlow(directResponseDataMock)).thenReturn(true);
        when(worldpayAddonEndpointServiceMock.getAutoSubmit3DSecure()).thenReturn(AUTO_SUBMIT_3DS_LEGACY);
        when(directResponseDataMock.getIssuerURL()).thenReturn(ISSUER_URL_VALUE);
        when(directResponseDataMock.getPaRequest()).thenReturn(PA_REQUEST_VALUE);
        when(acceleratorCheckoutFacadeMock.getCheckoutCart().getWorldpayOrderCode()).thenReturn(MERCHANT_VALUE);
        when(worldpayUrlServiceMock.getFullThreeDSecureTermURL()).thenReturn(TERM_URL_VALUE);

        final Map<String, String> result = testObj.retrieveAttributesForLegacy3dSecure(directResponseDataMock);

        assertThat(result.get(ISSUER_URL_PARAM_NAME)).isEqualTo(ISSUER_URL_VALUE);
        assertThat(result.get(PA_REQUEST_PARAM_NAME)).isEqualTo(PA_REQUEST_VALUE);
        assertThat(result.get(TERM_URL_PARAM_NAME)).isEqualTo(TERM_URL_VALUE);
        assertThat(result.get(MERCHANT_DATA_PARAM_NAME)).isEqualTo(MERCHANT_VALUE);
    }

    @Test
    public void retrieveAttributesWhenResponseDataIs3dSecureFlexFlowAnd3DVersionOne() throws WorldpayConfigurationException {
        when(worldpayDirectResponseServiceMock.is3DSecureFlexFlow(directResponseDataMock)).thenReturn(true);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(merchantConfigDataMock);
        when(worldpayJsonWebTokenServiceMock.createJsonWebTokenFor3DSecureFlexChallengeIframe(merchantConfigDataMock, directResponseDataMock)).thenReturn(JWT_CHALLENGE_VALUE);
        when(directResponseDataMock.getMajor3DSVersion()).thenReturn(V1);
        when(merchantConfigDataMock.getThreeDSFlexJsonWebTokenSettings().getChallengeUrl()).thenReturn(CHALLENGE_URL_VALUE);


        final Map<String, String> result = testObj.retrieveAttributesForFlex3dSecure(directResponseDataMock);

        assertThat(result.get(JWT_PARAM)).isEqualTo(JWT_CHALLENGE_VALUE);
        assertThat(result.get(CHALLENGE_URL_PARAM)).isEqualTo(CHALLENGE_URL_VALUE);
        assertThat(result.get(HEIGHT_PARAM)).isEqualTo(H400);
        assertThat(result.get(WIDTH_PARAM)).isEqualTo(W390);
    }

    @Test
    public void retrieveAttributesWhenResponseDataIs3dSecureFlexFlowAnd3DVersionTwo() throws WorldpayConfigurationException {
        when(worldpayDirectResponseServiceMock.is3DSecureFlexFlow(directResponseDataMock)).thenReturn(true);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(merchantConfigDataMock);
        when(worldpayJsonWebTokenServiceMock.createJsonWebTokenFor3DSecureFlexChallengeIframe(merchantConfigDataMock, directResponseDataMock)).thenReturn(JWT_CHALLENGE_VALUE);
        when(directResponseDataMock.getMajor3DSVersion()).thenReturn(V2);
        when(merchantConfigDataMock.getThreeDSFlexJsonWebTokenSettings().getChallengeUrl()).thenReturn(CHALLENGE_URL_VALUE);
        when(worldpaySessionServiceMock.getWindowSizeChallengeFromSession()).thenReturn(WINDOW_SIZE);

        final Map<String, String> result = testObj.retrieveAttributesForFlex3dSecure(directResponseDataMock);

        assertThat(result.get(JWT_PARAM)).isEqualTo(JWT_CHALLENGE_VALUE);
        assertThat(result.get(CHALLENGE_URL_PARAM)).isEqualTo(CHALLENGE_URL_VALUE);
        assertThat(result.get(HEIGHT_PARAM)).isEqualTo(H400);
        assertThat(result.get(WIDTH_PARAM)).isEqualTo(W250);
    }

    @Test
    public void retrieveAttributesWhenResponseDataIs3dSecureFlexFlowAnd3DVersionTwoForDefaultWindowSize() throws WorldpayConfigurationException {
        when(worldpayDirectResponseServiceMock.is3DSecureFlexFlow(directResponseDataMock)).thenReturn(true);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(merchantConfigDataMock);
        when(worldpayJsonWebTokenServiceMock.createJsonWebTokenFor3DSecureFlexChallengeIframe(merchantConfigDataMock, directResponseDataMock)).thenReturn(JWT_CHALLENGE_VALUE);
        when(directResponseDataMock.getMajor3DSVersion()).thenReturn(V2);
        when(merchantConfigDataMock.getThreeDSFlexJsonWebTokenSettings().getChallengeUrl()).thenReturn(CHALLENGE_URL_VALUE);
        when(worldpaySessionServiceMock.getWindowSizeChallengeFromSession()).thenReturn(null);

        final Map<String, String> result = testObj.retrieveAttributesForFlex3dSecure(directResponseDataMock);

        assertThat(result.get(JWT_PARAM)).isEqualTo(JWT_CHALLENGE_VALUE);
        assertThat(result.get(CHALLENGE_URL_PARAM)).isEqualTo(CHALLENGE_URL_VALUE);
        assertThat(result.get(HEIGHT_PARAM)).isEqualTo(H400);
        assertThat(result.get(WIDTH_PARAM)).isEqualTo(W390);
    }
}

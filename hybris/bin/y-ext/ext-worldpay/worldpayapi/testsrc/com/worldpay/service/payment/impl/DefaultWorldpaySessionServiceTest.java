package com.worldpay.service.payment.impl;

import com.worldpay.enums.order.ThreeDSecureFlowEnum;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpaySessionServiceTest {
    private static final String THREED_SECURE_ECHO_DATA_KEY = "3DSecureEchoData";
    private static final String THREED_SECURE_COOKIE_KEY = "3DSecureCookie";
    private static final String THREED_SECURE_WINDOW_KEY = "challengeWindowSize";
    private static final String THREED_SECURE_ECHO_DATA_VALUE = "echoData";
    private static final String THREED_SECURE_COOKIE_VALUE = "cookie";
    private static final String THREED_SECURE_WINDOW_SIZE_VALUE = ChallengeWindowSizeEnum.R_250_400.toString();

    @InjectMocks
    private DefaultWorldpaySessionService testObj;
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private DirectAuthoriseServiceResponse responseMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;

    @Test
    public void setSessionAttributesStoresInSessionTheValuesForTheCookieEchoDataAndWindowSize() {
        when(responseMock.getEchoData()).thenReturn(THREED_SECURE_ECHO_DATA_VALUE);
        when(responseMock.getCookie()).thenReturn(THREED_SECURE_COOKIE_VALUE);
        when(responseMock.get3DSecureFlow()).thenReturn(Optional.of(ThreeDSecureFlowEnum.THREEDSFLEX_FLOW));
        when(worldpayAdditionalInfoDataMock.getAdditional3DS2().getChallengeWindowSize()).thenReturn(THREED_SECURE_WINDOW_SIZE_VALUE);


        testObj.setSessionAttributesFor3DSecure(responseMock, worldpayAdditionalInfoDataMock);

        verify(sessionServiceMock).setAttribute(THREED_SECURE_ECHO_DATA_KEY, THREED_SECURE_ECHO_DATA_VALUE);
        verify(sessionServiceMock).setAttribute(THREED_SECURE_COOKIE_KEY, THREED_SECURE_COOKIE_VALUE);
        verify(sessionServiceMock).setAttribute(THREED_SECURE_WINDOW_KEY, THREED_SECURE_WINDOW_SIZE_VALUE);
    }

    @Test
    public void setSessionAttributeStoresNothingInSessionWhenItIsNot3DSecure() {
        when(responseMock.get3DSecureFlow()).thenReturn(Optional.empty());


        testObj.setSessionAttributesFor3DSecure(responseMock, worldpayAdditionalInfoDataMock);

        verify(sessionServiceMock, never()).setAttribute(THREED_SECURE_ECHO_DATA_KEY, THREED_SECURE_ECHO_DATA_VALUE);
        verify(sessionServiceMock, never()).setAttribute(THREED_SECURE_COOKIE_KEY, THREED_SECURE_COOKIE_VALUE);
        verify(sessionServiceMock, never()).setAttribute(THREED_SECURE_WINDOW_KEY, THREED_SECURE_WINDOW_SIZE_VALUE);
    }

    @Test
    public void getWindowSizeAttributeFromSession() {
        when(sessionServiceMock.getAttribute(THREED_SECURE_WINDOW_KEY)).thenReturn(THREED_SECURE_WINDOW_SIZE_VALUE);

        final ChallengeWindowSizeEnum result = testObj.getWindowSizeChallengeFromSession();

        verify(sessionServiceMock).getAttribute(THREED_SECURE_WINDOW_KEY);
        assertThat(result).isEqualTo(ChallengeWindowSizeEnum.getEnum(THREED_SECURE_WINDOW_SIZE_VALUE));

    }

    @Test
    public void testGetAndRemoveThreeDSecureCookie() {
        when(sessionServiceMock.getAttribute(THREED_SECURE_COOKIE_KEY)).thenReturn(THREED_SECURE_COOKIE_VALUE);
        doNothing().when(sessionServiceMock).removeAttribute(THREED_SECURE_COOKIE_KEY);

        final String response = testObj.getAndRemoveThreeDSecureCookie();

        verify(sessionServiceMock).getAttribute(THREED_SECURE_COOKIE_KEY);
        Assert.assertEquals(THREED_SECURE_COOKIE_VALUE, response);
    }
}

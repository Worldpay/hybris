package com.worldpay.service.payment.impl;

import com.worldpay.enums.order.ThreeDSecureFlowEnum;
import com.worldpay.model.WorldpayCartThreeDSChallengeSessionModel;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OccWorldpaySessionServiceTest {
    private static final String COOKIE = "cookie";
    private static final String ECHO_DATA = "echoData";
    private static final String SESSION_ID = "123-abc-def";
    private static final String WINDOW_SIZE = ChallengeWindowSizeEnum.R_600_400.name();

    @InjectMocks
    public OccWorldpaySessionService testObj;

    @Mock
    public CartService cartServiceMock;
    @Mock
    public ModelService modelServiceMock;

    @Mock
    public CartModel cartMock;
    @Mock
    public DirectAuthoriseServiceResponse authoriseServiceResponseMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    public WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    public WorldpayCartThreeDSChallengeSessionModel sessionMock;

    @Before
    public void setUp() {
        when(cartServiceMock.getSessionCart()).thenReturn(cartMock);
        when(modelServiceMock.create(WorldpayCartThreeDSChallengeSessionModel.class)).thenReturn(sessionMock);
    }

    @Test
    public void setSessionAttributesFor3DSecure_shouldPopulateSessionAttribute_whenFlex(){
        when(authoriseServiceResponseMock.get3DSecureFlow()).thenReturn(Optional.of(ThreeDSecureFlowEnum.THREEDSFLEX_FLOW));
        when(authoriseServiceResponseMock.getEchoData()).thenReturn(ECHO_DATA);
        when(authoriseServiceResponseMock.getCookie()).thenReturn(COOKIE);

        when(worldpayAdditionalInfoDataMock.getSessionId()).thenReturn(SESSION_ID);
        when(worldpayAdditionalInfoDataMock.getAdditional3DS2().getChallengeWindowSize()).thenReturn(WINDOW_SIZE);

        testObj.setSessionAttributesFor3DSecure(authoriseServiceResponseMock, worldpayAdditionalInfoDataMock);

        verify(sessionMock).setEchoData(ECHO_DATA);
        verify(sessionMock).setCookie(COOKIE);
        verify(sessionMock).setSessionId(SESSION_ID);
        verify(sessionMock).setChallengeWindowSize(WINDOW_SIZE);

        verify(cartMock).setThreeDSChallengeSession(sessionMock);
        verify(modelServiceMock).save(cartMock);
    }

    @Test
    public void getAndRemoveThreeDSecureCookie_shouldRemoveCookie() {
        when(cartMock.getThreeDSChallengeSession()).thenReturn(sessionMock);
        when(sessionMock.getCookie()).thenReturn(COOKIE);

        final String result = testObj.getAndRemoveThreeDSecureCookie();
        assertThat(result).isEqualTo(COOKIE);

        verify(sessionMock).setCookie(null);
        verify(modelServiceMock).save(sessionMock);
    }

    @Test
    public void getAndRemoveAdditionalDataSessionId_shouldRemoveSessionId() {
        when(cartMock.getThreeDSChallengeSession()).thenReturn(sessionMock);
        when(sessionMock.getSessionId()).thenReturn(SESSION_ID);

        final String result = testObj.getAndRemoveAdditionalDataSessionId();
        assertThat(result).isEqualTo(SESSION_ID);

        verify(sessionMock).setSessionId(null);
        verify(modelServiceMock).save(sessionMock);
    }

    @Test
    public void getWindowSizeChallengeFromSession_shouldRemoveWindowSize() {
        when(cartMock.getThreeDSChallengeSession()).thenReturn(sessionMock);
        when(sessionMock.getChallengeWindowSize()).thenReturn(WINDOW_SIZE);

        final ChallengeWindowSizeEnum result = testObj.getWindowSizeChallengeFromSession();
        assertThat(result.name()).isEqualTo(WINDOW_SIZE);

        verify(sessionMock).setChallengeWindowSize(null);
        verify(modelServiceMock).save(sessionMock);
    }

    @Test
    public void setSessionIdFor3dSecure_shouldSetSessionId() {
        when(cartMock.getThreeDSChallengeSession()).thenReturn(sessionMock);

        testObj.setSessionIdFor3dSecure(SESSION_ID);

        verify(sessionMock).setSessionId(SESSION_ID);
        verify(modelServiceMock).save(cartMock);
    }

    @Test
    public void setSessionIdFor3dSecure_shouldSetSessionId_whenSessionNotSet() {
        when(cartMock.getThreeDSChallengeSession()).thenReturn(null);

        testObj.setSessionIdFor3dSecure(SESSION_ID);

        verify(modelServiceMock).create(WorldpayCartThreeDSChallengeSessionModel.class);
        verify(cartMock).setThreeDSChallengeSession(sessionMock);
        verify(sessionMock).setSessionId(SESSION_ID);
        verify(modelServiceMock).save(cartMock);
    }
}

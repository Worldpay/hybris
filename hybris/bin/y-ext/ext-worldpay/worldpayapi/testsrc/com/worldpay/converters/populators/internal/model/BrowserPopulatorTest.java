package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Browser;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BrowserPopulatorTest {

    private static final String ACCEPTER_HEADER = "accepterHeader";
    private static final String DEVICE_OS = "deviceOS";
    private static final String DEVICE_TYPE = "deviceType";
    private static final String HTTP_ACCEPT_LANGUAGE = "httpAcceptLanguage";
    private static final String HTTP_REFERER = "httpReferer";
    private static final String USER_AGENT_HEADER = "userAgentHeader";
    private static final String LANGUAGE = "language";
    private static final String TIME_ZONE = "timeZone";
    private static final int HEIGHT = 1080;
    private static final int WIDTH = 1020;
    private static final int COLOUR_DEPTH = 24;

    @InjectMocks
    BrowserPopulator testObj;

    @Mock
    private Browser sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.Browser());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_ShouldPopulateBrowser() {
        when(sourceMock.getAcceptHeader()).thenReturn(ACCEPTER_HEADER);
        when(sourceMock.getDeviceOS()).thenReturn(DEVICE_OS);
        when(sourceMock.getDeviceType()).thenReturn(DEVICE_TYPE);
        when(sourceMock.getHttpAcceptLanguage()).thenReturn(HTTP_ACCEPT_LANGUAGE);
        when(sourceMock.getHttpReferer()).thenReturn(HTTP_REFERER);
        when(sourceMock.getUserAgentHeader()).thenReturn(USER_AGENT_HEADER);

        when(sourceMock.getLanguage()).thenReturn(LANGUAGE);
        when(sourceMock.getTimeZone()).thenReturn(TIME_ZONE);
        when(sourceMock.getJavascriptEnabled()).thenReturn(Boolean.TRUE);
        when(sourceMock.getJavaEnabled()).thenReturn(Boolean.FALSE);
        when(sourceMock.getColorDepth()).thenReturn(COLOUR_DEPTH);
        when(sourceMock.getScreenWidth()).thenReturn(WIDTH);
        when(sourceMock.getScreenHeight()).thenReturn(HEIGHT);

        final com.worldpay.internal.model.Browser targetMock = new com.worldpay.internal.model.Browser();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getAcceptHeader()).isEqualTo(ACCEPTER_HEADER);
        assertThat(targetMock.getDeviceOS()).isEqualTo(DEVICE_OS);
        assertThat(targetMock.getHttpAcceptLanguage()).isEqualTo(HTTP_ACCEPT_LANGUAGE);
        assertThat(targetMock.getHttpReferer()).isEqualTo(HTTP_REFERER);
        assertThat(targetMock.getUserAgentHeader()).isEqualTo(USER_AGENT_HEADER);
        assertThat(targetMock.getDeviceType()).isEqualTo(DEVICE_TYPE);
        assertThat(targetMock.getBrowserLanguage()).isEqualTo(LANGUAGE);
        assertThat(targetMock.getTimeZone()).isEqualTo(TIME_ZONE);
        assertThat(targetMock.getBrowserJavaEnabled()).isEqualTo(Boolean.FALSE.toString());
        assertThat(targetMock.getBrowserJavaScriptEnabled()).isEqualTo(Boolean.TRUE.toString());
        assertThat(targetMock.getBrowserScreenHeight()).isEqualTo(String.valueOf(HEIGHT));
        assertThat(targetMock.getBrowserScreenWidth()).isEqualTo(String.valueOf(WIDTH));
        assertThat(targetMock.getBrowserColourDepth()).isEqualTo(String.valueOf(COLOUR_DEPTH));
    }
}

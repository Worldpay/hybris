package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Browser;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BrowserPopulatorTest {

    private static final String ACCEPTER_HEADER = "accepterHeader";
    private static final String DEVICE_OS = "deviceOS";
    private static final String HTTP_ACCEPT_LANGUAGE = "httpAcceptLanguage";
    private static final String HTTP_REFERER = "httpReferer";
    private static final String USER_AGENT_HEADER = "userAgentHeader";

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
        when(sourceMock.getHttpAcceptLanguage()).thenReturn(HTTP_ACCEPT_LANGUAGE);
        when(sourceMock.getHttpReferer()).thenReturn(HTTP_REFERER);
        when(sourceMock.getUserAgentHeader()).thenReturn(USER_AGENT_HEADER);

        final com.worldpay.internal.model.Browser targetMock = new com.worldpay.internal.model.Browser();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getAcceptHeader()).isEqualTo(ACCEPTER_HEADER);
        assertThat(targetMock.getDeviceOS()).isEqualTo(DEVICE_OS);
        assertThat(targetMock.getHttpAcceptLanguage()).isEqualTo(HTTP_ACCEPT_LANGUAGE);
        assertThat(targetMock.getHttpReferer()).isEqualTo(HTTP_REFERER);
        assertThat(targetMock.getUserAgentHeader()).isEqualTo(USER_AGENT_HEADER);
    }
}

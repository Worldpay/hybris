package com.worldpay.interceptors.beforeview;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayJsVariablesBeforeViewHandlerTest {

    private static final int SECONDS = 2;
    private static final String HOP_DECLINE_MESSAGE_WAIT_TIMER_SECONDS = "hop.decline.message.wait.timer.seconds";
    private static final String PAYMENT_STATUS = "paymentStatus";
    private static final String VIEW_NAME = "viewName";
    private static final String PAYMENT_VALUE = "payment value";

    @InjectMocks
    private WorldpayJsVariablesBeforeViewHandler testObj;

    @Mock
    private SiteConfigService siteConfigServiceMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;
    @Mock
    private ModelMap modelMock;

    @Before
    public void setUp() {
        when(siteConfigServiceMock.getInt(HOP_DECLINE_MESSAGE_WAIT_TIMER_SECONDS, SECONDS)).thenReturn(5);
        when(modelMock.get(PAYMENT_STATUS)).thenReturn(PAYMENT_VALUE);
    }

    @Test
    public void beforeView_ShouldReturnTheViewName() {
        final String result = testObj.beforeView(requestMock, responseMock, modelMock, VIEW_NAME);

        assertThat(result).isEqualTo(VIEW_NAME);
    }
}

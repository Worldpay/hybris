package com.worldpay.interceptors.beforeview;

import com.worldpay.service.payment.WorldpayFraudSightStrategy;
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
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayJsVariablesBeforeViewHandlerTest {

    private static final int SECONDS = 2;
    private static final String ORGANIZATION_ID_VALUE = "5fd9e78c09c50b2d408f6805";
    private static final String PROFILING_DOMAIN_PROD = "ddc.worldpay.com";
    private static final String PROFILING_DOMAIN_TEST = "ddc-test.worldpay.com";
    private static final String HOP_DECLINE_MESSAGE_WAIT_TIMER_SECONDS = "hop.decline.message.wait.timer.seconds";
    private static final String WORLDPAY_CONFIG_ENVIRONMENT = "worldpay.config.environment";
    private static final String WORLDPAY_CONFIG_PROFILE_DOMAIN_PROD = "worldpay.config.profile.domain.prod";
    private static final String WORLDPAY_CONFIG_PROFILE_DOMAIN_TEST = "worldpay.config.profile.domain.test";
    private static final String WORLDPAY_CONFIG_MERCHANT_ORGANIZATION_ID = "worldpay.merchant.organization.id";
    private static final String PAYMENT_STATUS = "paymentStatus";
    private static final String VIEW_NAME = "viewName";
    private static final String PAYMENT_VALUE = "payment value";

    @InjectMocks
    private WorldpayJsVariablesBeforeViewHandler testObj;

    @Mock
    private SiteConfigService siteConfigServiceMock;
    @Mock
    private WorldpayFraudSightStrategy worldpayFraudSightStrategyMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;
    @Mock
    private ModelMap modelMock;

    @Before
    public void setUp() {
        when(worldpayFraudSightStrategyMock.isFraudSightEnabled()).thenReturn(true);
        when(siteConfigServiceMock.getInt(HOP_DECLINE_MESSAGE_WAIT_TIMER_SECONDS, SECONDS)).thenReturn(5);
        when(modelMock.getAttribute(PAYMENT_STATUS)).thenReturn(PAYMENT_VALUE);
        when(siteConfigServiceMock.getProperty(WORLDPAY_CONFIG_ENVIRONMENT)).thenReturn("TEST");
        when(siteConfigServiceMock.getProperty(WORLDPAY_CONFIG_PROFILE_DOMAIN_PROD)).thenReturn(PROFILING_DOMAIN_PROD);
        when(siteConfigServiceMock.getProperty(WORLDPAY_CONFIG_PROFILE_DOMAIN_TEST)).thenReturn(PROFILING_DOMAIN_TEST);
        when(siteConfigServiceMock.getProperty(WORLDPAY_CONFIG_MERCHANT_ORGANIZATION_ID)).thenReturn(ORGANIZATION_ID_VALUE);
    }

    @Test
    public void beforeView_WhenTestEnvAndFraudSightEnabled_ShouldReturnTheViewNameAndSetFSProperties() {
        final String result = testObj.beforeView(requestMock, responseMock, modelMock, VIEW_NAME);

        assertThat(result).isEqualTo(VIEW_NAME);
        verify(siteConfigServiceMock).getProperty(WORLDPAY_CONFIG_PROFILE_DOMAIN_TEST);
        verify(siteConfigServiceMock).getProperty(WORLDPAY_CONFIG_MERCHANT_ORGANIZATION_ID);
    }

    @Test
    public void beforeView_WhenTestProdAndFraudSightEnabled_ShouldReturnTheViewNameAndSetFSProperties() {
        when(siteConfigServiceMock.getProperty(WORLDPAY_CONFIG_ENVIRONMENT)).thenReturn("PROD");

        final String result = testObj.beforeView(requestMock, responseMock, modelMock, VIEW_NAME);

        assertThat(result).isEqualTo(VIEW_NAME);
        verify(siteConfigServiceMock).getProperty(WORLDPAY_CONFIG_PROFILE_DOMAIN_PROD);
        verify(siteConfigServiceMock).getProperty(WORLDPAY_CONFIG_MERCHANT_ORGANIZATION_ID);
    }

    @Test
    public void beforeView_WhenTestEnvAndFraudSightDisabled_ShouldReturnTheViewName() {
        when(worldpayFraudSightStrategyMock.isFraudSightEnabled()).thenReturn(false);

        final String result = testObj.beforeView(requestMock, responseMock, modelMock, VIEW_NAME);

        assertThat(result).isEqualTo(VIEW_NAME);
        verify(siteConfigServiceMock, never()).getProperty(WORLDPAY_CONFIG_PROFILE_DOMAIN_TEST);
        verify(siteConfigServiceMock, never()).getProperty(WORLDPAY_CONFIG_MERCHANT_ORGANIZATION_ID);
    }

    @Test
    public void beforeView_WhenTestProdAndFraudSightDisabled_ShouldReturnTheViewName() {
        when(worldpayFraudSightStrategyMock.isFraudSightEnabled()).thenReturn(false);
        when(siteConfigServiceMock.getProperty(WORLDPAY_CONFIG_ENVIRONMENT)).thenReturn("PROD");

        final String result = testObj.beforeView(requestMock, responseMock, modelMock, VIEW_NAME);

        assertThat(result).isEqualTo(VIEW_NAME);
        verify(siteConfigServiceMock, never()).getProperty(WORLDPAY_CONFIG_PROFILE_DOMAIN_PROD);
        verify(siteConfigServiceMock, never()).getProperty(WORLDPAY_CONFIG_MERCHANT_ORGANIZATION_ID);
    }
}

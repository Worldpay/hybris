package com.worldpay.service.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.model.WorldpayThreeDS2JsonWebTokenConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayUrlServiceTest {

    private static final String FULL_SITE_URL = "fullSiteUrl";
    private static final String URL = "url";
    private static final String EMPTY_URL = "";
    private static final String TERMS_URL = "termsUrl";
    private static final String AUTOSUBMIT_URL = "/checkout/multi/worldpay/3dsecureflex/sop/response/autosubmit";
    private static final String THREED_SECURE_FLEX_URL = "3D_SECURE_FLEX_URL";

    @Spy
    @InjectMocks
    private DefaultWorldpayUrlService testObj;

    @Mock
    private BaseSiteService baseSiteServiceMock;
    @Mock
    private SiteBaseUrlResolutionService siteBaseUrlResolutionServiceMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BaseSiteModel currentBaseSiteMock;
    @Mock
    private WorldpayThreeDS2JsonWebTokenConfigurationModel threeDSFlexSettingsMock;

    @Before
    public void setUp() {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(currentBaseSiteMock);
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, true, URL)).thenReturn(FULL_SITE_URL);
        when(currentBaseSiteMock.getWebMerchantConfiguration().getThreeDSFlexJsonWebTokenSettings()).thenReturn(threeDSFlexSettingsMock);
        when(threeDSFlexSettingsMock.getAuthSubmit()).thenReturn(AUTOSUBMIT_URL);
        when(threeDSFlexSettingsMock.getFlowReturnUrl()).thenReturn("threeDFlexPath");
    }

    @Test
    public void getFullUrl_WhenSecure_ShouldReturnSecureFullSiteURL() throws Exception {
        final String result = testObj.getFullUrl(URL, true);

        assertEquals(FULL_SITE_URL, result);
    }

    @Test
    public void getFullUrl_WhenUnSecure_ShouldReturnUnSecureFullSiteURL() throws Exception {
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, false, URL)).thenReturn(FULL_SITE_URL);

        final String result = testObj.getFullUrl(URL, false);

        assertEquals(FULL_SITE_URL, result);
    }

    @Test
    public void getBaseWebsiteUrlForSite_ShouldReturnBaseURLForCurrentSite() throws Exception {
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, true, EMPTY_URL)).thenReturn(FULL_SITE_URL);

        final String result = testObj.getBaseWebsiteUrlForSite();

        assertEquals(FULL_SITE_URL, result);
    }

    @Test
    public void getFullTermsUrl_ShouldReturnFullTermsUrl() throws Exception {
        doReturn(TERMS_URL).when(testObj).getTermsPath();
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, true, TERMS_URL)).thenReturn(FULL_SITE_URL);

        final String result = testObj.getFullTermsUrl();

        assertEquals(FULL_SITE_URL, result);
    }

    @Test(expected = WorldpayConfigurationException.class)
    public void getFullUrl_WhenServiceReturnsNull_ShouldThrowException() throws Exception {
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, true, URL)).thenReturn(null);

        testObj.getFullUrl(URL, true);
    }

    @Test
    public void getFullCancelURL_ShouldReturnCorrectURLs() throws WorldpayConfigurationException {
        // Returns the 3rd argument of the invocation, the Url passed in the getFullUrl invocation
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(eq(currentBaseSiteMock), eq(true), anyString())).thenAnswer(invocationOnMOck -> {
            final Object[] args = invocationOnMOck.getArguments();
            return args[2];
        });

        doReturn("cancelPath").when(testObj).getCancelPath();
        testObj.getFullCancelURL();
        verify(testObj).getFullUrl("cancelPath", true);

        doReturn("successPath").when(testObj).getSuccessPath();
        testObj.getFullSuccessURL();
        verify(testObj).getFullUrl("successPath", true);

        doReturn("pendingPath").when(testObj).getPendingPath();
        testObj.getFullPendingURL();
        verify(testObj).getFullUrl("pendingPath", true);

        doReturn("failurePath").when(testObj).getFailurePath();
        testObj.getFullFailureURL();
        verify(testObj).getFullUrl("failurePath", true);

        doReturn("threeDPath").when(testObj).getThreeDSecureTermPath();
        testObj.getFullThreeDSecureTermURL();
        verify(testObj).getFullUrl("threeDPath", true);

        testObj.getFullThreeDSecureFlexFlowReturnUrl();
        verify(testObj).getFullUrl("threeDFlexPath", true);
    }

    @Test
    public void getWebsiteUrlForCurrentSite_ShuldReturnSiteURL() {
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, true, null)).thenReturn(URL);

        final String result = testObj.getWebsiteUrlForCurrentSite();

        assertThat(result).isEqualTo(URL);
    }

    @Test
    public void getFullThreeDSecureFlexFlowReturnUrl_ShouldReturnFullFlexFlowURL() throws Exception {
        when(threeDSFlexSettingsMock.getFlowReturnUrl()).thenReturn(THREED_SECURE_FLEX_URL);
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, true, THREED_SECURE_FLEX_URL)).thenReturn(FULL_SITE_URL + THREED_SECURE_FLEX_URL);

        final String result = testObj.getFullThreeDSecureFlexFlowReturnUrl();

        assertEquals(FULL_SITE_URL + THREED_SECURE_FLEX_URL, result);
    }

    @Test
    public void getFullThreeDSecureFlexAutosubmitUrl_ShouldReturnFullFlexAutoSubmitURL() throws Exception {
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, true, AUTOSUBMIT_URL)).thenReturn(FULL_SITE_URL + AUTOSUBMIT_URL);

        final String result = testObj.getFullThreeDSecureFlexAutosubmitUrl();

        assertEquals(FULL_SITE_URL + AUTOSUBMIT_URL, result);
    }
}

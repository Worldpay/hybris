package com.worldpay.service.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

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
    @Mock
    private BaseSiteModel currentBaseSiteMock;

    @Test
    public void testGetFullUrlSecure() throws Exception {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(currentBaseSiteMock);
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, true, URL)).thenReturn(FULL_SITE_URL);

        final String result = testObj.getFullUrl(URL, true);

        assertEquals(FULL_SITE_URL, result);
    }

    @Test
    public void testGetFullUrlUnSecure() throws Exception {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(currentBaseSiteMock);
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, false, URL)).thenReturn(FULL_SITE_URL);

        final String result = testObj.getFullUrl(URL, false);

        assertEquals(FULL_SITE_URL, result);
    }

    @Test
    public void shouldReturnBaseURLForCurrentSite() throws Exception {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(currentBaseSiteMock);
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, true, EMPTY_URL)).thenReturn(FULL_SITE_URL);

        final String result = testObj.getBaseWebsiteUrlForSite();

        assertEquals(FULL_SITE_URL, result);
    }

    @Test
    public void shouldReturnFullTermsUrl() throws Exception {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(currentBaseSiteMock);
        doReturn(TERMS_URL).when(testObj).getTermsPath();
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, true, TERMS_URL)).thenReturn(FULL_SITE_URL);

        final String result = testObj.getFullTermsUrl();

        assertEquals(FULL_SITE_URL, result);
    }

    @Test(expected = WorldpayConfigurationException.class)
    public void testGetFullUrlShouldRaiseExceptionWhenServiceReturnsNull() throws Exception {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(currentBaseSiteMock);
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, true, URL)).thenReturn(null);

        testObj.getFullUrl(URL, true);
    }

    @Test
    public void testSetOfAddresses() throws WorldpayConfigurationException {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(currentBaseSiteMock);
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

        doReturn("threeDFlexPath").when(testObj).getThreeDSecureFlexFlowReturnUrl();
        final String result = testObj.getFullThreeDSecureFlexFlowReturnUrl();
        verify(testObj).getFullUrl("threeDFlexPath", true);
    }

    @Test
    public void testReturnCurrentBaseSiteDomain() {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(currentBaseSiteMock);
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, true, null)).thenReturn(URL);
        testObj.getWebsiteUrlForCurrentSite();
    }

    @Test
    public void testGetFullThreeDSecureFlexFlowReturnUrl() throws Exception {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(currentBaseSiteMock);
        doReturn(THREED_SECURE_FLEX_URL).when(testObj).getThreeDSecureFlexFlowReturnUrl();
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, true, THREED_SECURE_FLEX_URL)).thenReturn(FULL_SITE_URL + THREED_SECURE_FLEX_URL);

        final String result = testObj.getFullThreeDSecureFlexFlowReturnUrl();

        assertEquals(FULL_SITE_URL + THREED_SECURE_FLEX_URL, result);
    }

    @Test
    public void testGetFullThreeDSecureFlexAutosubmitUrl() throws Exception {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(currentBaseSiteMock);
        doReturn(AUTOSUBMIT_URL).when(testObj).getThreeDSecureFlexAuthSubmit();
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, true, AUTOSUBMIT_URL)).thenReturn(FULL_SITE_URL + AUTOSUBMIT_URL);

        final String result = testObj.getFullThreeDSecureFlexAutosubmitUrl();

        assertEquals(FULL_SITE_URL + AUTOSUBMIT_URL, result);
    }
}

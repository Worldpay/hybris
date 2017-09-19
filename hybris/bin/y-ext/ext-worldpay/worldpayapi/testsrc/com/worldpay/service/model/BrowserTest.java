package com.worldpay.service.model;

import com.worldpay.exception.WorldpayModelTransformationException;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@UnitTest
public class BrowserTest {

    private static final String HEADER = "header";
    private static final String USER_AGENT_HEADER = "user_agent_header";
    private static final String DEVICE_TYPE = "deviceType";
    private static final String DEVICE_OS = "deviceOS";
    private static final String HTTP_REFERER = "httpReferer";
    private static final String HTTP_ACCEPT_LANGUAGE = "httpAcceptLanguage";

    @Test
    public void shouldReturnAnInternalBrowser() throws WorldpayModelTransformationException {
        final Browser testObj = new Browser(null, null, null, null, null, null);

        final com.worldpay.internal.model.Browser result = (com.worldpay.internal.model.Browser) testObj.transformToInternalModel();

        assertTrue(result instanceof com.worldpay.internal.model.Browser);
    }

    @Test
    public void shouldSetAcceptHeaderOnInternalBrowser() throws WorldpayModelTransformationException {
        final Browser testObj = new Browser(HEADER, null, null, null, null, null);

        final com.worldpay.internal.model.Browser result = (com.worldpay.internal.model.Browser) testObj.transformToInternalModel();

        assertEquals(HEADER, result.getAcceptHeader());
    }


    @Test
    public void shouldSetUserAgentHeaderOnInternalBrowser() throws WorldpayModelTransformationException {
        final Browser testObj = new Browser(null, USER_AGENT_HEADER, null, null, null, null);

        final com.worldpay.internal.model.Browser result = (com.worldpay.internal.model.Browser) testObj.transformToInternalModel();

        assertEquals(USER_AGENT_HEADER, result.getUserAgentHeader());
    }

    @Test
    public void shouldSetDeviceTypeOnInternalBrowser() throws WorldpayModelTransformationException {
        final Browser testObj = new Browser(null, null, DEVICE_TYPE, null, null, null);

        final com.worldpay.internal.model.Browser result = (com.worldpay.internal.model.Browser) testObj.transformToInternalModel();

        assertEquals(DEVICE_TYPE, result.getDeviceType());
    }

    @Test
    public void shouldSetDeviceOsOnInternalBrowser() throws WorldpayModelTransformationException {
        final Browser testObj = new Browser(null, null, null, DEVICE_OS, null, null);

        final com.worldpay.internal.model.Browser result = (com.worldpay.internal.model.Browser) testObj.transformToInternalModel();

        assertEquals(DEVICE_OS, result.getDeviceOS());
    }

    @Test
    public void shouldSetHttpAcceptLanguageOnInternalBrowser() throws WorldpayModelTransformationException {
        final Browser testObj = new Browser(null, null, null, null, HTTP_ACCEPT_LANGUAGE, null);

        final com.worldpay.internal.model.Browser result = (com.worldpay.internal.model.Browser) testObj.transformToInternalModel();

        assertEquals(HTTP_ACCEPT_LANGUAGE, result.getHttpAcceptLanguage());
    }

    @Test
    public void shouldSetHttpRefererOnInternalBrowser() throws WorldpayModelTransformationException {
        final Browser testObj = new Browser(null, null, null, null, null, HTTP_REFERER);

        final com.worldpay.internal.model.Browser result = (com.worldpay.internal.model.Browser) testObj.transformToInternalModel();

        assertEquals(HTTP_REFERER, result.getHttpReferer());
    }
}

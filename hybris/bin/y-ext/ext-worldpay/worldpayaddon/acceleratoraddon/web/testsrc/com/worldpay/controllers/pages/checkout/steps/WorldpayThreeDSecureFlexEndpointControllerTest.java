package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.constants.WorldpayapiConstants;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.forms.ThreeDSecureFlexForm;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static de.hybris.platform.addonsupport.controllers.AbstractAddOnController.REDIRECT_PREFIX;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayThreeDSecureFlexEndpointControllerTest {

    private static final String CHECKOUT_MULTI_WORLDPAY_3_DSECUREFLEX_SOP_RESPONSE_AUTOSUBMIT = "/checkout/multi/worldpay/3dsecureflex/sop/response/autosubmit";
    private static final String WORLDPAY_ADDON_PREFIX = "worldpay.addon.prefix";
    private static final String CHECKOUT_3DSECUREFLEX_RESPONSE_AUTOSUBMIT = "pages/checkout/multi/threedsflex/autosubmitThreeDSecureFlexResponse";
    private static final String UNDEFINED_PREFIX = "undefined";
    private static final String ADDON_WORLDPAYADDON = "addon\\:/worldpayaddon/";
    private static final String CHECKOUT_ORDER_CONFIRMATION = "/checkout/orderConfirmation/";
    private static final String CSE_PAYMENT_DETAILS_PAGE = "CSEPaymentDetailsPage";

    @Spy
    @InjectMocks
    private WorldpayThreeDSecureFlexEndpointController testObj;

    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointServiceMock;
    @Mock
    private ConfigurationService configurationServiceMock;
    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;

    @Mock
    private Configuration configurationMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private ThreeDSecureFlexForm threeDSecureFlexFormMock;
    @Mock
    private RedirectAttributes redirectAttributesMock;
    @Mock
    private Model modelMock;
    @Mock
    private DirectResponseData directResponseDataMock;
    @Mock
    private HttpServletResponse responseMock;

    @Before
    public void setUp() {
        when(configurationServiceMock.getConfiguration()).thenReturn(configurationMock);
        when(configurationMock.getString(WorldpayapiConstants.WORLDPAY_3DSECURE_FLEX_SECOND_AUTH_SUBMIT_URL)).thenReturn(CHECKOUT_MULTI_WORLDPAY_3_DSECUREFLEX_SOP_RESPONSE_AUTOSUBMIT);
        when(configurationMock.getString(WORLDPAY_ADDON_PREFIX, UNDEFINED_PREFIX)).thenReturn(ADDON_WORLDPAYADDON);
        when(worldpayAddonEndpointServiceMock.getCSEPaymentDetailsPage()).thenReturn(CSE_PAYMENT_DETAILS_PAGE);
    }

    @Test
    public void testDoHandleThreeDSecureResponse() {
        final String response = testObj.doHandleThreeDSecureResponse(requestMock, threeDSecureFlexFormMock, redirectAttributesMock);

        assertEquals(REDIRECT_PREFIX + CHECKOUT_MULTI_WORLDPAY_3_DSECUREFLEX_SOP_RESPONSE_AUTOSUBMIT, response);
    }

    @Test
    public void testGetThreeDSecureResponseAutosubmit() {
        final String response = testObj.getThreeDSecureResponseAutosubmit(requestMock, threeDSecureFlexFormMock, redirectAttributesMock);

        assertEquals(ADDON_WORLDPAYADDON + CHECKOUT_3DSECUREFLEX_RESPONSE_AUTOSUBMIT, response);
    }

    @Test
    public void testDoHandleThreeDSecureResponseThatShouldRedirectToOrderConfirmation() throws CMSItemNotFoundException, WorldpayException, InvalidCartException {
        doReturn(CHECKOUT_ORDER_CONFIRMATION).when(testObj).handleDirectResponse(modelMock, directResponseDataMock, responseMock);
        when(worldpayDirectOrderFacadeMock.executeSecondPaymentAuthorisation3DSecure()).thenReturn(directResponseDataMock);

        final String result = testObj.doHandleThreeDSecureResponse(requestMock, modelMock, responseMock);

        assertEquals(CHECKOUT_ORDER_CONFIRMATION, result);
    }

    @Test
    public void testDoHandleThreeDSecureResponseWithException() throws CMSItemNotFoundException, InvalidCartException, WorldpayException {
        when(worldpayDirectOrderFacadeMock.executeSecondPaymentAuthorisation3DSecure()).thenThrow(new WorldpayException("errorMessage"));
        doNothing().when(testObj).setupAddPaymentPage(modelMock);

        final String result = testObj.doHandleThreeDSecureResponse(requestMock, modelMock, responseMock);

        assertEquals(CSE_PAYMENT_DETAILS_PAGE, result);
    }

}

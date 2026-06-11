package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.constants.WorldpayapiConstants;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.impl.WorldpayB2BAcceleratorCheckoutFacadeDecorator;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.forms.B2BCSEPaymentForm;
import com.worldpay.forms.ThreeDSecureFlexForm;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.configuration2.Configuration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.worldpay.payment.TransactionStatus.AUTHORISED;
import static de.hybris.platform.addonsupport.controllers.AbstractAddOnController.REDIRECT_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@ExtendWith(MockitoExtension.class)
class WorldpayB2BThreeDSecureFlexEndpointControllerTest {

    private static final String CHECKOUT_MULTI_WORLDPAY_3_DSECUREFLEX_SOP_RESPONSE_AUTOSUBMIT = "/checkout/multi/worldpay/3dsecureflex/sop/response/autosubmit";
    private static final String WORLDPAY_ADDON_PREFIX = "worldpay.addon.prefix";
    private static final String CHECKOUT_3DSECUREFLEX_RESPONSE_AUTOSUBMIT = "pages/checkout/multi/threedsflex/autosubmitThreeDSecureFlexResponse";
    private static final String UNDEFINED_PREFIX = "undefined";
    private static final String ADDON_WORLDPAYADDON = "addon\\:/worldpayaddon/";
    private static final String CHECKOUT_ORDER_CONFIRMATION = "redirect:/checkout/orderConfirmation/ORDER_CODE";
    private static final String CSE_PAYMENT_DETAILS_PAGE = "CSEPaymentDetailsPage";
    private static final String ORDER_CODE = "ORDER_CODE";

    @Spy
    @InjectMocks
    private WorldpayB2BThreeDSecureFlexEndpointController testObj;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointServiceMock;
    @Mock
    private ConfigurationService configurationServiceMock;
    @Mock(name = "checkoutFacade")
    private WorldpayB2BAcceleratorCheckoutFacadeDecorator worldpayCheckoutFacadeMock;
    @Mock(name = "worldpayDirectOrderFacade")
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
    private B2BCSEPaymentForm b2bCSEPaymentFormMock;
    @Mock
    private AbstractOrderData orderDataMock;
    @Mock
    private CheckoutFlowFacade checkoutFlowFacadeMock;
    @Mock
    private HttpServletResponse responseMock;

    @Test
    void testDoHandleThreeDSecureResponse() {
        when(configurationServiceMock.getConfiguration()).thenReturn(configurationMock);
        when(configurationMock.getString(WorldpayapiConstants.WORLDPAY_3DSECURE_FLEX_SECOND_AUTH_SUBMIT_URL)).thenReturn(CHECKOUT_MULTI_WORLDPAY_3_DSECUREFLEX_SOP_RESPONSE_AUTOSUBMIT);
        lenient().when(checkoutFlowFacadeMock.hasNoPaymentInfo()).thenReturn(true);

        final String response = testObj.doHandleThreeDSecureResponse(requestMock, threeDSecureFlexFormMock, redirectAttributesMock);

        assertEquals(REDIRECT_PREFIX + CHECKOUT_MULTI_WORLDPAY_3_DSECUREFLEX_SOP_RESPONSE_AUTOSUBMIT, response);
    }

    @Test
    void testGetThreeDSecureResponseAutoSubmit() {
        when(configurationServiceMock.getConfiguration()).thenReturn(configurationMock);
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_ADDON_PREFIX, UNDEFINED_PREFIX)).thenReturn(ADDON_WORLDPAYADDON);
        lenient().when(checkoutFlowFacadeMock.hasNoPaymentInfo()).thenReturn(true);

        final String response = testObj.getThreeDSecureResponseAutosubmit(requestMock, threeDSecureFlexFormMock, redirectAttributesMock);

        assertEquals(ADDON_WORLDPAYADDON + CHECKOUT_3DSECUREFLEX_RESPONSE_AUTOSUBMIT, response);
    }

    @Test
    void testDoHandleThreeDSecureResponseThatShouldRedirectToOrderConfirmation() throws CMSItemNotFoundException, WorldpayException, InvalidCartException {
        when(testObj.getB2BCheckoutFacade()).thenReturn(worldpayCheckoutFacadeMock);
        lenient().when(checkoutFlowFacadeMock.hasNoPaymentInfo()).thenReturn(true);

        lenient().doReturn(CHECKOUT_ORDER_CONFIRMATION).when(testObj).handleDirectResponse(modelMock, directResponseDataMock, responseMock);
        when(directResponseDataMock.getTransactionStatus()).thenReturn(AUTHORISED);
        when(worldpayDirectOrderFacadeMock.executeSecondPaymentAuthorisation3DSecure()).thenReturn(directResponseDataMock);
        when(testObj.getB2BCheckoutFacade().placeOrder(any(PlaceOrderData.class))).thenReturn(orderDataMock);
        when(orderDataMock.getCode()).thenReturn(ORDER_CODE);

        final String result = testObj.doHandleThreeDSecureResponse(requestMock, b2bCSEPaymentFormMock, modelMock, responseMock);

        assertEquals(CHECKOUT_ORDER_CONFIRMATION, result);
    }

    @Test
    void testDoHandleThreeDSecureResponseWithException() throws CMSItemNotFoundException, InvalidCartException, WorldpayException {
        when(worldpayAddonEndpointServiceMock.getCSEPaymentDetailsPage()).thenReturn(CSE_PAYMENT_DETAILS_PAGE);
        lenient().when(checkoutFlowFacadeMock.hasNoPaymentInfo()).thenReturn(true);

        when(worldpayDirectOrderFacadeMock.executeSecondPaymentAuthorisation3DSecure()).thenThrow(new WorldpayException("errorMessage"));
        doNothing().when(testObj).setupAddPaymentPage(modelMock);

        final String result = testObj.doHandleThreeDSecureResponse(requestMock, b2bCSEPaymentFormMock, modelMock, responseMock);

        assertEquals(CSE_PAYMENT_DETAILS_PAGE, result);
    }

}

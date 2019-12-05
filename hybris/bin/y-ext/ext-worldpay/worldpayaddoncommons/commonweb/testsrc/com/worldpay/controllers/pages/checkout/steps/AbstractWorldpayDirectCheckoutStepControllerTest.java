package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.facades.WorldpayDirectResponseFacade;
import com.worldpay.facades.order.impl.WorldpayCheckoutFacadeDecorator;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletResponse;

import static com.worldpay.payment.TransactionStatus.AUTHORISED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractWorldpayDirectCheckoutStepControllerTest {

    private static final String ORDER_CODE = "orderCode";
    private static final String RETURN_CODE = "A12";
    private static final String LOCALISED_DECLINE_MESSAGE = "localisedDeclineMessage";
    private static final String ERROR_VIEW = "error";
    private static final String AUTOSUBMIT_3DSECURE = "AutoSubmit3DSecure";
    private static final String AUTOSUBMIT_3DSECURE_FLEX = "AutoSubmit3dSecureFlex";
    private static final String MERCHANT_DATA_VALUE = "merchantDataValue";
    @Spy
    @InjectMocks
    private final AbstractWorldpayDirectCheckoutStepController testObj = new TestAbstractWorldpayDirectCheckoutStepController();
    @Mock
    private DirectResponseData directResponseDataMock;
    @Mock
    private Model modelMock;
    @Mock
    private OrderData orderDataMock;
    @Mock
    private CartData cartDataMock;
    @Mock
    private WorldpayCheckoutFacadeDecorator checkoutFacadeMock;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointService;
    @Mock
    private WorldpayDirectResponseFacade worldpayDirectResponseFacade;
    @Mock
    private HttpServletResponse responseMock;

    @Before
    public void setUp() {
        doReturn(LOCALISED_DECLINE_MESSAGE).when(testObj).getLocalisedDeclineMessage(RETURN_CODE);
        when(directResponseDataMock.getTransactionStatus()).thenReturn(AUTHORISED);
        when(directResponseDataMock.getOrderData()).thenReturn(orderDataMock);
        when(directResponseDataMock.getReturnCode()).thenReturn(RETURN_CODE);
        when(orderDataMock.getCode()).thenReturn(ORDER_CODE);
        when(cartDataMock.getWorldpayOrderCode()).thenReturn(MERCHANT_DATA_VALUE);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(worldpayAddonEndpointService.getAutoSubmit3DSecure()).thenReturn(AUTOSUBMIT_3DSECURE);
        when(worldpayAddonEndpointService.getAutoSubmit3DSecureFlex()).thenReturn(AUTOSUBMIT_3DSECURE_FLEX);
    }

    @Test
    public void shouldRedirectTo3DSecureIfAuthenticationRequiredForLegacyFlow() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(worldpayDirectResponseFacade.is3DSecureLegacyFlow(directResponseDataMock)).thenReturn(true);

        final String result = testObj.handleDirectResponse(modelMock, directResponseDataMock, responseMock);

        assertEquals(AUTOSUBMIT_3DSECURE, result);
    }

    @Test
    public void shouldRedirectTo3DSecureIfAuthenticationRequiredFor3DSecureFlexFlow() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(worldpayDirectResponseFacade.is3DSecureFlexFlow(directResponseDataMock)).thenReturn(true);

        final String result = testObj.handleDirectResponse(modelMock, directResponseDataMock, responseMock);

        assertEquals(AUTOSUBMIT_3DSECURE_FLEX, result);
    }

    @Test
    public void shouldRetrieveAllAttributesForModelIfPaymentRequires3dSecureLegacyFlowAuthentication() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(worldpayDirectResponseFacade.is3DSecureLegacyFlow(directResponseDataMock)).thenReturn(true);

        testObj.handleDirectResponse(modelMock, directResponseDataMock, responseMock);

        verify(worldpayDirectResponseFacade).retrieveAttributesForLegacy3dSecure(directResponseDataMock);
    }

    @Test
    public void shouldRetrieveAllAttributesForModelIfPaymentRequires3dSecureFlexFlowAuthentication() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(worldpayDirectResponseFacade.is3DSecureFlexFlow(directResponseDataMock)).thenReturn(true);

        testObj.handleDirectResponse(modelMock, directResponseDataMock, responseMock);

        verify(worldpayDirectResponseFacade).retrieveAttributesForFlex3dSecure(directResponseDataMock);
    }

    @Test
    public void shouldReturnErrorPageWhenCancelled() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(worldpayDirectResponseFacade.isCancelled(directResponseDataMock)).thenReturn(true);

        final String result = testObj.handleDirectResponse(modelMock, directResponseDataMock, responseMock);

        assertEquals(ERROR_VIEW, result);
    }

    @Test
    public void shouldRedirectToErrorIfResponseIsNotAuthorisedOrAuthRequired() throws CMSItemNotFoundException, WorldpayConfigurationException {
        final String result = testObj.handleDirectResponse(modelMock, directResponseDataMock, responseMock);

        assertEquals(ERROR_VIEW, result);
    }

    public class TestAbstractWorldpayDirectCheckoutStepController extends AbstractWorldpayDirectCheckoutStepController {
        @Override
        protected String getErrorView(final Model model) {
            return ERROR_VIEW;
        }
    }
}

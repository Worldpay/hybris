package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.facades.order.impl.WorldpayCheckoutFacadeDecorator;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayAddonEndpointService;
import com.worldpay.service.WorldpayUrlService;
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

import static com.worldpay.payment.TransactionStatus.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractWorldpayDirectCheckoutStepControllerTest {

    private static final String SECURE_TERM_URL = "termUrl";
    private static final String ORDER_CODE = "orderCode";
    private static final String RETURN_CODE = "A12";
    private static final String LOCALISED_DECLINE_MESSAGE = "localisedDeclineMessage";
    private static final String ERROR_VIEW = "error";
    private static final String AUTOSUBMIT_3DSECURE = "AutoSubmit3DSecure";
    private static final String TERM_URL_PARAM_NAME = "termURL";
    @Spy
    @InjectMocks
    private AbstractWorldpayDirectCheckoutStepController testObj = new TestAbstractWorldpayDirectCheckoutStepController();
    @Mock
    private DirectResponseData directResponseDataMock;
    @Mock
    private Model modelMock;
    @Mock
    private OrderData orderDataMock;
    @Mock
    private WorldpayUrlService worldpayUrlServiceMock;
    @Mock
    private CartData cartDataMock;
    @Mock
    private WorldpayCheckoutFacadeDecorator checkoutFacadeMock;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointService;

    @Before
    public void setUp() {
        doReturn(LOCALISED_DECLINE_MESSAGE).when(testObj).getLocalisedDeclineMessage(RETURN_CODE);
        when(directResponseDataMock.getTransactionStatus()).thenReturn(AUTHORISED);
        when(directResponseDataMock.getOrderData()).thenReturn(orderDataMock);
        when(directResponseDataMock.getReturnCode()).thenReturn(RETURN_CODE);
        when(orderDataMock.getCode()).thenReturn(ORDER_CODE);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(worldpayAddonEndpointService.getAutoSubmit3DSecure()).thenReturn(AUTOSUBMIT_3DSECURE);
    }

    @Test
    public void shouldRedirectTo3dSecureIfAuthenticationRequired() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(AUTHENTICATION_REQUIRED);

        final String result = testObj.handleDirectResponse(modelMock, directResponseDataMock);

        assertEquals(AUTOSUBMIT_3DSECURE, result);
    }

    @Test
    public void shouldAdd3DSecureTermUrlToModelIfPaymentRequiresAuth() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(worldpayUrlServiceMock.getFullThreeDSecureTermURL()).thenReturn(SECURE_TERM_URL);
        when(directResponseDataMock.getTransactionStatus()).thenReturn(AUTHENTICATION_REQUIRED);

        testObj.handleDirectResponse(modelMock, directResponseDataMock);

        verify(modelMock).addAttribute(TERM_URL_PARAM_NAME, SECURE_TERM_URL);
    }

    @Test
    public void shouldReturnErrorPageWhenCancelled() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(CANCELLED);

        final String result = testObj.handleDirectResponse(modelMock, directResponseDataMock);

        assertEquals(ERROR_VIEW, result);
    }

    @Test
    public void shouldRedirectToErrorIfResponseIsNotAuthorisedOrAuthRequired() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(ERROR);

        final String result = testObj.handleDirectResponse(modelMock, directResponseDataMock);

        assertEquals(ERROR_VIEW, result);
    }

    public class TestAbstractWorldpayDirectCheckoutStepController extends AbstractWorldpayDirectCheckoutStepController {
        @Override
        protected String getErrorView(final Model model) {
            return ERROR_VIEW;
        }
    }
}

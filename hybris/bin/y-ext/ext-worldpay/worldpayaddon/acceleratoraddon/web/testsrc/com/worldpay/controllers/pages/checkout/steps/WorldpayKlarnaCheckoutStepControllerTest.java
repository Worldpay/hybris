package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayKlarnaPaymentCheckoutFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.hostedorderpage.data.KlarnaRedirectAuthoriseResult;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.order.InvalidCartException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.REDIRECT_URL_CHOOSE_PAYMENT_METHOD;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayKlarnaCheckoutStepControllerTest {
    private static final String PAYMENT_STATUS_PARAMETER_NAME = "paymentStatus";
    private static final String CHOOSE_PAYMENT_REDIRECT_URL = REDIRECT_URL_CHOOSE_PAYMENT_METHOD + "?" + PAYMENT_STATUS_PARAMETER_NAME + "=%s";
    private static final String KLARNA_PAGE_REDIRECT = "klarnaContentPage";
    private static final String KLARNA_CONTENT_PAGE = "decodedContent";
    private static final String KLARNA_VIEW_DATA = "KLARNA_VIEW_DATA";

    @InjectMocks
    private WorldpayKlarnaCheckoutStepController testObj;
    @Mock
    private WorldpayKlarnaPaymentCheckoutFacade worldpayKlarnaPaymentCheckoutFacade;
    @Mock
    private KlarnaRedirectAuthoriseResult klarnaRedirectAuthoriseResponse;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointServiceMock;
    @Mock
    private WorldpayHostedOrderFacade worldpayHostedOrderFacadeMock;
    @Mock
    private AcceleratorCheckoutFacade checkoutFacadeMock;
    @Mock
    private RedirectAttributes redirectAttributesMock;
    @Mock
    private Model modelMock;

    @Test
    public void shouldHandleKlarnaConfirmation() throws WorldpayException, InvalidCartException {
        when(worldpayKlarnaPaymentCheckoutFacade.checkKlarnaOrderStatus()).thenReturn(klarnaRedirectAuthoriseResponse);
        when(klarnaRedirectAuthoriseResponse.getDecodedHTMLContent()).thenReturn(KLARNA_CONTENT_PAGE);
        when(worldpayAddonEndpointServiceMock.getKlarnaResponsePage()).thenReturn(KLARNA_PAGE_REDIRECT);

        final String result = testObj.doHandleKlarnaConfirmation(modelMock, redirectAttributesMock);

        assertEquals(KLARNA_PAGE_REDIRECT, result);
        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(klarnaRedirectAuthoriseResponse);
        verify(modelMock).addAttribute(KLARNA_VIEW_DATA, KLARNA_CONTENT_PAGE);
        verify(checkoutFacadeMock).placeOrder();
    }

    @Test
    public void shouldNotPlaceOrderWhenKlarnaConfirmationIsNotAuthorised() throws WorldpayException, InvalidCartException {
        when(worldpayKlarnaPaymentCheckoutFacade.checkKlarnaOrderStatus()).thenThrow(new WorldpayException("Chan chan"));

        final String result = testObj.doHandleKlarnaConfirmation(modelMock, redirectAttributesMock);

        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, AuthorisedStatus.ERROR.name()), result);
        verify(modelMock, never()).addAttribute(eq(KLARNA_VIEW_DATA), anyString());
        verify(checkoutFacadeMock, never()).placeOrder();
    }

    @Test
    public void shouldRedirectToErrorPageWhenPlaceOrderFails() throws WorldpayException, InvalidCartException {
        when(worldpayKlarnaPaymentCheckoutFacade.checkKlarnaOrderStatus()).thenReturn(klarnaRedirectAuthoriseResponse);
        when(checkoutFacadeMock.placeOrder()).thenThrow(new InvalidCartException("Chan chan"));

        final String result = testObj.doHandleKlarnaConfirmation(modelMock, redirectAttributesMock);

        verify(modelMock, never()).addAttribute(KLARNA_VIEW_DATA, KLARNA_PAGE_REDIRECT);
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, AuthorisedStatus.ERROR.name()), result);
    }

}

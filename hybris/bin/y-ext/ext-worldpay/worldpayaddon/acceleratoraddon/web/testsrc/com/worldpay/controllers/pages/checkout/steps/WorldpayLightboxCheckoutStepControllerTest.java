package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.forms.validation.PaymentDetailsFormValidator;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Locale;

import static com.worldpay.controllers.pages.checkout.steps.WorldpayChoosePaymentMethodCheckoutStepController.PAYMENT_DETAILS_FORM;
import static com.worldpay.controllers.pages.checkout.steps.WorldpayLightboxCheckoutStepController.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayLightboxCheckoutStepControllerTest {

    private static final String PAYMENT_METHOD = "paymentMethod";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String BILLINGADDRESS_INPAYMENTFORM = "billingAddressInPaymentForm";
    private static final String GLOBAL_ERRORFRAGMENT = "globalErrorFragment";

    @Spy
    @InjectMocks
    private WorldpayLightboxCheckoutStepController testObj = new WorldpayLightboxCheckoutStepController();

    @Mock
    private PaymentDetailsFormValidator paymentDetailsFormValidatorMock;

    @Mock
    private Model modelMock;
    @Mock
    private AddressForm billingAddressMock;
    @Mock
    private PaymentDetailsForm paymentDetailsFormMock;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private WorldpayHostedOrderFacade worldpayHostedOrderFacadeMock;
    @Mock
    private PaymentData paymentDataMock;
    @Mock

    private MessageSource themeSourceMock;
    @Mock
    private I18NService i18NServiceMock;
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointService;

    @Before
    public void setUp() throws CMSItemNotFoundException {
        when(paymentDetailsFormMock.getBillingAddress()).thenReturn(billingAddressMock);
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PAYMENT_METHOD);
        doNothing().when(testObj).resetDeclineCodeOnCart();
        doNothing().when(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
        doNothing().when(testObj).setupAddPaymentPage(modelMock);
        doNothing().when(testObj).prepareErrorView(modelMock, paymentDetailsFormMock);
        when(i18NServiceMock.getCurrentLocale()).thenReturn(Locale.UK);
        when(sessionServiceMock.getAttribute(PAYMENT_DETAILS_FORM)).thenReturn(paymentDetailsFormMock);
        when(sessionServiceMock.getAttribute(PAYMENT_DETAILS_FORM_ERRORS)).thenReturn(bindingResultMock);
        when(worldpayAddonEndpointService.getBillingAddressInPaymentForm()).thenReturn(BILLINGADDRESS_INPAYMENTFORM);
        when(worldpayAddonEndpointService.getGlobalErrorsFragment()).thenReturn(GLOBAL_ERRORFRAGMENT);
    }

    @Test
    public void testAddPaymentDetailsLightbox() throws Exception {
        final String result = testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, bindingResultMock);

        verify(sessionServiceMock).setAttribute(PAYMENT_DETAILS_FORM, paymentDetailsFormMock);
        verify(sessionServiceMock).setAttribute(PAYMENT_DETAILS_FORM_ERRORS, bindingResultMock);
        verify(testObj).setupAddPaymentPage(modelMock);
        verify(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
        verify(testObj).resetDeclineCodeOnCart();
        verify(paymentDetailsFormValidatorMock).validate(paymentDetailsFormMock, bindingResultMock);
        assertEquals(BILLINGADDRESS_INPAYMENTFORM, result);
    }

    @Test
    public void testAddPaymentDetailsLightboxShouldAddErrorsAndReturnViewFragment() throws Exception {
        doReturn(true).when(testObj).addGlobalErrors(modelMock, bindingResultMock);

        final String result = testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, bindingResultMock);

        verify(paymentDetailsFormValidatorMock).validate(paymentDetailsFormMock, bindingResultMock);
        verify(testObj, never()).setupAddPaymentPage(modelMock);
        verify(testObj, never()).handleAndSaveAddresses(paymentDetailsFormMock);
        verify(testObj).prepareErrorView(modelMock, paymentDetailsFormMock);
        verify(testObj).resetDeclineCodeOnCart();
        assertEquals(BILLINGADDRESS_INPAYMENTFORM, result);
    }

    @Test
    public void testGetGlobalErrors() throws Exception {
        doReturn(true).when(testObj).addGlobalErrors(modelMock, bindingResultMock);

        final String result = testObj.getGlobalErrors(modelMock);

        verify(testObj).addGlobalErrors(modelMock, bindingResultMock);
        verify(sessionServiceMock).getAttribute(PAYMENT_DETAILS_FORM_ERRORS);
        assertEquals(GLOBAL_ERRORFRAGMENT, result);
    }

    @Test
    public void testGetNextGenHopSlot() throws Exception {
        doReturn(additionalAuthInfoMock).when(testObj).createAdditionalAuthInfo(anyBoolean(), eq(PAYMENT_METHOD));
        when(worldpayHostedOrderFacadeMock.redirectAuthorise(additionalAuthInfoMock)).thenReturn(paymentDataMock);
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PAYMENT_METHOD);

        final PaymentData result = testObj.getNextGenHopSlot();

        verify(sessionServiceMock).getAttribute(PAYMENT_DETAILS_FORM);
        verify(sessionServiceMock).removeAttribute(PAYMENT_DETAILS_FORM);
        verify(sessionServiceMock).removeAttribute(PAYMENT_DETAILS_FORM_ERRORS);
        assertEquals(paymentDataMock, result);
    }

    @Test
    public void nextGentHopSlotShouldReturnGlobalErrorsFragmentWhenFailsToAuthorise() throws Exception {
        doThrow(new WorldpayException(EXCEPTION_MESSAGE)).when(worldpayHostedOrderFacadeMock).redirectAuthorise(additionalAuthInfoMock);
        doReturn(additionalAuthInfoMock).when(testObj).createAdditionalAuthInfo(anyBoolean(), eq(PAYMENT_METHOD));
        when(themeSourceMock.getMessage(CHECKOUT_MULTI_LIGHTBOX_REDIRECT_ERROR, null, Locale.UK)).thenReturn(ERROR_MESSAGE);

        final PaymentData result = testObj.getNextGenHopSlot();

        verify(sessionServiceMock).getAttribute(PAYMENT_DETAILS_FORM);
        assertEquals(ERROR_MESSAGE, result.getParameters().get(ERROR_MESSAGE_KEY));
    }
}
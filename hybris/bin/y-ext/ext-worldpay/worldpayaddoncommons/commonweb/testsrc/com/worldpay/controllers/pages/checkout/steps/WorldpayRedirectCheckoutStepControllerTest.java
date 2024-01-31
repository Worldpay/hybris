package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.facades.WorldpayCartFacade;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.forms.validation.PaymentDetailsFormValidator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.user.UserFacade;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Calendar;
import java.util.Date;

import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayDirectCheckoutStepController.BIRTHDAY_DATE;
import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.*;
import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayRedirectCheckoutStepControllerTest {

    private static final String BILLING_ERROR_VIEW = "billingErrorView";
    private static final String BANK_CODE = "bankCode";
    private static final String PAYMENT_METHOD = "paymentMethod";
    private static final Date BIRTHDAY_DATE_VALUE = new Date(1990, Calendar.MAY, 17);

    @Spy
    @InjectMocks
    private WorldpayRedirectCheckoutStepController testObj = new WorldpayRedirectCheckoutStepController();
    @Mock
    private PaymentDetailsForm paymentDetailsFormMock;
    @Mock
    private Model modelMock;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private RedirectAttributes redirectAttrsMock;
    @Mock
    private UserFacade userFacadeMock;
    @Mock(name = "paymentDetailsFormValidator")
    private PaymentDetailsFormValidator paymentDetailsFormValidatorMock;
    @Mock
    private AddressForm addressFormMock;
    @Mock
    private APMConfigurationLookupService apmConfigurationServiceMock;
    @Mock
    private WorldpayCartFacade worldpayCartFacadeMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;

    @Before
    public void setUp() {
        doNothing().when(worldpayCartFacadeMock).resetDeclineCodeAndShopperBankOnCart(anyString());
        doNothing().when(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
        doReturn(false).when(testObj).addGlobalErrors(modelMock, bindingResultMock);

        when(paymentDetailsFormMock.getSaveInAccount()).thenReturn(true);
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(ONLINE.getMethodCode());
        when(paymentDetailsFormMock.getDateOfBirth()).thenReturn(BIRTHDAY_DATE_VALUE);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(true);
    }

    @Test
    public void addPaymentDetails_WhenCustomerIsAnonymousAndBillingAddressIsDifferentFromShippingAddressAndWhenFSEnabled_ShouldSaveTheAddressAndAddTheDOBRedirectAttribute() throws CMSItemNotFoundException {
        when(userFacadeMock.isAnonymousUser()).thenReturn(TRUE);

        testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrsMock);

        verify(paymentDetailsFormValidatorMock).validate(paymentDetailsFormMock, bindingResultMock);
        verify(worldpayCartFacadeMock).resetDeclineCodeAndShopperBankOnCart(null);
        verify(testObj).handleAndSaveAddresses(paymentDetailsFormMock);

        verify(redirectAttrsMock).addFlashAttribute(SAVE_PAYMENT_INFO, true);
        verify(redirectAttrsMock).addFlashAttribute(PAYMENT_METHOD_PARAM, ONLINE.getMethodCode());
        verify(redirectAttrsMock).addFlashAttribute(BIRTHDAY_DATE, BIRTHDAY_DATE_VALUE);
    }

    @Test
    public void addPaymentDetails_WhenCustomerIsAnonymousAndBillingAddressIsDifferentFromShippingAddressAndWhenFSDisabled_ShouldSaveTheAddressAndNotAddTheDOBRedirectAttribute() throws CMSItemNotFoundException {
        when(userFacadeMock.isAnonymousUser()).thenReturn(TRUE);
        when(worldpayPaymentCheckoutFacadeMock.isFSEnabled()).thenReturn(false);

        testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrsMock);

        verify(paymentDetailsFormValidatorMock).validate(paymentDetailsFormMock, bindingResultMock);
        verify(worldpayCartFacadeMock).resetDeclineCodeAndShopperBankOnCart(null);
        verify(testObj).handleAndSaveAddresses(paymentDetailsFormMock);

        verify(redirectAttrsMock).addFlashAttribute(SAVE_PAYMENT_INFO, true);
        verify(redirectAttrsMock).addFlashAttribute(PAYMENT_METHOD_PARAM, ONLINE.getMethodCode());
        verify(redirectAttrsMock, never()).addFlashAttribute(BIRTHDAY_DATE, BIRTHDAY_DATE_VALUE);
    }

    @Test
    public void addBankCodeForAPMPaymentMethod() throws CMSItemNotFoundException {
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PAYMENT_METHOD);
        when(apmConfigurationServiceMock.getAllApmPaymentTypeCodes()).thenReturn(singleton(PAYMENT_METHOD));
        when(paymentDetailsFormMock.getShopperBankCode()).thenReturn(BANK_CODE);

        testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrsMock);

        final InOrder inOrder = inOrder(paymentDetailsFormValidatorMock, testObj, worldpayCartFacadeMock, redirectAttrsMock);
        inOrder.verify(paymentDetailsFormValidatorMock).validate(paymentDetailsFormMock, bindingResultMock);
        inOrder.verify(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
        inOrder.verify(worldpayCartFacadeMock).resetDeclineCodeAndShopperBankOnCart(BANK_CODE);
        inOrder.verify(redirectAttrsMock).addFlashAttribute(SHOPPER_BANK_CODE, BANK_CODE);
    }

    @Test
    public void shouldRedirectToErrorViewIfErrorsInForm() throws CMSItemNotFoundException {
        doReturn(true).when(testObj).addGlobalErrors(modelMock, bindingResultMock);
        doReturn(BILLING_ERROR_VIEW).when(testObj).handleFormErrors(modelMock, paymentDetailsFormMock);

        final String result = testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrsMock);

        assertEquals(BILLING_ERROR_VIEW, result);
    }
}

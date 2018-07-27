package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.facades.WorldpayCartFacade;
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

    @Spy
    @InjectMocks
    private WorldpayRedirectCheckoutStepController testObj = new WorldpayRedirectCheckoutStepController();

    private static final String BILLING_ERROR_VIEW = "billingErrorView";
    private static final String BANK_CODE = "bankCode";
    private static final String PAYMENT_METHOD = "paymentMethod";

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

    @Before
    public void setUp() {
        doNothing().when(worldpayCartFacadeMock).resetDeclineCodeAndShopperBankOnCart(anyString());
        doNothing().when(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
        doReturn(false).when(testObj).addGlobalErrors(modelMock, bindingResultMock);

        when(paymentDetailsFormMock.getSaveInAccount()).thenReturn(true);
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(ONLINE.getMethodCode());
        when(paymentDetailsFormMock.getBillingAddress()).thenReturn(addressFormMock);
    }

    @Test
    public void addressShouldBeSavedIfCustomerIsAnonymousAndBillingAddressIsDifferentFromShippingAddress() throws CMSItemNotFoundException {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(FALSE);
        when(userFacadeMock.isAnonymousUser()).thenReturn(TRUE);

        testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrsMock);

        verify(paymentDetailsFormValidatorMock).validate(paymentDetailsFormMock, bindingResultMock);
        verify(worldpayCartFacadeMock).resetDeclineCodeAndShopperBankOnCart(null);
        verify(testObj).handleAndSaveAddresses(paymentDetailsFormMock);

        verify(redirectAttrsMock).addFlashAttribute(SAVE_PAYMENT_INFO, true);
        verify(redirectAttrsMock).addFlashAttribute(PAYMENT_METHOD_PARAM, ONLINE.getMethodCode());
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

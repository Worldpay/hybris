package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.WorldpayCartFacade;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.forms.validation.PaymentDetailsFormValidator;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
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

import javax.servlet.http.HttpServletRequest;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayIframeCheckoutStepControllerTest {

    private static final String PAYMENT_METHOD = "paymentMethod";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String ERROR_VIEW = "errorView";
    private static final String PAGE_REDIRECT = "pageRedirect";
    private static final String OTHER_PAYMENT = "otherPayment";
    private static final String BANK_CODE = "shopperBankCode";
    private static final String SHOW_NGPP_IFRAME = "showNGPPIframe";
    private static final String REDIRECT_PREFIX = "redirect:";
    private static final String REDIRECT_CHECKOUT_MULTI_WORLDPAY_IFRAME_ADD_PAYMENT_DETAILS = REDIRECT_PREFIX + "/checkout/multi/worldpay/iframe/add-payment-details";

    private static final String PAYMENT_DATA = "paymentData";
    private static final String SHOPPER_BANK_CODE = "shopperBankCode";
    private static final String PAYMENT_METHOD_PARAM = "paymentMethod";
    private static final String SAVE_PAYMENT_INFO = "savePaymentInfo";

    @Spy
    @InjectMocks
    private WorldpayIframeCheckoutStepController testObj;

    @Mock
    private RedirectAttributes redirectAttrbsMock;
    @Mock
    private Model modelMock;
    @Mock
    private PaymentDetailsForm paymentDetailsFormMock;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private PaymentDetailsFormValidator paymentDetailsFormValidatorMock;
    @Mock
    private AddressForm billingAddressMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private WorldpayHostedOrderFacade worldpayHostedOrderFacadeMock;
    @Mock
    private PaymentData paymentDataMock;
    @Mock
    private APMConfigurationLookupService apmConfigurationServiceMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;
    @Mock
    private WorldpayCartFacade worldpayCartFacadeMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacadeMock;
    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Before
    public void setUp() {
        when(apmConfigurationServiceMock.getAllApmPaymentTypeCodes()).thenReturn(singleton(OTHER_PAYMENT));
        when(paymentDetailsFormMock.getBillingAddress()).thenReturn(billingAddressMock);
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PAYMENT_METHOD);
        when(paymentDetailsFormMock.getShopperBankCode()).thenReturn(BANK_CODE);
        when(worldpayPaymentCheckoutFacade.hasBillingDetails()).thenReturn(true);
        doReturn(true).when(testObj).paymentMethodIsOnline(PAYMENT_METHOD);
        doReturn(additionalAuthInfoMock).when(testObj).createAdditionalAuthInfo(anyBoolean(), eq(PAYMENT_METHOD));
        doNothing().when(worldpayCartFacadeMock).resetDeclineCodeAndShopperBankOnCart(BANK_CODE);
        doNothing().when(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
    }

    @Test
    public void addPaymentDetailsShouldReturnIframePageWithSaveInAccountSetAsTrue() throws WorldpayException, CMSItemNotFoundException {
        when(paymentDetailsFormMock.getSaveInAccount()).thenReturn(Boolean.TRUE);
        when(worldpayHostedOrderFacadeMock.redirectAuthorise(additionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(paymentDataMock);
        when(additionalAuthInfoMock.getSaveCard()).thenReturn(Boolean.TRUE);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(any())).thenReturn(worldpayAdditionalInfoDataMock);

        final String result = testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, httpServletRequestMock, bindingResultMock, redirectAttrbsMock);

        verify(worldpayCartFacadeMock).resetDeclineCodeAndShopperBankOnCart(BANK_CODE);
        verify(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
        verify(redirectAttrbsMock).addFlashAttribute(PAYMENT_DATA, paymentDataMock);
        verify(redirectAttrbsMock, never()).addFlashAttribute(eq(SHOPPER_BANK_CODE), any());
        verify(redirectAttrbsMock).addFlashAttribute(SHOW_NGPP_IFRAME, true);
        verify(paymentDetailsFormValidatorMock).validate(paymentDetailsFormMock, bindingResultMock);
        verify(worldpayHostedOrderFacadeMock).createPaymentInfoModelOnCart(Boolean.TRUE);
        assertEquals(REDIRECT_CHECKOUT_MULTI_WORLDPAY_IFRAME_ADD_PAYMENT_DETAILS, result);
    }

    @Test
    public void addPaymentDetailsShouldReturnIframePageWithSaveInAccountSetAsFalse() throws WorldpayException, CMSItemNotFoundException {
        when(worldpayHostedOrderFacadeMock.redirectAuthorise(additionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(paymentDataMock);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(any())).thenReturn(worldpayAdditionalInfoDataMock);

        final String result = testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, httpServletRequestMock, bindingResultMock, redirectAttrbsMock);

        verify(worldpayCartFacadeMock).resetDeclineCodeAndShopperBankOnCart(BANK_CODE);
        verify(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
        verify(redirectAttrbsMock).addFlashAttribute(PAYMENT_DATA, paymentDataMock);
        verify(redirectAttrbsMock, never()).addFlashAttribute(eq(SHOPPER_BANK_CODE), any());
        verify(redirectAttrbsMock).addFlashAttribute(SHOW_NGPP_IFRAME, true);
        verify(paymentDetailsFormValidatorMock).validate(paymentDetailsFormMock, bindingResultMock);
        verify(worldpayHostedOrderFacadeMock).createPaymentInfoModelOnCart(Boolean.FALSE);
        assertEquals(REDIRECT_CHECKOUT_MULTI_WORLDPAY_IFRAME_ADD_PAYMENT_DETAILS, result);
    }

    @Test
    public void addPaymentDetailsShouldReturnBillingPageWithErrors() throws WorldpayException, CMSItemNotFoundException {
        when(worldpayHostedOrderFacadeMock.redirectAuthorise(additionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenThrow(new WorldpayException(EXCEPTION_MESSAGE));
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(any())).thenReturn(worldpayAdditionalInfoDataMock);

        final String result = testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, httpServletRequestMock, bindingResultMock, redirectAttrbsMock);

        verify(paymentDetailsFormValidatorMock).validate(paymentDetailsFormMock, bindingResultMock);
        verify(worldpayCartFacadeMock).resetDeclineCodeAndShopperBankOnCart(BANK_CODE);
        verify(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
        verify(redirectAttrbsMock, never()).addFlashAttribute(eq(PAYMENT_DATA), any());
        verify(redirectAttrbsMock, never()).addFlashAttribute(eq(PAYMENT_METHOD_PARAM), any());
        verify(redirectAttrbsMock, never()).addFlashAttribute(eq(SHOPPER_BANK_CODE), any());
        verify(redirectAttrbsMock).addFlashAttribute(SHOW_NGPP_IFRAME, false);
        assertEquals(REDIRECT_CHECKOUT_MULTI_WORLDPAY_IFRAME_ADD_PAYMENT_DETAILS, result);
    }

    @Test
    public void addPaymentDetailsShouldReturnErrorsWhenValidationFails() throws CMSItemNotFoundException {
        when(testObj.addGlobalErrors(modelMock, bindingResultMock)).thenReturn(true);
        doReturn(ERROR_VIEW).when(testObj).handleFormErrors(modelMock, paymentDetailsFormMock);

        testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, httpServletRequestMock, bindingResultMock, redirectAttrbsMock);

        verify(testObj).handleFormErrors(modelMock, paymentDetailsFormMock);
    }

    @Test
    public void addPaymentDetailsShouldReturnFullPageRedirectForAPM() throws CMSItemNotFoundException {
        doReturn(PAGE_REDIRECT).when(testObj).getRedirectToPaymentMethod();
        when(apmConfigurationServiceMock.getAllApmPaymentTypeCodes()).thenReturn(singleton(PAYMENT_METHOD));
        when(paymentDetailsFormMock.getSaveInAccount()).thenReturn(true);

        final String result = testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, httpServletRequestMock, bindingResultMock, redirectAttrbsMock);

        assertEquals(PAGE_REDIRECT, result);
        final InOrder inOrder = inOrder(paymentDetailsFormValidatorMock, testObj, worldpayCartFacadeMock, redirectAttrbsMock);
        inOrder.verify(paymentDetailsFormValidatorMock).validate(paymentDetailsFormMock, bindingResultMock);
        inOrder.verify(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
        inOrder.verify(worldpayCartFacadeMock).resetDeclineCodeAndShopperBankOnCart(BANK_CODE);
        inOrder.verify(redirectAttrbsMock).addFlashAttribute(SHOPPER_BANK_CODE, BANK_CODE);
        inOrder.verify(redirectAttrbsMock).addFlashAttribute(SAVE_PAYMENT_INFO, true);
    }
}

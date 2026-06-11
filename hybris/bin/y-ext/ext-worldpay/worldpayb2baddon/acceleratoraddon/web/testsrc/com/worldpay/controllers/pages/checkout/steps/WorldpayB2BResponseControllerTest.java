package com.worldpay.controllers.pages.checkout.steps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.REDIRECT_URL_CHOOSE_PAYMENT_METHOD;
import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static com.worldpay.enums.order.AuthorisedStatus.ERROR;
import static com.worldpay.enums.order.AuthorisedStatus.OPEN;
import static com.worldpay.enums.order.AuthorisedStatus.REFUSED;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.facades.payment.hosted.WorldpayAfterRedirectValidationFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.enums.CountryType;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@UnitTest
@ExtendWith(MockitoExtension.class)
class WorldpayB2BResponseControllerTest {

    private static final String COUNTRY_ISO_CODE = "GB";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String TITLE = "title";
    private static final String LINE_1 = "line1";
    private static final String LINE_2 = "line2";
    private static final String TOWN = "town";
    private static final String POSTAL_CODE = "postalCode";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String BILLING_ADDRESS_FORM = "wpBillingAddressForm";
    private static final String BILLING_ADDRESS_FORM_ENDPOINT = "billingAddressForm";
    private static final String PAYMENT_STATUS_PARAMETER_NAME = "paymentStatus";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String ORDER_CODE = "orderCode";
    private static final String ORDER_GUID = "orderGuid";
    private static final String ORDER_CONFIRMATION_PAGE_GUEST = "redirect:/checkout/orderConfirmation/" + ORDER_GUID;
    private static final String ORDER_CONFIRMATION_PAGE_LOGGED_USER = "redirect:/checkout/orderConfirmation/" + ORDER_CODE;
    private static final String CHOOSE_PAYMENT_REDIRECT_URL = REDIRECT_URL_CHOOSE_PAYMENT_METHOD + "?" + PAYMENT_STATUS_PARAMETER_NAME + "=%s";

    private final MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

    @Spy
    @InjectMocks
    private WorldpayB2BResponseController testObj;
    @Mock
    private Model modelMock;
    @Mock
    private AcceleratorCheckoutFacade acceleratorCheckoutFacadeMock;
    @Mock
    private CheckoutFacade checkoutFacadeMock;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointService;
    @Mock
    private WorldpayHostedOrderFacade worldpayHostedOrderFacadeMock;
    @Mock
    private OrderData orderDataMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;
    @Mock
    private RedirectAuthoriseResult redirectAuthoriseResultMock;
    @Mock(name = "redirectAuthoriseResultConverter")
    private Converter<Map<String, String>, RedirectAuthoriseResult> redirectAuthoriseResultConverterMock;
    @Mock
    private Set<AuthorisedStatus> apmErrorResponseStatusesMock;
    @Mock
    private RedirectAttributes redirectAttributesMock;
    @Mock
    private CartData cartDataMock;
    @Mock
    private AddressData deliveryAddressMock;
    @Mock
    private I18NFacade i18NFacade;
    @Mock
    private RegionData regionDataMock;
    @Captor
    private ArgumentCaptor<PaymentDetailsForm> paymentDetailsFormArgumentCaptor;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointServiceMock;
    @Mock
    private WorldpayAfterRedirectValidationFacade worldpayAfterRedirectValidationFacadeMock;

    @Mock
    private CountryData shippingCountryModelMock;

    @Test
    void doHandleHopResponse_ShouldCompleteRedirectAuthoriseAndPlaceOrder_WhenRedirectIsValidAndPaymentStatusIsAuthorised() throws InvalidCartException {
        mockHttpServletRequest.setParameter(PAYMENT_STATUS_PARAMETER_NAME, AUTHORISED.name());

        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(AUTHORISED);

        when(acceleratorCheckoutFacadeMock.hasValidCart()).thenReturn(true);

        doReturn(checkoutFacadeMock).when(testObj).getB2BCheckoutFacade();
        when(checkoutFacadeMock.placeOrder(any())).thenReturn(orderDataMock);

        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(false);
        when(orderDataMock.getCode()).thenReturn(ORDER_CODE);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(acceleratorCheckoutFacadeMock).hasValidCart();
        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock).placeOrder(argThat(placeOrderData -> Boolean.TRUE.equals(placeOrderData.getTermsCheck())));
        verify(checkoutCustomerStrategyMock).isAnonymousCheckout();
        verify(redirectAttributesMock, never()).addFlashAttribute(eq(PAYMENT_STATUS_PARAMETER_NAME), any());

        assertEquals(ORDER_CONFIRMATION_PAGE_LOGGED_USER, result);
    }

    @Test
    void doHandleHopResponse_ShouldRedirectToChoosePaymentMethod_WhenPaymentStatusIsRefused() throws InvalidCartException {
        mockHttpServletRequest.setParameter(PAYMENT_STATUS_PARAMETER_NAME, REFUSED.name());

        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);

        when(acceleratorCheckoutFacadeMock.hasValidCart()).thenReturn(true);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(REFUSED);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(acceleratorCheckoutFacadeMock).hasValidCart();
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, REFUSED.name());
        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock, never()).placeOrder(any());

        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, REFUSED.name()), result);
    }

    @Test
    void doHandleHopResponse_ShouldRedirectToChoosePaymentMethodWithError_WhenRedirectValidationFails() throws InvalidCartException {
        mockHttpServletRequest.setParameter(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());

        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(false);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayAfterRedirectValidationFacadeMock).validateRedirectResponse(anyMap());
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        verify(acceleratorCheckoutFacadeMock, never()).hasValidCart();
        verify(redirectAuthoriseResultMock, never()).getPaymentStatus();
        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock, never()).placeOrder(any());

        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.name()), result);
    }

    @Test
    void doHandlePendingHopResponse_ShouldCompleteRedirectAuthoriseAndPlaceOrder_WhenRedirectIsValidAndPaymentStatusIsOpen() throws InvalidCartException {
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);

        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(OPEN);
        when(apmErrorResponseStatusesMock.contains(OPEN)).thenReturn(false);

        doReturn(checkoutFacadeMock).when(testObj).getB2BCheckoutFacade();
        when(checkoutFacadeMock.placeOrder(any())).thenReturn(orderDataMock);

        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(true);
        when(orderDataMock.getGuid()).thenReturn(ORDER_GUID);

        final String result = testObj.doHandlePendingHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayAfterRedirectValidationFacadeMock).validateRedirectResponse(anyMap());
        verify(redirectAuthoriseResultMock).setPending(true);
        verify(apmErrorResponseStatusesMock).contains(OPEN);
        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock).placeOrder(argThat(placeOrderData -> Boolean.TRUE.equals(placeOrderData.getTermsCheck())));
        verify(redirectAttributesMock, never()).addFlashAttribute(eq(PAYMENT_STATUS_PARAMETER_NAME), any());

        assertEquals(ORDER_CONFIRMATION_PAGE_GUEST, result);
    }

    @Test
    void doHandlePendingHopResponse_ShouldRedirectToChoosePaymentMethodWithError_WhenPlaceOrderThrowsInvalidCartException() throws InvalidCartException {
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);

        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(OPEN);
        when(apmErrorResponseStatusesMock.contains(OPEN)).thenReturn(false);

        doReturn(checkoutFacadeMock).when(testObj).getB2BCheckoutFacade();
        when(checkoutFacadeMock.placeOrder(any())).thenThrow(new InvalidCartException(EXCEPTION_MESSAGE));

        when(modelMock.asMap()).thenReturn(new HashMap<>());

        final String result = testObj.doHandlePendingHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayAfterRedirectValidationFacadeMock).validateRedirectResponse(anyMap());
        verify(redirectAuthoriseResultMock).setPending(true);
        verify(apmErrorResponseStatusesMock).contains(OPEN);
        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock).placeOrder(argThat(placeOrderData -> Boolean.TRUE.equals(placeOrderData.getTermsCheck())));
        verify(modelMock).addAttribute(eq(GlobalMessages.ERROR_MESSAGES_HOLDER), anyList());
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());

        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.name()), result);
    }

    @Test
    void doHandlePendingHopResponse_ShouldNotCompleteRedirectAuthoriseOrPlaceOrder_WhenPaymentStatusIsApmError() throws InvalidCartException {
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);

        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(ERROR);
        when(apmErrorResponseStatusesMock.contains(ERROR)).thenReturn(true);

        final String result = testObj.doHandlePendingHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayAfterRedirectValidationFacadeMock).validateRedirectResponse(anyMap());
        verify(redirectAuthoriseResultMock).setPending(true);
        verify(apmErrorResponseStatusesMock).contains(ERROR);
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock, never()).placeOrder(any());

        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.name()), result);
    }

    @Test
    void doHandlePendingHopResponse_ShouldRedirectToChoosePaymentMethodWithError_WhenRedirectValidationFails() throws InvalidCartException {
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(false);

        final String result = testObj.doHandlePendingHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayAfterRedirectValidationFacadeMock).validateRedirectResponse(anyMap());
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        verify(redirectAuthoriseResultConverterMock, never()).convert(anyMap());
        verify(redirectAuthoriseResultMock, never()).setPending(true);
        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock, never()).placeOrder(any());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.name()), result);
    }

    @Test
    void getCountryAddressForm_ShouldPopulateBillingAddressFromDeliveryAddress_WhenUseDeliveryAddressIsTrue() {
        final List<RegionData> regions = singletonList(regionDataMock);

        when(acceleratorCheckoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(i18NFacade.getRegionsForCountryIso(COUNTRY_ISO_CODE)).thenReturn(regions);

        when(acceleratorCheckoutFacadeMock.getCountries(CountryType.SHIPPING)).thenReturn(List.of(shippingCountryModelMock));
        when(cartDataMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);

        when(deliveryAddressMock.getTitleCode()).thenReturn(TITLE);
        when(deliveryAddressMock.getFirstName()).thenReturn(FIRST_NAME);
        when(deliveryAddressMock.getLastName()).thenReturn(LAST_NAME);
        when(deliveryAddressMock.getLine1()).thenReturn(LINE_1);
        when(deliveryAddressMock.getLine2()).thenReturn(LINE_2);
        when(deliveryAddressMock.getTown()).thenReturn(TOWN);
        when(deliveryAddressMock.getPostalCode()).thenReturn(POSTAL_CODE);
        when(deliveryAddressMock.getPhone()).thenReturn(PHONE_NUMBER);

        when(worldpayAddonEndpointService.getBillingAddressForm()).thenReturn(BILLING_ADDRESS_FORM_ENDPOINT);

        final String result = testObj.getCountryAddressForm(COUNTRY_ISO_CODE, true, modelMock);

        verify(modelMock).addAttribute("supportedCountries", List.of(shippingCountryModelMock));
        verify(modelMock).addAttribute("regions", regions);
        verify(modelMock).addAttribute("country", COUNTRY_ISO_CODE);
        verify(modelMock).addAttribute(eq(BILLING_ADDRESS_FORM), paymentDetailsFormArgumentCaptor.capture());

        final PaymentDetailsForm paymentDetailsForm = paymentDetailsFormArgumentCaptor.getValue();
        final AddressForm billingAddress = paymentDetailsForm.getBillingAddress();

        assertEquals(FIRST_NAME, billingAddress.getFirstName());
        assertEquals(LAST_NAME, billingAddress.getLastName());
        assertEquals(LINE_1, billingAddress.getLine1());
        assertEquals(LINE_2, billingAddress.getLine2());
        assertEquals(TOWN, billingAddress.getTownCity());
        assertEquals(POSTAL_CODE, billingAddress.getPostcode());
        assertEquals(COUNTRY_ISO_CODE, billingAddress.getCountryIso());
        assertEquals(PHONE_NUMBER, billingAddress.getPhone());
        assertEquals(TITLE, billingAddress.getTitleCode());

        assertThat(result).isEqualTo(BILLING_ADDRESS_FORM_ENDPOINT);
    }
}

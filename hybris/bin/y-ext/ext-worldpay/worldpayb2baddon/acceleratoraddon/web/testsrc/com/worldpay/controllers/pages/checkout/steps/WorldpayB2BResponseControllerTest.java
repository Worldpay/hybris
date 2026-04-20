package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.facades.payment.hosted.WorldpayAfterRedirectValidationFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.REDIRECT_URL_CHOOSE_PAYMENT_METHOD;
import static com.worldpay.enums.order.AuthorisedStatus.ERROR;
import static com.worldpay.enums.order.AuthorisedStatus.REFUSED;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayB2BResponseControllerTest {

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
    private static final String ORDER_GUID = "orderGuid";
    private static final String ORDER_CONFIRMATION_PAGE = "redirect:/checkout/orderConfirmation/" + ORDER_GUID;
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

    @Before
    public void setUp() {
        when(apmErrorResponseStatusesMock.contains(AuthorisedStatus.ERROR)).thenReturn(true);
        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(Boolean.TRUE);
        when(orderDataMock.getGuid()).thenReturn(ORDER_GUID);
        when(redirectAuthoriseResultConverterMock.convert(anyMap())).thenReturn(redirectAuthoriseResultMock);
        when(acceleratorCheckoutFacadeMock.hasValidCart()).thenReturn(true);
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(true);
        lenient().when(worldpayAddonEndpointServiceMock.getHostedOrderPostPage()).thenReturn("hostedOrderPostPage");
        mockHttpServletRequest.setParameter(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        doReturn(checkoutFacadeMock).when(testObj).getB2BCheckoutFacade();
    }

    @Test
    public void shouldPopulateBillingAddressWithShippingAddress() {
        when(acceleratorCheckoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
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
        when(acceleratorCheckoutFacadeMock.getCountries(CountryType.SHIPPING)).thenReturn(List.of(shippingCountryModelMock));
        final List<RegionData> regionDataInfos = singletonList(regionDataMock);
        when(i18NFacade.getRegionsForCountryIso(COUNTRY_ISO_CODE)).thenReturn(regionDataInfos);

        final String result = testObj.getCountryAddressForm(COUNTRY_ISO_CODE, true, modelMock);

        verify(modelMock).addAttribute(eq(BILLING_ADDRESS_FORM), paymentDetailsFormArgumentCaptor.capture());
        verify(modelMock).addAttribute("supportedCountries", List.of(shippingCountryModelMock));
        verify(modelMock).addAttribute("regions", regionDataInfos);
        verify(modelMock).addAttribute("country", COUNTRY_ISO_CODE);

        final PaymentDetailsForm paymentDetailsForm = paymentDetailsFormArgumentCaptor.getValue();
        assertEquals(FIRST_NAME, paymentDetailsForm.getBillingAddress().getFirstName());
        assertEquals(LAST_NAME, paymentDetailsForm.getBillingAddress().getLastName());
        assertEquals(LINE_1, paymentDetailsForm.getBillingAddress().getLine1());
        assertEquals(LINE_2, paymentDetailsForm.getBillingAddress().getLine2());
        assertEquals(TOWN, paymentDetailsForm.getBillingAddress().getTownCity());
        assertEquals(POSTAL_CODE, paymentDetailsForm.getBillingAddress().getPostcode());
        assertEquals(COUNTRY_ISO_CODE, paymentDetailsForm.getBillingAddress().getCountryIso());
        assertEquals(PHONE_NUMBER, paymentDetailsForm.getBillingAddress().getPhone());
        assertEquals(TITLE, paymentDetailsForm.getBillingAddress().getTitleCode());

        assertThat(result).isEqualTo(BILLING_ADDRESS_FORM_ENDPOINT);
    }

    @Test
    public void doHandleHopResponseREFUSEDShouldRedirectToChoosePaymentMethod() throws InvalidCartException {
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(REFUSED);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock, never()).placeOrder(any());
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, REFUSED.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, REFUSED.name()), result);
    }

    @Test
    public void doHandleHopResponseAUTHORISEDShouldCompleteRedirectAndPlaceOrderWhenResponseIsValid() throws InvalidCartException {
        when(checkoutFacadeMock.placeOrder(any())).thenReturn(orderDataMock);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(AuthorisedStatus.AUTHORISED);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock).placeOrder(any());
        assertEquals(ORDER_CONFIRMATION_PAGE, result);
    }

    @Test
    public void doHandleHopResponseShouldNOTCompleteRedirectAndNOTPlaceOrderWhenResponseIsNotValid() throws InvalidCartException {
        when(worldpayAfterRedirectValidationFacadeMock.validateRedirectResponse(anyMap())).thenReturn(false);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock, never()).placeOrder(any());
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.name()), result);
    }

    @Test
    public void doHandleHopResponseNotAUTHORISEDShouldNotCompleteRedirectAndNotPlaceOrderWhenResponseIsValid() throws InvalidCartException {
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(REFUSED);

        final String result = testObj.doHandleHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock, never()).placeOrder(any());
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, REFUSED.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, REFUSED.name()), result);
    }

    @Test
    public void doHandlePendingHopResponseOPENShouldCompleteRedirectForAPMAndPlaceOrderWhenResponseIsValid() throws InvalidCartException {
        when(checkoutFacadeMock.placeOrder(any())).thenReturn(orderDataMock);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(AuthorisedStatus.OPEN);

        final String result = testObj.doHandlePendingHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock).placeOrder(any());
        verify(redirectAuthoriseResultMock).setPending(true);
        assertEquals(ORDER_CONFIRMATION_PAGE, result);
    }

    @Test
    public void doHandlePendingHopResponseNotOPENShouldNotCompleteRedirectForAPMAndNotPlaceOrderWhenResponseIsValid() throws InvalidCartException {
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(AuthorisedStatus.ERROR);

        final String result = testObj.doHandlePendingHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock, never()).placeOrder(any());
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.name()), result);
    }

    @Test
    public void doHandlePendingHopResponseNotERRORShouldNOTCompleteRedirectForAPMAndNotPlaceOrderWhenResponseIsNotValid() throws InvalidCartException {
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(ERROR);

        final String result = testObj.doHandlePendingHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock, never()).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(checkoutFacadeMock, never()).placeOrder(any());
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.name()), result);
    }

    @Test
    public void doHandlePendingHopResponseRaisesExceptionWhenPlacingOrderAndPaymentStatusSet() throws InvalidCartException {
        when(checkoutFacadeMock.placeOrder(any())).thenThrow(new InvalidCartException(EXCEPTION_MESSAGE));

        final String result = testObj.doHandlePendingHopResponse(mockHttpServletRequest, modelMock, redirectAttributesMock);

        verify(worldpayHostedOrderFacadeMock).completeRedirectAuthorise(redirectAuthoriseResultMock);
        verify(redirectAttributesMock).addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, ERROR.name());
        assertEquals(format(CHOOSE_PAYMENT_REDIRECT_URL, ERROR.name()), result);
    }
}

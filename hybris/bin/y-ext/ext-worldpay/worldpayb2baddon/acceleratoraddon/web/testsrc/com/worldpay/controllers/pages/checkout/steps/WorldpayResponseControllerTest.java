package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.worldpay.controllers.pages.checkout.steps.WorldpayResponseController.BILLING_ADDRESS_FORM;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayResponseControllerTest {

    private static final String COUNTRY_ISO_CODE = "GB";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String LINE_1 = "line1";
    private static final String LINE_2 = "line2";
    private static final String TOWN = "town";
    private static final String POSTAL_CODE = "postalCode";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String ORDER_GUID = "orderGuid";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String BILLING_ADDRESS_FORM_ENDPOINT = "billingAddressForm";

    @Spy
    @InjectMocks
    private WorldpayResponseController testObj = new WorldpayResponseController();
    @Mock
    private Model modelMock;
    @Mock
    private AcceleratorCheckoutFacade checkoutFacadeMock;
    @Mock
    private WorldpayHostedOrderFacade worldpayHostedOrderFacadeMock;
    @Mock
    private OrderData orderDataMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;
    @Mock
    private RedirectAuthoriseResult redirectAuthoriseResultMock;
    @Mock
    private Converter<Map<String, String>, RedirectAuthoriseResult> redirectAuthoriseResultConverterMock;
    @Mock
    private Set<AuthorisedStatus> apmErrorResponseStatusesMock;
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
    private OrderModel orderModelMock;
    @Mock(name = "orderConverter")
    private Converter<AbstractOrderModel, OrderData> orderConverterMock;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointService;

    @Before
    public void setUp() {
        when(apmErrorResponseStatusesMock.contains(AuthorisedStatus.ERROR)).thenReturn(true);
        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(Boolean.TRUE);
        when(orderDataMock.getGuid()).thenReturn(ORDER_GUID);
        when(redirectAuthoriseResultConverterMock.convert(anyMapOf(String.class, String.class))).thenReturn(redirectAuthoriseResultMock);
        when(checkoutFacadeMock.hasValidCart()).thenReturn(true);
        when(orderConverterMock.convert(orderModelMock)).thenReturn(orderDataMock);
        when(redirectAuthoriseResultMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayHostedOrderFacadeMock.validateRedirectResponse(anyMapOf(String.class, String.class))).thenReturn(true);
        when(worldpayAddonEndpointService.getBillingAddressForm()).thenReturn(BILLING_ADDRESS_FORM_ENDPOINT);
    }

    @Test
    public void shouldPopulateBillingAddressWithShippingAddress() {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cartDataMock.getDeliveryAddress()).thenReturn(deliveryAddressMock);
        when(deliveryAddressMock.getFirstName()).thenReturn(FIRST_NAME);
        when(deliveryAddressMock.getLastName()).thenReturn(LAST_NAME);
        when(deliveryAddressMock.getLine1()).thenReturn(LINE_1);
        when(deliveryAddressMock.getLine2()).thenReturn(LINE_2);
        when(deliveryAddressMock.getTown()).thenReturn(TOWN);
        when(deliveryAddressMock.getPostalCode()).thenReturn(POSTAL_CODE);
        when(deliveryAddressMock.getPhone()).thenReturn(PHONE_NUMBER);

        final List<RegionData> regionDataInfos = singletonList(regionDataMock);
        when(i18NFacade.getRegionsForCountryIso(COUNTRY_ISO_CODE)).thenReturn(regionDataInfos);

        testObj.getCountryAddressForm(COUNTRY_ISO_CODE, true, modelMock);

        verify(modelMock).addAttribute(eq(BILLING_ADDRESS_FORM), paymentDetailsFormArgumentCaptor.capture());
        verify(modelMock).addAttribute("supportedCountries", checkoutFacadeMock.getDeliveryCountries());
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
    }
}

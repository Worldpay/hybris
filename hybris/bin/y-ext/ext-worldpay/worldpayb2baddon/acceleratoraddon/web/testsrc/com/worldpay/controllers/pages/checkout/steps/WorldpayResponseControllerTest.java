package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.enums.CountryType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayResponseControllerTest {

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

    @Spy
    @InjectMocks
    private WorldpayResponseController testObj;
    @Mock
    private Model modelMock;
    @Mock
    private AcceleratorCheckoutFacade checkoutFacadeMock;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointService;

    @Mock
    private CartData cartDataMock;
    @Mock
    private AddressData deliveryAddressMock;
    @Mock
    private I18NFacade i18NFacade;
    @Mock
    private RegionData regionDataMock;
    @Mock
    private CountryData shippingCountryModelMock;
    @Captor
    private ArgumentCaptor<PaymentDetailsForm> paymentDetailsFormArgumentCaptor;

    @Test
    public void shouldPopulateBillingAddressWithShippingAddress() {
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
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
        when(checkoutFacadeMock.getCountries(CountryType.SHIPPING)).thenReturn(singletonList(shippingCountryModelMock));
        final List<RegionData> regionDataInfos = singletonList(regionDataMock);
        when(i18NFacade.getRegionsForCountryIso(COUNTRY_ISO_CODE)).thenReturn(regionDataInfos);

        final String result = testObj.getCountryAddressForm(COUNTRY_ISO_CODE, true, modelMock);

        verify(modelMock).addAttribute(eq(BILLING_ADDRESS_FORM), paymentDetailsFormArgumentCaptor.capture());
        verify(modelMock).addAttribute("supportedCountries", singletonList(shippingCountryModelMock));
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
}

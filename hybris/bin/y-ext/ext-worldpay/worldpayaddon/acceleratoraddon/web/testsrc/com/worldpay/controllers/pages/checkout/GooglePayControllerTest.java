package com.worldpay.controllers.pages.checkout;

import com.worldpay.data.GooglePayAdditionalAuthInfo;
import com.worldpay.data.GooglePayAddressData;
import com.worldpay.data.GooglePayAuthorisationRequest;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.InvalidCartException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GooglePayControllerTest {

    @InjectMocks
    private GooglePayController testObj;

    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;
    @Mock
    private UserFacade userFacadeMock;
    @Mock
    private I18NFacade i18NFacadeMock;

    @Mock
    private CustomerModel customer;
    @Captor
    private ArgumentCaptor<AddressData> addressCaptor;

    @Before
    public void setup() {
        when(checkoutCustomerStrategyMock.getCurrentUserForCheckout()).thenReturn(customer);
    }

    @Test
    public void testAuthoriseOrder() throws WorldpayException, InvalidCartException {
        final GooglePayAuthorisationRequest authorisationRequest = new GooglePayAuthorisationRequest();
        final GooglePayAdditionalAuthInfo token = new GooglePayAdditionalAuthInfo();
        token.setProtocolVersion("2");
        token.setSignature("lorem ipsum dolor");
        token.setSignedMessage("wop wop wop");
        authorisationRequest.setToken(token);
        authorisationRequest.setSaved(Boolean.TRUE);

        final GooglePayAddressData billingAddress = new GooglePayAddressData();
        billingAddress.setAddress1("Av Aragon 30");
        billingAddress.setAddress2("Planta 8 oficina 999");
        billingAddress.setAdministrativeArea("Valencia");
        billingAddress.setCountryCode("ES");
        billingAddress.setName("James Bond");
        billingAddress.setPostalCode("46015");

        authorisationRequest.setBillingAddress(billingAddress);

        testObj.authoriseOrder(authorisationRequest);

        verify(i18NFacadeMock).getCountryForIsocode("ES");
        verify(checkoutCustomerStrategyMock).getCurrentUserForCheckout();
        verify(userFacadeMock).addAddress(any(AddressData.class));
        verify(worldpayDirectOrderFacadeMock).authoriseGooglePayDirect(authorisationRequest.getToken());
        verify(worldpayPaymentCheckoutFacadeMock).setBillingDetails(any(AddressData.class));
        assertTrue(token.getSaveCard());
    }

    @Test
    public void testAddressPopulation() {
        final GooglePayAddressData billingAddress = new GooglePayAddressData();
        billingAddress.setAddress1("Av Aragon 30");
        billingAddress.setAddress2("Planta 8 oficina 999");
        billingAddress.setAddress3("lol");
        billingAddress.setLocality("Valencia");
        billingAddress.setCountryCode("ES");
        billingAddress.setName("James Bond");
        billingAddress.setPostalCode("46015");

        final String email = "test@test.com";
        when(customer.getContactEmail()).thenReturn(email);
        final CountryData country = mock(CountryData.class);
        when(i18NFacadeMock.getCountryForIsocode("ES")).thenReturn(country);

        testObj.saveBillingAddresses(billingAddress);

        verify(checkoutCustomerStrategyMock).getCurrentUserForCheckout();
        verify(userFacadeMock).addAddress(addressCaptor.capture());
        verifyAddress(addressCaptor, country);

        verify(worldpayPaymentCheckoutFacadeMock).setBillingDetails(addressCaptor.capture());
        verifyAddress(addressCaptor, country);
    }

    @Test
    public void testSetRegion() {
        final AddressData addressData = mock(AddressData.class);
        final RegionData region = mock(RegionData.class);
        final String countryCode = "US";
        final String regionCode = "CA";
        when(i18NFacadeMock.getRegion(countryCode, regionCode)).thenReturn(region);

        final GooglePayAddressData address = new GooglePayAddressData();
        address.setCountryCode(countryCode);
        address.setAdministrativeArea(regionCode);

        testObj.setRegion(addressData, address);

        verify(addressData).setRegion(region);
    }

    @Test
    public void testSetRegionNotFound() {
        final String countryCode = "ES";
        final String regionCode = "Valencia";
        when(i18NFacadeMock.getRegion(countryCode, regionCode)).thenReturn(null);

        final GooglePayAddressData address = new GooglePayAddressData();
        address.setCountryCode(countryCode);
        address.setAdministrativeArea(regionCode);

        final AddressData addressData = mock(AddressData.class);

        testObj.setRegion(addressData, address);

        verifyNoInteractions(addressData);
    }

    private void verifyAddress(final ArgumentCaptor<AddressData> addressCaptor, final CountryData country) {
        assertEquals("James Bond", addressCaptor.getValue().getFirstName());
        assertNull(addressCaptor.getValue().getLastName());
        assertEquals(country, addressCaptor.getValue().getCountry());
        assertEquals("test@test.com", addressCaptor.getValue().getEmail());
        assertTrue(addressCaptor.getValue().isBillingAddress());
        assertEquals("Av Aragon 30", addressCaptor.getValue().getLine1());
        assertEquals("Planta 8 oficina 999 lol", addressCaptor.getValue().getLine2());
        assertEquals("46015", addressCaptor.getValue().getPostalCode());
        assertEquals("Valencia", addressCaptor.getValue().getTown());
    }
}

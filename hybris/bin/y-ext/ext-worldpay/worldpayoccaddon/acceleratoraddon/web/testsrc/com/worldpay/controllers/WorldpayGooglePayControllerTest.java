package com.worldpay.controllers;

import com.worldpay.config.merchant.GooglePayConfigData;
import com.worldpay.data.GooglePayAdditionalAuthInfo;
import com.worldpay.data.GooglePayAddressData;
import com.worldpay.data.GooglePayAuthorisationRequest;
import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.payment.DirectResponseData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayGooglePayControllerTest {
    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;
    @Mock
    private I18NFacade i18NFacadeMock;
    @Mock
    private UserFacade userFacadeMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;

    @Spy
    @InjectMocks
    public WorldpayGooglePayController testObj;

    @Mock
    private GooglePayConfigData googlePaySettingsMock;
    @Mock
    private HttpServletResponse responseMock;

    @Mock
    private CustomerModel customer;
    @Captor
    private ArgumentCaptor<AddressData> addressCaptor;
    @Mock
    private PlaceOrderResponseWsDTO placeOrderResponseMock;
    @Mock
    private DirectResponseData directResponseMock;

    @Before
    public void setup() throws WorldpayConfigurationException {
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData().getGooglePaySettings()).thenReturn(googlePaySettingsMock);
        when(checkoutCustomerStrategyMock.getCurrentUserForCheckout()).thenReturn(customer);

        doReturn(placeOrderResponseMock)
            .when(testObj)
            .handleDirectResponse(directResponseMock, responseMock, FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test
    public void getGooglePaySettings_shouldReturnSiteSettings() {
        final ResponseEntity<GooglePayConfigData> result = testObj.getGooglePaySettings();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(googlePaySettingsMock);
    }

    @Test
    public void authoriseOrder_shouldPopulateBillingAddressAndAuthoriseOrder() throws WorldpayException, InvalidCartException {
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
        billingAddress.setLocality("Valencia");
        billingAddress.setAdministrativeArea("Valencia");
        billingAddress.setCountryCode("ES");
        billingAddress.setName("James Bond");
        billingAddress.setPostalCode("46015");

        final String email = "test@test.com";
        when(customer.getContactEmail()).thenReturn(email);

        final CountryData country = mock(CountryData.class);
        when(i18NFacadeMock.getCountryForIsocode("ES")).thenReturn(country);

        authorisationRequest.setBillingAddress(billingAddress);

        when(worldpayDirectOrderFacadeMock.authoriseGooglePayDirect(token)).thenReturn(directResponseMock);

        testObj.authoriseOrder(authorisationRequest, FieldSetLevelHelper.DEFAULT_LEVEL, responseMock);

        verify(i18NFacadeMock).getCountryForIsocode("ES");
        verify(checkoutCustomerStrategyMock).getCurrentUserForCheckout();
        verify(userFacadeMock).addAddress(any(AddressData.class));
        verify(worldpayDirectOrderFacadeMock).authoriseGooglePayDirect(authorisationRequest.getToken());
        verify(worldpayPaymentCheckoutFacadeMock).setBillingDetails(any(AddressData.class));
        verify(userFacadeMock).addAddress(addressCaptor.capture());
        verifyAddress(addressCaptor, country);

        assertTrue(token.getSaveCard());
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

        verifyZeroInteractions(addressData);
    }

    private void verifyAddress(final ArgumentCaptor<AddressData> addressCaptor, final CountryData country) {
        assertEquals("James Bond", addressCaptor.getValue().getFirstName());
        assertEquals(StringUtils.EMPTY, addressCaptor.getValue().getLastName());
        assertEquals(country, addressCaptor.getValue().getCountry());
        assertEquals("test@test.com", addressCaptor.getValue().getEmail());
        assertTrue(addressCaptor.getValue().isBillingAddress());
        assertEquals("Av Aragon 30", addressCaptor.getValue().getLine1());
        assertEquals("Planta 8 oficina 999", addressCaptor.getValue().getLine2());
        assertEquals("46015", addressCaptor.getValue().getPostalCode());
        assertEquals("Valencia", addressCaptor.getValue().getTown());
    }
}


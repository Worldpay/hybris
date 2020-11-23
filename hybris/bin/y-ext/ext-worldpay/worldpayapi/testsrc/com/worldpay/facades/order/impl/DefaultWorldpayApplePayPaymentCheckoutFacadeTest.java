package com.worldpay.facades.order.impl;

import com.worldpay.config.merchant.ApplePayConfigData;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.data.ApplePayPaymentContact;
import com.worldpay.data.ApplePayPaymentRequest;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.payment.applepay.ValidateMerchantRequestDTO;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayApplePayPaymentCheckoutFacadeTest {
    @InjectMocks
    private DefaultWorldpayApplePayPaymentCheckoutFacade testObj;

    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;
    @Mock
    private CustomerModel customerMock;
    @Mock
    private I18NFacade i18NFacadeMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;
    @Mock
    private UserFacade userFacadeMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;
    @Mock
    private ApplePayConfigData applePayConfigDataMock;
    @Mock
    private Converter<ApplePayConfigData, ValidateMerchantRequestDTO> applePayConfigDataToValidateMerchantRequestDTOConverterMock;
    @Captor
    private ArgumentCaptor<AddressData> addressCaptor;
    @Mock
    private CartData cartMock;
    @Mock
    private PriceData totalPriceMock;

    @Before
    public void setup() {
        when(checkoutCustomerStrategyMock.getCurrentUserForCheckout()).thenReturn(customerMock);
    }

    @Test
    public void testSetRegion() {
        final AddressData addressData = mock(AddressData.class);
        final RegionData region = mock(RegionData.class);
        final String countryCode = "US";
        final String regionCode = "CA";
        when(i18NFacadeMock.getRegion(countryCode, regionCode)).thenReturn(region);

        final ApplePayPaymentContact address = new ApplePayPaymentContact();
        address.setCountryCode(countryCode);
        address.setAdministrativeArea(regionCode);

        testObj.setRegion(addressData, address);

        verify(addressData).setRegion(region);
    }

    @Test
    public void testSetRegionNotFound() {
        final String countryCode = "ES";
        final String regionCode = "Valencia";
        when(i18NFacadeMock.getRegion(countryCode, regionCode)).thenThrow(new UnknownIdentifierException("region not found"));

        final ApplePayPaymentContact address = new ApplePayPaymentContact();
        address.setCountryCode(countryCode);
        address.setAdministrativeArea(regionCode);

        final AddressData addressData = mock(AddressData.class);

        testObj.setRegion(addressData, address);

        verifyZeroInteractions(addressData);
    }

    @Test
    public void testAddressPopulation() {
        doNothing().when(worldpayPaymentCheckoutFacadeMock).setBillingDetails(any(AddressData.class));
        final ApplePayPaymentContact billingAddress = new ApplePayPaymentContact();
        billingAddress.setAddressLines(Arrays.asList("Av Aragon 30", "Planta 8 oficina 999"));

        billingAddress.setAdministrativeArea("Valencia");
        billingAddress.setCountryCode("es");
        billingAddress.setGivenName("James");
        billingAddress.setFamilyName("Bond");
        billingAddress.setPostalCode("46015");

        final String email = "test@test.com";
        when(customerMock.getContactEmail()).thenReturn(email);
        final CountryData country = mock(CountryData.class);
        when(i18NFacadeMock.getCountryForIsocode("ES")).thenReturn(country);

        testObj.saveBillingAddresses(billingAddress);

        verify(checkoutCustomerStrategyMock).getCurrentUserForCheckout();

        verify(userFacadeMock).addAddress(addressCaptor.capture());
        verifyAddress(addressCaptor, country);

        verify(worldpayPaymentCheckoutFacadeMock).setBillingDetails(addressCaptor.capture());
        verifyAddress(addressCaptor, country);
    }

    private void verifyAddress(final ArgumentCaptor<AddressData> addressCaptor, final CountryData country) {
        final AddressData addressData = addressCaptor.getValue();
        assertEquals("James", addressData.getFirstName());
        assertEquals("Bond", addressData.getLastName());
        assertEquals(country, addressData.getCountry());
        assertEquals("test@test.com", addressData.getEmail());
        assertTrue(addressData.isBillingAddress());
        assertEquals("Av Aragon 30", addressData.getLine1());
        assertEquals("Planta 8 oficina 999", addressData.getLine2());
        assertEquals("46015", addressData.getPostalCode());
        assertEquals("Valencia", addressData.getTown());
    }

    @Test
    public void testGetValidateMerchantRequestDTO() {
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData().getApplePaySettings()).thenReturn(applePayConfigDataMock);
        testObj.getValidateMerchantRequestDTO();
        verify(applePayConfigDataToValidateMerchantRequestDTOConverterMock).convert(applePayConfigDataMock);
    }

    @Test
    public void getApplePayPaymentRequest_shouldCreatePaymentRequest_whenGivenSessionCart() {
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData().getApplePaySettings()).thenReturn(applePayConfigDataMock);

        final List<String> merchantCapabilities = Arrays.asList("supports3DS", "supportsDebit");
        when(applePayConfigDataMock.getMerchantCapabilities())
            .thenReturn(merchantCapabilities);

        when(applePayConfigDataMock.getCountryCode()).thenReturn("ES");
        when(applePayConfigDataMock.getMerchantName()).thenReturn("Merchant name");

        final List<String> supportedNetworks = Arrays.asList("visa", "mastercard");
        when(applePayConfigDataMock.getSupportedNetworks()).thenReturn(supportedNetworks);

        when(totalPriceMock.getCurrencyIso()).thenReturn("EUR");
        when(totalPriceMock.getValue()).thenReturn(BigDecimal.valueOf(66.66f).setScale(2, RoundingMode.HALF_UP));
        when(cartMock.getTotalPrice()).thenReturn(totalPriceMock);

        final ApplePayPaymentRequest result = testObj.getApplePayPaymentRequest(cartMock);

        assertEquals("ES", result.getCountryCode());
        assertEquals("EUR", result.getCurrencyCode());
        assertEquals("66.66", result.getTotal().getAmount());
        assertEquals("Merchant name", result.getTotal().getLabel());
        assertEquals("final", result.getTotal().getType());

        assertEquals(merchantCapabilities, result.getMerchantCapabilities());
        assertEquals(supportedNetworks, result.getSupportedNetworks());
    }
}

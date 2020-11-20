package com.worldpay.worldpayextocc.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.CountryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.RegionWsDTO;
import de.hybris.platform.core.model.user.CustomerModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayAddressWsDTOAddressDataPopulatorTest {

    private static final String SPAIN_ISOCODE = "ES";
    private static final String USER_EMAIL = "user@worldpay.com";
    private static final String SPAIN_NAME = "SPAIN";
    private static final String VLC_ISOCODE = "VLC";
    private static final String VLC_NAME = "Valencia";

    @InjectMocks
    private WorldpayAddressWsDTOAddressDataPopulator testObj;

    @Mock
    private I18NFacade i18nFacadeMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;

    @Mock
    private RegionData regionDataMock;
    @Mock
    private CountryData countryDataMock;
    @Mock
    private CustomerModel customerModelMock;


    private RegionWsDTO regionWsDTOStub;
    private CountryWsDTO countryWsDTOStub;
    private AddressWsDTO addressWsDTOStub;

    @Before
    public void setUp() throws Exception {
        addressWsDTOStub = new AddressWsDTO();
        addressWsDTOStub.setShippingAddress(Boolean.TRUE);

        countryWsDTOStub = new CountryWsDTO();
        countryWsDTOStub.setIsocode(SPAIN_ISOCODE);
        countryWsDTOStub.setName(SPAIN_NAME);

        regionWsDTOStub = new RegionWsDTO();
        regionWsDTOStub.setIsocode(VLC_ISOCODE);
        regionWsDTOStub.setName(VLC_NAME);

        addressWsDTOStub.setCountry(countryWsDTOStub);
        addressWsDTOStub.setRegion(regionWsDTOStub);

        when(i18nFacadeMock.getCountryForIsocode(SPAIN_ISOCODE)).thenReturn(countryDataMock);
        when(i18nFacadeMock.getRegion(SPAIN_ISOCODE, VLC_ISOCODE)).thenReturn(regionDataMock);
        when(checkoutCustomerStrategyMock.getCurrentUserForCheckout()).thenReturn(customerModelMock);
        when(customerModelMock.getContactEmail()).thenReturn(USER_EMAIL);
    }

    @Test
    public void populate_shouldSetCountryRegionEmailAndShippingAddress() {
        final AddressData result = new AddressData();

        testObj.populate(addressWsDTOStub, result);

        assertThat(result.getCountry()).isEqualTo(countryDataMock);
        assertThat(result.getRegion()).isEqualTo(regionDataMock);
        assertThat(result.getEmail()).isEqualTo(USER_EMAIL);
        assertThat(result.isShippingAddress()).isTrue();
    }

    @Test
    public void populate_shouldNotSetShippingAddressWhenWsDTOFieldIsNotSet() {
        final AddressData result = new AddressData();
        addressWsDTOStub.setShippingAddress(null);

        testObj.populate(addressWsDTOStub, result);

        assertThat(result.isShippingAddress()).isFalse();
    }

    @Test
    public void populate_shouldNotSetRegionWhenWsDTOFieldIsNotSet() {
        final AddressData result = new AddressData();
        addressWsDTOStub.setRegion(null);

        testObj.populate(addressWsDTOStub, result);

        assertThat(result.getRegion()).isNull();
    }
}

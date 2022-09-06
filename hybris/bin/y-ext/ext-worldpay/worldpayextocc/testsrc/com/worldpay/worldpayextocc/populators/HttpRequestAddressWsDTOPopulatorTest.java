package com.worldpay.worldpayextocc.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class HttpRequestAddressWsDTOPopulatorTest {

    private static final String COUNTRY = "JP";
    private static final String REGION = "JP-27";

    @InjectMocks
    private HttpRequestAddressWsDTOPopulator testObject;

    @Mock
    private I18NFacade i18NFacade;

    @Mock
    private HttpServletRequest httpServletRequestMock;

    private AddressWsDTO addressWsDTO = new AddressWsDTO();
    private CountryData countryData = new CountryData();
    private RegionData regionData = new RegionData();

    @Before
    public void setUp() {
        countryData.setIsocode(COUNTRY);
        regionData.setIsocode(REGION);

        when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.COMPANY_NAME)).thenReturn(HttpRequestAddressWsDTOPopulator.COMPANY_NAME);
        when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.EMAIL)).thenReturn(HttpRequestAddressWsDTOPopulator.EMAIL);
        when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.FIRST_NAME)).thenReturn(HttpRequestAddressWsDTOPopulator.FIRST_NAME);
        when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.FORMATTED_ADDRESS)).thenReturn(HttpRequestAddressWsDTOPopulator.FORMATTED_ADDRESS);
        when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.ADDRESS_ID)).thenReturn(HttpRequestAddressWsDTOPopulator.ADDRESS_ID);
        when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.LAST_NAME)).thenReturn(HttpRequestAddressWsDTOPopulator.LAST_NAME);
        when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.LINE1)).thenReturn(HttpRequestAddressWsDTOPopulator.LINE1);
        when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.LINE2)).thenReturn(HttpRequestAddressWsDTOPopulator.LINE2);
        when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.PHONE)).thenReturn(HttpRequestAddressWsDTOPopulator.PHONE);
        when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.POSTCODE)).thenReturn(HttpRequestAddressWsDTOPopulator.POSTCODE);
        when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.TITLE_CODE)).thenReturn(HttpRequestAddressWsDTOPopulator.TITLE_CODE);
        when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.TITLE)).thenReturn(HttpRequestAddressWsDTOPopulator.TITLE);
        when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.TOWN)).thenReturn(HttpRequestAddressWsDTOPopulator.TOWN);
        when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.COUNTRY)).thenReturn(COUNTRY);
        when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.REGION)).thenReturn(REGION);

        when(i18NFacade.getCountryForIsocode(COUNTRY)).thenReturn(countryData);
        when(i18NFacade.getRegion(COUNTRY, REGION)).thenReturn(regionData);
    }

    @Test
    public void populate_ShouldPopulateAllFields() {
        // Setup
        // Execute
        testObject.populate(httpServletRequestMock, addressWsDTO);

        // Verify
        assertEquals(HttpRequestAddressWsDTOPopulator.COMPANY_NAME, addressWsDTO.getCompanyName());
        assertEquals(HttpRequestAddressWsDTOPopulator.EMAIL, addressWsDTO.getEmail());
        assertEquals(HttpRequestAddressWsDTOPopulator.FIRST_NAME, addressWsDTO.getFirstName());
        assertEquals(HttpRequestAddressWsDTOPopulator.FORMATTED_ADDRESS, addressWsDTO.getFormattedAddress());
        assertEquals(HttpRequestAddressWsDTOPopulator.ADDRESS_ID, addressWsDTO.getId());
        assertEquals(HttpRequestAddressWsDTOPopulator.LAST_NAME, addressWsDTO.getLastName());
        assertEquals(HttpRequestAddressWsDTOPopulator.LINE1, addressWsDTO.getLine1());
        assertEquals(HttpRequestAddressWsDTOPopulator.LINE2, addressWsDTO.getLine2());
        assertEquals(HttpRequestAddressWsDTOPopulator.PHONE, addressWsDTO.getPhone());
        assertEquals(HttpRequestAddressWsDTOPopulator.POSTCODE, addressWsDTO.getPostalCode());
        assertEquals(HttpRequestAddressWsDTOPopulator.TITLE_CODE, addressWsDTO.getTitleCode());
        assertEquals(HttpRequestAddressWsDTOPopulator.TITLE, addressWsDTO.getTitle());
        assertEquals(HttpRequestAddressWsDTOPopulator.TOWN, addressWsDTO.getTown());

        assertFalse(addressWsDTO.getShippingAddress());
        assertFalse(addressWsDTO.getVisibleInAddressBook());

        assertEquals(COUNTRY, addressWsDTO.getCountry().getIsocode());
        assertEquals(REGION, addressWsDTO.getRegion().getIsocode());
    }
}

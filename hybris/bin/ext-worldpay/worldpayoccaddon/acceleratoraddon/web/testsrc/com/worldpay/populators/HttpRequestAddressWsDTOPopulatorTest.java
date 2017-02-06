package com.worldpay.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

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

        Mockito.when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.COMPANY_NAME)).thenReturn(HttpRequestAddressWsDTOPopulator.COMPANY_NAME);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.EMAIL)).thenReturn(HttpRequestAddressWsDTOPopulator.EMAIL);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.FIRST_NAME)).thenReturn(HttpRequestAddressWsDTOPopulator.FIRST_NAME);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.FORMATTED_ADDRESS)).thenReturn(HttpRequestAddressWsDTOPopulator.FORMATTED_ADDRESS);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.ADDRESS_ID)).thenReturn(HttpRequestAddressWsDTOPopulator.ADDRESS_ID);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.LAST_NAME)).thenReturn(HttpRequestAddressWsDTOPopulator.LAST_NAME);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.LINE1)).thenReturn(HttpRequestAddressWsDTOPopulator.LINE1);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.LINE2)).thenReturn(HttpRequestAddressWsDTOPopulator.LINE2);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.PHONE)).thenReturn(HttpRequestAddressWsDTOPopulator.PHONE);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.POSTCODE)).thenReturn(HttpRequestAddressWsDTOPopulator.POSTCODE);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.TITLE_CODE)).thenReturn(HttpRequestAddressWsDTOPopulator.TITLE_CODE);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.TITLE)).thenReturn(HttpRequestAddressWsDTOPopulator.TITLE);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.TOWN)).thenReturn(HttpRequestAddressWsDTOPopulator.TOWN);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.COUNTRY)).thenReturn(COUNTRY);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestAddressWsDTOPopulator.REGION)).thenReturn(REGION);

        Mockito.when(i18NFacade.getCountryForIsocode(COUNTRY)).thenReturn(countryData);
        Mockito.when(i18NFacade.getRegion(COUNTRY, REGION)).thenReturn(regionData);
    }

    @Test
    public void testBasicPopulationOfAllFields() {
        // Setup
        // Execute
        testObject.populate(httpServletRequestMock, addressWsDTO);

        // Verify
        Assert.assertEquals(HttpRequestAddressWsDTOPopulator.COMPANY_NAME , addressWsDTO.getCompanyName());
        Assert.assertEquals(HttpRequestAddressWsDTOPopulator.EMAIL, addressWsDTO.getEmail());
        Assert.assertEquals(HttpRequestAddressWsDTOPopulator.FIRST_NAME, addressWsDTO.getFirstName());
        Assert.assertEquals(HttpRequestAddressWsDTOPopulator.FORMATTED_ADDRESS, addressWsDTO.getFormattedAddress());
        Assert.assertEquals(HttpRequestAddressWsDTOPopulator.ADDRESS_ID, addressWsDTO.getId());
        Assert.assertEquals(HttpRequestAddressWsDTOPopulator.LAST_NAME, addressWsDTO.getLastName());
        Assert.assertEquals(HttpRequestAddressWsDTOPopulator.LINE1, addressWsDTO.getLine1());
        Assert.assertEquals(HttpRequestAddressWsDTOPopulator.LINE2, addressWsDTO.getLine2());
        Assert.assertEquals(HttpRequestAddressWsDTOPopulator.PHONE, addressWsDTO.getPhone());
        Assert.assertEquals(HttpRequestAddressWsDTOPopulator.POSTCODE, addressWsDTO.getPostalCode());
        Assert.assertEquals(HttpRequestAddressWsDTOPopulator.TITLE_CODE, addressWsDTO.getTitleCode());
        Assert.assertEquals(HttpRequestAddressWsDTOPopulator.TITLE, addressWsDTO.getTitle());
        Assert.assertEquals(HttpRequestAddressWsDTOPopulator.TOWN, addressWsDTO.getTown());

        Assert.assertFalse(addressWsDTO.getShippingAddress());
        Assert.assertFalse(addressWsDTO.getVisibleInAddressBook());

        Assert.assertEquals(COUNTRY, addressWsDTO.getCountry().getIsocode());
        Assert.assertEquals(REGION, addressWsDTO.getRegion().getIsocode());
    }

}

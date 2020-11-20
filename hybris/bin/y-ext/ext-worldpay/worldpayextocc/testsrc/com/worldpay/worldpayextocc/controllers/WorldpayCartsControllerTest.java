package com.worldpay.worldpayextocc.controllers;

import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.worldpayextocc.exceptions.NoCheckoutCartException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayCartsControllerTest {

    @InjectMocks
    private WorldpayCartsController testObj;

    @Mock
    private UserFacade userFacadeMock;
    @Mock
    private DataMapper dataMapperMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;
    @Mock
    private Populator<AddressWsDTO, AddressData> worldpayAdressWsDTOAddressDataPopulatorMock;

    @Mock
    private AddressData addressDataMock;
    @Mock
    private AddressWsDTO addressWsDTOMock;


    @Test
    public void addBillingAddressToCart_shouldPopulateFieldsWithDataMapperAndWorldpayAddressPopulatorAddingAddressWithUserFacadeAndSetBillingDetails() throws WorldpayException, NoCheckoutCartException {
        final String fieldsStub = "";
        when(dataMapperMock.map(addressWsDTOMock, AddressData.class)).thenReturn(addressDataMock);

        testObj.addBillingAddressToCart(addressWsDTOMock, fieldsStub);

        verify(dataMapperMock).map(addressWsDTOMock, AddressData.class);
        verify(worldpayAdressWsDTOAddressDataPopulatorMock).populate(addressWsDTOMock, addressDataMock);
        verify(userFacadeMock).addAddress(addressDataMock);
        verify(worldpayPaymentCheckoutFacade).setBillingDetails(addressDataMock);
    }
}

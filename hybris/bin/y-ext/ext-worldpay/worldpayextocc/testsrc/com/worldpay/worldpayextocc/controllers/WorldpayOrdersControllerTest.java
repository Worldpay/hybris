package com.worldpay.worldpayextocc.controllers;

import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.impl.WorldpayCheckoutFacadeDecorator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayOrdersControllerTest {


    @InjectMocks
    private WorldpayOrdersController testObj;

    @Mock
    private WorldpayCheckoutFacadeDecorator worldpayCheckoutFacadeDecoratorMock;

    @Mock
    private OrderData orderDataMock;
    @Mock
    private DataMapper dataMapperMock;
    @Mock
    private OrderWsDTO orderWsDTOMock;

    @Test(expected = WorldpayException.class)
    public void placeRedirectOrder_ShouldThrowException() throws InvalidCartException, WorldpayException {
        when(worldpayCheckoutFacadeDecoratorMock.placeOrder()).thenThrow(new InvalidCartException("Chan chan"));

        testObj.placeRedirectOrder(FieldSetLevelHelper.DEFAULT_LEVEL);
    }

    @Test
    public void placeRedirectOrder_ShouldReturnOrderWsDTO() throws InvalidCartException, WorldpayException {
        when(worldpayCheckoutFacadeDecoratorMock.placeOrder()).thenReturn(orderDataMock);
        lenient().when(dataMapperMock.map(orderDataMock, OrderWsDTO.class)).thenReturn(orderWsDTOMock);

        testObj.placeRedirectOrder(FieldSetLevelHelper.DEFAULT_LEVEL);

        assertThat(orderWsDTOMock).isNotNull();
    }

}

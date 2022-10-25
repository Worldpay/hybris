package com.worldpay.facades.impl;

import com.worldpay.converters.WorldpayBinRangeConverter;
import com.worldpay.data.WorldpayBinRangeData;
import com.worldpay.facades.WorldpayBinRangeFacade;
import com.worldpay.model.WorldpayBinRangeModel;
import com.worldpay.service.WorldpayBinRangeService;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayBinRangeFacadeTest {

    @InjectMocks
    private WorldpayBinRangeFacade worldpayBinRangeFacade = new DefaultWorldpayBinRangeFacade();
    @Mock
    private WorldpayBinRangeConverter worldpayBinRangeConverter;
    @Mock
    private WorldpayBinRangeService worldpayBinRangeService;
    @Mock
    private WorldpayBinRangeModel worldpayBinRangeModelMock;
    @Mock
    private WorldpayBinRangeData worldpayBinRangeDataMock;

    private static final String CARD_PREFIX = "123456";

    @Test
    public void shouldReturnData() throws Exception {
        Mockito.when(worldpayBinRangeService.getBinRange(CARD_PREFIX)).thenReturn(worldpayBinRangeModelMock);
        Mockito.when(worldpayBinRangeConverter.convert(worldpayBinRangeModelMock)).thenReturn(worldpayBinRangeDataMock);

        WorldpayBinRangeData data = worldpayBinRangeFacade.getWorldpayBinRange(CARD_PREFIX);

        Assert.assertNotNull(data);
    }

    @Test
    public void shouldReturnNullData() throws Exception {
        Mockito.when(worldpayBinRangeService.getBinRange(CARD_PREFIX)).thenReturn(null);

        WorldpayBinRangeData data = worldpayBinRangeFacade.getWorldpayBinRange(CARD_PREFIX);

        Assert.assertNull(data);
    }

}
package com.worldpay.facades.payment.impl;

import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpayAdditionalInfoService;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayAdditionalInfoFacadeTest {

    @Mock
    private HttpServletRequest httpServletRequestMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private WorldpayAdditionalInfoService worldpayAdditionalDataService;

    @InjectMocks
    private DefaultWorldpayAdditionalInfoFacade testObj;

    @Test
    public void shouldCreateWorldpayAdditionalInfoDataFromRequest() {
        when(worldpayAdditionalDataService.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);

        final WorldpayAdditionalInfoData result = testObj.createWorldpayAdditionalInfoData(httpServletRequestMock);

        assertEquals(result, worldpayAdditionalInfoDataMock);
    }
}
    


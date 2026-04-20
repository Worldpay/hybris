package com.worldpay.worldpayextocc.controllers;

import com.worldpay.data.cms.WorldpayAPMComponentData;
import com.worldpay.dto.cms.WorldpayAPMComponentWsDTO;
import com.worldpay.dto.cms.WorldpayAPMComponentsWsDTO;
import com.worldpay.facades.WorldpayAPMComponentFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayComponentControllerTest {

    private static final String FIELDS = "DEFAULT";
    private static final String APM_CODE = "apmCode";

    @InjectMocks
    private WorldpayComponentController testObj;

    @Mock
    private DataMapper dataMapperMock;
    @Mock
    private WorldpayAPMComponentFacade worldpayAPMComponentFacadeMock;

    @Mock
    private WorldpayAPMComponentData worldpayAPMComponentDataMock;
    @Mock
    private WorldpayAPMComponentsWsDTO worldpayAPMComponentsWsDTOMock;
    @Mock
    private WorldpayAPMComponentWsDTO worldpayAPMComponentWsDTOMock;

    @Test
    public void getAvailableApmComponents_ShouldReturnAnObjectWithComponents() {
        when(worldpayAPMComponentFacadeMock.getAllAvailableWorldpayAPMComponents()).thenReturn(List.of(worldpayAPMComponentDataMock));
        lenient().when(dataMapperMock.map(any(), eq(WorldpayAPMComponentsWsDTO.class), anyString())).thenReturn(worldpayAPMComponentsWsDTOMock);

        final WorldpayAPMComponentsWsDTO result = testObj.getAvailableApmComponents(FIELDS);

        assertThat(result).isEqualTo(worldpayAPMComponentsWsDTOMock);
    }

    @Test
    public void getApmComponent_ShouldReturnMappedDto() {
        when(worldpayAPMComponentFacadeMock.getWorldpayAPMComponentByCode(APM_CODE)).thenReturn(worldpayAPMComponentDataMock);
        when(dataMapperMock.map(worldpayAPMComponentDataMock, WorldpayAPMComponentWsDTO.class, FIELDS)).thenReturn(worldpayAPMComponentWsDTOMock);

        final WorldpayAPMComponentWsDTO result = testObj.getApmComponent(APM_CODE, FIELDS);

        assertThat(result).isEqualTo(worldpayAPMComponentWsDTOMock);
    }

    @Test
    public void getApmComponent_ShouldReturnNull_WhenComponentNotFound() {
        when(worldpayAPMComponentFacadeMock.getWorldpayAPMComponentByCode(APM_CODE)).thenReturn(null);
        when(dataMapperMock.map(null, WorldpayAPMComponentWsDTO.class, FIELDS)).thenReturn(null);

        final WorldpayAPMComponentWsDTO result = testObj.getApmComponent(APM_CODE, FIELDS);

        assertThat(result).isNull();
    }

}

package com.worldpay.worldpayextocc.controllers;

import com.worldpay.data.cms.WorldpayAPMComponentData;
import com.worldpay.dto.cms.WorldpayAPMComponentsWsDTO;
import com.worldpay.facades.WorldpayAPMComponentFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayComponentControllerTest {

    private static final String FIELDS = "DEFAULT";

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

    @Test
    public void getAvailableApmComponents_ShouldReturnAnObjectWithComponents() {
        when(worldpayAPMComponentFacadeMock.getAllAvailableWorldpayAPMComponents()).thenReturn(List.of(worldpayAPMComponentDataMock));
        when(dataMapperMock.map(anyObject(), eq(WorldpayAPMComponentsWsDTO.class), anyString())).thenReturn(worldpayAPMComponentsWsDTOMock);

        final WorldpayAPMComponentsWsDTO result = testObj.getAvailableApmComponents(FIELDS);

        assertThat(result).isEqualTo(worldpayAPMComponentsWsDTOMock);
    }
}

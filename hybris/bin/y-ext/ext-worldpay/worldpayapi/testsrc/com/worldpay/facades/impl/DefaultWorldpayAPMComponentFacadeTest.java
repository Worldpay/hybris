package com.worldpay.facades.impl;

import com.worldpay.core.services.WorldpayAPMComponentService;
import com.worldpay.data.cms.WorldpayAPMComponentData;
import com.worldpay.model.WorldpayAPMComponentModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayAPMComponentFacadeTest {

    private static final String NOTFOUND = "notfound";

    @InjectMocks
    private DefaultWorldpayAPMComponentFacade testObj;

    @Mock
    private WorldpayAPMComponentService worldpayAPMComponentServiceMock;
    @Mock
    private Converter<WorldpayAPMComponentModel, WorldpayAPMComponentData> worldpayAPMComponentConverterMock;

    @Mock
    private WorldpayAPMComponentData worldpayAPMComponentData1Mock, worldpayAPMComponentData2Mock;
    @Mock
    private WorldpayAPMComponentModel worldpayAPMComponentModel1Mock, worldpayAPMComponentModel2Mock;

    @Test
    public void getAllAvailableWorldpayAPMComponents_WhenThereAreAvailableComponents_ShouldReturnAListOfComponentData() {
        when(worldpayAPMComponentServiceMock.getAllAvailableWorldpayAPMComponents()).thenReturn(List.of(worldpayAPMComponentModel1Mock, worldpayAPMComponentModel2Mock));
        when(worldpayAPMComponentConverterMock.convertAll(List.of(worldpayAPMComponentModel1Mock, worldpayAPMComponentModel2Mock))).thenReturn(List.of(worldpayAPMComponentData1Mock, worldpayAPMComponentData2Mock));

        final List<WorldpayAPMComponentData> result = testObj.getAllAvailableWorldpayAPMComponents();

        assertThat(result).isEqualTo(List.of(worldpayAPMComponentData1Mock, worldpayAPMComponentData2Mock));
    }

    @Test
    public void getAllAvailableWorldpayAPMComponents_WhenThereAreNotAvailableComponents_ShouldReturnAnEmptyList() {
        when(worldpayAPMComponentServiceMock.getAllAvailableWorldpayAPMComponents()).thenReturn(Collections.emptyList());
        when(worldpayAPMComponentConverterMock.convertAll(Collections.emptyList())).thenReturn(Collections.emptyList());

        final List<WorldpayAPMComponentData> result = testObj.getAllAvailableWorldpayAPMComponents();

        assertThat(result).isEmpty();
    }

    @Test
    public void getWorldpayAPMComponentByCode_WhenComponentExists_ShouldReturnComponentData() {
        String apmCode = "apm1";
        when(worldpayAPMComponentServiceMock.getWorldpayAPMComponentByCode(apmCode)).thenReturn(worldpayAPMComponentModel1Mock);
        when(worldpayAPMComponentConverterMock.convert(worldpayAPMComponentModel1Mock)).thenReturn(worldpayAPMComponentData1Mock);

        final WorldpayAPMComponentData result = testObj.getWorldpayAPMComponentByCode(apmCode);

        assertThat(result).isEqualTo(worldpayAPMComponentData1Mock);
        verify(worldpayAPMComponentServiceMock).getWorldpayAPMComponentByCode(apmCode);
        verify(worldpayAPMComponentConverterMock).convert(worldpayAPMComponentModel1Mock);
    }

    @Test
    public void getWorldpayAPMComponentByCode_WhenComponentDoesNotExist_ShouldReturnNull() {
        when(worldpayAPMComponentServiceMock.getWorldpayAPMComponentByCode(NOTFOUND)).thenReturn(null);
        when(worldpayAPMComponentConverterMock.convert(null)).thenReturn(null);

        final WorldpayAPMComponentData result = testObj.getWorldpayAPMComponentByCode(NOTFOUND);

        assertThat(result).isNull();
        verify(worldpayAPMComponentServiceMock).getWorldpayAPMComponentByCode(NOTFOUND);
        verify(worldpayAPMComponentConverterMock).convert(null);
    }

    @Test
    public void getWorldpayAPMComponentByCode_WhenCodeIsNull_ShouldReturnNull() {
        when(worldpayAPMComponentServiceMock.getWorldpayAPMComponentByCode(null)).thenReturn(null);
        when(worldpayAPMComponentConverterMock.convert(null)).thenReturn(null);

        final WorldpayAPMComponentData result = testObj.getWorldpayAPMComponentByCode(null);

        assertThat(result).isNull();
        verify(worldpayAPMComponentServiceMock).getWorldpayAPMComponentByCode(null);
        verify(worldpayAPMComponentConverterMock).convert(null);
    }
}

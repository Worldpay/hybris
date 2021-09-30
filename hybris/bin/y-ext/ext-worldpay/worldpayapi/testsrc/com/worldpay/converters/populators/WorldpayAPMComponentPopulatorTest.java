package com.worldpay.converters.populators;

import com.worldpay.data.apm.WorldpayAPMConfigurationData;
import com.worldpay.data.cms.WorldpayAPMComponentData;
import com.worldpay.model.WorldpayAPMComponentModel;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
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
public class WorldpayAPMComponentPopulatorTest {

    @InjectMocks
    private WorldpayAPMComponentPopulator testObj;

    @Mock
    private Converter<MediaModel, MediaData> mediaModelConverterMock;
    @Mock
    private Converter<WorldpayAPMConfigurationModel, WorldpayAPMConfigurationData> worldpayAPMConfigurationConverterMock;

    @Mock
    private WorldpayAPMComponentModel sourceMock;
    @Mock
    private MediaModel mediaModelMock;
    @Mock
    private MediaData mediaDataMock;
    @Mock
    private WorldpayAPMConfigurationModel worldpayAPMConfigurationModelMock;
    @Mock
    private WorldpayAPMConfigurationData worldpayAPMConfigurationDataMock;

    @Before
    public void setUp() {
        testObj = new WorldpayAPMComponentPopulator(worldpayAPMConfigurationConverterMock, mediaModelConverterMock);
    }

    @Test
    public void populate_WhenSourceIsNotNullAndTargetIsNotNullAndMediaIsNotNull_ShouldPopulateAllFields() {
        when(sourceMock.getApmConfiguration()).thenReturn(worldpayAPMConfigurationModelMock);
        when(worldpayAPMConfigurationConverterMock.convert(worldpayAPMConfigurationModelMock)).thenReturn(worldpayAPMConfigurationDataMock);
        when(sourceMock.getMedia()).thenReturn(mediaModelMock);
        when(mediaModelConverterMock.convert(mediaModelMock)).thenReturn(mediaDataMock);
        final WorldpayAPMComponentData target = new WorldpayAPMComponentData();

        testObj.populate(sourceMock, target);

        assertThat(target.getApmConfiguration()).isEqualTo(worldpayAPMConfigurationDataMock);
        assertThat(target.getMedia()).isEqualTo(mediaDataMock);
    }

    @Test
    public void populate_WhenSourceIsNotNullAndTargetIsNotNullAndMediaIsNull_ShouldOnlyPopulateApmConfig() {
        when(sourceMock.getApmConfiguration()).thenReturn(worldpayAPMConfigurationModelMock);
        when(worldpayAPMConfigurationConverterMock.convert(worldpayAPMConfigurationModelMock)).thenReturn(worldpayAPMConfigurationDataMock);
        when(sourceMock.getMedia()).thenReturn(null);
        final WorldpayAPMComponentData target = new WorldpayAPMComponentData();

        testObj.populate(sourceMock, target);

        assertThat(target.getApmConfiguration()).isEqualTo(worldpayAPMConfigurationDataMock);
        assertThat(target.getMedia()).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new WorldpayAPMComponentData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }
}

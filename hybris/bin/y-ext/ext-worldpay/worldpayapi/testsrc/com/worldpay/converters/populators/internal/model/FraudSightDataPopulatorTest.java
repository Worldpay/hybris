package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.CustomNumericFields;
import com.worldpay.data.CustomStringFields;
import com.worldpay.data.FraudSightData;
import com.worldpay.data.ShopperFields;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FraudSightDataPopulatorTest {

    @InjectMocks
    private FraudSightDataPopulator testObj;

    @Mock
    private Converter<ShopperFields, com.worldpay.internal.model.ShopperFields> internalShopperFieldsConverterMock;
    @Mock
    private Converter<CustomNumericFields, com.worldpay.internal.model.CustomNumericFields> internalCustomNumericFieldsConverterMock;
    @Mock
    private Converter<CustomStringFields, com.worldpay.internal.model.CustomStringFields> internalCustomStringFieldsConverterMock;

    @Mock
    private FraudSightData sourceMock;
    @Mock
    private ShopperFields shopperFieldsMock;
    @Mock
    private com.worldpay.internal.model.ShopperFields intShopperFieldsMock;
    @Mock
    private CustomNumericFields customNumericFieldsMock;
    @Mock
    private com.worldpay.internal.model.CustomNumericFields intCustomNumericFieldsMock;
    @Mock
    private CustomStringFields customStringFieldsMock;
    @Mock
    private com.worldpay.internal.model.CustomStringFields intCustomStringFieldsMock;

    @Before
    public void setUp() {
        testObj = new FraudSightDataPopulator(internalShopperFieldsConverterMock, internalCustomNumericFieldsConverterMock, internalCustomStringFieldsConverterMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.FraudSightData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenGetShopperFieldsIsNull_ShouldNotPopulateShopperFields() {
        when(sourceMock.getShopperFields()).thenReturn(null);

        final com.worldpay.internal.model.FraudSightData target = new com.worldpay.internal.model.FraudSightData();
        testObj.populate(sourceMock, target);

        assertThat(target.getShopperFields()).isNull();
    }

    @Test
    public void populate_WhenGetCustomNumericFieldsIsNull_ShouldNotPopulateCustomNumericFields() {
        when(sourceMock.getCustomNumericFields()).thenReturn(null);

        final com.worldpay.internal.model.FraudSightData target = new com.worldpay.internal.model.FraudSightData();
        testObj.populate(sourceMock, target);

        assertThat(target.getCustomNumericFields()).isNull();
    }

    @Test
    public void populate_WhenGetCustomStringFieldsIsNull_ShouldNotPopulateCustomStringFields() {
        when(sourceMock.getCustomStringFields()).thenReturn(null);

        final com.worldpay.internal.model.FraudSightData target = new com.worldpay.internal.model.FraudSightData();
        testObj.populate(sourceMock, target);

        assertThat(target.getCustomNumericFields()).isNull();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.getShopperFields()).thenReturn(shopperFieldsMock);
        when(sourceMock.getCustomNumericFields()).thenReturn(customNumericFieldsMock);
        when(sourceMock.getCustomStringFields()).thenReturn(customStringFieldsMock);
        when(internalShopperFieldsConverterMock.convert(shopperFieldsMock)).thenReturn(intShopperFieldsMock);
        when(internalCustomNumericFieldsConverterMock.convert(customNumericFieldsMock)).thenReturn(intCustomNumericFieldsMock);
        when(internalCustomStringFieldsConverterMock.convert(customStringFieldsMock)).thenReturn(intCustomStringFieldsMock);

        final com.worldpay.internal.model.FraudSightData target = new com.worldpay.internal.model.FraudSightData();
        testObj.populate(sourceMock, target);

        assertThat(target.getShopperFields()).isEqualTo(intShopperFieldsMock);
        assertThat(target.getCustomNumericFields()).isEqualTo(intCustomNumericFieldsMock);
        assertThat(target.getCustomStringFields()).isEqualTo(intCustomStringFieldsMock);
    }
}

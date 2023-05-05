package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Address;
import com.worldpay.data.AlternativeShippingAddress;
import com.worldpay.enums.alternativeShippingAddress.ShippingMethod;
import com.worldpay.enums.alternativeShippingAddress.ShippingType;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AlternativeShippingAddressPopulatorTest {
    public static final String NORMAL = "normal";
    public static final String REGISTERED_BOX = "registered box";
    @InjectMocks
    private AlternativeShippingAddressPopulator testObj;

    @Mock
    private Converter<Address, com.worldpay.internal.model.Address> internalAddressConverterMock;

    @Mock
    private AlternativeShippingAddress sourceMock;
    @Mock
    private Address addressMock;
    @Mock
    private com.worldpay.internal.model.Address internalAddressMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.AlternativeShippingAddress());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenSourceHasNullFields_ShouldNotPopulateStreetOrHouseNameOrHouseNumberOrHouseNumberExtensionOrAddress1OrAddress2OrAddress3() {
        when(sourceMock.getAddress()).thenReturn(null);
        when(sourceMock.getShippingMethod()).thenReturn(null);
        when(sourceMock.getShippingType()).thenReturn(null);

        final com.worldpay.internal.model.AlternativeShippingAddress target = new com.worldpay.internal.model.AlternativeShippingAddress();
        testObj.populate(sourceMock, target);

        assertThat(target.getAddress()).isNull();
        assertThat(target.getShippingSummary().getShippingMethod()).isNull();
        assertThat(target.getShippingSummary().getShippingType()).isNull();
    }


    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.getShippingType()).thenReturn(ShippingType.NORMAL);
        when(sourceMock.getShippingMethod()).thenReturn(ShippingMethod.REGISTERED_BOX);
        when(sourceMock.getAddress()).thenReturn(addressMock);
        when(internalAddressConverterMock.convert(addressMock)).thenReturn(internalAddressMock);

        final com.worldpay.internal.model.AlternativeShippingAddress target = new com.worldpay.internal.model.AlternativeShippingAddress();
        testObj.populate(sourceMock, target);

        assertThat(target.getShippingSummary()).isNotNull();
        assertThat(target.getShippingSummary().getShippingType()).isEqualTo(NORMAL);
        assertThat(target.getShippingSummary().getShippingMethod()).isEqualTo(REGISTERED_BOX);
        assertThat(target.getAddress()).isEqualTo(internalAddressMock);
    }
}
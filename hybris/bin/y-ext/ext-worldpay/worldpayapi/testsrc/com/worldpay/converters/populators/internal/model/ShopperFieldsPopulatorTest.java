package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Address;
import com.worldpay.data.Date;
import com.worldpay.data.ShopperFields;
import de.hybris.bootstrap.annotations.UnitTest;
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
public class ShopperFieldsPopulatorTest {

    private static final String SHOPPER_NAME = "shopperName";
    private static final String SHOPPER_ID = "shopperID";

    @InjectMocks
    private ShopperFieldsPopulator testObj;

    @Mock
    private Converter<Date, com.worldpay.internal.model.Date> dateConverterMock;
    @Mock
    private Converter<Address, com.worldpay.internal.model.Address> addressConverterMock;

    @Mock
    private ShopperFields sourceMock;
    @Mock
    private Address addressMock;
    @Mock
    private com.worldpay.internal.model.Address intAddressMock;
    @Mock
    private Date dateMock;
    @Mock
    private com.worldpay.internal.model.Date intDateMock;

    @Before
    public void setUp() {
        testObj = new ShopperFieldsPopulator(dateConverterMock, addressConverterMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.ShopperFields());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenBirthDateIsNull_ShouldNotPopulateBirthDate() {
        when(sourceMock.getBirthDate()).thenReturn(null);

        final com.worldpay.internal.model.ShopperFields target = new com.worldpay.internal.model.ShopperFields();
        testObj.populate(sourceMock, target);

        assertThat(target.getBirthDate()).isNull();
    }

    @Test
    public void populate_WhenShopperAddressIsNull_ShouldNotPopulateShopperAddress() {
        when(sourceMock.getShopperAddress()).thenReturn(null);

        final com.worldpay.internal.model.ShopperFields target = new com.worldpay.internal.model.ShopperFields();
        testObj.populate(sourceMock, target);

        assertThat(target.getShopperAddress()).isNull();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.getShopperName()).thenReturn(SHOPPER_NAME);
        when(sourceMock.getShopperId()).thenReturn(SHOPPER_ID);
        when(sourceMock.getBirthDate()).thenReturn(dateMock);
        when(dateConverterMock.convert(dateMock)).thenReturn(intDateMock);
        when(sourceMock.getShopperAddress()).thenReturn(addressMock);
        when(addressConverterMock.convert(addressMock)).thenReturn(intAddressMock);

        final com.worldpay.internal.model.ShopperFields target = new com.worldpay.internal.model.ShopperFields();
        testObj.populate(sourceMock, target);

        assertThat(target.getShopperName()).isEqualTo(SHOPPER_NAME);
        assertThat(target.getShopperId()).isEqualTo(SHOPPER_ID);
        assertThat(target.getBirthDate().getDate()).isEqualTo(intDateMock);
        assertThat(target.getShopperAddress().getAddress()).isEqualTo(intAddressMock);
    }
}

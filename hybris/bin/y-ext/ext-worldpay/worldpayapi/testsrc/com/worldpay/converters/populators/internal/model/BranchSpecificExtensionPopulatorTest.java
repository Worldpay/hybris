package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.BranchSpecificExtension;
import com.worldpay.data.Purchase;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BranchSpecificExtensionPopulatorTest {

    @InjectMocks
    private BranchSpecificExtensionPopulator testObj;

    @Mock
    private Converter<Purchase, com.worldpay.internal.model.Purchase> internalPurchaseConverterMock;

    @Mock
    private BranchSpecificExtension sourceMock;
    @Mock
    private Purchase purchaseMock;
    @Mock
    private com.worldpay.internal.model.Purchase intPurchaseMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.BranchSpecificExtension());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenGetPurchaseIsNull_ShouldNotPopulateAirlineOrPurchaseOrHotelOrLodging() {
        when(sourceMock.getPurchase()).thenReturn(null);

        final com.worldpay.internal.model.BranchSpecificExtension target = new com.worldpay.internal.model.BranchSpecificExtension();
        testObj.populate(sourceMock, target);

        assertThat(target.getAirlineOrRailwayOrCarRentalOrPurchaseOrHotelOrLodgingOrFerryOrEvents()).isEmpty();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.getPurchase()).thenReturn(List.of(purchaseMock));
        when(internalPurchaseConverterMock.convertAll(List.of(purchaseMock))).thenReturn(List.of(intPurchaseMock));

        final com.worldpay.internal.model.BranchSpecificExtension target = new com.worldpay.internal.model.BranchSpecificExtension();
        testObj.populate(sourceMock, target);

        assertThat(target.getAirlineOrRailwayOrCarRentalOrPurchaseOrHotelOrLodgingOrFerryOrEvents()).isEqualTo(List.of(intPurchaseMock));
    }
}

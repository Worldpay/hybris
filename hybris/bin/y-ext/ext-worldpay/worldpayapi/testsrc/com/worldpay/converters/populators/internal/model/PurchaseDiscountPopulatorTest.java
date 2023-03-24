package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.PurchaseDiscount;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PurchaseDiscountPopulatorTest {

    public static final String PERCENTAGE = "50";
    public static final String CODE = "code";
    public static final String AMOUNT = "10";

    @InjectMocks
    private PurchaseDiscountPopulator testObj;

    @Mock
    private PurchaseDiscount sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_whenSourceIsNull_shouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.PurchaseDiscount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_whenTargetIsNull_shouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_whenSourceAndTargetAreNotNull_shouldPopulatePurchaseDiscount() {
        when(sourceMock.getPurchaseDiscountPercentage()).thenReturn(PERCENTAGE);
        when(sourceMock.getPurchaseDiscountCode()).thenReturn(CODE);
        when(sourceMock.getPurchaseDiscountAmount()).thenReturn(AMOUNT);

        final com.worldpay.internal.model.PurchaseDiscount target = new com.worldpay.internal.model.PurchaseDiscount();
        testObj.populate(sourceMock, target);

        assertThat(target.getPurchaseDiscountAmount()).isEqualTo(AMOUNT);
        assertThat(target.getPurchaseDiscountCode()).isEqualTo(CODE);
        assertThat(target.getPurchaseDiscountPercentage()).isEqualTo(PERCENTAGE);
    }
}

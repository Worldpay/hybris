package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Product;
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
public class ProductPopulatorTest {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PRICE = "price";
    private static final String QUANTITY = "quantity";
    private static final String CATEGORY = "category";
    private static final String TRUE = "true";
    private static final String SUB_CATEGORY = "subCategory";

    @InjectMocks
    private ProductPopulator testObj;

    @Mock
    private Product sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_whenSourceIsNull_shouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.Product());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_whenTargetIsNull_shouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_whenSourceAndTargetAreNotNull_shouldPopulateProduct() {
        when(sourceMock.getItemId()).thenReturn(ID);
        when(sourceMock.getItemName()).thenReturn(NAME);
        when(sourceMock.getItemPrice()).thenReturn(PRICE);
        when(sourceMock.getItemIsDigital()).thenReturn(TRUE);
        when(sourceMock.getItemQuantity()).thenReturn(QUANTITY);
        when(sourceMock.getItemCategory()).thenReturn(CATEGORY);
        when(sourceMock.getItemSubCategory()).thenReturn(SUB_CATEGORY);

        final com.worldpay.internal.model.Product target = new com.worldpay.internal.model.Product();
        testObj.populate(sourceMock, target);

        assertThat(target.getItemId()).isEqualTo(ID);
        assertThat(target.getItemName()).isEqualTo(NAME);
        assertThat(target.getItemPrice()).isEqualTo(PRICE);
        assertThat(target.getItemIsDigital()).isEqualTo(TRUE);
        assertThat(target.getItemQuantity()).isEqualTo(QUANTITY);
        assertThat(target.getItemCategory()).isEqualTo(CATEGORY);
        assertThat(target.getItemSubCategory()).isEqualTo(SUB_CATEGORY);
    }
}

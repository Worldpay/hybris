package com.worldpay.converters.populators;

import com.worldpay.data.Product;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayProductPopulatorTest {

    private static final String CODE = "code";
    private static final String NAME = "name";
    private static final long QUANTITY = 1;
    private static final BigDecimal VALUE = new BigDecimal(10);
    public static final String SUBCATEGORY = "subcategory";
    public static final String CATEGORY = "category";

    @InjectMocks
    private WorldpayProductPopulator testObj;

    @Mock
    private Converter<ProductModel, ProductData> productPriceDataConverterMock;

    @Mock
    private AbstractOrderEntryModel sourceMock;
    @Mock
    private ProductModel productModelMock;
    @Mock
    private ProductData productDataMock;
    @Mock
    private PriceData priceDataMock;
    @Mock
    private CategoryModel subcategoryModel;
    @Mock
    private CategoryModel categoryModel;


    @Test(expected = IllegalArgumentException.class)
    public void populate_whenSourceIsNull_shouldThrowAnException() {
        testObj.populate(null, new com.worldpay.data.Product());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_whenTargetIsNull_shouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_whenSourceAndTargetAreNotNull_shouldPopulateProduct() {
        when(sourceMock.getQuantity()).thenReturn(QUANTITY);
        when(sourceMock.getProduct()).thenReturn(productModelMock);
        when(productModelMock.getCode()).thenReturn(CODE);
        when(productModelMock.getName()).thenReturn(NAME);
        when(productModelMock.getSupercategories()).thenReturn(Collections.singletonList(subcategoryModel));
        when(subcategoryModel.getName()).thenReturn(SUBCATEGORY);
        when(subcategoryModel.getSupercategories()).thenReturn(Collections.singletonList(categoryModel));
        when(categoryModel.getName()).thenReturn(CATEGORY);

        doReturn(productDataMock).when(productPriceDataConverterMock).convert(productModelMock);
        when(productDataMock.getPrice()).thenReturn(priceDataMock);
        when(priceDataMock.getValue()).thenReturn(VALUE);

        final Product target = new Product();

        testObj.populate(sourceMock, target);

        assertThat(target.getItemQuantity()).isEqualTo(String.valueOf(QUANTITY));
        assertThat(target.getItemCategory()).isEqualTo(CATEGORY);
        assertThat(target.getItemSubCategory()).isEqualTo(SUBCATEGORY);
        assertThat(target.getItemId()).isEqualTo(CODE);
        assertThat(target.getItemName()).isEqualTo(NAME);
        assertThat(target.getItemIsDigital()).isEqualTo(Boolean.FALSE.toString());
        assertThat(target.getItemPrice()).isEqualTo(String.valueOf(VALUE));

    }
}

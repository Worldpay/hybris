package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Amount;
import com.worldpay.data.Item;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ItemPopulatorTest {

    private static final String PRODUCT_CODE = "productCode";
    private static final String COMMODITY_CODE = "commodityCode";
    private static final String QUANTITY = "quantity";
    private static final String UNIT_OF_MEASURE = "unitOfMeasure";
    private static final String DESCRIPTION = "description";

    @InjectMocks
    private ItemPopulator testObj;

    @Mock
    private Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverterMock;

    @Mock
    private Item sourceMock;
    @Mock
    private Amount unitCostMock, itemTotalMock, itemTotalWithTaxMock, itemDiscountAmountMock, taxAmountMock;
    @Mock
    private com.worldpay.internal.model.Amount intUnitCostMock, intItemTotalMock, intItemTotalWithTaxMock, intItemDiscountAmountMock, intTaxAmountMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.Item());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenDescriptionIsNull_ShouldNotPopulateDescription() {
        when(sourceMock.getDescription()).thenReturn(null);

        final com.worldpay.internal.model.Item target = new com.worldpay.internal.model.Item();
        testObj.populate(sourceMock, target);

        assertThat(target.getDescription()).isNull();
    }

    @Test
    public void populate_WhenUnitCostIsNull_ShouldNotPopulateUnitCost() {
        when(sourceMock.getUnitCost()).thenReturn(null);

        final com.worldpay.internal.model.Item target = new com.worldpay.internal.model.Item();
        testObj.populate(sourceMock, target);

        assertThat(target.getUnitCost()).isNull();
    }

    @Test
    public void populate_WhenItemTotalIsNull_ShouldNotPopulateItemTotal() {
        when(sourceMock.getItemTotal()).thenReturn(null);

        final com.worldpay.internal.model.Item target = new com.worldpay.internal.model.Item();
        testObj.populate(sourceMock, target);

        assertThat(target.getItemTotal()).isNull();
    }

    @Test
    public void populate_WhenItemTotalWithTaxIsNull_ShouldNotPopulateItemTotalWithTax() {
        when(sourceMock.getItemTotalWithTax()).thenReturn(null);

        final com.worldpay.internal.model.Item target = new com.worldpay.internal.model.Item();
        testObj.populate(sourceMock, target);

        assertThat(target.getItemTotalWithTax()).isNull();
    }

    @Test
    public void populate_WhenItemDiscountAmountIsNull_ShouldNotPopulateItemDiscountAmount() {
        when(sourceMock.getItemDiscountAmount()).thenReturn(null);

        final com.worldpay.internal.model.Item target = new com.worldpay.internal.model.Item();
        testObj.populate(sourceMock, target);

        assertThat(target.getItemDiscountAmount()).isNull();
    }

    @Test
    public void populate_WhenTaxAmountNull_ShouldNotPopulateTaxAmount() {
        when(sourceMock.getTaxAmount()).thenReturn(null);

        final com.worldpay.internal.model.Item target = new com.worldpay.internal.model.Item();
        testObj.populate(sourceMock, target);

        assertThat(target.getTaxAmount()).isNull();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(internalAmountConverterMock.convert(unitCostMock)).thenReturn(intUnitCostMock);
        when(internalAmountConverterMock.convert(itemTotalMock)).thenReturn(intItemTotalMock);
        when(internalAmountConverterMock.convert(itemTotalWithTaxMock)).thenReturn(intItemTotalWithTaxMock);
        when(internalAmountConverterMock.convert(itemDiscountAmountMock)).thenReturn(intItemDiscountAmountMock);
        when(internalAmountConverterMock.convert(taxAmountMock)).thenReturn(intTaxAmountMock);
        when(sourceMock.getProductCode()).thenReturn(PRODUCT_CODE);
        when(sourceMock.getCommodityCode()).thenReturn(COMMODITY_CODE);
        when(sourceMock.getQuantity()).thenReturn(QUANTITY);
        when(sourceMock.getUnitOfMeasure()).thenReturn(UNIT_OF_MEASURE);
        when(sourceMock.getDescription()).thenReturn(DESCRIPTION);
        when(sourceMock.getUnitCost()).thenReturn(unitCostMock);
        when(sourceMock.getItemTotal()).thenReturn(itemTotalMock);
        when(sourceMock.getItemTotalWithTax()).thenReturn(itemTotalWithTaxMock);
        when(sourceMock.getItemDiscountAmount()).thenReturn(itemDiscountAmountMock);
        when(sourceMock.getTaxAmount()).thenReturn(taxAmountMock);

        final com.worldpay.internal.model.Item target = new com.worldpay.internal.model.Item();
        testObj.populate(sourceMock, target);

        assertThat(target.getProductCode()).isEqualTo(PRODUCT_CODE);
        assertThat(target.getCommodityCode()).isEqualTo(COMMODITY_CODE);
        assertThat(target.getQuantity()).isEqualTo(QUANTITY);
        assertThat(target.getUnitOfMeasure()).isEqualTo(UNIT_OF_MEASURE);
        assertThat(target.getDescription().getvalue()).isEqualTo(DESCRIPTION);
        assertThat(target.getUnitCost().getAmount()).isEqualTo(intUnitCostMock);
        assertThat(target.getItemTotal().getAmount()).isEqualTo(intItemTotalMock);
        assertThat(target.getItemTotalWithTax().getAmount()).isEqualTo(intItemTotalWithTaxMock);
        assertThat(target.getItemDiscountAmount().getAmount()).isEqualTo(intItemDiscountAmountMock);
        assertThat(target.getTaxAmount().getAmount()).isEqualTo(intTaxAmountMock);
    }
}

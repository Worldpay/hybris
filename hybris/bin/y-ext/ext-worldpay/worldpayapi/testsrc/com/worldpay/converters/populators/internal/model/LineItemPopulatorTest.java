package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.LineItem;
import com.worldpay.data.LineItemReference;
import com.worldpay.enums.lineItem.LineItemType;
import com.worldpay.internal.model.Reference;
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
public class LineItemPopulatorTest {

    private static final String NAME = "name";
    private static final String QUANTITY = "quantity";
    private static final String QUANTITY_UNIT = "quantityUnit";
    private static final String TAX_RATE = "taxRate";
    private static final String TOTAL_AMOUNT = "totalAmount";
    private static final String TOTAL_DISCOUNT_AMOUNT = "totalDiscountAmount";
    private static final String TOTAL_TAX_AMOUNT = "totalTaxAmount";

    @InjectMocks
    private LineItemPopulator testObj;

    @Mock
    private Converter<LineItemReference, com.worldpay.internal.model.Reference> internalReferencePopulator;

    @Mock
    private LineItem sourceMock;
    @Mock
    private Reference intReferenceMock;
    @Mock
    private LineItemReference lineItemReferenceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowException() {
        testObj.populate(null, new com.worldpay.internal.model.LineItem());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenLineItemReferenceIsNull_ShouldNotPopulate() {
        when(sourceMock.getLineItemReference()).thenReturn(null);

        final com.worldpay.internal.model.LineItem targetMock = new com.worldpay.internal.model.LineItem();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getReference()).isNull();
    }

    @Test
    public void populate_WhenLineItemTypeIsNull_ShouldNotPopulate() {
        when(sourceMock.getLineItemType()).thenReturn(null);

        final com.worldpay.internal.model.LineItem targetMock = new com.worldpay.internal.model.LineItem();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.
            getPhysicalOrDiscountOrShippingFeeOrDigitalOrGiftCardOrSalesTaxTypeOrStoreCreditOrSurcharge())
            .isEmpty();
    }

    @Test
    public void populator_ShouldPopulateLineItem() {
        when(sourceMock.getName()).thenReturn(NAME);
        when(sourceMock.getQuantity()).thenReturn(QUANTITY);
        when(sourceMock.getQuantityUnit()).thenReturn(QUANTITY_UNIT);
        when(sourceMock.getTaxRate()).thenReturn(TAX_RATE);
        when(sourceMock.getTotalAmount()).thenReturn(TOTAL_AMOUNT);
        when(sourceMock.getTotalDiscountAmount()).thenReturn(TOTAL_DISCOUNT_AMOUNT);
        when(sourceMock.getTotalTaxAmount()).thenReturn(TOTAL_TAX_AMOUNT);
        when(sourceMock.getLineItemReference()).thenReturn(lineItemReferenceMock);
        when(internalReferencePopulator.convert(sourceMock.getLineItemReference())).thenReturn(intReferenceMock);
        when(sourceMock.getLineItemType()).thenReturn(LineItemType.DISCOUNT);

        final com.worldpay.internal.model.LineItem targetMock = new com.worldpay.internal.model.LineItem();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getName()).isEqualTo(NAME);
        assertThat(targetMock.getQuantity()).isEqualTo(QUANTITY);
        assertThat(targetMock.getQuantityUnit()).isEqualTo(QUANTITY_UNIT);
        assertThat(targetMock.getTaxRate()).isEqualTo(TAX_RATE);
        assertThat(targetMock.getTotalAmount()).isEqualTo(TOTAL_AMOUNT);
        assertThat(targetMock.getTotalDiscountAmount()).isEqualTo(TOTAL_DISCOUNT_AMOUNT);
        assertThat(targetMock.getTotalTaxAmount()).isEqualTo(TOTAL_TAX_AMOUNT);
    }
}

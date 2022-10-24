package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.LineItem;
import com.worldpay.data.OrderLines;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderLinesPopulatorTest {

    private static final String ORDER_TAX_AMOUNT = "orderTaxAmount";
    private static final String TERMS_URL = "termsURL";

    @InjectMocks
    private OrderLinesPopulator testObj;

    @Mock
    private Converter<LineItem, com.worldpay.internal.model.LineItem> internalLineItemConverter;

    @Mock
    private OrderLines sourceMock;
    @Mock
    private LineItem lineItemsMock;
    @Mock
    private com.worldpay.internal.model.LineItem internalLineItemMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowException() {
        testObj.populate(null, new com.worldpay.internal.model.OrderLines());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenLineItemsIsNull_ShouldNotPopulate() {
       when(sourceMock.getLineItems()).thenReturn(Collections.emptyList());

       final com.worldpay.internal.model.OrderLines targetMock = new com.worldpay.internal.model.OrderLines();
       testObj.populate(sourceMock, targetMock);

       assertThat(targetMock.getLineItem()).isEmpty();
    }


    @Test
    public void populate_ShouldPopulateOrderLines() {
        when(sourceMock.getOrderTaxAmount()).thenReturn(ORDER_TAX_AMOUNT);
        when(sourceMock.getTermsURL()).thenReturn(TERMS_URL);
        when(sourceMock.getLineItems()).thenReturn(List.of(lineItemsMock));
        when(internalLineItemConverter.convertAll(sourceMock.getLineItems())).thenReturn(List.of(internalLineItemMock));

        final com.worldpay.internal.model.OrderLines targetMock = new com.worldpay.internal.model.OrderLines();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getOrderTaxAmount()).isEqualTo(ORDER_TAX_AMOUNT);
        assertThat(targetMock.getTermsURL()).isEqualTo(TERMS_URL);
        assertThat(targetMock.getLineItem()).isNotEmpty();
    }
}

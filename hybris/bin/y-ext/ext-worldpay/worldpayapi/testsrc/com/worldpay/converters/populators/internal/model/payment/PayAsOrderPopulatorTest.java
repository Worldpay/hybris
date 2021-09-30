package com.worldpay.converters.populators.internal.model.payment;

import com.worldpay.data.Amount;
import com.worldpay.data.payment.PayAsOrder;
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
public class PayAsOrderPopulatorTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String ORDER_CODE = "orderCode";
    private static final String CVC = "cvc";

    @InjectMocks
    private PayAsOrderPopulator testObj;

    @Mock
    private Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverterMock;

    @Mock
    private PayAsOrder sourceMock;
    @Mock
    private Amount amountMock;
    @Mock
    private com.worldpay.internal.model.Amount intAmountMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.PayAsOrder());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenGetOriginalMerchantCodeIsNull_ShouldNotPopulateMerchantCode() {
        when(sourceMock.getOriginalMerchantCode()).thenReturn(null);

        final com.worldpay.internal.model.PayAsOrder targetMock = new com.worldpay.internal.model.PayAsOrder();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getMerchantCode()).isNull();
    }

    @Test
    public void populate_WhenGetOriginalOrderCodeIsNull_ShouldNotPopulateOrderCode() {
        when(sourceMock.getOriginalOrderCode()).thenReturn(null);

        final com.worldpay.internal.model.PayAsOrder targetMock = new com.worldpay.internal.model.PayAsOrder();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getOrderCode()).isNull();
    }

    @Test
    public void populate_WhenGetAmountIsNull_ShouldNotPopulateAmount() {
        when(sourceMock.getAmount()).thenReturn(null);

        final com.worldpay.internal.model.PayAsOrder targetMock = new com.worldpay.internal.model.PayAsOrder();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getAmount()).isNull();
    }

    @Test
    public void populate_WhenGetCvcIsNull_ShouldNotPopulateCvc() {
        when(sourceMock.getCvc()).thenReturn(null);

        final com.worldpay.internal.model.PayAsOrder targetMock = new com.worldpay.internal.model.PayAsOrder();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getCvc()).isNull();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.getOriginalMerchantCode()).thenReturn(MERCHANT_CODE);
        when(sourceMock.getOriginalOrderCode()).thenReturn(ORDER_CODE);
        when(sourceMock.getAmount()).thenReturn(amountMock);
        when(internalAmountConverterMock.convert(amountMock)).thenReturn(intAmountMock);
        when(sourceMock.getCvc()).thenReturn(CVC);

        final com.worldpay.internal.model.PayAsOrder targetMock = new com.worldpay.internal.model.PayAsOrder();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getMerchantCode()).isEqualTo(MERCHANT_CODE);
        assertThat(targetMock.getOrderCode()).isEqualTo(ORDER_CODE);
        assertThat(targetMock.getAmount()).isEqualTo(intAmountMock);
        assertThat(targetMock.getCvc()).isEqualTo(CVC);
    }
}

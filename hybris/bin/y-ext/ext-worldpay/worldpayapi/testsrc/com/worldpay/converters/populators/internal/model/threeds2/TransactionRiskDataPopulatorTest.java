package com.worldpay.converters.populators.internal.model.threeds2;

import com.worldpay.data.Date;
import com.worldpay.data.threeds2.TransactionRiskData;
import com.worldpay.data.threeds2.TransactionRiskDataGiftCardAmount;
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
public class TransactionRiskDataPopulatorTest {

    private static final String DELIVERY_EMAIL_ADDRESS = "deliveryEmailAddress";
    private static final String DELIVERY_TIMEFRAME = "deliveryTimeframe";
    private static final String GIFT_CARD_COUNT = "giftCardCount";
    private static final String PRE_ORDER_PURCHASE = "preOrderPurchase";
    private static final String REORDERING_PREVIOUS_PURCHASES = "reorderingPreviousPurchases";
    private static final String SHIPPING_METHOD = "shippingMethod";

    @InjectMocks
    private TransactionRiskDataPopulator testObj;

    @Mock
    private Converter<Date, com.worldpay.internal.model.Date> internalDateConverterMock;
    @Mock
    private Converter<TransactionRiskDataGiftCardAmount, com.worldpay.internal.model.TransactionRiskDataGiftCardAmount> internalTransactionRiskDataGiftCardAmountConverterMock;

    @Mock
    private TransactionRiskData sourceMock;
    @Mock
    private Date dateMock;
    @Mock
    private com.worldpay.internal.model.Date internalDateMock;
    @Mock
    private TransactionRiskDataGiftCardAmount amountMock;
    @Mock
    private com.worldpay.internal.model.TransactionRiskDataGiftCardAmount internalAmountMock;

    @Before
    public void setup() {
        testObj = new TransactionRiskDataPopulator(internalDateConverterMock, internalTransactionRiskDataGiftCardAmountConverterMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.TransactionRiskData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenDateIsNull_ShouldNotPopulateInternalDate() {
        when(sourceMock.getTransactionRiskDataPreOrderDate()).thenReturn(null);
        when(internalDateConverterMock.convert(sourceMock.getTransactionRiskDataPreOrderDate())).thenReturn(internalDateMock);

        final com.worldpay.internal.model.TransactionRiskData targetMock = new com.worldpay.internal.model.TransactionRiskData();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getTransactionRiskDataPreOrderDate()).isNull();
    }

    @Test
    public void populate_WhenAmountIsNull_ShouldNotPopulateAmount() {
        when(sourceMock.getTransactionRiskDataGiftCardAmount()).thenReturn(null);
        when(internalTransactionRiskDataGiftCardAmountConverterMock.convert(sourceMock.getTransactionRiskDataGiftCardAmount())).thenReturn(internalAmountMock);

        final com.worldpay.internal.model.TransactionRiskData targetMock = new com.worldpay.internal.model.TransactionRiskData();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getTransactionRiskDataGiftCardAmount()).isNull();
    }

    @Test
    public void populate_ShouldPopulateTransactionRiskData() {
        when(sourceMock.getDeliveryEmailAddress()).thenReturn(DELIVERY_EMAIL_ADDRESS);
        when(sourceMock.getDeliveryTimeframe()).thenReturn(DELIVERY_TIMEFRAME);
        when(sourceMock.getGiftCardCount()).thenReturn(GIFT_CARD_COUNT);
        when(sourceMock.getPreOrderPurchase()).thenReturn(PRE_ORDER_PURCHASE);
        when(sourceMock.getReorderingPreviousPurchases()).thenReturn(REORDERING_PREVIOUS_PURCHASES);
        when(sourceMock.getShippingMethod()).thenReturn(SHIPPING_METHOD);
        when(sourceMock.getTransactionRiskDataPreOrderDate()).thenReturn(dateMock);
        when(sourceMock.getTransactionRiskDataGiftCardAmount()).thenReturn(amountMock);

        when(internalDateConverterMock.convert(dateMock)).thenReturn(internalDateMock);
        when(internalTransactionRiskDataGiftCardAmountConverterMock.convert(amountMock)).thenReturn(internalAmountMock);

        final com.worldpay.internal.model.TransactionRiskData targetMock = new com.worldpay.internal.model.TransactionRiskData();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getDeliveryEmailAddress()).isEqualTo(DELIVERY_EMAIL_ADDRESS);
        assertThat(targetMock.getDeliveryTimeframe()).isEqualTo(DELIVERY_TIMEFRAME);
        assertThat(targetMock.getGiftCardCount()).isEqualTo(GIFT_CARD_COUNT);
        assertThat(targetMock.getPreOrderPurchase()).isEqualTo(PRE_ORDER_PURCHASE);
        assertThat(targetMock.getReorderingPreviousPurchases()).isEqualTo(REORDERING_PREVIOUS_PURCHASES);
        assertThat(targetMock.getShippingMethod()).isEqualTo(SHIPPING_METHOD);
        assertThat(targetMock.getTransactionRiskDataPreOrderDate().getDate()).isEqualTo(internalDateMock);
        assertThat(targetMock.getTransactionRiskDataGiftCardAmount()).isEqualTo(internalAmountMock);
    }
}

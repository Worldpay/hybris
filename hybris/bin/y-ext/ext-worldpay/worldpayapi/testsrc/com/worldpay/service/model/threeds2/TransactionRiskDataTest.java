package com.worldpay.service.model.threeds2;

import com.worldpay.service.model.Date;
import de.hybris.bootstrap.annotations.UnitTest;
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
public class TransactionRiskDataTest {

    @InjectMocks
    private TransactionRiskData testObj;

    @Mock
    private Date transactionRiskDataPreOrderDateMock;
    @Mock
    private TransactionRiskDataGiftCardAmount transactionRiskDataGiftCardAmountMock;

    @Mock
    private com.worldpay.internal.model.Date intTransactionRiskDataPreOrderDateMock;
    @Mock
    private com.worldpay.internal.model.TransactionRiskDataGiftCardAmount intTransactionRiskDataGiftCardAmountMock;

    @Before
    public void setUp() throws Exception {
        when(transactionRiskDataPreOrderDateMock.transformToInternalModel()).thenReturn(intTransactionRiskDataPreOrderDateMock);
        when(transactionRiskDataGiftCardAmountMock.transformToInternalModel()).thenReturn(intTransactionRiskDataGiftCardAmountMock);
    }

    @Test
    public void transformToInternalModel_ShouldAddAllMembersOfTransactionRiskData() {
        testObj.setDeliveryEmailAddress("deliveryEmailAddress");
        testObj.setDeliveryTimeframe("deliveryTimeframe");
        testObj.setGiftCardCount("giftCardCount");
        testObj.setPreOrderPurchase("preOrderPurchase");
        testObj.setReorderingPreviousPurchases("reorderingPreviousPurchases");
        testObj.setShippingMethod("shippingMethod");

        final com.worldpay.internal.model.TransactionRiskData result = testObj.transformToInternalModel();

        assertThat(result.getDeliveryEmailAddress()).isEqualTo("deliveryEmailAddress");
        assertThat(result.getDeliveryTimeframe()).isEqualTo("deliveryTimeframe");
        assertThat(result.getGiftCardCount()).isEqualTo("giftCardCount");
        assertThat(result.getPreOrderPurchase()).isEqualTo("preOrderPurchase");
        assertThat(result.getReorderingPreviousPurchases()).isEqualTo("reorderingPreviousPurchases");
        assertThat(result.getShippingMethod()).isEqualTo("shippingMethod");

        assertThat(result.getTransactionRiskDataPreOrderDate().getDate()).isEqualTo(intTransactionRiskDataPreOrderDateMock);
        assertThat(result.getTransactionRiskDataGiftCardAmount()).isEqualTo(intTransactionRiskDataGiftCardAmountMock);
    }

    @Test
    public void transformToInternalModel_ShouldNotAddPreorderDate_WhenEmpty() {
        testObj.setTransactionRiskDataPreOrderDate(null);

        final com.worldpay.internal.model.TransactionRiskData result = testObj.transformToInternalModel();

        assertThat(result.getTransactionRiskDataPreOrderDate()).isNull();
    }

    @Test
    public void transformToInternalModel_ShouldNotAddGiftCardAmount_WhenEmpty() {
        testObj.setTransactionRiskDataGiftCardAmount(null);

        final com.worldpay.internal.model.TransactionRiskData result = testObj.transformToInternalModel();

        assertThat(result.getTransactionRiskDataGiftCardAmount()).isNull();
    }
}

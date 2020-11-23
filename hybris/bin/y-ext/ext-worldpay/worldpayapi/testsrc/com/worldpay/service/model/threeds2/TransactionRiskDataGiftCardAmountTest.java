package com.worldpay.service.model.threeds2;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.service.model.Amount;
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
public class TransactionRiskDataGiftCardAmountTest {

    @InjectMocks
    private TransactionRiskDataGiftCardAmount testObj;

    @Mock
    private Amount amountMock;
    @Mock
    private com.worldpay.internal.model.Amount intAmountMock;

    @Test
    public void transformToInternalModel_ShouldReturnInternalTransactionRiskDataGiftCardAmount() {
        when(amountMock.transformToInternalModel()).thenReturn(intAmountMock);

        final var result = testObj.transformToInternalModel();

        assertThat(result.getAmount()).isEqualTo(intAmountMock);

    }
}

package com.worldpay.converters.populators.internal.model.threeds2;

import com.worldpay.data.Amount;
import com.worldpay.data.threeds2.TransactionRiskDataGiftCardAmount;
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
public class TransactionRiskDataGiftCardAmountPopulatorTest {

    @InjectMocks
    private TransactionRiskDataGiftCardAmountPopulator testObj;

    @Mock
    private Converter<Amount, com.worldpay.internal.model.Amount> internalAmountConverterMock;

    @Mock
    private TransactionRiskDataGiftCardAmount sourceMock;
    @Mock
    private Amount amountMock;
    @Mock
    private com.worldpay.internal.model.Amount internalAmountMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.TransactionRiskDataGiftCardAmount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_ShouldPopulateTransactionRiskDataGiftCardAmount() {
        when(sourceMock.getAmount()).thenReturn(amountMock);
        when(internalAmountConverterMock.convert(amountMock)).thenReturn(internalAmountMock);

        final com.worldpay.internal.model.TransactionRiskDataGiftCardAmount targetMock = new com.worldpay.internal.model.TransactionRiskDataGiftCardAmount();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getAmount()).isEqualTo(internalAmountMock);
    }

    @Test
    public void populate_WhenAmountIsNull_ShouldNotPopulateAmount() {
        when(sourceMock.getAmount()).thenReturn(null);
        when(internalAmountConverterMock.convert(sourceMock.getAmount())).thenReturn(internalAmountMock);

        final com.worldpay.internal.model.TransactionRiskDataGiftCardAmount targetMock = new com.worldpay.internal.model.TransactionRiskDataGiftCardAmount();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getAmount()).isNull();
    }
}

package com.worldpay.converters.populators.reverse.internal.model;

import com.worldpay.enums.DebitCreditIndicator;
import com.worldpay.internal.model.Amount;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AmountReversePopulatorTest {

    @InjectMocks
    private AmountReversePopulator testObj;

    @Mock
    private Amount sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.data.Amount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_ShouldPopulateAmount() {
        when(sourceMock.getCurrencyCode()).thenReturn("EUR");
        when(sourceMock.getExponent()).thenReturn("2");
        when(sourceMock.getValue()).thenReturn("100");
        when(sourceMock.getDebitCreditIndicator()).thenReturn("CREDIT");

        final com.worldpay.data.Amount target = new com.worldpay.data.Amount();
        testObj.populate(sourceMock, target);

        assertThat(target.getCurrencyCode()).isEqualTo("EUR");
        assertThat(target.getExponent()).isEqualTo("2");
        assertThat(target.getValue()).isEqualTo("100");
        assertThat(target.getDebitCreditIndicator()).isEqualTo(DebitCreditIndicator.CREDIT);
    }


}

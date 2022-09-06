package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Amount;
import com.worldpay.enums.DebitCreditIndicator;
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
public class AmountPopulatorTest {

    private static final String CREDIT = "credit";
    private static final String VALUE = "value";
    private static final String CURRENCY_CODE = "currencyCode";
    private static final String EXPONENT = "exponent";

    @InjectMocks
    private AmountPopulator testObj;

    @Mock
    private Amount sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.Amount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenDebitCreditIndicatorIsNull_ShouldNotPopulateDebitCreditIndicator() {
        when(sourceMock.getDebitCreditIndicator()).thenReturn(null);

        final com.worldpay.internal.model.Amount target = new com.worldpay.internal.model.Amount();
        testObj.populate(sourceMock, target);

        assertThat(target.getDebitCreditIndicator()).isEqualTo(CREDIT);
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.getValue()).thenReturn(VALUE);
        when(sourceMock.getCurrencyCode()).thenReturn(CURRENCY_CODE);
        when(sourceMock.getExponent()).thenReturn(EXPONENT);
        when(sourceMock.getDebitCreditIndicator()).thenReturn(DebitCreditIndicator.CREDIT);

        final com.worldpay.internal.model.Amount target = new com.worldpay.internal.model.Amount();
        testObj.populate(sourceMock, target);

        assertThat(target.getValue()).isEqualTo(VALUE);
        assertThat(target.getCurrencyCode()).isEqualTo(CURRENCY_CODE);
        assertThat(target.getExponent()).isEqualTo(EXPONENT);
        assertThat(target.getDebitCreditIndicator()).isEqualTo(DebitCreditIndicator.CREDIT.getCode());
    }
}

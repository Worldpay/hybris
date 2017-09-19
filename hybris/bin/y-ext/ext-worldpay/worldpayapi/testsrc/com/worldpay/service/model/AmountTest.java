package com.worldpay.service.model;

import com.worldpay.internal.helper.InternalModelObject;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@UnitTest
public class AmountTest {

    private static final String VALUE = "value";
    private static final String CURRENCY_CODE = "currencyCode";
    private static final String EXPONENT = "exponent";
    private static final DebitCreditIndicator DEBIT_CREDIT_INDICATOR = DebitCreditIndicator.DEBIT;

    @Test
    public void shouldReturnAnAmount() {
        final Amount amount = new Amount(null, null, null);

        final InternalModelObject result = amount.transformToInternalModel();

        assertTrue(result instanceof com.worldpay.internal.model.Amount);
    }

    @Test
    public void shouldSetValueOnAmount() {
        final Amount amount = new Amount(VALUE, null, null);

        final com.worldpay.internal.model.Amount result = (com.worldpay.internal.model.Amount) amount.transformToInternalModel();

        assertEquals(VALUE, result.getValue());
    }

    @Test
    public void shouldSetCurrencyCodeOnAmount() {
        final Amount amount = new Amount(null, CURRENCY_CODE, null);

        final com.worldpay.internal.model.Amount result = (com.worldpay.internal.model.Amount) amount.transformToInternalModel();

        assertEquals(CURRENCY_CODE, result.getCurrencyCode());
    }

    @Test
    public void shouldSetExponentOnAmount() {
        final Amount amount = new Amount(null, null, EXPONENT);

        final com.worldpay.internal.model.Amount result = (com.worldpay.internal.model.Amount) amount.transformToInternalModel();

        assertEquals(EXPONENT, result.getExponent());
    }

    @Test
    public void shouldSetCreditOnAmountIfDebitCreditIndicatorIsNotSupplied() {
        final Amount amount = new Amount(null, null, null);

        final com.worldpay.internal.model.Amount result = (com.worldpay.internal.model.Amount) amount.transformToInternalModel();

        assertEquals(DebitCreditIndicator.CREDIT.getCode(), result.getDebitCreditIndicator());
    }

    @Test
    public void shouldSetDebitCreditIndicatorOnAmount() {
        final Amount amount = new Amount(null, null, null, DEBIT_CREDIT_INDICATOR);

        final com.worldpay.internal.model.Amount result = (com.worldpay.internal.model.Amount) amount.transformToInternalModel();

        assertEquals(DEBIT_CREDIT_INDICATOR.getCode(), result.getDebitCreditIndicator());
    }

    @Test
    public void shouldSetCreditOnAmountIfDebitCreditIndicatorIsNull() {
        final Amount amount = new Amount(null, null, null, null);

        final com.worldpay.internal.model.Amount result = (com.worldpay.internal.model.Amount) amount.transformToInternalModel();

        // We're actually testing, that the getter of debitCreditIndicator defaults to CREDIT...
        assertEquals(DebitCreditIndicator.CREDIT.getCode(), result.getDebitCreditIndicator());
    }
}

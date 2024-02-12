package com.worldpay.transaction.impl;

import com.worldpay.data.Amount;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.transaction.EntryCodeStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Map;

import static de.hybris.platform.payment.enums.PaymentTransactionType.*;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultWorldpayPaymentTransactionUtilsTest {

    private static final String TRANSACTION_CODE = "transactionEntryCode";
    private static final String CURRENCY_CODE = "currencyCode";

    @InjectMocks
    private DefaultWorldpayPaymentTransactionUtils testObj;

    @Mock
    private EntryCodeStrategy entryCodeStrategyMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock
    private CommonI18NService commonI18NServiceMock;
    @Mock
    private CurrencyModel currencyModelMock;

    @Mock
    private Amount amountMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(testObj, "paymentTransactionDependency", Map.of(
            CAPTURE, AUTHORIZATION,
            SETTLED, CAPTURE
        ));
        when(entryCodeStrategyMock.generateCode(paymentTransactionModelMock)).thenReturn(TRANSACTION_CODE);
        when(worldpayOrderServiceMock.convertAmount(amountMock)).thenReturn(BigDecimal.TEN);
        when(amountMock.getCurrencyCode()).thenReturn(CURRENCY_CODE);
        when(commonI18NServiceMock.getCurrency(CURRENCY_CODE)).thenReturn(currencyModelMock);
        when(configurationServiceMock.getConfiguration().getDouble("worldpayapi.authoriseamount.validation.tolerance")).thenReturn(0.01);
    }

    @Test
    public void generatePaymentTransactionCode_ShouldReturnTheGeneratedCode() {
        final String result = testObj.generatePaymentTransactionCode(paymentTransactionModelMock);

        assertThat(result).isEqualTo(TRANSACTION_CODE);
    }

    @Test
    public void getPaymentTransactionDependency_ShouldReturnTheMap() {
        Map<PaymentTransactionType, PaymentTransactionType> result = testObj.getPaymentTransactionDependency();
        assertThat(result).containsKeys(CAPTURE, SETTLED);
        assertThat(result).containsValues(AUTHORIZATION, CAPTURE);
    }

    @Test
    public void getCurrencyFromAmount_ShouldreturnTheCurrency() {
        final CurrencyModel result = testObj.getCurrencyFromAmount(amountMock);

        assertThat(result).isEqualTo(currencyModelMock);
    }

    @Test
    public void getAuthoriseAmountToleranceFromConfig_ShouldReturnConfigValue() {
        final double result = testObj.getAuthoriseAmountToleranceFromConfig();

        assertThat(result).isEqualTo(0.01);
    }
}

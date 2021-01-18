package com.worldpay.converters;

import com.worldpay.service.model.Amount;
import com.worldpay.service.model.ErrorDetail;
import com.worldpay.service.payment.WorldpayOrderService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static de.hybris.platform.payment.dto.TransactionStatusDetails.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayAbstractServiceResponseConverterTest {

    private WorldpayAbstractServiceResponseConverter testObj;

    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;

    @Mock
    private Amount amountMock;

    private BigDecimal convertedAmount = BigDecimal.TEN;

    @Before
    public void setUp() throws Exception {
        testObj = Mockito.mock(
            WorldpayAbstractServiceResponseConverter.class,
            Mockito.CALLS_REAL_METHODS);
        Whitebox.setInternalState(testObj, "worldpayOrderService", worldpayOrderServiceMock);

        when(worldpayOrderServiceMock.convertAmount(amountMock)).thenReturn(convertedAmount);
    }

    @Test
    public void getTotalAmount_ShouldGetTheConvertedAmount() {
        final BigDecimal result = testObj.getTotalAmount(amountMock);

        assertThat(result).isEqualTo(convertedAmount);
    }

    @Test
    public void getTransactionStatusDetails_WhenErrorCode1_ShouldReturnERROR_INTERNAL() {
        final TransactionStatusDetails result = testObj.getTransactionStatusDetails(new ErrorDetail("1", "error"));

        assertThat(result).isEqualTo(ERROR_INTERNAL);
    }

    @Test
    public void getTransactionStatusDetails_WhenErrorCode2_ShouldReturnERROR_PARSE() {
        final TransactionStatusDetails result = testObj.getTransactionStatusDetails(new ErrorDetail("2", "error"));

        assertThat(result).isEqualTo(ERROR_PARSE);
    }

    @Test
    public void getTransactionStatusDetails_WhenErrorCode3_ShouldReturnERROR_ORDER_AMOUNT() {
        final TransactionStatusDetails result = testObj.getTransactionStatusDetails(new ErrorDetail("3", "error"));

        assertThat(result).isEqualTo(ERROR_ORDER_AMOUNT);
    }

    @Test
    public void getTransactionStatusDetails_WhenErrorCode4_ShouldReturnERROR_SECURITY() {
        final TransactionStatusDetails result = testObj.getTransactionStatusDetails(new ErrorDetail("4", "error"));

        assertThat(result).isEqualTo(ERROR_SECURITY);
    }

    @Test
    public void getTransactionStatusDetails_WhenErrorCode5_ShouldReturnERROR_INVALID_REQUEST() {
        final TransactionStatusDetails result = testObj.getTransactionStatusDetails(new ErrorDetail("5", "error"));

        assertThat(result).isEqualTo(ERROR_INVALID_REQUEST);
    }

    @Test
    public void getTransactionStatusDetails_WhenErrorCode6_ShouldReturnERROR_INVALID_CONTENT() {
        final TransactionStatusDetails result = testObj.getTransactionStatusDetails(new ErrorDetail("6", "error"));

        assertThat(result).isEqualTo(ERROR_INVALID_CONTENT);
    }

    @Test
    public void getTransactionStatusDetails_WhenErrorCode7_ShouldReturnERROR_PAYMENT_DETAILS() {
        final TransactionStatusDetails result = testObj.getTransactionStatusDetails(new ErrorDetail("7", "error"));

        assertThat(result).isEqualTo(ERROR_PAYMENT_DETAILS);
    }

    @Test
    public void getTransactionStatusDetails_WhenErrorCode8_ShouldReturnERROR_NOT_AVAILABLE() {
        final TransactionStatusDetails result = testObj.getTransactionStatusDetails(new ErrorDetail("8", "error"));

        assertThat(result).isEqualTo(ERROR_NOT_AVAILABLE);
    }

    @Test
    public void getTransactionStatusDetails_WhenErrorCode9_ShouldReturnERROR_IDEMPOTENCY_SERVICE() {
        final TransactionStatusDetails result = testObj.getTransactionStatusDetails(new ErrorDetail("9", "error"));

        assertThat(result).isEqualTo(ERROR_IDEMPOTENCY_SERVICE);
    }

    @Test
    public void getTransactionStatusDetails_WhenErrorCode10_ShouldReturnERROR_PRIME_ROUTING() {
        final TransactionStatusDetails result = testObj.getTransactionStatusDetails(new ErrorDetail("10", "error"));

        assertThat(result).isEqualTo(ERROR_PRIME_ROUTING);
    }

    @Test
    public void getTransactionStatusDetails_WhenErrorCode11_ShouldReturnERROR_L2_L3_DATA() {
        final TransactionStatusDetails result = testObj.getTransactionStatusDetails(new ErrorDetail("11", "error"));

        assertThat(result).isEqualTo(ERROR_L2_L3_DATA);
    }

    @Test
    public void getTransactionStatusDetails_WhenErrorCode12_ShouldReturnERROR_LODGING_DATA() {
        final TransactionStatusDetails result = testObj.getTransactionStatusDetails(new ErrorDetail("12", "error"));

        assertThat(result).isEqualTo(ERROR_LODGING_DATA);
    }

    @Test
    public void getTransactionStatusDetails_WhenErrorCode13_ShouldReturnERROR_SPLIT_AUTH() {
        final TransactionStatusDetails result = testObj.getTransactionStatusDetails(new ErrorDetail("13", "error"));

        assertThat(result).isEqualTo(ERROR_SPLIT_AUTH);
    }

    @Test
    public void getTransactionStatusDetails_WhenErrorCode14_ShouldReturnTheDefaultValue() {
        final TransactionStatusDetails result = testObj.getTransactionStatusDetails(new ErrorDetail("14", "error"));

        assertThat(result).isEqualTo(UNKNOWN_CODE);
    }

    @Test
    public void getTransactionStatusDetails_WhenErrorNull_ShouldReturnTheDefaultValue() {
        final TransactionStatusDetails result = testObj.getTransactionStatusDetails(null);

        assertThat(result).isEqualTo(UNKNOWN_CODE);
    }
}

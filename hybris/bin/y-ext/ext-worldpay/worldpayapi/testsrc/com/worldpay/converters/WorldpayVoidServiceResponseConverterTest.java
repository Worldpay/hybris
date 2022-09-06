package com.worldpay.converters;

import com.worldpay.service.response.CancelServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.commands.result.VoidResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatus.ERROR;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.ERROR_ORDER_AMOUNT;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.SUCCESFULL;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayVoidServiceResponseConverterTest {

    private static final String ORDER_CODE = "orderCode";

    @Spy
    @InjectMocks
    private WorldpayVoidServiceResponseConverter testObj;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CancelServiceResponse cancelServiceRequestMock;

    @Before
    public void setUp() {
        testObj.setTargetClass(VoidResult.class);
        when(cancelServiceRequestMock.getOrderCode()).thenReturn(ORDER_CODE);
    }

    @Test
    public void testConvert() {
        final VoidResult result = testObj.convert(cancelServiceRequestMock);

        assertEquals(ORDER_CODE, result.getRequestId());
        assertEquals(ACCEPTED, result.getTransactionStatus());
        assertEquals(SUCCESFULL, result.getTransactionStatusDetails());
    }

    @Test
    public void convert_whenResponseHasErrors_shouldSetTransactionStatusAndDetails() {
        when(cancelServiceRequestMock.isError()).thenReturn(true);
        when(cancelServiceRequestMock.getErrorDetail().getCode()).thenReturn("3");

        final VoidResult result = testObj.convert(cancelServiceRequestMock);

        assertEquals(ORDER_CODE, result.getRequestId());
        assertEquals(ERROR, result.getTransactionStatus());
        assertEquals(ERROR_ORDER_AMOUNT, result.getTransactionStatusDetails());
    }
}

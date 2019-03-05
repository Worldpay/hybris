package com.worldpay.fulfilmentprocess.actions.order;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Date;

import static de.hybris.platform.core.enums.OrderStatus.FRAUD_CHECKED;
import static de.hybris.platform.core.enums.OrderStatus.SUSPENDED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayOrderManualCheckedActionTest {

    private static final String OK = "OK";
    private static final String NOK = "NOK";
    private static final String UNDEFINED = "UNDEFINED";

    @InjectMocks
    private WorldpayOrderManualCheckedAction testObj;

    @Mock
    private OrderProcessModel orderProcessModelMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrderModel orderModelMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private OrderHistoryEntryModel orderHistoryEntryModelMock;
    @Mock
    private TimeService timeServiceMock;
    @Mock
    private PaymentInfoModel originalPaymentInfo;

    @Before
    public void setUp() {
        when(modelServiceMock.create(OrderHistoryEntryModel.class)).thenReturn(orderHistoryEntryModelMock);
        when(orderProcessModelMock.getOrder()).thenReturn(orderModelMock);
        when(timeServiceMock.getCurrentTime()).thenReturn(Date.from(Instant.now()));

    }

    @Test
    public void executeShouldReturnOK() {
        when(orderModelMock.getFraudulent()).thenReturn(false);

        final String result = testObj.execute(orderProcessModelMock);

        verify(modelServiceMock).save(orderHistoryEntryModelMock);
        verify(orderModelMock, never()).setStatus(SUSPENDED);
        verify(orderModelMock).setStatus(FRAUD_CHECKED);
        verify(modelServiceMock).save(orderModelMock);
        assertEquals(OK, result);
    }

    @Test
    public void executeShouldReturnNOK() {
        when(orderModelMock.getFraudulent()).thenReturn(true);
        when(orderModelMock.getPaymentInfo().getOriginal()).thenReturn(originalPaymentInfo);

        final String result = testObj.execute(orderProcessModelMock);

        verify(modelServiceMock).save(orderHistoryEntryModelMock);
        verify(orderModelMock).setStatus(SUSPENDED);
        verify(modelServiceMock).save(orderModelMock);
        assertEquals(NOK, result);
    }

    @Test
    public void executeShouldReturnUNDEFINED() {
        when(orderModelMock.getFraudulent()).thenReturn(null);

        final String result = testObj.execute(orderProcessModelMock);

        verify(modelServiceMock, never()).save(any(OrderHistoryEntryModel.class));
        verify(orderModelMock, never()).setStatus(any(OrderStatus.class));
        assertEquals(UNDEFINED, result);
    }

    @Test
    public void executeDeletePaymentInfoWhenCheckFraudAndPaymentSaved() {
        when(orderModelMock.getFraudulent()).thenReturn(true);
        when(orderModelMock.getPaymentInfo().getOriginal()).thenReturn(originalPaymentInfo);
        when(originalPaymentInfo.isSaved()).thenReturn(true);

        final String result = testObj.execute(orderProcessModelMock);

        verify(modelServiceMock).remove(originalPaymentInfo);
        assertEquals(NOK, result);
    }
}

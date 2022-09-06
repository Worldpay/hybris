package com.worldpay.cronjob;

import com.worldpay.core.services.WorldpayOrderModificationProcessService;
import com.worldpay.worldpaynotifications.model.OrderModificationCronJobModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static de.hybris.platform.payment.enums.PaymentTransactionType.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderModificationProcessorJobPerformableTest {

    @InjectMocks
    private OrderModificationProcessorJobPerformable testObj;

    @Mock
    private OrderModificationCronJobModel cronJobModelMock;
    @Mock
    private WorldpayOrderModificationProcessService worldpayOrderModificationProcessServiceMock;

    private Set<PaymentTransactionType> paymentTransactionTypes = Set.of(AUTHORIZATION, CAPTURE, CANCEL);

    @Test
    public void performShouldProcessAllProcessableModifications() {
        when(cronJobModelMock.getPaymentTransactionTypes()).thenReturn(paymentTransactionTypes);
        when(worldpayOrderModificationProcessServiceMock.processOrderModificationMessages(any(PaymentTransactionType.class))).thenReturn(true);

        final PerformResult result = testObj.perform(cronJobModelMock);

        assertEquals(CronJobResult.SUCCESS, result.getResult());
        assertEquals(CronJobStatus.FINISHED, result.getStatus());

        verify(worldpayOrderModificationProcessServiceMock).processOrderModificationMessages(AUTHORIZATION);
        verify(worldpayOrderModificationProcessServiceMock).processOrderModificationMessages(CAPTURE);
        verify(worldpayOrderModificationProcessServiceMock).processOrderModificationMessages(CANCEL);
    }

    @Test
    public void performShouldMarkErrorWhenFalseReturnedFromStrategy() {
        when(cronJobModelMock.getPaymentTransactionTypes()).thenReturn(paymentTransactionTypes);
        when(worldpayOrderModificationProcessServiceMock.processOrderModificationMessages(any(PaymentTransactionType.class))).thenReturn(false);

        final PerformResult result = testObj.perform(cronJobModelMock);

        assertEquals(CronJobResult.ERROR, result.getResult());
        assertEquals(CronJobStatus.FINISHED, result.getStatus());

        verify(worldpayOrderModificationProcessServiceMock).processOrderModificationMessages(AUTHORIZATION);
        verify(worldpayOrderModificationProcessServiceMock).processOrderModificationMessages(CAPTURE);
        verify(worldpayOrderModificationProcessServiceMock).processOrderModificationMessages(CANCEL);
    }

}

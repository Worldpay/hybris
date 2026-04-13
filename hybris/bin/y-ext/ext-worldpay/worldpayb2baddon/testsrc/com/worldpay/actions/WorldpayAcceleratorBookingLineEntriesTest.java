package com.worldpay.actions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2b.process.approval.actions.SetBookingLineEntries;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.SAPGenericPaymentInfoModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayAcceleratorBookingLineEntriesTest {

    @Spy
    @InjectMocks
    private WorldpayAcceleratorBookingLineEntries testObj;

    @Mock
    private ModelService modelServiceMock;

    @Mock
    private B2BApprovalProcessModel procssMock;
    @Mock
    private OrderModel orderMock;
    @Mock
    private PaymentInfoModel paymentInfoMock;
    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoMock;
    @Mock
    private SAPGenericPaymentInfoModel sapPGenericPaymentInfo;


    @Test
    public void executeAction_WithCreditCardPaymentType_ShouldReturnOK() throws RetryLaterException {
        when(procssMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoMock);
        when(orderMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);

        final SetBookingLineEntries.Transition result = testObj.executeAction(procssMock);

        assertEquals(SetBookingLineEntries.Transition.OK, result);
        verify(modelServiceMock).refresh(orderMock);
        verify(procssMock).getOrder();
        verify(orderMock).getPaymentInfo();
        verify(orderMock).getPaymentType();
    }

    @Test
    public void executeAction_WithAPMPaymentAndCardType_ShouldReturnOK() throws RetryLaterException {
        when(procssMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getIsApm()).thenReturn(Boolean.TRUE);
        when(orderMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);

        final SetBookingLineEntries.Transition result = testObj.executeAction(procssMock);

        assertEquals(SetBookingLineEntries.Transition.OK, result);
        verify(modelServiceMock).refresh(orderMock);
        verify(orderMock).getPaymentType();
    }

    @Test
    public void executeAction_WithSAPGenericPaymentAndCardType_ShouldReturnOK() throws RetryLaterException {
        when(procssMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenReturn(sapPGenericPaymentInfo);
        when(orderMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);

        final SetBookingLineEntries.Transition result = testObj.executeAction(procssMock);

        // Assert
        assertEquals(SetBookingLineEntries.Transition.OK, result);
        verify(modelServiceMock).refresh(orderMock);
        verify(orderMock).getPaymentType();
    }

    @Test
    public void executeAction_WithCardPaymentTypeButNoMatchingPaymentInfo_ShouldCallSuper() throws RetryLaterException {
        final SetBookingLineEntries.Transition expectedTransition = SetBookingLineEntries.Transition.NOK;
        doReturn(expectedTransition).when(testObj).executeAction(procssMock);

        final SetBookingLineEntries.Transition result = testObj.executeAction(procssMock);

        assertEquals(expectedTransition, result);
    }

    @Test
    public void executeAction_WithNonCardPaymentType_ShouldCallSuper() throws RetryLaterException {
        final SetBookingLineEntries.Transition expectedTransition = SetBookingLineEntries.Transition.NOK;
        doReturn(expectedTransition).when(testObj).executeAction(procssMock);

        final SetBookingLineEntries.Transition result = testObj.executeAction(procssMock);

        assertEquals(expectedTransition, result);
    }

    @Test
    public void executeAction_WithNullPaymentInfo_ShouldCallSuper() throws RetryLaterException {

        final SetBookingLineEntries.Transition expectedTransition = SetBookingLineEntries.Transition.NOK;
        doReturn(expectedTransition).when(testObj).executeAction(procssMock);

        final SetBookingLineEntries.Transition result = testObj.executeAction(procssMock);

        assertEquals(expectedTransition, result);
    }

    @Test
    public void executeAction_WithAPMFalseAndCardType_ShouldCallSuper() throws RetryLaterException {
        final SetBookingLineEntries.Transition expectedTransition = SetBookingLineEntries.Transition.NOK;
        doReturn(expectedTransition).when(testObj).executeAction(procssMock);

        final SetBookingLineEntries.Transition result = testObj.executeAction(procssMock);

        assertEquals(expectedTransition, result);
    }

    @Test(expected = RetryLaterException.class)
    public void executeAction_ShouldPropagateRetryLaterException() throws RetryLaterException {
        doThrow(new RetryLaterException()).when(testObj).executeAction(procssMock);

        testObj.executeAction(procssMock);
    }
}

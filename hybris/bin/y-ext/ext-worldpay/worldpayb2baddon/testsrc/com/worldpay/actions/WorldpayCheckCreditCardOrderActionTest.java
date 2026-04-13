package com.worldpay.actions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2b.process.approval.actions.AbstractSimpleB2BApproveOrderDecisionAction;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.SAPGenericPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
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
public class WorldpayCheckCreditCardOrderActionTest {

    @Spy
    @InjectMocks
    private WorldpayCheckCreditCardOrderAction testObj;

    @Mock
    private ModelService mockModelServiceMock;

    @Mock
    private B2BApprovalProcessModel mockProcessMock;
    @Mock
    private OrderModel orderMock;
    @Mock
    private PaymentInfoModel paymentInfoMock;
    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoMock;
    @Mock
    private SAPGenericPaymentInfoModel sapGenericPaymentInfoMock;

    @Test
    public void executeAction_WithCreditCardPaymentType_ShouldReturnOK() throws RetryLaterException {
        when(mockProcessMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoMock);
        when(orderMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.OK, result);
        verify(mockProcessMock).getOrder();
        verify(orderMock).getPaymentInfo();
        verify(orderMock).getPaymentType();
    }

    @Test
    public void executeAction_WithAPMPaymentAndCardType_ShouldReturnOK() throws RetryLaterException {
        when(mockProcessMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getIsApm()).thenReturn(Boolean.TRUE);
        when(orderMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.OK, result);
        verify(mockProcessMock).getOrder();
        verify(orderMock).getPaymentInfo();
        verify(orderMock).getPaymentType();
    }

    @Test
    public void executeAction_WithSAPGenericPaymentAndCardType_ShouldReturnOK() throws RetryLaterException {
        when(mockProcessMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenReturn(sapGenericPaymentInfoMock);
        when(orderMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.OK, result);
        verify(mockProcessMock).getOrder();
        verify(orderMock).getPaymentInfo();
        verify(orderMock).getPaymentType();
    }

    @Test
    public void executeAction_WithNonCardPaymentType_ShouldReturnNOK() throws RetryLaterException {
        when(mockProcessMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoMock);
        when(orderMock.getPaymentType()).thenReturn(CheckoutPaymentType.ACCOUNT);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.NOK, result);
        verify(mockProcessMock).getOrder();
        verify(orderMock).getPaymentInfo();
        verify(orderMock).getPaymentType();
    }

    @Test
    public void executeAction_WithCardPaymentTypeButGenericPaymentInfo_ShouldReturnNOK() throws RetryLaterException {
        when(mockProcessMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getIsApm()).thenReturn(Boolean.FALSE);
        when(orderMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.NOK, result);
        verify(mockProcessMock).getOrder();
        verify(orderMock).getPaymentInfo();
        verify(orderMock).getPaymentType();
    }

    @Test
    public void executeAction_WithCardPaymentTypeButNullPaymentInfo_ShouldReturnNOK() throws RetryLaterException {
        when(mockProcessMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenReturn(null);
        when(orderMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.NOK, result);
        verify(mockProcessMock).getOrder();
        verify(orderMock).getPaymentInfo();
        verify(orderMock).getPaymentType();
    }

    @Test
    public void executeAction_WithAPMFalseAndCardType_ShouldReturnNOK() throws RetryLaterException {
        when(mockProcessMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getIsApm()).thenReturn(Boolean.FALSE);
        when(orderMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.NOK, result);
        verify(mockProcessMock).getOrder();
        verify(orderMock).getPaymentInfo();
        verify(orderMock).getPaymentType();
    }

    @Test
    public void executeAction_WithCreditCardPaymentAndNullOrder_ShouldReturnNOK() throws RetryLaterException {
        when(mockProcessMock.getOrder()).thenReturn(null);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.NOK, result);
        verify(mockProcessMock).getOrder();
    }

    @Test
    public void executeAction_WithExceptionThrown_ShouldCatchExceptionAndReturnNOK() throws RetryLaterException {
        when(mockProcessMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenThrow(new RuntimeException("Test exception"));

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.NOK, result);
        verify(mockProcessMock).getOrder();
        verify(orderMock).getPaymentInfo();
    }

    @Test
    public void executeAction_WithExceptionAndValidOrder_ShouldReturnNOK()
            throws RetryLaterException {
        final RuntimeException testException = new RuntimeException("Test exception");
        when(mockProcessMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenThrow(testException);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.NOK, result);
        verify(mockProcessMock).getOrder();
        verify(orderMock).getPaymentInfo();
    }

    @Test
    public void executeAction_WithExceptionAndNullOrder_ShouldReturnNOK() throws RetryLaterException {
        final RuntimeException testException = new RuntimeException("Test exception");
        when(mockProcessMock.getOrder()).thenThrow(testException);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.NOK, result);
        verify(mockProcessMock).getOrder();
    }

    @Test
    public void executeAction_WithWorldpayAPMPaymentAndCardType_ShouldReturnOK() throws RetryLaterException {
        when(mockProcessMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(orderMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);
        when(paymentInfoMock.getIsApm()).thenReturn(Boolean.TRUE);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.OK, result);
        verify(mockProcessMock).getOrder();
        verify(orderMock).getPaymentInfo();
        verify(orderMock).getPaymentType();
    }

    @Test
    public void executeAction_WithNullPaymentType_ShouldReturnNOK() throws RetryLaterException {
        when(mockProcessMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoMock);
        when(orderMock.getPaymentType()).thenReturn(null);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.NOK, result);
        verify(mockProcessMock).getOrder();
        verify(orderMock).getPaymentInfo();
        verify(orderMock).getPaymentType();
    }

    @Test
    public void executeAction_WithPaymentInfoGetIsApmNull_ShouldReturnNOK() throws RetryLaterException {
        when(mockProcessMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getIsApm()).thenReturn(null);
        when(orderMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.NOK, result);
        verify(mockProcessMock).getOrder();
        verify(orderMock).getPaymentInfo();
        verify(orderMock).getPaymentType();
    }


    @Test
    public void executeAction_WithMultipleExceptionHandlingCalls_ShouldReturnNOKEachTime() throws RetryLaterException {
        final RuntimeException firstException = new RuntimeException("First exception");
        final RuntimeException secondException = new RuntimeException("Second exception");
        when(mockProcessMock.getOrder()).thenThrow(firstException);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result1 = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.NOK, result1);

        reset(mockProcessMock, orderMock);
        when(mockProcessMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenThrow(secondException);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result2 = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.NOK, result2);
    }

    @Test
    public void executeAction_WithCreditCardAndMultipleCalls_ShouldConsistentlyReturnOK() throws RetryLaterException {
        when(mockProcessMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoMock);
        when(orderMock.getPaymentType()).thenReturn(CheckoutPaymentType.CARD);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result1 = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.OK, result1);

        final AbstractSimpleB2BApproveOrderDecisionAction.Transition result2 = testObj.executeAction(mockProcessMock);

        assertEquals(AbstractSimpleB2BApproveOrderDecisionAction.Transition.OK, result2);
    }

}

package com.worldpay.commands.impl;

import com.worldpay.core.services.WorldpayPrimeRoutingService;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.CancelServiceRequest;
import com.worldpay.service.request.VoidSaleServiceRequest;
import com.worldpay.service.response.CancelServiceResponse;
import com.worldpay.service.response.VoidSaleServiceResponse;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.commands.request.VoidRequest;
import de.hybris.platform.payment.commands.result.VoidResult;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayVoidCommandTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";

    @InjectMocks
    private DefaultWorldpayVoidCommand testObj;

    @Mock
    private Converter<CancelServiceResponse, VoidResult> voidServiceResponseConverterMock;
    @Mock
    private WorldpayPrimeRoutingService worldpayPrimeRoutingServiceMock;
    @Mock
    private VoidRequest voidRequestMock;
    @Mock
    private WorldpayServiceGateway worldpayGatewayMock;
    @Mock
    private CancelServiceResponse cancelResponseMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private VoidResult voidResultMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private VoidSaleServiceResponse voidResponseMock;

    @Before
    public void setUp() throws WorldpayException {
        when(worldpayMerchantInfoServiceMock.getMerchantInfoFromTransaction(paymentTransactionModelMock)).thenReturn(merchantInfoMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(voidServiceResponseConverterMock.convert(cancelResponseMock)).thenReturn(voidResultMock);
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);
        when(voidRequestMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(voidServiceResponseConverterMock.convert(voidResponseMock)).thenReturn(voidResultMock);
        when(worldpayGatewayMock.voidSale(any(VoidSaleServiceRequest.class))).thenReturn(voidResponseMock);
    }

    @Test
    public void perform_ShouldReturnASuccessfulVoidResponse() throws Exception {
        when(worldpayGatewayMock.cancel(any(CancelServiceRequest.class))).thenReturn(cancelResponseMock);

        testObj.perform(voidRequestMock);

        verify(worldpayGatewayMock).cancel(any(CancelServiceRequest.class));
    }

    @Test
    public void perform_WhenAnExceptionOccurredInvokingTheGateway_ShouldReturnAFailingVoidResponse() throws Exception {
        doThrow(new WorldpayException(EXCEPTION_MESSAGE)).when(worldpayGatewayMock).cancel(any(CancelServiceRequest.class));

        testObj.perform(voidRequestMock);

        verify(worldpayGatewayMock).cancel(any(CancelServiceRequest.class));
        verify(voidServiceResponseConverterMock, never()).convert(any(CancelServiceResponse.class));
    }

    @Test
    public void perform_WhenResponseFromGatewayIsNull_ShouldReturnAFailingVoidResponse() throws Exception {
        when(worldpayGatewayMock.cancel(any(CancelServiceRequest.class))).thenReturn(null);

        testObj.perform(voidRequestMock);

        verify(worldpayGatewayMock).cancel(any(CancelServiceRequest.class));
        verify(voidServiceResponseConverterMock, never()).convert(any(CancelServiceResponse.class));
    }

    @Test
    public void perform_WhenOrderAuthorisedWithPrimeRouting_ShouldVoidSaleAndNotCallCancel() throws Exception {
        when(worldpayPrimeRoutingServiceMock.isOrderAuthorisedWithPrimeRouting(WORLDPAY_ORDER_CODE)).thenReturn(true);

        testObj.perform(voidRequestMock);

        verify(worldpayGatewayMock).voidSale(any(VoidSaleServiceRequest.class));
        verify(worldpayGatewayMock, never()).cancel(any(CancelServiceRequest.class));
    }

    @Test
    public void perform_WhenAnExceptionOccurredInvokingTheGatewayVoid_ShouldReturnAFailingVoidResponse() throws Exception {
        when(worldpayPrimeRoutingServiceMock.isOrderAuthorisedWithPrimeRouting(WORLDPAY_ORDER_CODE)).thenReturn(true);
        doThrow(new WorldpayException(EXCEPTION_MESSAGE)).when(worldpayGatewayMock).voidSale(any(VoidSaleServiceRequest.class));

        testObj.perform(voidRequestMock);

        verify(worldpayGatewayMock).voidSale(any(VoidSaleServiceRequest.class));
        verify(voidServiceResponseConverterMock, never()).convert(any(VoidSaleServiceResponse.class));
    }

    @Test
    public void perform_WhenResponseFromVoidGatewayIsNull_ShouldReturnAFailingVoidResponse() throws Exception {
        when(worldpayPrimeRoutingServiceMock.isOrderAuthorisedWithPrimeRouting(WORLDPAY_ORDER_CODE)).thenReturn(true);
        when(worldpayGatewayMock.voidSale(any(VoidSaleServiceRequest.class))).thenReturn(null);

        testObj.perform(voidRequestMock);

        verify(worldpayGatewayMock).voidSale(any(VoidSaleServiceRequest.class));
        verify(voidServiceResponseConverterMock, never()).convert(any(VoidSaleServiceResponse.class));
    }
}

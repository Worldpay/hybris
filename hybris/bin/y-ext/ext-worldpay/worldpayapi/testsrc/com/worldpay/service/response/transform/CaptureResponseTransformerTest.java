package com.worldpay.service.response.transform;

import com.worldpay.data.Amount;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.CaptureReceived;
import com.worldpay.internal.model.Ok;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reply;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CaptureResponseTransformerTest {

    @InjectMocks
    private CaptureResponseTransformer testObj;

    @Mock
    private ServiceResponseTransformerHelper serviceResponseTransformerHelperMock;
    @Mock
    private Converter<com.worldpay.internal.model.Amount, Amount> internalAmountReverseConverter;
    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private Reply replyMock;
    @Mock
    private CaptureReceived captureReceivedMock;
    @Mock
    private Ok okMock;
    @Mock
    private com.worldpay.internal.model.Amount intAmountMock;
    @Mock
    private Amount amountMock;

    @Test
    public void transform_ShouldFullyPopulateCaptureResponse_WhenAllDataIsPresentWithNoErrors() throws WorldpayModelTransformationException {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(List.of(replyMock));
        when(replyMock.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrCheckCardHolderNameResponseOrEcheckVerificationResponseOrPaymentOptionOrToken())
            .thenReturn(List.of(okMock));
        when(okMock.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceivedOrCancelRetryDoneOrCryptogramReceivedOrVoidSaleReceived())
            .thenReturn(List.of(captureReceivedMock));
        when(captureReceivedMock.getAmount()).thenReturn(intAmountMock);
        when(internalAmountReverseConverter.convert(intAmountMock)).thenReturn(amountMock);

        testObj.transform(paymentServiceMock);

        verify(captureReceivedMock).getOrderCode();
        verify(captureReceivedMock).getAmount();
    }

    @Test
    public void transform_ShouldNotFullyPopulateCaptureResponse_WhenThereAreErrors() throws WorldpayModelTransformationException {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(List.of(replyMock));
        when(serviceResponseTransformerHelperMock.checkForError(any(), any())).thenReturn(true);

        testObj.transform(paymentServiceMock);

        verify(captureReceivedMock, never()).getOrderCode();
        verify(captureReceivedMock, never()).getAmount();
    }

    @Test
    public void transform_ShouldThrowWorldpayModelTransformationException_WhenThereIsNoReplyInpPaymentService() {
        assertThrows(WorldpayModelTransformationException.class, () -> testObj.transform(paymentServiceMock));
    }
}

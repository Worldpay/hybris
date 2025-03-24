package com.worldpay.service.response.transform;

import com.worldpay.data.PaymentReply;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderInquiryResponseTransformerTest {

    @InjectMocks
    private OrderInquiryResponseTransformer testObj;

    @Mock
    private ServiceResponseTransformerHelper serviceResponseTransformerHelperMock;
    @Mock
    private OrderInquiryServiceResponse orderInquiryServiceResponseMock;
    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private Reply replyMock;
    @Mock
    private OrderStatus orderStatusMock;
    @Mock
    private Reference referenceMock;
    @Mock
    private Payment paymentMock;

    @Test
    public void transform_ShouldFullyPopulateOrderInquiry_WhenAllDataIsPresentWithNoErrors() throws WorldpayModelTransformationException {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(List.of(replyMock));
        when(replyMock.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrCheckCardHolderNameResponseOrEcheckVerificationResponseOrPaymentOptionOrToken())
            .thenReturn(List.of(orderStatusMock));
        when(orderStatusMock.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrInstalmentPlanOrRetryDetailsOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrSelectedSchemeOrAuthenticateResponse())
            .thenReturn(List.of(paymentMock, paymentReplyMock, referenceMock));

        testObj.transform(paymentServiceMock);

        verify(referenceMock).getvalue();
        verify(referenceMock).getId();
        verify(serviceResponseTransformerHelperMock, times(2)).buildPaymentReply(paymentMock);
    }

    @Test
    public void transform_ShouldNotFullyPopulateOrderInquiry_WhenThereAreErrors() throws WorldpayModelTransformationException {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(List.of(replyMock));
        when(serviceResponseTransformerHelperMock.checkForError(any(), any())).thenReturn(true);

        testObj.transform(paymentServiceMock);

        verify(referenceMock, never()).getvalue();
        verify(referenceMock, never()).getId();
        verify(serviceResponseTransformerHelperMock, never()).buildPaymentReply(paymentMock);
    }

    @Test
    public void transform_ShouldThrowWorldpayModelTransformationException_WhenThereIsNoReplyInpPaymentService() {
        assertThrows(WorldpayModelTransformationException.class, () -> testObj.transform(paymentServiceMock));
    }
}

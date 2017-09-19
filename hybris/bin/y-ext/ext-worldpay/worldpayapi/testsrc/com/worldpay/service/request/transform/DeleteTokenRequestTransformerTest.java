package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenDelete;
import com.worldpay.service.request.DeleteTokenServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DeleteTokenRequestTransformerTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String VERSION = "version";

    private DeleteTokenRequestTransformer testObj = new DeleteTokenRequestTransformer();
    @Mock(answer = RETURNS_DEEP_STUBS)
    private DeleteTokenServiceRequest serviceRequestMock;
    @Mock
    private PaymentTokenDelete paymentTokenDeleteMock;

    @Test(expected = WorldpayModelTransformationException.class)
    public void shouldRaiseExceptionWhenServiceRequestIsNull() throws Exception {
        testObj.transform(null);
    }

    @Test
    public void shouldReturnPaymentServiceWithPaymentTokenDelete() throws Exception {
        when(serviceRequestMock.getDeleteTokenRequest().transformToInternalModel()).thenReturn(paymentTokenDeleteMock);
        when(serviceRequestMock.getMerchantInfo().getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(serviceRequestMock.getWorldpayConfig().getVersion()).thenReturn(VERSION);

        final PaymentService result = testObj.transform(serviceRequestMock);

        assertEquals(MERCHANT_CODE, result.getMerchantCode());
        assertEquals(VERSION, result.getVersion());
        assertTrue(result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0) instanceof Modify);
        final Modify modify = (Modify) result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        assertTrue(modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().get(0) instanceof PaymentTokenDelete);
        final PaymentTokenDelete paymentTokenDelete = (PaymentTokenDelete) modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().get(0);
        assertEquals(paymentTokenDeleteMock, paymentTokenDelete);
    }
}

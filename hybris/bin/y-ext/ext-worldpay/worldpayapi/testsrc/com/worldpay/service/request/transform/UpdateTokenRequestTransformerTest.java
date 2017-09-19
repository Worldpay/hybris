package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenUpdate;
import com.worldpay.service.request.UpdateTokenServiceRequest;
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
public class UpdateTokenRequestTransformerTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String VERSION = "version";

    private UpdateTokenRequestTransformer testObj = new UpdateTokenRequestTransformer();
    @Mock(answer = RETURNS_DEEP_STUBS)
    private UpdateTokenServiceRequest serviceRequestMock;
    @Mock
    private PaymentTokenUpdate paymentTokenUpdateMock;

    @Test(expected = WorldpayModelTransformationException.class)
    public void shouldRaiseExceptionWhenServiceRequestIsNull() throws Exception {
        testObj.transform(null);
    }

    @Test
    public void shouldReturnPaymentServiceWithPaymentTokenUpdate() throws Exception {
        when(serviceRequestMock.getUpdateTokenRequest().transformToInternalModel()).thenReturn(paymentTokenUpdateMock);
        when(serviceRequestMock.getMerchantInfo().getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(serviceRequestMock.getWorldpayConfig().getVersion()).thenReturn(VERSION);

        final PaymentService result = testObj.transform(serviceRequestMock);

        assertEquals(MERCHANT_CODE, result.getMerchantCode());
        assertEquals(VERSION, result.getVersion());
        assertTrue(result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0) instanceof Modify);
        final Modify modify = (Modify) result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        assertTrue(modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().get(0) instanceof PaymentTokenUpdate);
        final PaymentTokenUpdate paymentTokenUpdate = (PaymentTokenUpdate) modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().get(0);
        assertEquals(paymentTokenUpdateMock, paymentTokenUpdate);
    }
}

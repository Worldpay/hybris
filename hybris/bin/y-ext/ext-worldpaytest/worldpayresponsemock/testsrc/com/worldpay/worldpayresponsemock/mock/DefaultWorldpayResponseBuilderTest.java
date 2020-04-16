package com.worldpay.worldpayresponsemock.mock;

import com.worldpay.internal.model.*;
import com.worldpay.worldpayresponsemock.responses.impl.DefaultWorldpayResponseBuilder;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayResponseBuilderTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";

    @InjectMocks
    private DefaultWorldpayResponseBuilder testObj  ;

    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private Submit submitMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Order orderMock;
    @Mock
    private HttpServletRequest request;

    @Test
    public void shouldCreateRedirectResponse() {
        when(paymentServiceMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(submitMock));
        when(submitMock.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreateOrChallenge()).thenReturn(singletonList(orderMock));
        when(orderMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);

        final PaymentService result = testObj.buildRedirectResponse(paymentServiceMock, request);

        final List<Object> paymentServiceElements = result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        for (final Object paymentServiceElement : paymentServiceElements) {
            if (paymentServiceElement instanceof Reply) {
                final Reply reply = (Reply) paymentServiceElement;
                final List<Object> replyElements = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken();
                assertThat(replyElements, hasItems(instanceOf(OrderStatus.class)));
                for (final Object replyElement : replyElements) {
                    if (replyElement instanceof OrderStatus) {
                        final OrderStatus orderStatus = (OrderStatus) replyElement;
                        assertEquals(WORLDPAY_ORDER_CODE, orderStatus.getOrderCode());
                        final List<Object> orderStatusElements = orderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrAuthenticateResponse();
                        assertThat(orderStatusElements, hasItems(instanceOf(Reference.class)));
                        for (final Object orderStatusElement : orderStatusElements) {
                            if (orderStatusElement instanceof Reference) {
                                final Reference reference = (Reference) orderStatusElement;
                                assertTrue(reference.getvalue().endsWith("/worldpayresponsemock/redirect?"));
                            }
                        }
                    }
                }
            }
        }
        assertThat(paymentServiceElements, hasItems(instanceOf(Reply.class)));

        assertEquals(MERCHANT_CODE, result.getMerchantCode());
    }
}

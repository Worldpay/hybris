package com.worldpay.worldpayresponsemock.facades.impl;

import com.worldpay.exception.WorldpayException;
import com.worldpay.internal.model.*;
import com.worldpay.service.marshalling.impl.DefaultPaymentServiceMarshaller;
import com.worldpay.worldpayresponsemock.responses.WorldpayCaptureResponseBuilder;
import com.worldpay.worldpayresponsemock.responses.WorldpayDirectAuthoriseResponseBuilder;
import com.worldpay.worldpayresponsemock.responses.WorldpayResponseBuilder;
import com.worldpay.worldpayresponsemock.responses.WorldpayTokenCreateResponseBuilder;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayMockFacadeTest {

    private static final String XML_RESULT = "RESULT";
    private static final String CAPTURE_OK = "captureOk";
    private static final String REDIRECT_XML = "redirectXML";
    private static final String TOKEN_REPLY_XML = "tokenReplyXML";

    @InjectMocks
    private DefaultWorldpayMockFacade testObj;

    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private PaymentService responsePaymentService;
    @Mock
    private Submit submitMock;
    @Mock
    private Order orderMock;
    @Mock
    private WorldpayResponseBuilder worldpayResponseBuilder;
    @Mock
    private WorldpayTokenCreateResponseBuilder worldpayTokenCreateResponseBuilderMock;
    @Mock
    private WorldpayCaptureResponseBuilder worldpayCaptureResponseBuilder;
    @Mock
    private WorldpayDirectAuthoriseResponseBuilder worldpayDirectAuthoriseResponseBuilder;
    @Mock
    private PaymentDetails paymentMethodDetailMock;
    @Mock
    private Modify modifyMock;
    @Mock
    private OrderModification orderModificationMock;
    @Mock
    private Capture captureMock;
    @Mock
    private PaymentMethodMask paymentMethodMaskMock;
    @Mock
    private HttpServletRequest httpRequestMock;
    @Mock
    private BatchModification batchModification;
    @Mock
    private DefaultPaymentServiceMarshaller paymentServiceMarshaller;
    @Mock
    private PaymentTokenCreate paymentTokenCreateMock;

    @Before
    public void setUp() throws WorldpayException {
        when(worldpayDirectAuthoriseResponseBuilder.buildDirectResponse(paymentServiceMock)).thenReturn(responsePaymentService);
        when(worldpayCaptureResponseBuilder.buildCaptureResponse(paymentServiceMock)).thenReturn(responsePaymentService);
        when(worldpayResponseBuilder.buildRedirectResponse(paymentServiceMock, httpRequestMock)).thenReturn(responsePaymentService);
        when(worldpayTokenCreateResponseBuilderMock.buildTokenResponse(paymentServiceMock)).thenReturn(responsePaymentService);
        when(paymentServiceMarshaller.marshal(responsePaymentService)).thenReturn(XML_RESULT);
    }

    @Test
    public void shouldBuildDirectResponseIfPaymentServiceContainsSubmitWithPaymentDetails() throws WorldpayException {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(submitMock));
        when(submitMock.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate()).thenReturn(singletonList(orderMock));
        when(orderMock.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrInfo3DSecureOrSession())
                .thenReturn(singletonList(paymentMethodDetailMock));

        final String result = testObj.buildResponse(paymentServiceMock, httpRequestMock);

        assertEquals(XML_RESULT, result);
    }

    @Test
    public void shouldReturnCaptureResponseIfRequestContainsModifyWithCapture() throws WorldpayException {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(modifyMock));
        when(modifyMock.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete()).thenReturn(singletonList(orderModificationMock));
        when(orderModificationMock.getCancelOrCaptureOrRefundOrRevokeOrAddBackOfficeCodeOrAuthoriseOrIncreaseAuthorisationOrCancelOrRefundOrDefendOrShopperWebformRefundDetailsOrExtendExpiryDateOrCancelRefundOrCancelRetryOrVoidSale()).thenReturn(singletonList(captureMock));
        when(paymentServiceMarshaller.marshal(responsePaymentService)).thenReturn(CAPTURE_OK);

        final String result = testObj.buildResponse(paymentServiceMock, httpRequestMock);

        assertEquals(CAPTURE_OK, result);
    }

    @Test
    public void shouldReturnNullIfRequestContainsModifyWithoutCapture() throws WorldpayException {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(modifyMock));
        when(modifyMock.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete()).thenReturn(singletonList(batchModification));

        final String result = testObj.buildResponse(paymentServiceMock, httpRequestMock);

        assertNull(result);
    }

    @Test
    public void shouldReturnRedirectXMLIfRequestContainsSubmitWithPaymentMethodMask() throws WorldpayException {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(submitMock));
        when(submitMock.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate()).thenReturn(singletonList(orderMock));
        when(orderMock.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrInfo3DSecureOrSession())
                .thenReturn(singletonList(paymentMethodMaskMock));
        when(paymentServiceMarshaller.marshal(responsePaymentService)).thenReturn(REDIRECT_XML);

        final String result = testObj.buildResponse(paymentServiceMock, httpRequestMock);

        assertEquals(REDIRECT_XML, result);
    }

    @Test
    public void shouldReturnTokenWhenRequestContainsPaymentTokenCreate() throws WorldpayException {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(submitMock));
        when(submitMock.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate()).thenReturn(singletonList(paymentTokenCreateMock));
        when(paymentServiceMarshaller.marshal(responsePaymentService)).thenReturn(TOKEN_REPLY_XML);

        final String result = testObj.buildResponse(paymentServiceMock, httpRequestMock);

        assertEquals(TOKEN_REPLY_XML, result);
    }
}

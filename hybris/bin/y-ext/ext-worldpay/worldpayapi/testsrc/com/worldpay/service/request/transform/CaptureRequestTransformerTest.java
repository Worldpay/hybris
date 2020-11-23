package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.Date;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.CaptureServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CaptureRequestTransformerTest {

    private static final String VERSION = "version";
    private static final String ORDER_CODE = "orderCode";
    private static final String TRACKING_ID_1 = "trackingId1";
    private static final String TRACKING_ID_2 = "trackingId2";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";

    @InjectMocks
    private CaptureRequestTransformer testObj;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;

    @Mock
    private CaptureServiceRequest captureRequestMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private Amount amountMock;
    @Mock
    private com.worldpay.internal.model.Amount internalAmountMock;
    @Mock
    private Date dateMock;
    @Mock
    private com.worldpay.internal.model.Date internalDateMock;

    @Before
    public void setUp() throws Exception {
        when(captureRequestMock.getMerchantInfo()).thenReturn(merchantInfoMock);
        when(captureRequestMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(captureRequestMock.getAmount()).thenReturn(amountMock);
        when(captureRequestMock.getDate()).thenReturn(dateMock);
        when(captureRequestMock.getTrackingIds()).thenReturn(List.of(TRACKING_ID_1, TRACKING_ID_2));
        when(dateMock.transformToInternalModel()).thenReturn(internalDateMock);
        when(amountMock.transformToInternalModel()).thenReturn(internalAmountMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_VERSION)).thenReturn(VERSION);
    }

    @Test
    public void transform_WhenACaptureRequestIsReceived_ShouldGenerateACaptureWithShippingInfosContainingTrackingIds() throws WorldpayModelTransformationException {
        final PaymentService result = testObj.transform(captureRequestMock);
        final Capture captureResult = getCaptureResult(result);
        final List<ShippingInfo> shippingInfoResults = captureResult.getShipping().getShippingInfo();

        assertThat(shippingInfoResults.size()).isEqualTo(2);
        final List<String> trackingIdsFromShippingInfoResults = shippingInfoResults.stream().map(ShippingInfo::getTrackingId).collect(Collectors.toList());
        assertThat(trackingIdsFromShippingInfoResults).contains(TRACKING_ID_1, TRACKING_ID_2);
    }

    @Test
    public void transform_WhenCaptureRequestIsReceived_ShouldGenerateACaptureWithOutShippingIfThereIsNoTrackingInfoOnCaptureRequest() throws WorldpayModelTransformationException {
        when(captureRequestMock.getTrackingIds()).thenReturn(Collections.emptyList());

        final PaymentService result = testObj.transform(captureRequestMock);
        final Capture captureResult = getCaptureResult(result);
        final Shipping shippingResult = captureResult.getShipping();

        assertThat(shippingResult).isNull();
    }

    @Test
    public void transform_WhenCaptureRequestIsReceived_ShouldGenerateACaptureWithRequestedDateAndRequestedAmount() throws WorldpayModelTransformationException {
        final PaymentService result = testObj.transform(captureRequestMock);
        final Capture captureResult = getCaptureResult(result);

        assertThat(captureResult.getDate()).isEqualTo(internalDateMock);
        assertThat(captureResult.getAmount()).isEqualTo(internalAmountMock);
    }

    @Test
    public void transform_WhenCaptureRequestIsReceived_ShouldGenerateAnOrderModificationWithRequestedOrderCode() throws WorldpayModelTransformationException {
        final PaymentService result = testObj.transform(captureRequestMock);
        final OrderModification orderModificationResult = getOrderModificationResult(result);

        assertThat(orderModificationResult.getOrderCode()).isEqualTo(ORDER_CODE);
    }

    @Test
    public void transform_WhenCaptureRequestIsReceived_ShouldGenerateAPaymentServiceWithRequestedMerchantCodeAndWorldpayConfiguredVersion() throws WorldpayModelTransformationException {
        final PaymentService result = testObj.transform(captureRequestMock);

        assertThat(result.getMerchantCode()).isEqualTo(MERCHANT_CODE);
        assertThat(result.getVersion()).isEqualTo(VERSION);
    }

    @Test(expected = WorldpayModelTransformationException.class)
    public void transform_WhenNullCaptureRequestIsReceived_ShouldThrownWorldpayModelTransformationExceptionWhenTheRequestIsNull() throws WorldpayModelTransformationException {
        testObj.transform(null);
    }

    @Test(expected = WorldpayModelTransformationException.class)
    public void transform_WhenMerchantInfoIsNullOnCaptureRequest_ShouldThrownWorldpayModelTransformationExceptionWhenMerchantInfoOnRequestIsNull() throws WorldpayModelTransformationException {
        when(captureRequestMock.getMerchantInfo()).thenReturn(null);
        testObj.transform(captureRequestMock);
    }

    @Test(expected = WorldpayModelTransformationException.class)
    public void transform_WhenOrderCodeIsNullOnCaptureRequest_ShouldThrownWorldpayModelTransformationExceptionWhenOrderCodeOnRequestIsNull() throws WorldpayModelTransformationException {
        when(captureRequestMock.getOrderCode()).thenReturn(null);
        testObj.transform(captureRequestMock);
    }

    @Test(expected = WorldpayModelTransformationException.class)
    public void transform_WhenAmountIsNullOnCaptureRequest_ShouldThrownWorldpayModelTransformationExceptionWhenAmountOnRequestIsNull() throws WorldpayModelTransformationException {
        when(captureRequestMock.getAmount()).thenReturn(null);
        testObj.transform(captureRequestMock);
    }

    protected Capture getCaptureResult(final PaymentService result) {
        final OrderModification orderModificationResult = getOrderModificationResult(result);
        return (Capture) orderModificationResult.getCancelOrCaptureOrRefundOrRevokeOrAddBackOfficeCodeOrAuthoriseOrIncreaseAuthorisationOrCancelOrRefundOrDefendOrShopperWebformRefundDetailsOrExtendExpiryDateOrCancelRefundOrCancelRetryOrVoidSaleOrApprove().get(0);
    }

    protected OrderModification getOrderModificationResult(final PaymentService result) {
        final Modify modifyResult = (Modify) result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        return (OrderModification) modifyResult.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().get(0);
    }
}

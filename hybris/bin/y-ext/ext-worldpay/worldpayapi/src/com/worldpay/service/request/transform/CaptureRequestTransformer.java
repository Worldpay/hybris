package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.request.CaptureServiceRequest;
import com.worldpay.service.request.ServiceRequest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Specific class for transforming an {@link CaptureServiceRequest} into a {@link PaymentService} object
 * <p/>
 * <p>The external model objects each know how to transform themselves into an internal model object representation. This class adds the surrounding classes that are required
 * to generate xml in the form:
 * <pre>
 *  &lt;paymentService merchantCode="MYMERCHANT" version="1.4"&gt;
 *      &lt;modify&gt;
 *          &lt;orderModification orderCode="1234"&gt;
 *              &lt;capture&gt;
 *                  &lt;amount value="10965" currencyCode="EURGBP" exponent="2" debitCreditIndicator="credit"/&gt;
 *                  &lt;date dayOfMonth="21" month="05" year="2004"/&gt;
 *              &lt;/capture&gt;
 *          &lt;/orderModification&gt;
 *      &lt;/modify&gt;
 *  &lt;/paymentService&gt;
 * </pre>
 * </p>
 */
public class CaptureRequestTransformer implements ServiceRequestTransformer {
    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";

    protected final ConfigurationService configurationService;

    public CaptureRequestTransformer(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.worldpay.service.request.transform.ServiceRequestTransformer#transform(com.worldpay.service.request.ServiceRequest)
     */
    @Override
    public PaymentService transform(final ServiceRequest request) throws WorldpayModelTransformationException {
        if (request == null || request.getMerchantInfo() == null || request.getOrderCode() == null) {
            throw new WorldpayModelTransformationException("Request provided to do the capture is invalid.");
        }
        final CaptureServiceRequest captureRequest = (CaptureServiceRequest) request;

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(request.getMerchantInfo().getMerchantCode());
        paymentService.setVersion(configurationService.getConfiguration().getString(WORLDPAY_CONFIG_VERSION));

        if (captureRequest.getAmount() == null) {
            throw new WorldpayModelTransformationException("No amount object to transform on the capture request");
        }
        final Modify modify = new Modify();
        final OrderModification orderModification = new OrderModification();
        orderModification.setOrderCode(request.getOrderCode());
        final Capture capture = new Capture();
        capture.setAmount((Amount) captureRequest.getAmount().transformToInternalModel());
        if (captureRequest.getDate() != null) {
            capture.setDate((Date) captureRequest.getDate().transformToInternalModel());
        }

        setShippingInfo(captureRequest.getTrackingIds(), capture);

        orderModification.getCancelOrCaptureOrRefundOrRevokeOrAddBackOfficeCodeOrAuthoriseOrIncreaseAuthorisationOrCancelOrRefundOrDefendOrShopperWebformRefundDetailsOrExtendExpiryDateOrCancelRefundOrCancelRetryOrVoidSaleOrApprove().add(capture);
        modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().add(orderModification);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(modify);
        return paymentService;
    }

    protected void setShippingInfo(final List<String> trackingIds, final Capture capture) {
        if (CollectionUtils.isNotEmpty(trackingIds)) {
            final Shipping shipping = new Shipping();
            shipping.getShippingInfo().addAll(getShippingInfos(trackingIds));
            capture.setShipping(shipping);
        }
    }

    protected List<ShippingInfo> getShippingInfos(final List<String> trackingIds) {
        return trackingIds.stream()
                .map(this::getShippingInfoWithTrackingId)
                .collect(Collectors.toList());
    }

    protected ShippingInfo getShippingInfoWithTrackingId(final String trackingId) {
        final ShippingInfo shippingInfo = new ShippingInfo();
        shippingInfo.setTrackingId(trackingId);
        return shippingInfo;
    }
}

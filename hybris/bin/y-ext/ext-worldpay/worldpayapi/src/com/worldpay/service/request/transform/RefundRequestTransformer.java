package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.request.RefundServiceRequest;
import com.worldpay.service.request.ServiceRequest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Specific class for transforming an {@link RefundServiceRequest} into a {@link PaymentService} object
 * <p/>
 * <p>The external model objects each know how to transform themselves into an internal model object representation. This class adds the surrounding classes that are required
 * to generate xml in the form:
 * <pre>
 *  &lt;paymentService merchantCode="MYMERCHANT" version="1.4"&gt;
 *      &lt;modify&gt;
 *          &lt;orderModification orderCode="1234"&gt;
 *              &lt;refund&gt;
 *                  &lt;amount value="12720" currencyCode="EUR" exponent="2" debitCreditIndicator="credit"/&gt;
 *              &lt;/refund&gt;
 *          &lt;/orderModification&gt;
 *      &lt;/modify&gt;
 *  &lt;/paymentService&gt;
 * </pre>
 * </p>
 */
public class RefundRequestTransformer implements ServiceRequestTransformer {
    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";

    private final ConfigurationService configurationService;

    public RefundRequestTransformer(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /* (non-Javadoc)
     * @see ServiceRequestTransformer#transform(ServiceRequest)
     */
    @Override
    public PaymentService transform(final ServiceRequest request) throws WorldpayModelTransformationException {
        if (request == null || request.getMerchantInfo() == null || request.getOrderCode() == null) {
            throw new WorldpayModelTransformationException("Request provided to do the refund is invalid.");
        }
        final RefundServiceRequest refundRequest = (RefundServiceRequest) request;

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(request.getMerchantInfo().getMerchantCode());
        paymentService.setVersion(configurationService.getConfiguration().getString(WORLDPAY_CONFIG_VERSION));

        if (refundRequest.getAmount() == null) {
            throw new WorldpayModelTransformationException("No amount object to transform on the refund request");
        }
        final Modify modify = new Modify();
        final OrderModification orderModification = new OrderModification();
        orderModification.setOrderCode(request.getOrderCode());
        final Refund refund = new Refund();
        refund.setReference(refundRequest.getReference());
        if (Boolean.TRUE.equals(refundRequest.getShopperWebformRefund())) {
            refund.setShopperWebformRefund(Boolean.TRUE.toString());
        }
        refund.setAmount((Amount) refundRequest.getAmount().transformToInternalModel());
        orderModification.getCancelOrCaptureOrRefundOrRevokeOrAddBackOfficeCodeOrAuthoriseOrIncreaseAuthorisationOrCancelOrRefundOrDefendOrShopperWebformRefundDetailsOrExtendExpiryDateOrCancelRefundOrCancelRetryOrVoidSale().add(refund);
        modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().add(orderModification);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(modify);
        return paymentService;
    }
}

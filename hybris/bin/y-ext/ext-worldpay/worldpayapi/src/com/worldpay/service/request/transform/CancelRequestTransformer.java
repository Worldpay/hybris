/**
 *
 */
package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.CancelOrRefund;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.OrderModification;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.request.CancelServiceRequest;
import com.worldpay.service.request.ServiceRequest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Specific class for transforming an {@link CancelServiceRequest} into a {@link PaymentService} object
 * <p/>
 * <p>The external model objects each know how to transform themselves into an internal model object representation. This class adds the surrounding classes that are required
 * to generate xml in the form:
 * <pre>
 *  &lt;paymentService merchantCode="MYMERCHANT" version="1.4"&gt;
 *      &lt;modify&gt;
 *          &lt;orderModification orderCode="1234"&gt;
 *              &lt;cancel/&gt;
 *          &lt;/orderModification&gt;
 *      &lt;/modify&gt;
 *  &lt;/paymentService&gt;
 * </pre>
 * </p>
 */
public class CancelRequestTransformer implements ServiceRequestTransformer {
    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";

    protected final ConfigurationService configurationService;

    public CancelRequestTransformer(final ConfigurationService configurationService) {
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
            throw new WorldpayModelTransformationException("Request provided to do the cancelOrRefund is invalid.");
        }
        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(request.getMerchantInfo().getMerchantCode());
        paymentService.setVersion(configurationService.getConfiguration().getString(WORLDPAY_CONFIG_VERSION));

        final Modify modify = new Modify();
        final OrderModification orderModification = new OrderModification();
        orderModification.setOrderCode(request.getOrderCode());
        final CancelOrRefund cancelOrRefund = new CancelOrRefund();
        orderModification.getCancelOrCaptureOrProvideCryptogramOrRefundOrRevokeOrAddBackOfficeCodeOrAuthoriseOrIncreaseAuthorisationOrCancelOrRefundOrDefendOrShopperWebformRefundDetailsOrExtendExpiryDateOrCancelRefundOrCancelRetryOrVoidSaleOrApprove().add(cancelOrRefund);
        modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDeleteOrDeleteNetworkPaymentToken().add(orderModification);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(modify);
        return paymentService;
    }
}

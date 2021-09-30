package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.AddBackOfficeCode;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.OrderModification;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.request.AddBackOfficeCodeServiceRequest;
import com.worldpay.service.request.ServiceRequest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Specific class for transforming an {@link AddBackOfficeCodeServiceRequest} into a {@link PaymentService} object
 * <p/>
 * <p>The external model objects each know how to transform themselves into an internal model object representation. This class adds the surrounding classes that are required
 * to generate xml in the form:
 * <pre>
 *  &lt;paymentService merchantCode="MYMERCHANT" version="1.4"&gt;
 *      &lt;modify&gt;
 *          &lt;orderModification orderCode="1234"&gt;
 *              &lt;addBackOfficeCode backOfficeCode="CAP1234"/&gt;
 *          &lt;/orderModification&gt;
 *      &lt;/modify&gt;
 *  &lt;/paymentService&gt;
 * </pre>
 * </p>
 */
public class AddBackOfficeCodeRequestTransformer implements ServiceRequestTransformer {
    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";

    protected final ConfigurationService configurationService;

    public AddBackOfficeCodeRequestTransformer(final ConfigurationService configurationService) {
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
            throw new WorldpayModelTransformationException("Request provided to do the add back office code is invalid.");
        }
        final AddBackOfficeCodeServiceRequest addBackOfficeCodeRequest = (AddBackOfficeCodeServiceRequest) request;

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(request.getMerchantInfo().getMerchantCode());
        paymentService.setVersion(configurationService.getConfiguration().getString(WORLDPAY_CONFIG_VERSION));

        if (addBackOfficeCodeRequest.getBackOfficeCode() == null) {
            throw new WorldpayModelTransformationException("No back office code object to transform on the add back office request");
        }
        final Modify modify = new Modify();
        final OrderModification orderModification = new OrderModification();
        orderModification.setOrderCode(request.getOrderCode());
        final AddBackOfficeCode addBackOfficeCode = new AddBackOfficeCode();
        addBackOfficeCode.setBackOfficeCode(addBackOfficeCodeRequest.getBackOfficeCode());
        orderModification.getCancelOrCaptureOrRefundOrRevokeOrAddBackOfficeCodeOrAuthoriseOrIncreaseAuthorisationOrCancelOrRefundOrDefendOrShopperWebformRefundDetailsOrExtendExpiryDateOrCancelRefundOrCancelRetryOrVoidSaleOrApprove().add(addBackOfficeCode);
        modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().add(orderModification);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(modify);
        return paymentService;
    }

}

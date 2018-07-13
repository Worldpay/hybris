package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Authorise;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.OrderModification;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.request.AuthorisationCodeServiceRequest;
import com.worldpay.service.request.ServiceRequest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.springframework.beans.factory.annotation.Required;

/**
 * Specific class for transforming an {@link AuthorisationCodeServiceRequest} into a {@link PaymentService} object
 * <p/>
 * <p>The external model objects each know how to transform themselves into an internal model object representation. This class adds the surrounding classes that are required
 * to generate xml in the form:
 * <pre>
 *  &lt;paymentService merchantCode="MYMERCHANT" version="1.4"&gt;
 *      &lt;modify&gt;
 *          &lt;orderModification orderCode="1234"&gt;
 *              &lt;authorise authorisationCode="acbsdf"/&gt;
 *          &lt;/orderModification&gt;
 *      &lt;/modify&gt;
 *  &lt;/paymentService&gt;
 * </pre>
 * </p>
 */
public class AuthorisationCodeRequestTransformer implements ServiceRequestTransformer {
    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";

    private ConfigurationService configurationService;

    /**
     * (non-Javadoc)
     *
     * @see com.worldpay.service.request.transform.ServiceRequestTransformer#transform(com.worldpay.service.request.ServiceRequest)
     */
    @Override
    public PaymentService transform(final ServiceRequest request) throws WorldpayModelTransformationException {
        if (request == null || request.getMerchantInfo() == null || request.getOrderCode() == null) {
            throw new WorldpayModelTransformationException("Request provided to do the authorisation code is invalid.");
        }
        final AuthorisationCodeServiceRequest authorisationCodeRequest = (AuthorisationCodeServiceRequest) request;

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(request.getMerchantInfo().getMerchantCode());
        paymentService.setVersion(configurationService.getConfiguration().getString(WORLDPAY_CONFIG_VERSION));


        if (authorisationCodeRequest.getAuthorisationCode() == null) {
            throw new WorldpayModelTransformationException("No authorisation code object to transform on the authorisation code request");
        }
        final Modify modify = new Modify();
        final OrderModification orderModification = new OrderModification();
        orderModification.setOrderCode(request.getOrderCode());
        final Authorise authorise = new Authorise();
        authorise.setAuthorisationCode(authorisationCodeRequest.getAuthorisationCode());
        orderModification.getCancelOrCaptureOrRefundOrRevokeOrAddBackOfficeCodeOrAuthoriseOrIncreaseAuthorisationOrCancelOrRefundOrDefendOrShopperWebformRefundDetailsOrExtendExpiryDateOrCancelRefundOrCancelRetry().add(authorise);
        modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDelete().add(orderModification);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(modify);
        return paymentService;
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}

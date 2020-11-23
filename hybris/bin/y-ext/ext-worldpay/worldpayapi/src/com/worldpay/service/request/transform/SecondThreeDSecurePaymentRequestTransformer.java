package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.request.SecondThreeDSecurePaymentRequest;
import com.worldpay.service.request.ServiceRequest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Specific class for transforming an {@link SecondThreeDSecurePaymentRequest} into a {@link PaymentService} object
 * <p/>
 * <p>The external model objects each know how to transform themselves into an internal model object representation. This class adds the surrounding classes that are required
 * to generate xml in the form:
 * <pre>
 *  &lt;paymentService version=&quot;1.4&quot; merchantCode=&quot;YOUR_MERCHANT_CODE&quot;&gt;  &lt;submit&gt;
 *     &lt;order orderCode=&quot;YOUR_ORDER_CODE&quot;&gt; &lt;!--The order code supplied in the first request--&gt;
 *       &lt;info3DSecure&gt;
 *         &lt;completedAuthentication/&gt;
 *       &lt;/info3DSecure&gt;
 *       &lt;session id=&quot;SESSION_ID&quot;/&gt; &lt;!--The session id supplied in the first request--&gt;
 *     &lt;/order&gt;
 *   &lt;/submit&gt;
 * &lt;/paymentService&gt;
 * </pre>
 * </p>
 */
public class SecondThreeDSecurePaymentRequestTransformer implements ServiceRequestTransformer {

    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";

    protected final ConfigurationService configurationService;

    public SecondThreeDSecurePaymentRequestTransformer(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public PaymentService transform(final ServiceRequest request) throws WorldpayModelTransformationException {
        if (request == null || request.getMerchantInfo() == null || StringUtils.isBlank(request.getMerchantInfo().getMerchantCode()) || StringUtils.isBlank(request.getOrderCode())) {
            throw new WorldpayModelTransformationException("Request provided to do the refund is invalid.");
        }
        final SecondThreeDSecurePaymentRequest second3DSPaymentRequest = (SecondThreeDSecurePaymentRequest) request;

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(request.getMerchantInfo().getMerchantCode());
        paymentService.setVersion(configurationService.getConfiguration().getString(WORLDPAY_CONFIG_VERSION));

        if (StringUtils.isBlank(second3DSPaymentRequest.getSessionId())) {
            throw new WorldpayModelTransformationException("No session id to transform on the second 3ds payment request");
        }

        final List<Object> submitList = paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        final Submit submit = new Submit();
        final List<Object> submitElements = submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreateOrChallenge();
        final Order order = new Order();
        order.setOrderCode(request.getOrderCode());
        submitElements.add(order);
        final List<Object> orderElements = order.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrExemptionOrShippingMethodOrProductSkuOrFraudSightDataOrDeviceSessionOrInfo3DSecureOrSession();
        final Info3DSecure info3DSecure = new Info3DSecure();
        final List<Object> completedAuthenticationList = info3DSecure.getPaResponseOrMpiProviderOrMpiResponseOrAttemptedAuthenticationOrCompletedAuthenticationOrThreeDSVersionOrMerchantNameOrXidOrDsTransactionIdOrCavvOrEci();
        completedAuthenticationList.add(new CompletedAuthentication());
        orderElements.add(info3DSecure);
        final Session session = new Session();
        session.setId(second3DSPaymentRequest.getSessionId());
        orderElements.add(session);
        submitList.add(submit);
        return paymentService;
    }
}

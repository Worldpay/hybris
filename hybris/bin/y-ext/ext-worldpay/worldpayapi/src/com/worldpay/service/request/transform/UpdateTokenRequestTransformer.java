package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenUpdate;
import com.worldpay.data.token.UpdateTokenRequest;
import com.worldpay.service.request.ServiceRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Transformer/Converter class that transforms a CreateTokenServiceRequest (abstraction) into a PaymentService (XML model)
 * to be sent as an XML file to Worldpay
 */
public class UpdateTokenRequestTransformer implements ServiceRequestTransformer {
    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";

    protected final ConfigurationService configurationService;
    protected final Converter<UpdateTokenRequest, PaymentTokenUpdate> internalPaymentTokenUpdateConverter;

    public UpdateTokenRequestTransformer(final ConfigurationService configurationService,
                                         final Converter<UpdateTokenRequest, PaymentTokenUpdate> internalPaymentTokenUpdateConverter) {
        this.configurationService = configurationService;
        this.internalPaymentTokenUpdateConverter = internalPaymentTokenUpdateConverter;
    }

    @Override
    public PaymentService transform(final ServiceRequest request) throws WorldpayModelTransformationException {
        if (request == null) {
            throw new WorldpayModelTransformationException("Request provided to update token is invalid.");
        }

        final UpdateTokenServiceRequest updateTokenRequest = (UpdateTokenServiceRequest) request;

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(updateTokenRequest.getMerchantInfo().getMerchantCode());
        paymentService.setVersion(configurationService.getConfiguration().getString(WORLDPAY_CONFIG_VERSION));

        final PaymentTokenUpdate updateToken = internalPaymentTokenUpdateConverter.convert(updateTokenRequest.getUpdateTokenRequest());

        final Modify modify = new Modify();
        modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDeleteOrDeleteNetworkPaymentToken().add(updateToken);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(modify);

        return paymentService;
    }

}

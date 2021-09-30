package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenCreate;
import com.worldpay.internal.model.Submit;
import com.worldpay.data.token.CardTokenRequest;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.ServiceRequest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Transformer/Converter class that transforms a CreateTokenServiceRequest (abstraction) into a PaymentService (XML model)
 * to be sent as an XML file to Worldpay
 */
public class CreateTokenRequestTransformer implements ServiceRequestTransformer {
    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";

    protected final ConfigurationService configurationService;
    protected final Converter<CardTokenRequest, PaymentTokenCreate> internalPaymentTokenCreateConverter;

    public CreateTokenRequestTransformer(final ConfigurationService configurationService,
                                         final Converter<CardTokenRequest, PaymentTokenCreate> internalPaymentTokenCreateConverter) {
        this.configurationService = configurationService;
        this.internalPaymentTokenCreateConverter = internalPaymentTokenCreateConverter;
    }

    /**
     * (non-Javadoc)
     *
     * @see ServiceRequestTransformer#transform(ServiceRequest)
     */
    @Override
    public PaymentService transform(final ServiceRequest request) throws WorldpayModelTransformationException {
        if (request == null) {
            throw new WorldpayModelTransformationException("Request provided to create token is invalid.");
        }
        final CreateTokenServiceRequest tokenRequest = (CreateTokenServiceRequest) request;

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(tokenRequest.getMerchantInfo().getMerchantCode());
        paymentService.setVersion(configurationService.getConfiguration().getString(WORLDPAY_CONFIG_VERSION));

        final PaymentTokenCreate paymentTokenCreate = internalPaymentTokenCreateConverter.convert(tokenRequest.getCardTokenRequest());
        final Submit submit = new Submit();
        submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreateOrChallenge().add(paymentTokenCreate);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(submit);
        return paymentService;
    }

}

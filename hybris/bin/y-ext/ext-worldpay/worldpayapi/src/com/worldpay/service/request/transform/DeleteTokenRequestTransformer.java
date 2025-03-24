package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenDelete;
import com.worldpay.data.token.DeleteTokenRequest;
import com.worldpay.service.request.DeleteTokenServiceRequest;
import com.worldpay.service.request.ServiceRequest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Transformer/Converter class that transforms a DeleteTokenRequestTransformer (abstraction) into a PaymentService (XML model)
 * to be sent as an XML file to Worldpay
 */
public class DeleteTokenRequestTransformer implements ServiceRequestTransformer {

    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";

    protected final ConfigurationService configurationService;
    protected final Converter<DeleteTokenRequest, PaymentTokenDelete> internalPaymentTokenDeleteConverter;

    public DeleteTokenRequestTransformer(final ConfigurationService configurationService,
                                         final Converter<DeleteTokenRequest, PaymentTokenDelete> internalPaymentTokenDeleteConverter) {
        this.configurationService = configurationService;
        this.internalPaymentTokenDeleteConverter = internalPaymentTokenDeleteConverter;
    }

    @Override
    public PaymentService transform(ServiceRequest request) throws WorldpayModelTransformationException {
        if (request == null) {
            throw new WorldpayModelTransformationException("Request provided to delete token is invalid.");
        }

        final DeleteTokenServiceRequest deleteTokenRequest = (DeleteTokenServiceRequest) request;

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(deleteTokenRequest.getMerchantInfo().getMerchantCode());
        paymentService.setVersion(configurationService.getConfiguration().getString(WORLDPAY_CONFIG_VERSION));

        final PaymentTokenDelete deleteToken = internalPaymentTokenDeleteConverter.convert(deleteTokenRequest.getDeleteTokenRequest());

        final Modify modify = new Modify();
        modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdateOrPaymentTokenDeleteOrDeleteNetworkPaymentToken().add(deleteToken);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(modify);

        return paymentService;
    }
}

package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reply;
import com.worldpay.internal.model.Token;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.ServiceResponse;

/**
 * Transformer/Converter class that transforms a CreateTokenResponseTransformer (XML model) into a ServiceResponse (abstraction)
 * to be handled in an easier way.
 */
public class CreateTokenResponseTransformer extends AbstractServiceResponseTransformer {

    @Override
    public ServiceResponse transform(final PaymentService paymentService) throws WorldpayModelTransformationException {
        final CreateTokenResponse createTokenResponse = new CreateTokenResponse();

        final Object responseType = paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        if (responseType == null) {
            throw new WorldpayModelTransformationException("No reply message in Worldpay create token response");
        }

        if (!(responseType instanceof Reply)) {
            throw new WorldpayModelTransformationException("Reply type from Worldpay not the expected type");
        }

        final Reply intReply = (Reply) responseType;

        final Object response = intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken().get(0);

        if (getServiceResponseTransformerHelper().checkForError(createTokenResponse, intReply)) {
            return createTokenResponse;
        } else if (response instanceof Token) {
            createTokenResponse.setToken(getServiceResponseTransformerHelper().buildTokenReply((Token) response));
        }

        return createTokenResponse;
    }
}

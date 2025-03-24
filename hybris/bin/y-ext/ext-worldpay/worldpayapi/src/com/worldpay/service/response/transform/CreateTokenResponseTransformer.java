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

    public CreateTokenResponseTransformer(final ServiceResponseTransformerHelper serviceResponseTransformerHelper) {
        super(serviceResponseTransformerHelper);
    }

    @Override
    public ServiceResponse transform(final PaymentService paymentService) throws WorldpayModelTransformationException {
        final CreateTokenResponse createTokenResponse = new CreateTokenResponse();

        final Reply intReply = paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()
            .stream()
            .filter(Reply.class::isInstance)
            .map(Reply.class::cast)
            .findAny()
            .orElseThrow(() -> new WorldpayModelTransformationException("Reply has no reply message or the reply type is not the expected one"));

        if (serviceResponseTransformerHelper.checkForError(createTokenResponse, intReply)) {
            return createTokenResponse;
        }
        intReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrCheckCardHolderNameResponseOrEcheckVerificationResponseOrPaymentOptionOrToken()
            .stream()
            .filter(Token.class::isInstance)
            .map(Token.class::cast)
            .findAny()
            .ifPresent(response -> createTokenResponse.setToken(serviceResponseTransformerHelper.buildTokenReply(response)));

        return createTokenResponse;
    }
}

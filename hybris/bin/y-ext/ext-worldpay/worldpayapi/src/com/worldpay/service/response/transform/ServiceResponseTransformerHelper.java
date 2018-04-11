package com.worldpay.service.response.transform;

import com.worldpay.internal.model.*;
import com.worldpay.service.model.ErrorDetail;
import com.worldpay.service.model.JournalReply;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.WebformRefundReply;
import com.worldpay.service.model.token.DeleteTokenReply;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.model.token.UpdateTokenReply;
import com.worldpay.service.response.ServiceResponse;

/**
 * Helper class with commonly used methods by the {@link ServiceResponseTransformerHelper} objects
 */
public interface ServiceResponseTransformerHelper {
    /**
     * Check the reply for any errors and if so add the details to the supplied response
     *
     * @param response response to add {@link ErrorDetail} to
     * @param reply    reply to interrogate for errors
     * @return true if errors exist, else false
     */
    boolean checkForError(ServiceResponse response, Reply reply);


    /**
     * Build a {@link PaymentReply representation} given an internal representation of the payment
     *
     * @param intPayment intPayment to transform
     * @return PaymentReply representation
     */
    PaymentReply buildPaymentReply(Payment intPayment);

    UpdateTokenReply buildUpdateTokenReply(UpdateTokenReceived intUpdateTokenReceived);

    DeleteTokenReply buildDeleteTokenReply(DeleteTokenReceived intDeleteTokenReceived);

    TokenReply buildTokenReply(Token intToken);

    WebformRefundReply buildWebformRefundReply(ShopperWebformRefundDetails intShopperWebformRefundDetails);

    JournalReply buildJournalReply(Journal intJournal);
}

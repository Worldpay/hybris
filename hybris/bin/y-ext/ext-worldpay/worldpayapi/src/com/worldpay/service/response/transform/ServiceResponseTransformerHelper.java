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
    boolean checkForError(final ServiceResponse response, final Reply reply);

    /**
     * Builds a {@link PaymentReply representation} given an internal representation of the payment
     *
     * @param intPayment intPayment to transform
     * @return PaymentReply representation
     */
    PaymentReply buildPaymentReply(final Payment intPayment);

    /**
     * Builds a UpdateTokenReply using the UpdateTokenReceived passed as parameter
     *
     * @param intUpdateTokenReceived
     * @return
     */
    UpdateTokenReply buildUpdateTokenReply(final UpdateTokenReceived intUpdateTokenReceived);

    /**
     * Builds a DeleteTokenReply using the DeleteTokenReceived passed as parameter
     *
     * @param intDeleteTokenReceived
     * @return
     */
    DeleteTokenReply buildDeleteTokenReply(final DeleteTokenReceived intDeleteTokenReceived);

    /**
     * Builds a tokenReply using the Token passed as parameter
     *
     * @param intToken
     * @return
     */
    TokenReply buildTokenReply(final Token intToken);

    /**
     * builds a webformRefundReply using the ShopperWebformRefundDetails passed as parameter
     *
     * @param intShopperWebformRefundDetails
     * @return
     */
    WebformRefundReply buildWebformRefundReply(final ShopperWebformRefundDetails intShopperWebformRefundDetails);

    /**
     * Builds a JournalReply using the Journal passed as parameter
     *
     * @param intJournal
     * @return
     */
    JournalReply buildJournalReply(final Journal intJournal);
}

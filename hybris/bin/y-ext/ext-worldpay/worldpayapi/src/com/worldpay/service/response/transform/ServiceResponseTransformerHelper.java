package com.worldpay.service.response.transform;

import com.worldpay.internal.model.*;
import com.worldpay.service.model.JournalReply;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.WebformRefundReply;
import com.worldpay.service.model.token.DeleteTokenReply;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.model.token.UpdateTokenReply;
import com.worldpay.service.response.ServiceResponse;

public interface ServiceResponseTransformerHelper {
    boolean checkForError(ServiceResponse response, Reply reply);

    PaymentReply buildPaymentReply(Payment intPayment);

    UpdateTokenReply buildUpdateTokenReply(UpdateTokenReceived intUpdateTokenReceived);

    DeleteTokenReply buildDeleteTokenReply(DeleteTokenReceived intDeleteTokenReceived);

    TokenReply buildTokenReply(Token intToken);

    WebformRefundReply buildWebformRefundReply(ShopperWebformRefundDetails intShopperWebformRefundDetails);

    JournalReply buildJournalReply(Journal intJournal);
}

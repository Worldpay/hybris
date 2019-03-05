package com.worldpay.core.services;

import com.worldpay.data.ApplePayAdditionalAuthInfo;
import com.worldpay.data.GooglePayAdditionalAuthInfo;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import com.worldpay.service.response.CreateTokenResponse;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.util.Optional;

/**
 * Payment Info Service interface.
 * The Service is responsible for creating {@link WorldpayAPMPaymentInfoModel} and updating the Payment Info of the given Payment Transaction.
 */
public interface WorldpayPaymentInfoService {

    /**
     * Saves the payment type of the payment info of the payment transaction based on the passed method code.
     *
     * @param transactionModel the {@link PaymentTransactionModel} object with the {@link PaymentInfoModel} to be updated
     * @param methodCode       the payment type
     */
    void savePaymentType(final PaymentTransactionModel transactionModel, final String methodCode);

    /**
     * Attaches the given {@link PaymentInfoModel} to the {@link AbstractOrderModel} and {@link PaymentTransactionModel}
     *
     * @param paymentTransactionModel
     * @param orderModel
     * @param paymentInfoModel
     */
    void updateAndAttachPaymentInfoModel(final PaymentTransactionModel paymentTransactionModel, final AbstractOrderModel orderModel, final PaymentInfoModel paymentInfoModel);


    /**
     * Creates {@link WorldpayAPMPaymentInfoModel} for the given {@link PaymentTransactionModel}
     *
     * @param paymentTransactionModel the {@link PaymentTransactionModel} to be updated
     * @return newly created {@link WorldpayAPMPaymentInfoModel}
     */
    WorldpayAPMPaymentInfoModel createWorldpayApmPaymentInfo(final PaymentTransactionModel paymentTransactionModel);

    /**
     * Sets {@link PaymentInfoModel} for the {@link PaymentTransactionModel} and the {@link AbstractOrderModel} depending on the information from {@link OrderNotificationMessage}
     *
     * @param paymentTransactionModel  the {@link PaymentTransactionModel} to be updated
     * @param orderModel               the {@link AbstractOrderModel} to be updated
     * @param orderNotificationMessage the {@link OrderNotificationMessage} from the notification message from Worldpay
     */
    void setPaymentInfoModel(final PaymentTransactionModel paymentTransactionModel, final AbstractOrderModel orderModel, final OrderNotificationMessage orderNotificationMessage);

    /**
     * Creates a paymentInfo based on the passed cart {@link CartModel}
     *
     * @param cartModel cart to base the paymentInfo on
     * @return
     */
    PaymentInfoModel createPaymentInfo(final CartModel cartModel);

    /**
     * Creates a paymentInfo saving parameters from Google
     *
     * @param cartModel cart to base the paymentInfo on
     * @return
     */
    PaymentInfoModel createPaymentInfoGooglePay(final CartModel cartModel, final GooglePayAdditionalAuthInfo googleAuthInfo);

    /**
     * Creates a CreditCardPaymentInfo based on the passed cart {@link CartModel} and {@link CreateTokenResponse}
     *
     * @param cartModel           the session cart
     * @param createTokenResponse the {@link CreateTokenResponse} from Worldpay
     * @param saveCard            the flag to display card in user account
     * @param merchantId          the merchant id for the payment info
     * @return
     */
    CreditCardPaymentInfoModel createCreditCardPaymentInfo(final CartModel cartModel, final CreateTokenResponse createTokenResponse, final boolean saveCard, final String merchantId);

    /**
     * Converts and sets the {@link CreditCardType} on the {@link CreditCardPaymentInfoModel} based on the methodCode of the {@link PaymentReply}
     *
     * @param creditCardPaymentInfoModel The {@link CreditCardPaymentInfoModel} to be updated
     * @param paymentReply               The paymentReply to get the methodCode from. This methodCode will be converted based on configurations and mappings in Hybris.
     */
    void setCreditCardType(CreditCardPaymentInfoModel creditCardPaymentInfoModel, PaymentReply paymentReply);

    /**
     * Updates the {@link CreditCardPaymentInfoModel} based on the {@UpdateTokenServiceRequest} if there is a matching tokenised card.
     *
     * @param cartModel
     * @param updateTokenServiceRequest
     * @return Optional {@link Optional} that will contain the updated CreditCardPaymentInfoModel or empty if no matching tokenised card is found
     */
    Optional<CreditCardPaymentInfoModel> updateCreditCardPaymentInfo(final CartModel cartModel, final UpdateTokenServiceRequest updateTokenServiceRequest);

    /**
     * Creates an ApplePay payment info
     *
     * @param cartModel
     * @param applePayAdditionalAuthInfo
     * @return
     */
    PaymentInfoModel createPaymentInfoApplePay(final CartModel cartModel, final ApplePayAdditionalAuthInfo applePayAdditionalAuthInfo);

}

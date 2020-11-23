package com.worldpay.service.payment;

import com.worldpay.data.ApplePayAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.model.Address;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.BasicOrderInfo;
import com.worldpay.service.model.payment.Cse;
import com.worldpay.service.model.payment.PayWithGoogleSSL;
import com.worldpay.service.model.payment.Payment;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Exposes utility methods to create objects used in the request creation.
 */
public interface WorldpayOrderService {

    /**
     * Creates a Worldpay {@link Amount} object using the  currencyIsoCode and the amount.
     *
     * @param currency currency to be used
     * @param amount   total amount of the order
     * @return Amount object
     */
    Amount createAmount(final Currency currency, final int amount);

    /**
     * Creates a Worldpay {@link Amount} object using the {@link CurrencyModel} and the amount.
     *
     * @param currencyModel currency to be used
     * @param amount        total amount of the order
     * @return Amount object
     */
    Amount createAmount(final CurrencyModel currencyModel, final double amount);

    /**
     * Creates a Worldpay {@link Amount} object using the {@link Currency} and the amount.
     *
     * @param currency
     * @param amount
     * @return Amount object required by DirectAuthoriseServiceRequest
     */
    Amount createAmount(final Currency currency, final double amount);

    /**
     * Converts Amount Object into a BigDecimal value taking into account the currency
     *
     * @param amount
     * @return amount as a BigDecimal
     */
    BigDecimal convertAmount(final Amount amount);


    /**
     * Creates a Worldpay {@link BasicOrderInfo} object
     *
     * @param worldpayOrderCode Identifier of the order in Worldpay
     * @param description       Description of the order
     * @param amount            Payable amount {@link Amount}
     * @return Basic order information object
     */
    BasicOrderInfo createBasicOrderInfo(final String worldpayOrderCode, final String description, final Amount amount);


    /**
     * Creates a payment element to be used in klarna
     *
     * @param countryCode         indicates the shopper country code
     * @param language            indicates the session language code of the user
     * @param extraMerchantData   extra data to be filled by the merchant
     * @param klarnaPaymentMethod indicates the klarna payment type
     * @return Payment object
     */
    Payment createKlarnaPayment(final String countryCode, final LanguageModel language, final String extraMerchantData, final String klarnaPaymentMethod) throws WorldpayConfigurationException;


    /**
     * Creates an Payment of type ApplePay with the requested details
     *
     * @param worldpayAdditionalInfoApplePayData
     * @return
     */
    Payment createApplePayPayment(final ApplePayAdditionalAuthInfo worldpayAdditionalInfoApplePayData);

    /**
     * Creates an Payment of type CSE with the requested details
     *
     * @param cseAdditionalAuthInfo
     * @param billingAddress
     * @return
     */
    Cse createCsePayment(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final Address billingAddress);

    /**
     * Creates an Payment of type GooglePay with the requested details
     *
     * @param protocolVersion
     * @param signature
     * @param signedMessage
     * @return
     */
    PayWithGoogleSSL createGooglePayPayment(final String protocolVersion, final String signature, final String signedMessage);

    /**
     * @param paymentInfoModel
     * @param authorisationAmount
     * @param cartModel
     * @return
     */
    CommerceCheckoutParameter createCheckoutParameterAndSetPaymentInfo(final PaymentInfoModel paymentInfoModel, final BigDecimal authorisationAmount, final CartModel cartModel);

    /**
     * Creates a {@link CommerceCheckoutParameter} based on the passed {@link CartModel} and {@link PaymentInfoModel} given
     *
     * @param abstractOrderModel  The abstractOrderModel to base the commerceCheckoutParameter on
     * @param paymentInfoModel    The paymentInfo to base the commerceCheckoutParameter on
     * @param authorisationAmount The authorised amount by the payment provider
     * @return the created parameters
     */
    CommerceCheckoutParameter createCommerceCheckoutParameter(final AbstractOrderModel abstractOrderModel, final PaymentInfoModel paymentInfoModel, final BigDecimal authorisationAmount);

    /**
     * Generates worldpay order code from the cart
     *
     * @param abstractOrderModel the cart
     * @return the generated code
     */
    String generateWorldpayOrderCode(final AbstractOrderModel abstractOrderModel);
}

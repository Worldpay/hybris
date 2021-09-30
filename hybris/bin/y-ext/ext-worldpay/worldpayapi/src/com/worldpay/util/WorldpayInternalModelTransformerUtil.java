package com.worldpay.util;

import com.worldpay.data.Date;
import com.worldpay.data.PaymentDetails;
import com.worldpay.data.klarna.KlarnaMerchantUrls;
import com.worldpay.data.klarna.KlarnaPayment;
import com.worldpay.data.klarna.KlarnaRedirectURLs;
import com.worldpay.data.payment.AlternativePayment;
import com.worldpay.data.payment.AlternativeShopperBankCodePayment;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.request.AuthoriseRequestParameters;

import java.time.LocalDateTime;

import static com.worldpay.service.model.payment.PaymentType.KLARNASSL;

public class WorldpayInternalModelTransformerUtil {

    private WorldpayInternalModelTransformerUtil() {
    }

    public static Date newDateFromLocalDateTime(final LocalDateTime localDateTime) {
        final Date date = new Date();
        date.setDayOfMonth(String.valueOf(localDateTime.getDayOfMonth()));
        date.setMonth(String.valueOf(localDateTime.getMonth().getValue()));
        date.setYear(String.valueOf(localDateTime.getYear()));
        date.setHour(String.valueOf(localDateTime.getHour()));
        date.setMinute(String.valueOf(localDateTime.getMinute()));
        date.setSecond(String.valueOf(localDateTime.getSecond()));

        return date;
    }

    public static AlternativePayment createAlternativePayment(final PaymentType paymentType, final String successURL, final String failureURL, final String cancelURL, final String pendingUrl, final String shopperCountryCode) {
        return setAlternativePaymentData(paymentType, successURL, failureURL, cancelURL, pendingUrl, new AlternativePayment(), shopperCountryCode);
    }

    public static AlternativeShopperBankCodePayment createAlternativeShopperBankCodePayment(final PaymentType paymentType, final String shopperBankCode, final String successURL, final String failureURL, final String cancelURL, final String pendingUrl, final String shopperCountryCode) {
        final AlternativeShopperBankCodePayment alternativeShopperBankCodePayment = new AlternativeShopperBankCodePayment();
        setAlternativePaymentData(paymentType, successURL, failureURL, cancelURL, pendingUrl, alternativeShopperBankCodePayment, shopperCountryCode);
        alternativeShopperBankCodePayment.setShopperBankCode(shopperBankCode);

        return alternativeShopperBankCodePayment;
    }

    private static AlternativePayment setAlternativePaymentData(final PaymentType paymentType, final String successURL, final String failureURL, final String cancelURL, final String pendingUrl, final AlternativePayment alternativePayment, final String shopperCountryCode) {
        alternativePayment.setPaymentType(paymentType.getMethodCode());
        alternativePayment.setSuccessURL(successURL);
        alternativePayment.setFailureURL(failureURL);
        alternativePayment.setCancelURL(cancelURL);
        alternativePayment.setPendingURL(pendingUrl);
        alternativePayment.setShopperCountryCode(shopperCountryCode);

        return alternativePayment;
    }

    public static PaymentDetails createPaymentDetailsFromRequestParameters(final AuthoriseRequestParameters requestParameters) {
        final PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setPayment(requestParameters.getPayment());
        paymentDetails.setSession(requestParameters.getShopper() != null ? requestParameters.getShopper().getSession() : null);
        paymentDetails.setStoredCredentials(requestParameters.getStoredCredentials());
        paymentDetails.setAction(requestParameters.getAction());

        return paymentDetails;
    }

    public static KlarnaPayment createKlarnaPayment(final String purchaseCountry, final String shopperLocale, final KlarnaMerchantUrls merchantUrls, final String extraMerchantData) {
        final KlarnaPayment klarnaPayment = new KlarnaPayment();
        klarnaPayment.setPaymentType(KLARNASSL.getMethodCode());
        klarnaPayment.setPurchaseCountry(purchaseCountry);
        klarnaPayment.setShopperLocale(shopperLocale);
        klarnaPayment.setMerchantUrls(merchantUrls);
        klarnaPayment.setExtraMerchantData(extraMerchantData);

        return klarnaPayment;
    }

    public static KlarnaPayment createKlarnaPayment(final String shopperCountryCode, final String shopperLocale, final String extraMerchantData,
                                                    final String klarnaPaymentMethod, final KlarnaRedirectURLs klarnaRedirectURLs) {
        final KlarnaPayment klarnaPayment = new KlarnaPayment();
        klarnaPayment.setPaymentType(klarnaPaymentMethod);
        klarnaPayment.setShopperCountryCode(shopperCountryCode);
        klarnaPayment.setLocale(shopperLocale);
        klarnaPayment.setSuccessURL(klarnaRedirectURLs.getSuccessURL());
        klarnaPayment.setPendingURL(klarnaRedirectURLs.getPendingURL());
        klarnaPayment.setFailureURL(klarnaRedirectURLs.getFailureURL());
        klarnaPayment.setCancelURL(klarnaRedirectURLs.getCancelURL());
        klarnaPayment.setExtraMerchantData(extraMerchantData);

        return klarnaPayment;
    }
}

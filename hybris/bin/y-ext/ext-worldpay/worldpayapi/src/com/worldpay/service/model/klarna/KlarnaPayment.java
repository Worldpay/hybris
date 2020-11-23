package com.worldpay.service.model.klarna;

import com.worldpay.internal.model.MerchantUrls;
import com.worldpay.service.model.payment.AbstractPayment;
import com.worldpay.service.model.payment.PaymentType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.worldpay.service.model.payment.PaymentType.KLARNASSL;

public class KlarnaPayment extends AbstractPayment {

    private String purchaseCountry;
    private String shopperCountryCode;
    private String locale;
    private String shopperLocale;
    private String successURL;
    private String pendingURL;
    private String failureURL;
    private String cancelURL;
    private KlarnaMerchantUrls merchantUrls;
    private final String extraMerchantData;


    public KlarnaPayment(final String purchaseCountry, final String shopperLocale, final KlarnaMerchantUrls merchantUrls, final String extraMerchantData) {
        this.setPaymentType(KLARNASSL);
        this.purchaseCountry = purchaseCountry;
        this.shopperLocale = shopperLocale;
        this.merchantUrls = merchantUrls;
        this.extraMerchantData = extraMerchantData;
    }

    public KlarnaPayment(final String purchaseCountry, final String shopperLocale, final String extraMerchantData,
                         final String klarnaPaymentMethod, final KlarnaRedirectURLs klarnaRedirectURLs) {
        this.setPaymentType(PaymentType.getPaymentType(klarnaPaymentMethod));
        this.shopperCountryCode = purchaseCountry;
        this.locale = shopperLocale;
        this.successURL = klarnaRedirectURLs.getSuccessURL();
        this.pendingURL = klarnaRedirectURLs.getPendingURL();
        this.failureURL = klarnaRedirectURLs.getFailureURL();
        this.cancelURL = klarnaRedirectURLs.getCancelURL();
        this.extraMerchantData = extraMerchantData;
    }

    @SuppressWarnings("java:S3776")
    @Override
    public void invokeSetter(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException {
        final String methodName = method.getName();
        if ("setPurchaseCountry".equals(methodName) && purchaseCountry != null) {
            method.invoke(targetObject, purchaseCountry);
        }
        if ("setShopperLocale".equals(methodName) && shopperLocale != null) {
            method.invoke(targetObject, shopperLocale);
        }
        if ("setShopperCountryCode".equals(methodName) && shopperCountryCode != null) {
            method.invoke(targetObject, shopperCountryCode);
        }
        if ("setLocale".equals(methodName) && locale != null) {
            method.invoke(targetObject, locale);
        }
        if ("setSuccessURL".equals(methodName) && successURL != null) {
            method.invoke(targetObject, successURL);
        }
        if ("setPendingURL".equals(methodName) && pendingURL != null) {
            method.invoke(targetObject, pendingURL);
        }
        if ("setFailureURL".equals(methodName) && failureURL != null) {
            method.invoke(targetObject, failureURL);
        }
        if ("setCancelURL".equals(methodName) && cancelURL != null) {
            method.invoke(targetObject, cancelURL);
        }
        if ("setMerchantUrls".equals(methodName) && merchantUrls != null) {
            final MerchantUrls intMerchantUrls = new MerchantUrls();
            intMerchantUrls.setCheckoutURL(merchantUrls.getCheckoutURL());
            intMerchantUrls.setConfirmationURL(merchantUrls.getConfirmationURL());
            method.invoke(targetObject, intMerchantUrls);
        }
        if ("setExtraMerchantData".equals(methodName) && extraMerchantData != null) {
            method.invoke(targetObject, extraMerchantData);
        }

    }

    public String getPurchaseCountry() {
        return purchaseCountry;
    }

    public String getShopperCountryCode() {
        return shopperCountryCode;
    }

    public String getLocale() {
        return locale;
    }

    public String getShopperLocale() {
        return shopperLocale;
    }

    public KlarnaMerchantUrls getMerchantUrls() {
        return merchantUrls;
    }

    public String getExtraMerchantData() {
        return extraMerchantData;
    }

    public String getSuccessURL() {
        return successURL;
    }

    public String getPendingURL() {
        return pendingURL;
    }

    public String getFailureURL() {
        return failureURL;
    }
}

package com.worldpay.service.model.klarna;

import com.worldpay.internal.model.MerchantUrls;
import com.worldpay.service.model.payment.AbstractPayment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.worldpay.service.model.payment.PaymentType.KLARNASSL;

public class KlarnaPayment extends AbstractPayment {
    private String purchaseCountry;
    private String shopperLocale;
    private KlarnaMerchantUrls merchantUrls;
    private String extraMerchantData;

    public KlarnaPayment(final String purchaseCountry, final String shopperLocale, final KlarnaMerchantUrls merchantUrls, final String extraMerchantData) {
        this.setPaymentType(KLARNASSL);
        this.purchaseCountry = purchaseCountry;
        this.shopperLocale = shopperLocale;
        this.merchantUrls = merchantUrls;
        this.extraMerchantData = extraMerchantData;
    }

    @Override
    public void invokeSetter(final Method method, final Object targetObject) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        String methodName = method.getName();
        if (methodName.startsWith("set")) {
            if ("setPurchaseCountry".equals(methodName) && purchaseCountry != null) {
                method.invoke(targetObject, purchaseCountry);
            }
            if ("setShopperLocale".equals(methodName) && shopperLocale != null) {
                method.invoke(targetObject, shopperLocale);
            }
            if ("setMerchantUrls".equals(methodName) && merchantUrls != null) {
                MerchantUrls intMerchantUrls = new MerchantUrls();
                intMerchantUrls.setCheckoutURL(merchantUrls.getCheckoutURL());
                intMerchantUrls.setConfirmationURL(merchantUrls.getConfirmationURL());
                method.invoke(targetObject, intMerchantUrls);
            }
            if ("setExtraMerchantData".equals(methodName) && extraMerchantData != null) {
                method.invoke(targetObject, extraMerchantData);
            }
        }
    }

    public String getPurchaseCountry() {
        return purchaseCountry;
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
}

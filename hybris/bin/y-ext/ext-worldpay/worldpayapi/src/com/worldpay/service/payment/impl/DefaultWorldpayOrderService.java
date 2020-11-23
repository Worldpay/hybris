package com.worldpay.service.payment.impl;

import com.worldpay.core.services.strategies.RecurringGenerateMerchantTransactionCodeStrategy;
import com.worldpay.data.ApplePayAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.klarna.WorldpayKlarnaUtils;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.model.*;
import com.worldpay.service.model.applepay.ApplePay;
import com.worldpay.service.model.applepay.Header;
import com.worldpay.service.model.klarna.KlarnaMerchantUrls;
import com.worldpay.service.model.klarna.KlarnaRedirectURLs;
import com.worldpay.service.model.payment.*;
import com.worldpay.service.payment.WorldpayOrderService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayOrderService implements WorldpayOrderService {

    protected final CommonI18NService commonI18NService;
    protected final WorldpayUrlService worldpayUrlService;
    protected final WorldpayKlarnaUtils worldpayKlarnaUtils;
    protected final CommerceCheckoutService commerceCheckoutService;
    protected final RecurringGenerateMerchantTransactionCodeStrategy recurringGenerateMerchantTransactionCodeStrategy;

    public DefaultWorldpayOrderService(final CommonI18NService commonI18NService,
                                       final WorldpayUrlService worldpayUrlService,
                                       final WorldpayKlarnaUtils worldpayKlarnaUtils,
                                       final CommerceCheckoutService commerceCheckoutService,
                                       final RecurringGenerateMerchantTransactionCodeStrategy recurringGenerateMerchantTransactionCodeStrategy) {
        this.commonI18NService = commonI18NService;
        this.worldpayUrlService = worldpayUrlService;
        this.worldpayKlarnaUtils = worldpayKlarnaUtils;
        this.commerceCheckoutService = commerceCheckoutService;
        this.recurringGenerateMerchantTransactionCodeStrategy = recurringGenerateMerchantTransactionCodeStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Amount createAmount(final Currency currency, final int amount) {
        final Double roundedValue = commonI18NService.convertAndRoundCurrency(Math.pow(10, currency.getDefaultFractionDigits()), 1, currency.getDefaultFractionDigits(), amount);
        return new Amount(String.valueOf(roundedValue), currency.getCurrencyCode(), String.valueOf(currency.getDefaultFractionDigits()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Amount createAmount(final CurrencyModel currencyModel, final double amount) {
        final Currency currency = Currency.getInstance(currencyModel.getIsocode());
        return createAmount(currency, amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Amount createAmount(final Currency currency, final double amount) {
        final Double roundedValue = commonI18NService.convertAndRoundCurrency(1, Math.pow(10, currency.getDefaultFractionDigits()), 0, amount);
        return new Amount(String.valueOf(roundedValue.intValue()), currency.getCurrencyCode(), String.valueOf(currency.getDefaultFractionDigits()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal convertAmount(final Amount amount) {
        final Currency currency = Currency.getInstance(amount.getCurrencyCode());
        return new BigDecimal(amount.getValue()).movePointLeft(currency.getDefaultFractionDigits());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BasicOrderInfo createBasicOrderInfo(final String worldpayOrderCode, final String description, final Amount amount) {
        return new BasicOrderInfo(worldpayOrderCode, description, amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment createKlarnaPayment(final String countryCode, final LanguageModel language, final String extraMerchantData, final String klarnaPaymentMethod) throws WorldpayConfigurationException {
        PaymentType klarnaPaymentType = null;

        if (worldpayKlarnaUtils.isKlarnaPaymentType(klarnaPaymentMethod)) {
            klarnaPaymentType = PaymentType.getPaymentType(klarnaPaymentMethod);
        }

        final String languageCode = commonI18NService.getLocaleForLanguage(language).toLanguageTag();
        if (Objects.nonNull(klarnaPaymentType) && PaymentType.KLARNASSL.equals(klarnaPaymentType)) {
            final KlarnaMerchantUrls merchantUrls = new KlarnaMerchantUrls(worldpayUrlService.getBaseWebsiteUrlForSite(), worldpayUrlService.getKlarnaConfirmationURL());
            return PaymentBuilder.createKlarnaPayment(countryCode, languageCode,
                merchantUrls, extraMerchantData);
        } else if (Objects.nonNull(klarnaPaymentType)) {
            final KlarnaRedirectURLs klarnaRedirectURLs = new KlarnaRedirectURLs(worldpayUrlService.getFullSuccessURL(), worldpayUrlService.getFullCancelURL(), worldpayUrlService.getFullPendingURL(), worldpayUrlService.getFullFailureURL());
            return PaymentBuilder.createKlarnaPayment(countryCode, languageCode, extraMerchantData, klarnaPaymentMethod, klarnaRedirectURLs);
        } else {
            throw new WorldpayConfigurationException("Invalid Klarna Payment Method");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment createApplePayPayment(final ApplePayAdditionalAuthInfo worldpayAdditionalInfoApplePayData) {
        final Header header = new Header(worldpayAdditionalInfoApplePayData.getHeader().getEphemeralPublicKey(), worldpayAdditionalInfoApplePayData.getHeader().getPublicKeyHash(), worldpayAdditionalInfoApplePayData.getHeader().getTransactionId(), null);
        return new ApplePay(header, worldpayAdditionalInfoApplePayData.getSignature(), worldpayAdditionalInfoApplePayData.getVersion(), worldpayAdditionalInfoApplePayData.getData(), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cse createCsePayment(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final Address billingAddress) {
        return PaymentBuilder.createCSE(cseAdditionalAuthInfo.getEncryptedData(), billingAddress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PayWithGoogleSSL createGooglePayPayment(final String protocolVersion, final String signature, final String signedMessage) {
        return new PayWithGoogleSSL(protocolVersion, signature, signedMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommerceCheckoutParameter createCheckoutParameterAndSetPaymentInfo(final PaymentInfoModel paymentInfoModel, final BigDecimal authorisationAmount, final CartModel cartModel) {
        final CommerceCheckoutParameter commerceCheckoutParameter = createCommerceCheckoutParameter(cartModel, paymentInfoModel, authorisationAmount);
        commerceCheckoutService.setPaymentInfo(commerceCheckoutParameter);
        return commerceCheckoutParameter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommerceCheckoutParameter createCommerceCheckoutParameter(final AbstractOrderModel abstractOrderModel, final PaymentInfoModel paymentInfoModel, final BigDecimal authorisationAmount) {
        final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
        parameter.setEnableHooks(true);
        if (abstractOrderModel instanceof CartModel) {
            parameter.setCart((CartModel) abstractOrderModel);
        } else {
            parameter.setOrder(abstractOrderModel);
        }
        parameter.setPaymentInfo(paymentInfoModel);
        parameter.setAuthorizationAmount(authorisationAmount);
        parameter.setPaymentProvider(commerceCheckoutService.getPaymentProvider());
        return parameter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateWorldpayOrderCode(final AbstractOrderModel abstractOrderModel){
       return recurringGenerateMerchantTransactionCodeStrategy.generateCode(abstractOrderModel);
    }

}

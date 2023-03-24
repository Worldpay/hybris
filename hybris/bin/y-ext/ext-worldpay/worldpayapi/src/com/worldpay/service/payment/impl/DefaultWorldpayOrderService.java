package com.worldpay.service.payment.impl;

import com.worldpay.core.services.strategies.RecurringGenerateMerchantTransactionCodeStrategy;
import com.worldpay.data.*;
import com.worldpay.data.applepay.ApplePay;
import com.worldpay.data.applepay.Header;
import com.worldpay.data.klarna.KlarnaRedirectURLs;
import com.worldpay.data.payment.Cse;
import com.worldpay.data.payment.PayWithGoogleSSL;
import com.worldpay.data.payment.Payment;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.payment.WorldpayKlarnaService;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.util.WorldpayInternalModelTransformerUtil;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import java.util.Optional;

import static com.worldpay.service.model.payment.PaymentType.CSEDATA;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayOrderService implements WorldpayOrderService {

    private static final String WEB = "WEB";

    protected final CommonI18NService commonI18NService;
    protected final WorldpayUrlService worldpayUrlService;
    protected final WorldpayKlarnaService worldpayKlarnaService;
    protected final CommerceCheckoutService commerceCheckoutService;
    protected final BaseSiteService baseSiteService;
    protected final RecurringGenerateMerchantTransactionCodeStrategy recurringGenerateMerchantTransactionCodeStrategy;

    public DefaultWorldpayOrderService(final CommonI18NService commonI18NService,
                                       final WorldpayUrlService worldpayUrlService,
                                       final WorldpayKlarnaService worldpayKlarnaService,
                                       final CommerceCheckoutService commerceCheckoutService,
                                       final BaseSiteService baseSiteService,
                                       final RecurringGenerateMerchantTransactionCodeStrategy recurringGenerateMerchantTransactionCodeStrategy) {
        this.commonI18NService = commonI18NService;
        this.worldpayUrlService = worldpayUrlService;
        this.worldpayKlarnaService = worldpayKlarnaService;
        this.commerceCheckoutService = commerceCheckoutService;
        this.baseSiteService = baseSiteService;
        this.recurringGenerateMerchantTransactionCodeStrategy = recurringGenerateMerchantTransactionCodeStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Amount createAmount(final Currency currency, final int amount) {
        final Double roundedValue = commonI18NService.convertAndRoundCurrency(Math.pow(10, currency.getDefaultFractionDigits()), 1, currency.getDefaultFractionDigits(), amount);
        final Amount amountData = new Amount();
        amountData.setCurrencyCode(currency.getCurrencyCode());
        amountData.setValue(String.valueOf(roundedValue));
        amountData.setExponent(String.valueOf(currency.getDefaultFractionDigits()));
        return amountData;
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
        final Amount amountData = new Amount();
        amountData.setCurrencyCode(currency.getCurrencyCode());
        amountData.setValue(String.valueOf(roundedValue.intValue()));
        amountData.setExponent(String.valueOf(currency.getDefaultFractionDigits()));
        return amountData;
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
        final BasicOrderInfo basicOrderInfor = new BasicOrderInfo();
        basicOrderInfor.setAmount(amount);
        basicOrderInfor.setDescription(description);
        basicOrderInfor.setOrderCode(worldpayOrderCode);
        basicOrderInfor.setOrderChannel(WEB);
        return basicOrderInfor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment createKlarnaPayment(final String countryCode, final LanguageModel language, final String extraMerchantData, final String klarnaPaymentMethod) throws WorldpayConfigurationException {
        PaymentType klarnaPaymentType = null;

        if (worldpayKlarnaService.isKlarnaPaymentType(klarnaPaymentMethod)) {
            klarnaPaymentType = PaymentType.getPaymentType(klarnaPaymentMethod);
        }

        final String languageCode = Optional.ofNullable(language)
            .map(languageModel -> commonI18NService.getLocaleForLanguage(languageModel).toLanguageTag())
            .orElseGet(() -> commonI18NService.getLocaleForLanguage(baseSiteService.getCurrentBaseSite().getDefaultLanguage()).toLanguageTag());

        final String locale = languageCode.concat("-").concat(countryCode);
        if (Objects.nonNull(klarnaPaymentType)) {
            final KlarnaRedirectURLs klarnaRedirectURLs = new KlarnaRedirectURLs();
            klarnaRedirectURLs.setSuccessURL(worldpayUrlService.getFullSuccessURL());
            klarnaRedirectURLs.setCancelURL(worldpayUrlService.getFullCancelURL());
            klarnaRedirectURLs.setPendingURL(worldpayUrlService.getFullPendingURL());
            klarnaRedirectURLs.setFailureURL(worldpayUrlService.getFullFailureURL());
            return WorldpayInternalModelTransformerUtil.createKlarnaPayment(countryCode, locale, extraMerchantData, klarnaPaymentMethod, klarnaRedirectURLs);
        } else {
            throw new WorldpayConfigurationException("Invalid Klarna Payment Method");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment createApplePayPayment(final ApplePayAdditionalAuthInfo worldpayAdditionalInfoApplePayData) {
        final Header header = new Header();
        header.setEphemeralPublicKey(worldpayAdditionalInfoApplePayData.getHeader().getEphemeralPublicKey());
        header.setPublicKeyHash(worldpayAdditionalInfoApplePayData.getHeader().getPublicKeyHash());
        header.setTransactionId(worldpayAdditionalInfoApplePayData.getHeader().getTransactionId());

        final ApplePay applePay = new ApplePay();
        applePay.setHeader(header);
        applePay.setSignature(worldpayAdditionalInfoApplePayData.getSignature());
        applePay.setVersion(worldpayAdditionalInfoApplePayData.getVersion());
        applePay.setData(worldpayAdditionalInfoApplePayData.getData());
        applePay.setPaymentType(PaymentType.APPLEPAYSSL.getMethodCode());

        return applePay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cse createCsePayment(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final Address billingAddress) {
        final Cse cse = new Cse();
        cse.setAddress(billingAddress);
        cse.setEncryptedData(cseAdditionalAuthInfo.getEncryptedData());
        cse.setPaymentType(CSEDATA.getMethodCode());

        return cse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PayWithGoogleSSL createGooglePayPayment(final String protocolVersion, final String signature, final String signedMessage) {
        final PayWithGoogleSSL payWithGoogleSSL = new PayWithGoogleSSL();
        payWithGoogleSSL.setProtocolVersion(protocolVersion);
        payWithGoogleSSL.setSignature(signature);
        payWithGoogleSSL.setSignedMessage(signedMessage);
        payWithGoogleSSL.setPaymentType(PaymentType.PAYWITHGOOGLESSL.getMethodCode());

        return payWithGoogleSSL;
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
    public String generateWorldpayOrderCode(final AbstractOrderModel abstractOrderModel) {
        return recurringGenerateMerchantTransactionCodeStrategy.generateCode(abstractOrderModel);
    }

}

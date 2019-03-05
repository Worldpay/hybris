package com.worldpay.service.payment.impl;

import com.worldpay.data.ApplePayAdditionalAuthInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.model.*;
import com.worldpay.service.model.applepay.ApplePay;
import com.worldpay.service.model.applepay.Header;
import com.worldpay.service.model.klarna.KlarnaMerchantUrls;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.PaymentBuilder;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.Token;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.util.Currency;

import static com.worldpay.service.model.payment.PaymentType.IDEAL;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayOrderService implements WorldpayOrderService {

    private static final String WORLDPAY_MERCHANT_TOKEN_ENABLED = "worldpay.merchant.token.enabled";
    private CommonI18NService commonI18NService;
    private WorldpayUrlService worldpayUrlService;
    private SiteConfigService siteConfigService;

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
    public Session createSession(final WorldpayAdditionalInfoData worldpayAdditionalInfo) {
        return new Session(worldpayAdditionalInfo.getCustomerIPAddress(), worldpayAdditionalInfo.getSessionId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Browser createBrowser(final WorldpayAdditionalInfoData worldpayAdditionalInfo) {
        return new Browser(worldpayAdditionalInfo.getAcceptHeader(), worldpayAdditionalInfo.getUserAgentHeader(), worldpayAdditionalInfo.getDeviceType());
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
    public Shopper createShopper(final String customerEmail, final Session session, final Browser browser) {
        return new Shopper(customerEmail, null, browser, session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Shopper createAuthenticatedShopper(final String customerEmail, final String authenticatedShopperID, final Session session, final Browser browser) {
        if (isMerchantTokenEnabled()) {
            return new Shopper(customerEmail, null, browser, session);
        }
        return new Shopper(customerEmail, authenticatedShopperID, browser, session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TokenRequest createTokenRequest(final String tokenEventReference, final String tokenReason) {
        if (isMerchantTokenEnabled()) {
            return createMerchantTokenRequest(tokenEventReference, tokenReason);
        }
        return createShopperTokenRequest(tokenEventReference, tokenReason);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TokenRequest createTokenRequestForDeletion(final String tokenEventReference, final String tokenReason, final String authenticatedShopperId) {
        if (StringUtils.isBlank(authenticatedShopperId)) {
            return createMerchantTokenRequest(tokenEventReference, tokenReason);
        }
        return createShopperTokenRequest(tokenEventReference, tokenReason);
    }

    private TokenRequest createMerchantTokenRequest(final String tokenEventReference, final String tokenReason) {
        return new TokenRequest(tokenEventReference, tokenReason, true);
    }

    private TokenRequest createShopperTokenRequest(final String tokenEventReference, final String tokenReason) {
        return new TokenRequest(tokenEventReference, tokenReason, false);
    }

    @Override
    public CreateTokenServiceRequest createTokenServiceRequest(final MerchantInfo merchantInfo, final String authenticatedShopperId,
                                                               final Payment csePayment, final TokenRequest tokenRequest) {
        if (isMerchantTokenEnabled()) {
            return CreateTokenServiceRequest.createTokenRequestForMerchantToken(merchantInfo, csePayment, tokenRequest);
        }
        return CreateTokenServiceRequest.createTokenRequestForShopperToken(merchantInfo, authenticatedShopperId, csePayment, tokenRequest);
    }

    private boolean isMerchantTokenEnabled() {
        return siteConfigService.getBoolean(WORLDPAY_MERCHANT_TOKEN_ENABLED, false);
    }

    @Override
    public Payment createBankPayment(final String paymentMethod, final String shopperBankCode) throws WorldpayConfigurationException {
        if (IDEAL.getMethodCode().equals(paymentMethod)) {
            return PaymentBuilder.createIDEALSSL(shopperBankCode, worldpayUrlService.getFullSuccessURL(), worldpayUrlService.getFullFailureURL(), worldpayUrlService.getFullCancelURL());
        }
        return null;
    }

    @Override
    public Payment createKlarnaPayment(final String countryCode, final String languageCode, final String extraMerchantData) throws WorldpayConfigurationException {
        final KlarnaMerchantUrls merchantUrls = new KlarnaMerchantUrls(worldpayUrlService.getBaseWebsiteUrlForSite(), worldpayUrlService.getKlarnaConfirmationURL());
        return PaymentBuilder.createKLARNASSL(countryCode, languageCode, merchantUrls, extraMerchantData);
    }

    @Override
    public Token createToken(final String subscriptionId, final String securityCode) {
        if (isMerchantTokenEnabled()) {
            return PaymentBuilder.createToken(subscriptionId, securityCode, true);
        }
        return PaymentBuilder.createToken(subscriptionId, securityCode, false);
    }

    @Override
    public UpdateTokenServiceRequest createUpdateTokenServiceRequest(final MerchantInfo merchantInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                                     final TokenRequest tokenRequest, final String paymentTokenID,
                                                                     final CardDetails cardDetails) {
        if (isMerchantTokenEnabled()) {
            return UpdateTokenServiceRequest.updateTokenRequestWithMerchantScope(merchantInfo, paymentTokenID, tokenRequest, cardDetails);
        }
        return UpdateTokenServiceRequest.updateTokenRequestWithShopperScope(merchantInfo, worldpayAdditionalInfoData.getAuthenticatedShopperId(), paymentTokenID, tokenRequest, cardDetails);
    }

    @Override
    public Payment createApplePayPayment(final ApplePayAdditionalAuthInfo worldpayAdditionalInfoApplePayData) {
        final Header header = new Header(worldpayAdditionalInfoApplePayData.getHeader().getEphemeralPublicKey(), worldpayAdditionalInfoApplePayData.getHeader().getPublicKeyHash(), worldpayAdditionalInfoApplePayData.getHeader().getTransactionId(), null);
        return new ApplePay(header, worldpayAdditionalInfoApplePayData.getSignature(), worldpayAdditionalInfoApplePayData.getVersion(), worldpayAdditionalInfoApplePayData.getData(), null);
    }


    @Required
    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    @Required
    public void setWorldpayUrlService(WorldpayUrlService worldpayUrlService) {
        this.worldpayUrlService = worldpayUrlService;
    }

    @Required
    public void setSiteConfigService(final SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }
}

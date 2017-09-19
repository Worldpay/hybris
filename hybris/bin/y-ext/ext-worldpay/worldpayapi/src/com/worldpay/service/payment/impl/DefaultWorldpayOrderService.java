package com.worldpay.service.payment.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.model.*;
import com.worldpay.service.model.klarna.KlarnaMerchantUrls;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.PaymentBuilder;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.payment.WorldpayOrderService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.springframework.beans.factory.annotation.Required;

import java.util.Currency;

import static com.worldpay.service.model.payment.PaymentType.IDEAL;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayOrderService implements WorldpayOrderService {

    private CommonI18NService commonI18NService;
    private WorldpayUrlService worldpayUrlService;

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
        return new Shopper(customerEmail, authenticatedShopperID, browser, session);
    }

    /**
     * WorldpaySummaryCheckoutStepController.java
     * {@inheritDoc}
     */
    @Override
    public TokenRequest createTokenRequest(final String tokenEventReference, final String tokenReason) {
        return new TokenRequest(tokenEventReference, tokenReason);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayServiceGateway getWorldpayServiceGateway() {
        return WorldpayServiceGateway.getInstance();
    }

    @Required
    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    @Required
    public void setWorldpayUrlService(WorldpayUrlService worldpayUrlService) {
        this.worldpayUrlService = worldpayUrlService;
    }
}

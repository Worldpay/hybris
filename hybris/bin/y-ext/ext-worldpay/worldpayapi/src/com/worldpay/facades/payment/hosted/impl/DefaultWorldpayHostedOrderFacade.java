package com.worldpay.facades.payment.hosted.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.core.services.OrderInquiryService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.merchant.strategies.WorldpayOrderInfoStrategy;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Map;

/**
 */
public class DefaultWorldpayHostedOrderFacade implements WorldpayHostedOrderFacade {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayHostedOrderFacade.class);
    protected static final String WORLDPAY_MERCHANT_CODE = "worldpayMerchantCode";

    private SessionService sessionService;
    private WorldpayRedirectOrderService worldpayRedirectOrderService;
    private WorldpayMerchantInfoService worldpayMerchantInfoService;
    private CartService cartService;
    private WorldpayOrderInfoStrategy worldpayOrderInfoStrategy;
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;
    private OrderInquiryService orderInquiryService;
    private APMConfigurationLookupService apmConfigurationLookupService;

    /**
     * {@inheritDoc}
     *
     * @see WorldpayHostedOrderFacade#redirectAuthorise(AdditionalAuthInfo)
     */
    @Override
    public PaymentData redirectAuthorise(final AdditionalAuthInfo additionalAuthInfo) throws WorldpayException {
        final CartModel cart = getCartService().getSessionCart();
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        populateAdditionalAuthInfo(additionalAuthInfo);
        return getWorldpayRedirectOrderService().redirectAuthorise(merchantInfo, cart, additionalAuthInfo);
    }

    protected void populateAdditionalAuthInfo(final AdditionalAuthInfo additionalAuthInfo) {
        final WorldpayMerchantConfigData currentSiteMerchant = worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData();
        worldpayOrderInfoStrategy.populateAdditionalAuthInfo(additionalAuthInfo, currentSiteMerchant);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeRedirectAuthorise(final RedirectAuthoriseResult result) {
        final String merchantCode = sessionService.getAttribute(WORLDPAY_MERCHANT_CODE);
        final CartModel cart = getCartService().getSessionCart();
        getWorldpayRedirectOrderService().completeRedirectAuthorise(result, merchantCode, cart);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateRedirectResponse(final Map<String, String> worldpayResponse) {
        final String merchantCode = sessionService.getAttribute(WORLDPAY_MERCHANT_CODE);
        try {
            final MerchantInfo merchantInfo = getWorldpayMerchantInfoService().getMerchantInfoByCode(merchantCode);
            return getWorldpayRedirectOrderService().validateRedirectResponse(merchantInfo, worldpayResponse);
        } catch (WorldpayConfigurationException e) {
            LOG.error(MessageFormat.format("There was an error getting the configuration for the merchants: [{0}]", e.getMessage()), e);
        }
        return false;
    }

    @Override
    public RedirectAuthoriseResult inquiryPaymentStatus() throws WorldpayException {
        final String worldpayOrderCode = cartService.getSessionCart().getWorldpayOrderCode();
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        final OrderInquiryServiceResponse inquiryResponse = orderInquiryService.inquireOrder(merchantInfo, worldpayOrderCode);
        final PaymentReply paymentReply = inquiryResponse.getPaymentReply();
        final RedirectAuthoriseResult redirectAuthoriseResult = new RedirectAuthoriseResult();
        redirectAuthoriseResult.setPaymentStatus(paymentReply.getAuthStatus());
        final int paymentExponent = Integer.parseInt(paymentReply.getAmount().getExponent());
        final BigDecimal paymentAmount = new BigDecimal(paymentReply.getAmount().getValue()).movePointLeft(paymentExponent);
        redirectAuthoriseResult.setPaymentAmount(paymentAmount);
        redirectAuthoriseResult.setPending(apmConfigurationLookupService.getAPMConfigurationForCode(paymentReply.getMethodCode()) != null);
        return redirectAuthoriseResult;
    }

    public WorldpayRedirectOrderService getWorldpayRedirectOrderService() {
        return worldpayRedirectOrderService;
    }

    @Required
    public void setWorldpayRedirectOrderService(final WorldpayRedirectOrderService worldpayRedirectOrderService) {
        this.worldpayRedirectOrderService = worldpayRedirectOrderService;
    }

    public WorldpayMerchantInfoService getWorldpayMerchantInfoService() {
        return worldpayMerchantInfoService;
    }

    @Required
    public void setWorldpayMerchantInfoService(final WorldpayMerchantInfoService worldpayMerchantInfoService) {
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
    }

    protected CartService getCartService() {
        return cartService;
    }

    @Required
    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }


    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Required
    public void setWorldpayOrderInfoStrategy(WorldpayOrderInfoStrategy worldpayOrderInfoStrategy) {
        this.worldpayOrderInfoStrategy = worldpayOrderInfoStrategy;
    }

    @Required
    public void setWorldpayMerchantConfigDataFacade(final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade) {
        this.worldpayMerchantConfigDataFacade = worldpayMerchantConfigDataFacade;
    }

    @Required
    public void setOrderInquiryService(final OrderInquiryService orderInquiryService) {
        this.orderInquiryService = orderInquiryService;
    }

    @Required
    public void setApmConfigurationLookupService(final APMConfigurationLookupService apmConfigurationLookupService) {
        this.apmConfigurationLookupService = apmConfigurationLookupService;
    }
}

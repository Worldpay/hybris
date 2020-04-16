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
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;

/**
 * Default implementation for {@link WorldpayHostedOrderFacade}
 */
public class DefaultWorldpayHostedOrderFacade implements WorldpayHostedOrderFacade {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayHostedOrderFacade.class);
    private static final String WORLDPAY_MERCHANT_CODE = "worldpayMerchantCode";

    private final SessionService sessionService;
    private final WorldpayRedirectOrderService worldpayRedirectOrderService;
    private final WorldpayMerchantInfoService worldpayMerchantInfoService;
    private final CartService cartService;
    private final WorldpayOrderInfoStrategy worldpayOrderInfoStrategy;
    private final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;
    private final OrderInquiryService orderInquiryService;
    private final APMConfigurationLookupService apmConfigurationLookupService;
    private final WorldpayOrderService worldpayOrderService;

    public DefaultWorldpayHostedOrderFacade(final SessionService sessionService, final WorldpayRedirectOrderService worldpayRedirectOrderService,
                                            final WorldpayMerchantInfoService worldpayMerchantInfoService, final CartService cartService,
                                            final WorldpayOrderInfoStrategy worldpayOrderInfoStrategy,
                                            final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade,
                                            final OrderInquiryService orderInquiryService,
                                            final APMConfigurationLookupService apmConfigurationLookupService,
                                            final WorldpayOrderService worldpayOrderService) {
        this.sessionService = sessionService;
        this.worldpayRedirectOrderService = worldpayRedirectOrderService;
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
        this.cartService = cartService;
        this.worldpayOrderInfoStrategy = worldpayOrderInfoStrategy;
        this.worldpayMerchantConfigDataFacade = worldpayMerchantConfigDataFacade;
        this.orderInquiryService = orderInquiryService;
        this.apmConfigurationLookupService = apmConfigurationLookupService;
        this.worldpayOrderService = worldpayOrderService;
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayHostedOrderFacade#redirectAuthorise(AdditionalAuthInfo)
     */
    @Override
    public PaymentData redirectAuthorise(final AdditionalAuthInfo additionalAuthInfo) throws WorldpayException {
        final CartModel cart = cartService.getSessionCart();
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        populateAdditionalAuthInfo(additionalAuthInfo);
        return worldpayRedirectOrderService.redirectAuthorise(merchantInfo, cart, additionalAuthInfo);
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
        String merchantCode = sessionService.getAttribute(WORLDPAY_MERCHANT_CODE);
        if (Objects.isNull(merchantCode)) {
            merchantCode = worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData().getCode();
        }
        final CartModel cart = cartService.getSessionCart();
        worldpayRedirectOrderService.completePendingRedirectAuthorise(result, merchantCode, cart);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateRedirectResponse(final Map<String, String> worldpayResponse) {
        final String merchantCode = sessionService.getAttribute(WORLDPAY_MERCHANT_CODE);
        try {
            final MerchantInfo merchantInfo = worldpayMerchantInfoService.getMerchantInfoByCode(merchantCode);
            return worldpayRedirectOrderService.validateRedirectResponse(merchantInfo, worldpayResponse);
        } catch (final WorldpayConfigurationException e) {
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
        final BigDecimal paymentAmount = worldpayOrderService.convertAmount(paymentReply.getAmount());
        redirectAuthoriseResult.setPaymentAmount(paymentAmount);
        redirectAuthoriseResult.setPending(apmConfigurationLookupService.getAPMConfigurationForCode(paymentReply.getMethodCode()) != null);
        return redirectAuthoriseResult;
    }
}

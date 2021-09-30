package com.worldpay.facades.payment.hosted.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.merchant.strategies.WorldpayOrderInfoStrategy;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Objects;

/**
 * Default implementation for {@link WorldpayHostedOrderFacade}
 */
public class DefaultWorldpayHostedOrderFacade implements WorldpayHostedOrderFacade {

    private static final String WORLDPAY_MERCHANT_CODE = "worldpayMerchantCode";

    protected final CartService cartService;
    protected final SessionService sessionService;
    protected final WorldpayOrderInfoStrategy worldpayOrderInfoStrategy;
    protected final WorldpayMerchantInfoService worldpayMerchantInfoService;
    protected final WorldpayRedirectOrderService worldpayRedirectOrderService;
    protected final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;
    protected final WorldpayPaymentInfoService worldpayPaymentInfoService;

    public DefaultWorldpayHostedOrderFacade(final CartService cartService,
                                            final SessionService sessionService,
                                            final WorldpayOrderInfoStrategy worldpayOrderInfoStrategy,
                                            final WorldpayMerchantInfoService worldpayMerchantInfoService,
                                            final WorldpayRedirectOrderService worldpayRedirectOrderService,
                                            final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade,
                                            final WorldpayPaymentInfoService worldpayPaymentInfoService) {
        this.cartService = cartService;
        this.sessionService = sessionService;
        this.worldpayOrderInfoStrategy = worldpayOrderInfoStrategy;
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
        this.worldpayRedirectOrderService = worldpayRedirectOrderService;
        this.worldpayMerchantConfigDataFacade = worldpayMerchantConfigDataFacade;
        this.worldpayPaymentInfoService = worldpayPaymentInfoService;
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayHostedOrderFacade#redirectAuthorise(AdditionalAuthInfo, WorldpayAdditionalInfoData)
     */
    @Override
    public PaymentData redirectAuthorise(final AdditionalAuthInfo additionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        final CartModel cart = cartService.getSessionCart();
        final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
        populateAdditionalAuthInfo(additionalAuthInfo);
        return worldpayRedirectOrderService.redirectAuthorise(merchantInfo, cart, additionalAuthInfo, worldpayAdditionalInfoData);
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
    public void createPaymentInfoModelOnCart(final boolean isSaved) {
        final CartModel sessionCart = cartService.getSessionCart();
        worldpayPaymentInfoService.createPaymentInfoModelOnCart(sessionCart, isSaved);
    }
}

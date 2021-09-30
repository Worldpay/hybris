package com.worldpay.core.services.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.core.services.WorldpayHybrisOrderService;
import com.worldpay.core.services.WorldpayPrimeRoutingService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;

import java.util.Objects;

/**
 * Default implementation fo the {@link WorldpayPrimeRoutingService}.
 */
public class DefaultWorldpayPrimeRoutingService implements WorldpayPrimeRoutingService {

    private static final String US_COUNTRY_ISO_CODE = "US";

    protected final WorldpayCartService worldpayCartService;
    protected final CartService cartService;
    protected final WorldpayHybrisOrderService worldpayHybrisOrderService;

    public DefaultWorldpayPrimeRoutingService(final WorldpayCartService worldpayCartService,
                                              final CartService cartService,
                                              final WorldpayHybrisOrderService worldpayHybrisOrderService) {
        this.worldpayCartService = worldpayCartService;
        this.cartService = cartService;
        this.worldpayHybrisOrderService = worldpayHybrisOrderService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPrimeRoutingEnabled(final AbstractOrderModel cart) {
        final AddressModel paymentAddress = cart.getPaymentAddress();
        if (Objects.nonNull(paymentAddress)) {
            return cart.getSite().getEnablePR() && US_COUNTRY_ISO_CODE.equals(paymentAddress.getCountry().getIsocode());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAuthorisedWithPrimeRoutingOnCart(final AbstractOrderModel cart) {
        cart.setIsPrimeRouteAuth(Boolean.TRUE);
        cartService.saveOrder((CartModel) cart);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOrderAuthorisedWithPrimeRouting(final String worldpayOrderCode) {
        final OrderModel order = worldpayHybrisOrderService.findOrderByWorldpayOrderCode(worldpayOrderCode);
        return order.getIsPrimeRouteAuth();
    }
}

package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayCartDao;
import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.service.payment.WorldpaySessionService;
import com.worldpay.service.payment.impl.OccWorldpaySessionService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import org.apache.log4j.Logger;

import java.text.MessageFormat;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayCartService implements WorldpayCartService {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayCartService.class);

    protected final WorldpayCartDao worldpayCartDao;
    protected final CartService cartService;
    protected final WorldpaySessionService worldpaySessionService;

    public DefaultWorldpayCartService(final WorldpayCartDao worldpayCartDao,
                                      final CartService cartService,
                                      final WorldpaySessionService worldpaySessionService) {
        this.worldpayCartDao = worldpayCartDao;
        this.cartService = cartService;
        this.worldpaySessionService = worldpaySessionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetDeclineCodeAndShopperBankOnCart(final String shopperBankCode) {
        if (cartService.hasSessionCart()) {
            final CartModel cart = cartService.getSessionCart();
            cart.setShopperBankCode(shopperBankCode);
            cart.setWorldpayDeclineCode("0");
            cartService.saveOrder(cart);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWorldpayDeclineCodeOnCart(final String worldpayOrderCode, final String declineCode) {
        try {
            final CartModel cart = findCartByWorldpayOrderCode(worldpayOrderCode);
            cart.setWorldpayDeclineCode(declineCode);
            cartService.saveOrder(cart);
        } catch (final ModelNotFoundException e) {
            LOG.error(MessageFormat.format("No carts found for worldpayOrderCode {0}", worldpayOrderCode), e);
        } catch (final AmbiguousIdentifierException e) {
            LOG.error(MessageFormat.format("Found more than one cart for worldpayOrderCode = {0}", worldpayOrderCode), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CartModel findCartByWorldpayOrderCode(final String worldpayOrderCode) {
        return worldpayCartDao.findCartByWorldpayOrderCode(worldpayOrderCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSessionId(final String sessionId) {
        if (worldpaySessionService instanceof OccWorldpaySessionService) {
            ((OccWorldpaySessionService) worldpaySessionService).setSessionIdFor3dSecure(sessionId);
        }
    }
}

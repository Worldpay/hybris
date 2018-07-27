package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayCartDao;
import com.worldpay.core.services.WorldpayCartService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.text.MessageFormat;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayCartService implements WorldpayCartService {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayCartService.class);
    private WorldpayCartDao worldpayCartDao;
    private CartService cartService;

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
    public void setWorldpayDeclineCodeOnCart(String worldpayOrderCode, String declineCode) {
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

    @Override
    public CartModel findCartByWorldpayOrderCode(final String worldpayOrderCode) {
        return worldpayCartDao.findCartByWorldpayOrderCode(worldpayOrderCode);
    }

    @Required
    public void setWorldpayCartDao(final WorldpayCartDao worldpayCartDao) {
        this.worldpayCartDao = worldpayCartDao;
    }

    @Required
    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }
}

package com.worldpay.core.services.impl;

import com.google.common.base.Preconditions;
import com.worldpay.core.dao.WorldpayCartDao;
import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.Address;
import com.worldpay.service.payment.WorldpaySessionService;
import com.worldpay.service.payment.impl.OccWorldpaySessionService;
import com.worldpay.strategy.WorldpayDeliveryAddressStrategy;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

import static java.text.MessageFormat.format;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayCartService implements WorldpayCartService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpayCartService.class);

    protected final WorldpayCartDao worldpayCartDao;
    protected final CartService cartService;
    protected final WorldpaySessionService worldpaySessionService;
    protected final Converter<AddressModel, Address> worldpayAddressConverter;
    protected final WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategy;
    protected final CustomerEmailResolutionService customerEmailResolutionService;

    public DefaultWorldpayCartService(final WorldpayCartDao worldpayCartDao,
                                      final CartService cartService,
                                      final WorldpaySessionService worldpaySessionService,
                                      final Converter<AddressModel, Address> worldpayAddressConverter,
                                      final WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategy,
                                      final CustomerEmailResolutionService customerEmailResolutionService) {
        this.worldpayCartDao = worldpayCartDao;
        this.cartService = cartService;
        this.worldpaySessionService = worldpaySessionService;
        this.worldpayAddressConverter = worldpayAddressConverter;
        this.worldpayDeliveryAddressStrategy = worldpayDeliveryAddressStrategy;
        this.customerEmailResolutionService = customerEmailResolutionService;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthenticatedShopperId(final AbstractOrderModel cartModel) {
        final UserModel userModel = cartModel.getUser();

        Preconditions.checkNotNull(userModel, "The user is null");
        if (userModel instanceof CustomerModel) {
            final String customerID = ((CustomerModel) userModel).getCustomerID();
            return StringUtils.defaultIfBlank(customerID, ((CustomerModel) userModel).getOriginalUid());
        }
        throw new IllegalArgumentException(format("The user {0} is not of type Customer.", userModel));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Address getAddressFromCart(final AbstractOrderModel abstractOrder, final boolean isDeliveryAddress) {
        final AddressModel address = isDeliveryAddress ? worldpayDeliveryAddressStrategy.getDeliveryAddress(abstractOrder) : abstractOrder.getPaymentAddress();
        return convertAddressModelToAddress(address);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Address getBillingAddress(final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo) {
        final AddressModel deliveryAddressModel = cartModel.getDeliveryAddress();
        if (deliveryAddressModel != null && Boolean.TRUE.equals(additionalAuthInfo.getUsingShippingAsBilling())) {
            return convertAddressModelToAddress(deliveryAddressModel);
        } else {
            if (cartModel.getPaymentAddress() != null) {
                return convertAddressModelToAddress(cartModel.getPaymentAddress());
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Address convertAddressModelToAddress(final AddressModel address) {
        return worldpayAddressConverter.convert(address);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEmailForCustomer(final AbstractOrderModel cart) {
        return customerEmailResolutionService.getEmailForCustomer((CustomerModel) cart.getUser());
    }
}

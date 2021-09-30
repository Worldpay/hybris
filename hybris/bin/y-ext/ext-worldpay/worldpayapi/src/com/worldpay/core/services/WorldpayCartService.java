package com.worldpay.core.services;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.Address;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;

/**
 * Service providing extended Worldpay cart service functionality.
 *
 * @spring.bean worldpayCartService
 */
public interface WorldpayCartService {

    /**
     * Sets shopper bank code on the {@link de.hybris.platform.core.model.order.CartModel}.
     *
     * @param shopperBankCode the shopper bank code
     */
    void resetDeclineCodeAndShopperBankOnCart(String shopperBankCode);

    /**
     * Sets worldpay decline code on the {@link de.hybris.platform.core.model.order.CartModel}.
     *
     * @param worldpayOrderCode the worldpay order code
     * @param declineCode       the decline code
     */
    void setWorldpayDeclineCodeOnCart(String worldpayOrderCode, String declineCode);

    /**
     * Returns the cart for the given WorldpayOrderCode
     *
     * @param worldpayOrderCode
     * @return cart for the given worldpayCode
     */
    CartModel findCartByWorldpayOrderCode(String worldpayOrderCode);

    /**
     * Store the session id from the initial payment request on the cart
     *
     * @param sessionId
     */
    void setSessionId(String sessionId);

    /**
     * Returns a unique authenticated shopper ID.
     *
     * @param cartModel {@link CartModel}
     * @return AuthenticatedShopperId
     */
    String getAuthenticatedShopperId(AbstractOrderModel cartModel);


    /**
     * Gets the delivery Address for given cart, id delivery address true will return the delivery address, otherwise payment address
     *
     * @param abstractOrder
     * @param isDeliveryAddress if it's delivery addresss
     * @return {@link Address}
     */
    Address getAddressFromCart(AbstractOrderModel abstractOrder, boolean isDeliveryAddress);

    /**
     * Gets the billing address from cart
     *
     * @param cartModel
     * @param additionalAuthInfo
     * @return {@link Address}
     */
    Address getBillingAddress(CartModel cartModel, AdditionalAuthInfo additionalAuthInfo);

    /**
     * Converts the addressModel type to an address
     *
     * @param addressModel the model to convert
     * @return {@link Address}
     */
    Address convertAddressModelToAddress(AddressModel addressModel);

    /**
     * Get customer email
     *
     * @param cart the customer cart
     * @return the customer email
     */
    String getEmailForCustomer(AbstractOrderModel cart);
}

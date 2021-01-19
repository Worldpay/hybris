package com.worldpay.facades.order.impl;

import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.*;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.enums.CountryType;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

/**
 * Adds Worldpay functionality by decorating the CheckoutFlowFacade
 */
public class WorldpayCheckoutFacadeDecorator implements CheckoutFlowFacade {

    private static final Logger LOG = LogManager.getLogger(WorldpayCheckoutFacadeDecorator.class);

    protected final Converter<AddressModel, AddressData> addressConverter;
    protected final CartService cartService;
    protected final CheckoutFlowFacade checkoutFlowFacade;
    protected final CheckoutCustomerStrategy checkoutCustomerStrategy;
    protected final CommerceCheckoutService commerceCheckoutService;

    public WorldpayCheckoutFacadeDecorator(final Converter<AddressModel, AddressData> addressConverter,
                                           final CartService cartService,
                                           final CheckoutFlowFacade checkoutFlowFacade,
                                           final CheckoutCustomerStrategy checkoutCustomerStrategy,
                                           final CommerceCheckoutService commerceCheckoutService) {
        this.addressConverter = addressConverter;
        this.cartService = cartService;
        this.checkoutFlowFacade = checkoutFlowFacade;
        this.checkoutCustomerStrategy = checkoutCustomerStrategy;
        this.commerceCheckoutService = commerceCheckoutService;
    }

    public AddressData getBillingAddress() {
        final CartModel cartModel = getSessionCart();
        if (cartModel == null) {
            return null;
        }
        final AddressModel addressModel = cartModel.getPaymentAddress();
        if (addressModel != null) {
            return addressConverter.convert(addressModel);
        }
        return null;
    }

    protected CartModel getSessionCart() {
        if (getCheckoutFlowFacade().hasCheckoutCart()) {
            return cartService.getSessionCart();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CCPaymentInfoData createPaymentSubscription(final CCPaymentInfoData paymentInfoData) {
        return getCheckoutFlowFacade().createPaymentSubscription(paymentInfoData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean authorizePayment(final String securityCode) {
        return getCheckoutFlowFacade().authorizePayment(securityCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNoPaymentInfo() {
        final CartData cartData = getCheckoutCart();
        return cartData == null || (cartData.getPaymentInfo() == null && cartData.getWorldpayAPMPaymentInfo() == null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderData placeOrder() throws InvalidCartException {
        return getCheckoutFlowFacade().placeOrder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CartData getCheckoutCart() {
        return getCheckoutFlowFacade().getCheckoutCart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends AddressData> getSupportedDeliveryAddresses(boolean visibleAddressesOnly) {
        return getCheckoutFlowFacade().getSupportedDeliveryAddresses(visibleAddressesOnly);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setCheapestDeliveryModeForCheckout() {
        return getCheckoutFlowFacade().setCheapestDeliveryModeForCheckout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTaxEstimationEnabledForCart() {
        return getCheckoutFlowFacade().isTaxEstimationEnabledForCart();
    }

    @Override
    public boolean isNewAddressEnabledForCart() {
        return getCheckoutFlowFacade().isNewAddressEnabledForCart();
    }

    @Override
    public boolean isRemoveAddressEnabledForCart() {
        return getCheckoutFlowFacade().isRemoveAddressEnabledForCart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setPaymentDetails(final String paymentInfoId) {
        validateParameterNotNullStandardMessage("paymentInfoId", paymentInfoId);

        if (checkIfCurrentUserIsTheCartUser() && StringUtils.isNotBlank(paymentInfoId)) {
            final CustomerModel currentUserForCheckout = getCurrentUserForCheckout();
            final PaymentInfoModel matchingPaymentInfoModel = currentUserForCheckout.getPaymentInfos().stream()
                .filter(paymentInfoModel -> paymentInfoId.equalsIgnoreCase(paymentInfoModel.getPk().toString()))
                .findFirst()
                .orElse(null);
            final CartModel cartModel = getCart();
            if (matchingPaymentInfoModel != null) {
                cartModel.setPaymentAddress(matchingPaymentInfoModel.getBillingAddress());
                final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
                parameter.setPaymentInfo(matchingPaymentInfoModel);
                return commerceCheckoutService.setPaymentInfo(parameter);
            }
            LOG.warn(
                "Did not find CreditCardPaymentInfoModel for user: {}, cart: {} &  paymentInfoId: {}. PaymentInfo Will not get set.",
                () -> currentUserForCheckout, cartModel::getCode, () -> paymentInfoId);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasCheckoutCart() {
        return getCheckoutFlowFacade().hasCheckoutCart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasShippingItems() {
        return getCheckoutFlowFacade().hasShippingItems();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PointOfServiceData> getConsolidatedPickupOptions() {
        return getCheckoutFlowFacade().getConsolidatedPickupOptions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends DeliveryModeData> getSupportedDeliveryModes() {
        return getCheckoutFlowFacade().getSupportedDeliveryModes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeDeliveryMode() {
        return getCheckoutFlowFacade().removeDeliveryMode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setDeliveryMode(final String deliveryModeCode) {
        return getCheckoutFlowFacade().setDeliveryMode(deliveryModeCode);
    }

    /**
     * @deprecated since 1808. Please use {@link CheckoutFacade#getCountries(CountryType)} instead.
     */
    @Override
    @Deprecated(since = "1808")
    public List<CountryData> getDeliveryCountries() {
        return getCheckoutFlowFacade().getDeliveryCountries();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setDeliveryAddressIfAvailable() {
        return getCheckoutFlowFacade().setDeliveryAddressIfAvailable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsTaxValues() {
        return getCheckoutFlowFacade().containsTaxValues();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CardTypeData> getSupportedCardTypes() {
        return getCheckoutFlowFacade().getSupportedCardTypes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setDeliveryModeIfAvailable() {
        return getCheckoutFlowFacade().setDeliveryModeIfAvailable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setDefaultPaymentInfoForCheckout() {
        return getCheckoutFlowFacade().setDefaultPaymentInfoForCheckout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CheckoutPciOptionEnum getSubscriptionPciOption() {
        return getCheckoutFlowFacade().getSubscriptionPciOption();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNoDeliveryAddress() {
        return getCheckoutFlowFacade().hasNoDeliveryAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExpressCheckoutAllowedForCart() {
        return getCheckoutFlowFacade().isExpressCheckoutAllowedForCart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CartModificationData> consolidateCheckoutCart(final String pickupPointOfServiceName) throws CommerceCartModificationException {
        return getCheckoutFlowFacade().consolidateCheckoutCart(pickupPointOfServiceName);
    }

    /**
     * @deprecated since 1808. Please use {@link CheckoutFacade#getCountries(CountryType)} instead.
     */
    @Override
    @Deprecated(since = "1808")
    public List<CountryData> getBillingCountries() {
        return getCheckoutFlowFacade().getBillingCountries();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CountryData> getCountries(final CountryType countryType) {
        return getCheckoutFlowFacade().getCountries(countryType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNoDeliveryMode() {
        return getCheckoutFlowFacade().hasNoDeliveryMode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AddressData getDeliveryAddressForCode(final String code) {
        return getCheckoutFlowFacade().getDeliveryAddressForCode(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setPaymentInfoIfAvailable() {
        return getCheckoutFlowFacade().setPaymentInfoIfAvailable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setDeliveryAddress(final AddressData address) {
        return getCheckoutFlowFacade().setDeliveryAddress(address);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AddressData getAddressDataForId(final String addressId, final boolean visibleAddressesOnly) {
        return getCheckoutFlowFacade().getAddressDataForId(addressId, visibleAddressesOnly);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExpressCheckoutEnabledForStore() {
        return getCheckoutFlowFacade().isExpressCheckoutEnabledForStore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setDefaultDeliveryAddressForCheckout() {
        return getCheckoutFlowFacade().setDefaultDeliveryAddressForCheckout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExpressCheckoutResult performExpressCheckout() {
        final ExpressCheckoutResult result = getCheckoutFlowFacade().performExpressCheckout();
        setPaymentInfoBillingAddressOnSessionCart();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCheckoutFlowGroupForCheckout() {
        return getCheckoutFlowFacade().getCheckoutFlowGroupForCheckout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeDeliveryAddress() {
        return getCheckoutFlowFacade().removeDeliveryAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPickUpItems() {
        return getCheckoutFlowFacade().hasPickUpItems();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareCartForCheckout() {
        getCheckoutFlowFacade().prepareCartForCheckout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasValidCart() {
        return getCheckoutFlowFacade().hasValidCart();
    }

    /**
     * Gets checkout flow facade.
     *
     * @return the checkout flow facade
     */
    public CheckoutFlowFacade getCheckoutFlowFacade() {
        return checkoutFlowFacade;
    }

    protected CartModel getCart() {
        return hasCheckoutCart() ? cartService.getSessionCart() : null;
    }

    protected boolean checkIfCurrentUserIsTheCartUser() {
        final CartModel cartModel = getCart();
        return cartModel != null && cartModel.getUser().equals(getCurrentUserForCheckout());
    }

    protected CustomerModel getCurrentUserForCheckout() {
        return checkoutCustomerStrategy.getCurrentUserForCheckout();
    }

    protected void setPaymentInfoBillingAddressOnSessionCart() {
        final CartModel sessionCart = getSessionCart();
        sessionCart.setPaymentAddress(sessionCart.getPaymentInfo().getBillingAddress());
        cartService.saveOrder(sessionCart);
    }

    protected CommerceCheckoutParameter createCommerceCheckoutParameter(final CartModel cart, final boolean enableHooks) {
        final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
        parameter.setEnableHooks(enableHooks);
        parameter.setCart(cart);
        return parameter;
    }
}

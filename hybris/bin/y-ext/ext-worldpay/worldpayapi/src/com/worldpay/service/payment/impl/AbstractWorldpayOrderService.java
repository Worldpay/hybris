package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.Address;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.strategies.GenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.AddressService;

import java.math.BigDecimal;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Abstract implementation of the {@link WorldpayRedirectOrderService} allows configuration of standard services required
 * by implementors
 */
public abstract class AbstractWorldpayOrderService {

    private final CommonI18NService commonI18NService;
    private final CommerceCheckoutService commerceCheckoutService;
    private final CustomerEmailResolutionService customerEmailResolutionService;
    private final GenerateMerchantTransactionCodeStrategy worldpayGenerateMerchantTransactionCodeStrategy;
    private final WorldpayPaymentInfoService worldpayPaymentInfoService;
    private final WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    private final WorldpayOrderService worldpayOrderService;
    private final Converter<AddressModel, Address> worldpayAddressConverter;
    private final WorldpayServiceGateway worldpayServiceGateway;
    private final AddressService addressService;

    protected AbstractWorldpayOrderService(final CommonI18NService commonI18NService, final CommerceCheckoutService commerceCheckoutService, final CustomerEmailResolutionService customerEmailResolutionService, final GenerateMerchantTransactionCodeStrategy worldpayGenerateMerchantTransactionCodeStrategy, final WorldpayPaymentInfoService worldpayPaymentInfoService, final WorldpayPaymentTransactionService worldpayPaymentTransactionService, final WorldpayOrderService worldpayOrderService, final Converter<AddressModel, Address> worldpayAddressConverter, final WorldpayServiceGateway worldpayServiceGateway, final AddressService addressService) {
        this.commonI18NService = commonI18NService;
        this.commerceCheckoutService = commerceCheckoutService;
        this.customerEmailResolutionService = customerEmailResolutionService;
        this.worldpayGenerateMerchantTransactionCodeStrategy = worldpayGenerateMerchantTransactionCodeStrategy;
        this.worldpayPaymentInfoService = worldpayPaymentInfoService;
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
        this.worldpayOrderService = worldpayOrderService;
        this.worldpayAddressConverter = worldpayAddressConverter;
        this.worldpayServiceGateway = worldpayServiceGateway;
        this.addressService = addressService;
    }


    /**
     * Creates a {@link CommerceCheckoutParameter} based on the passed {@link CartModel} and {@link PaymentInfoModel} given
     *
     * @param abstractOrderModel  The abstractOrderModel to base the commerceCheckoutParameter on
     * @param paymentInfoModel    The paymentInfo to base the commerceCheckoutParameter on
     * @param authorisationAmount The authorised amount by the payment provider
     * @return the created parameters
     */
    public CommerceCheckoutParameter createCommerceCheckoutParameter(final AbstractOrderModel abstractOrderModel, final PaymentInfoModel paymentInfoModel, final BigDecimal authorisationAmount) {
        final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
        parameter.setEnableHooks(true);
        if (abstractOrderModel instanceof CartModel) {
            parameter.setCart((CartModel) abstractOrderModel);
        } else {
            parameter.setOrder(abstractOrderModel);
        }
        parameter.setPaymentInfo(paymentInfoModel);
        parameter.setAuthorizationAmount(authorisationAmount);
        parameter.setPaymentProvider(commerceCheckoutService.getPaymentProvider());
        return parameter;
    }

    /**
     * Workaround: Extra address created when an order is placed
     * Potential bug in class: DefaultCommercePlaceOrderStrategy
     * Method: public CommerceOrderResult placeOrder(CommerceCheckoutParameter parameter) throws InvalidCartException {...}
     * Logic: if(cartModel.getPaymentInfo() != null && cartModel.getPaymentInfo().getBillingAddress() != null) {...}
     *
     * @param cartModel        holding the source address
     * @param paymentInfoModel holding the address owner
     * @return the cloned address model
     */
    public AddressModel cloneAndSetBillingAddressFromCart(final CartModel cartModel, final PaymentInfoModel paymentInfoModel) {
        final AddressModel paymentAddress = cartModel.getPaymentAddress();
        validateParameterNotNull(paymentAddress, "Payment Address cannot be null.");
        final AddressModel clonedAddress = addressService.cloneAddressForOwner(paymentAddress, paymentInfoModel);
        clonedAddress.setBillingAddress(true);
        clonedAddress.setShippingAddress(false);
        clonedAddress.setOwner(paymentInfoModel);
        paymentInfoModel.setBillingAddress(clonedAddress);
        return clonedAddress;
    }

    public CommerceCheckoutService getCommerceCheckoutService() {
        return commerceCheckoutService;
    }

    public CustomerEmailResolutionService getCustomerEmailResolutionService() {
        return customerEmailResolutionService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }


    public WorldpayPaymentInfoService getWorldpayPaymentInfoService() {
        return worldpayPaymentInfoService;
    }


    public WorldpayPaymentTransactionService getWorldpayPaymentTransactionService() {
        return worldpayPaymentTransactionService;
    }

    public WorldpayOrderService getWorldpayOrderService() {
        return worldpayOrderService;
    }

    public Converter<AddressModel, Address> getWorldpayAddressConverter() {
        return worldpayAddressConverter;
    }


    public GenerateMerchantTransactionCodeStrategy getWorldpayGenerateMerchantTransactionCodeStrategy() {
        return worldpayGenerateMerchantTransactionCodeStrategy;
    }


    public WorldpayServiceGateway getWorldpayServiceGateway() {
        return worldpayServiceGateway;
    }
}

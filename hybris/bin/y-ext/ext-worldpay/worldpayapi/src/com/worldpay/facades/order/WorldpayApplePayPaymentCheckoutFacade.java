package com.worldpay.facades.order;

import com.worldpay.data.ApplePayPaymentContact;
import com.worldpay.data.ApplePayPaymentRequest;
import com.worldpay.payment.applepay.ValidateMerchantRequestDTO;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;

/**
 * Exposes methods to handle payments done through ApplePay
 */
public interface WorldpayApplePayPaymentCheckoutFacade {
    /**
     * Save the billing address on the current user
     *
     * @param billingContact
     */
    void saveBillingAddresses(final ApplePayPaymentContact billingContact);

    /**
     * Set the region from the ApplePayPaymentContact on the adressData
     *
     * @param addressData
     * @param address
     */
    void setRegion(final AddressData addressData, final ApplePayPaymentContact address);

    /**
     * Retrieves the request object to validate the merchant request
     *
     * @return the request
     */
    ValidateMerchantRequestDTO getValidateMerchantRequestDTO();

    /**
     * Create a basic payment request for the session cart that can be used to initialize the ApplePaySession in the browser
     *
     * @param sessionCart
     * @return
     */
    ApplePayPaymentRequest getApplePayPaymentRequest(final CartData sessionCart);
}

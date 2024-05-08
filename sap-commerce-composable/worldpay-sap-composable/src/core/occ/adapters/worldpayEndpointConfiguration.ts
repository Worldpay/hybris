import { OccConfig, OccEndpoint } from '@spartacus/core';

export const wordlpayOccConfig: OccConfig = {
  backend: {
    occ: {
      endpoints: {
        useExistingPaymentDetails: 'users/${userId}/carts/${cartId}/paymentdetails',
        createWorldpayPaymentDetails: 'users/${userId}/carts/${cartId}/worldpaypaymentdetails',
        getPublicKey: 'worldpayapi/cse-public-key',
        setPaymentAddress: 'users/${userId}/carts/${cartId}/worldpaybillingaddress',
        setAPMPaymentInfo: '/users/${userId}/carts/${cartId}/worldpayAPMPaymentInfo',
        getDDC3dsJwt: 'worldpayapi/ddc-3ds-jwt',
        getDDC3dsChallengeSubmit: 'worldpayapi/challenge/submit',
        initialPaymentRequest: 'users/${userId}/carts/${cartId}/place-order',
        getOrder: 'users/${userId}/orders/${code}?fields=DEFAULT,deliveryAddress(FULL),deliveryOrderGroups(FULL),consignments(FULL),paymentInfo(FULL)',
        getOrderForGuest: '/orders/${code}/user/${userId}?fields=DEFAULT,deliveryAddress(FULL),deliveryOrderGroups(FULL),consignments(FULL),paymentInfo(FULL)',
        requestApplePayPaymentRequest: 'users/${userId}/carts/${cartId}/apple/payment-request',
        setDeliveryAsBillingAddress: '/users/${userId}/carts/${cartId}/addresses/setDeliveryAsBilling',
        validateApplePayMerchant: 'users/${userId}/carts/${cartId}/apple/request-session',
        authorizeApplePayPayment: 'users/${userId}/carts/${cartId}/apple/authorise-order',
        getGooglePayMerchantConfiguration: '/users/${userId}/carts/${cartId}/google/merchant-configuration',
        authoriseGooglePayPayment: '/users/${userId}/carts/${cartId}/google/authorise-order',
        authoriseApmRedirect: '/users/${userId}/carts/${cartId}/payment-method/redirect-authorise',
        getAvailableApms: '/users/${userId}/carts/${cartId}/cms/components/availableapmcomponents',
        isFraudSightEnabled: 'worldpayapi/fraudsight/enabled',
        placeRedirectOrder: '/users/${userId}/carts/${cartId}/worldpayorders/place-redirect-order',
        placeBankTransferRedirectOrder: '/users/${userId}/carts/${cartId}/worldpayorders/place-banktransfer-redirect-order',

        /* Guaranteed Payments */
        isGuaranteedPaymentsEnabled: '/worldpayapi/guaranteedpayments/enabled',
        getCheckoutDetails: 'users/${userId}/carts/${cartId}?fields=deliveryAddress(FULL),deliveryMode(FULL),paymentInfo(FULL),worldpayAPMPaymentInfo',
        // eslint-disable-next-line max-len
        cart: 'users/${userId}/carts/${cartId}?fields=DEFAULT,potentialProductPromotions,appliedProductPromotions,potentialOrderPromotions,appliedOrderPromotions,entries(totalPrice(formattedValue),product(images(FULL),stock(FULL)),basePrice(formattedValue,value),updateable),totalPrice(formattedValue),totalItems,totalPriceWithTax(formattedValue),totalDiscounts(value,formattedValue),subTotal(formattedValue),totalUnitCount,deliveryItemsQuantity,deliveryCost(formattedValue),totalTax(formattedValue, value),pickupItemsQuantity,net,appliedVouchers,productDiscounts(formattedValue),user,saveTime,name,description,worldpayAPMPaymentInfo(FULL)',
        // ACH
        getACHBankAccountTypes: '/users/${userId}/carts/${cartId}/payment-method/achdirectdebit/types',
        placeACHOrder: '/users/${userId}/carts/${cartId}/worldpayorders/place-ach-direct-order',
      },
    },
  },
};

declare module '@spartacus/core' {
  interface OccEndpoints {
    authorizeApplePayPayment?: string | OccEndpoint;
    authoriseGooglePayPayment?: string | OccEndpoint;
    authoriseApmRedirect?: string | OccEndpoint;
    createWorldpayPaymentDetails?: string | OccEndpoint;
    getAvailableApms?: string | OccEndpoint;
    getDDC3dsJwt?: string | OccEndpoint;
    getDDC3dsChallengeSubmit?: string | OccEndpoint;
    getGooglePayMerchantConfiguration?: string | OccEndpoint;
    getOrder?: string | OccEndpoint;
    getOrderForGuest?: string | OccEndpoint;
    getPublicKey?: string | OccEndpoint;
    initialPaymentRequest?: string | OccEndpoint;
    isFraudSightEnabled?: string | OccEndpoint;
    placeRedirectOrder?: string | OccEndpoint;
    setDeliveryAsBillingAddress?: string | OccEndpoint;
    setPaymentAddress?: string | OccEndpoint;
    requestApplePayPaymentRequest?: string | OccEndpoint;
    useExistingPaymentDetails?: string | OccEndpoint;
    validateApplePayMerchant?: string | OccEndpoint;
    isGuaranteedPaymentsEnabled?: string | OccEndpoint;
    setAPMPaymentInfo?: string | OccEndpoint;
    placeBankTransferRedirectOrder?: string | OccEndpoint;
    getCheckoutDetails?: string | OccEndpoint;
    cart?: string | OccEndpoint;
    getACHBankAccountTypes?: string | OccEndpoint;
    placeACHOrder?: string | OccEndpoint;
  }
}

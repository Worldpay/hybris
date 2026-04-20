import { OccConfig, OccEndpoint } from '@spartacus/core';

export const worldpayB2BOccConfig: OccConfig = {
  backend: {
    occ: {
      endpoints: {
        // eslint-disable-next-line max-len
        getCheckoutDetails: 'users/${userId}/carts/${cartId}?fields=deliveryAddress(FULL),deliveryMode(FULL),paymentInfo(FULL),paymentType(FULL),costCenter(FULL),purchaseOrderNumber,paymentType(FULL),worldpayAPMPaymentInfo(FULL)',
      }
    }
  }
};

declare module '@spartacus/core' {
  interface OccEndpoints {
    getCheckoutDetails?: string | OccEndpoint;
  }
}
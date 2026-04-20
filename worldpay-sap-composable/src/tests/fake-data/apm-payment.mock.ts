import { Category } from '@spartacus/core';

import { ACHPaymentForm, ApmPaymentDetails, PaymentMethod } from '../../core/interfaces';
import { generateOneCategory } from './categories.mock';

const paymentMethods: { code: PaymentMethod | string; name: string; shopperBankCode: string }[] = [
  {
    code: PaymentMethod.ApplePay,
    name: 'Apple Pay',
    shopperBankCode: 'AP001'
  },
  {
    code: PaymentMethod.Card,
    name: 'Credit/Debit Card',
    shopperBankCode: 'CC001'
  },
  {
    code: PaymentMethod.GooglePay,
    name: 'Google Pay',
    shopperBankCode: 'GP001'
  },
  {
    code: PaymentMethod.PayPal,
    name: 'PayPal',
    shopperBankCode: 'PP001'
  },
  {
    code: PaymentMethod.ACH,
    name: 'ACH Direct Debit',
    shopperBankCode: 'ACH001'
  }
];

export const generateAccountType = (): ACHPaymentForm => ({
  accountType: 'Checking'
});
export const generateOneApmPaymentDetail = (): ApmPaymentDetails => {
  return {
    ...paymentMethods[Math.floor(Math.random() * paymentMethods.length)],
    ...generateAccountType()
  };
};

export const generateManyApmPaymentDetail = (size: number): Category[] => {
  const users: Category[] = [];
  for (let index = 0; index < size; index++) {
    users.push(generateOneCategory());
  }
  return [...users];
};
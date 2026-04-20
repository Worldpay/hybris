import { ICON_TYPE } from '@spartacus/storefront';
import { PaymentMethod, WorldpayApmPaymentInfo, WorldpayCard, WorldpayIconType } from '../interfaces';

export const createCreditCardCard: (paymentDetails: WorldpayApmPaymentInfo, textExpires: string) => WorldpayCard =
  (paymentDetails: WorldpayApmPaymentInfo, textExpires: string): WorldpayCard => ({
    textBold: paymentDetails.apmName ?? paymentDetails.accountHolderName,
    text: [paymentDetails.cardNumber || '', textExpires].filter((value: string): boolean => !!value),
  });

const normalizeCode: (value: string) => string = (value: string): string =>
  (value ?? '').trim().toLowerCase().replace(/[\s-]+/g, '_');

const CARD_ICON_BY_CODE: Record<string, string> = {
  // Amex
  [normalizeCode('american_express')]: ICON_TYPE.AMEX,
  [normalizeCode(ICON_TYPE.AMEX)]: ICON_TYPE.AMEX,

  // Cartes Bancaires - Carte Bleue
  [normalizeCode('cb')]: WorldpayIconType.CARTE_BLEUE,
  [normalizeCode('carte_bleue')]: WorldpayIconType.CARTE_BLEUE,

  // Diners
  [normalizeCode('diners')]: ICON_TYPE.DINERS_CLUB,
  [normalizeCode(ICON_TYPE.DINERS_CLUB)]: ICON_TYPE.DINERS_CLUB,

  // Discover
  [normalizeCode('discover')]: WorldpayIconType.DISCOVER,

  // JCB
  [normalizeCode('jcb')]: WorldpayIconType.JCB,

  // Maestro
  [normalizeCode('maestro')]: WorldpayIconType.MAESTRO,

  // Mastercard
  [normalizeCode('master')]: ICON_TYPE.MASTER_CARD,
  [normalizeCode('mastercard')]: ICON_TYPE.MASTER_CARD,
  [normalizeCode('mastercard_eurocard')]: ICON_TYPE.MASTER_CARD,
  [normalizeCode(ICON_TYPE.MASTER_CARD)]: ICON_TYPE.MASTER_CARD,

  // APMs
  [normalizeCode(PaymentMethod.SepaDirectDebit)]: WorldpayIconType.SEPA,
  [normalizeCode(PaymentMethod.PayPalSSL)]: WorldpayIconType.PAYPAL,
  [normalizeCode(PaymentMethod.PayPal)]: WorldpayIconType.PAYPAL,
  [normalizeCode(PaymentMethod.PayPalSSLExpress)]: WorldpayIconType.PAYPAL,

  // Visa
  [normalizeCode(ICON_TYPE.VISA)]: ICON_TYPE.VISA,

};

export const worldpayGetCardIcon: (code: string) => string = (code: string): string => CARD_ICON_BY_CODE[normalizeCode(code)] ?? ICON_TYPE.CREDIT_CARD;
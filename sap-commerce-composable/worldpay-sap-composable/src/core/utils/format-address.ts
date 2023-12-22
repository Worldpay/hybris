import { Address } from '@spartacus/core';
import { Card } from '@spartacus/storefront';

/**
 * Format address content
 * @since 6.4.0
 * @param address - Address
 * @param prependText - Text to prepend
 * @param appendText - Text to append
 * @returns string[] - Array of strings
 */
export const formatTextAddressContent = (
  address: Address,
  prependText: string = null,
  appendText: string = null
): string[] => {
  const textContent: string[] = [];
  const region: string = address?.region?.isocode ? address?.region?.isocode + ', ' : '';
  const town: string = address?.town ? address.town + ', ' : '';
  const countryIsoCode: string = address?.country?.isocode ?? '';
  const countryData = town + region + countryIsoCode;

  if (prependText) {
    textContent.push(prependText);
  }

  textContent.push(address?.firstName + ' ' + address?.lastName);

  if (address.line1) {
    textContent.push(address.line1);
  }

  if (address.line2) {
    textContent.push(address.line2);
  }

  if (countryData) {
    textContent.push(countryData);
  }

  if (address.postalCode) {
    textContent.push(address.postalCode);
  }

  if (address.phone) {
    textContent.push(address.phone);
  }

  if (appendText) {
    textContent.push(appendText);
  }

  return textContent;
};

/**
 * Get card for address
 * @since 6.4.0
 * @param address - Address
 * @returns Card - Card
 */
export const generateAddressCard = (address: Address): Card => ({
  textBold: address.firstName + ' ' + address.lastName,
  text: formatTextAddressContent(address, null, null),
});

/**
 * Get card for billing address
 * @since 6.4.0
 * @param textTitle - Text for Title
 * @param textBillTo - Text for bill to
 * @param billingAddress - Billing Address
 * @returns Card - Card
 */
export const generateBillingAddressCard = (
  textTitle: string,
  textBillTo: string,
  billingAddress: Address
): Card => ({
  title: textTitle,
  text: formatTextAddressContent(billingAddress, textBillTo, null),
} as Card);

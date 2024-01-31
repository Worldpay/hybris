import { WindowRef } from '@spartacus/core';

/**
 * Create ApplePaySession
 * @since 4.3.6
 * @param windowRef
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const createApplePaySession = (windowRef: WindowRef): any => {
  if (!windowRef.isBrowser()) {
    return null;
  }

  const applePaySession: string = 'ApplePaySession';
  // @ts-ignore
  return windowRef.nativeWindow[applePaySession];
};

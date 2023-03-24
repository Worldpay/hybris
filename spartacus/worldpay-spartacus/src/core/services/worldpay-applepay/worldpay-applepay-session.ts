import { WindowRef } from '@spartacus/core';

export const createApplePaySession = (windowRef: WindowRef): null => {
  if (!windowRef.isBrowser()) {
    return null;
  }

  return windowRef.nativeWindow['ApplePaySession'];
};

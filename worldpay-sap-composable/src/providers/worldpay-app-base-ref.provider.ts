import { APP_BASE_HREF, PlatformLocation } from '@angular/common';
import { Provider } from '@angular/core';
import { getBaseHref } from '../core';

export const worldpayAppBaseRefProvider: () => Provider = (): Provider => ({
  provide: APP_BASE_HREF,
  useFactory: getBaseHref,
  deps: [PlatformLocation]
});

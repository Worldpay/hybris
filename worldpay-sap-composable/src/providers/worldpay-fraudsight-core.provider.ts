import { Provider } from '@angular/core';
import { worldpayFraudsightConnectorProviders, worldpayFraudsightFacadeProviders } from '../core';

export const worldpayFraudsightCoreProviders: () => Provider[] = (): Provider[] => [
  ...worldpayFraudsightConnectorProviders,
  ...worldpayFraudsightFacadeProviders
];
import { Provider } from '@angular/core';
import { OccConfig, provideConfig } from '@spartacus/core';
import { worldpayOccConfig } from '../core/occ/adapters/worldpayEndpointConfiguration';

export const worldpayOccConfigProvider: () => Provider = (): Provider => (
  provideConfig(worldpayOccConfig as OccConfig)
);
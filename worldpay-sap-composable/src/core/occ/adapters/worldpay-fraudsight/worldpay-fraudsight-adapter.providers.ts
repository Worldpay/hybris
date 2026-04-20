import { Provider } from '@angular/core';
import { WorldpayFraudsightAdapter } from '../../../connectors';
import { OccWorldpayFraudsightAdapter } from './occ-worldpay-fraudsight.adapter';

export const worldpayFraudsightAdapterProvider: Provider[] = [
  {
    provide: WorldpayFraudsightAdapter,
    useClass: OccWorldpayFraudsightAdapter,
  },
];

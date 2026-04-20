import { Provider } from '@angular/core';
import { WorldpayFraudsightService } from '../services/worldpay-fraudsight/worldpay-fraudsight.service';
import { WorldpayFraudsightFacade } from './worldpay-fraudsight.facade';

export const worldpayFraudsightFacadeProviders: Provider[] = [
  WorldpayFraudsightService,
  {
    provide: WorldpayFraudsightFacade,
    useExisting: WorldpayFraudsightService,
  },
];

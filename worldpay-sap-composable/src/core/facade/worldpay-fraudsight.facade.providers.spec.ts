import { createEnvironmentInjector, EnvironmentInjector } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { QueryService } from '@spartacus/core';
import { of } from 'rxjs';
import { WorldpayFraudsightConnector } from '../connectors/worldpay-fraudsight/worldpay-fraudsight.connector';
import { WorldpayFraudsightFacade } from './worldpay-fraudsight.facade';
import { worldpayFraudsightFacadeProviders } from './worldpay-fraudsight.facade.providers';

describe('WorldpayFraudsight facade provider scopes', () => {
  let profilingInjector: EnvironmentInjector;
  let checkoutFacade: WorldpayFraudsightFacade;
  let profilingFacade: WorldpayFraudsightFacade;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        QueryService,
        ...worldpayFraudsightFacadeProviders,
        {
          provide: WorldpayFraudsightConnector,
          useValue: {
            isFraudSightEnabled: () => of(true),
            getFraudPublicKey: () => of('publishable_key_test_xxx')
          }
        }
      ]
    });
    checkoutFacade = TestBed.inject(WorldpayFraudsightFacade);
    profilingInjector = createEnvironmentInjector(
      worldpayFraudsightFacadeProviders,
      TestBed.inject(EnvironmentInjector)
    );
    profilingFacade = profilingInjector.get(WorldpayFraudsightFacade);
  });

  afterEach(() => {
    profilingInjector.destroy();
  });

  it('should share the service between standalone profiling and checkout injectors', () => {
    expect(profilingFacade).toBe(checkoutFacade);
  });

  it('should deliver the generated device id to an already subscribed checkout', () => {
    const receivedIds: string[] = [];
    const subscription = checkoutFacade.getFraudSightIdFromState().subscribe((id: string) => {
      receivedIds.push(id);
    });

    profilingFacade.setFraudSightId('rjs-profiled-device');

    expect(receivedIds).toEqual(['', 'rjs-profiled-device']);
    subscription.unsubscribe();
  });

  it('should retain the device id when checkout subscribes after profiling', () => {
    profilingFacade.setFraudSightId('rjs-profiled-device');
    let receivedId: string;
    const subscription = checkoutFacade.getFraudSightIdFromState().subscribe((id: string) => {
      receivedId = id;
    });

    expect(receivedId).toBe('rjs-profiled-device');
    subscription.unsubscribe();
  });
});

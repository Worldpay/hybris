import { TestBed } from '@angular/core/testing';

import { WorldpayGuaranteedPaymentsService } from './worldpay-guaranteed-payments.service';
import { LoadScriptService } from '../../utils/load-script.service';
import { WindowRef } from '@spartacus/core';
import { Store, StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { WorldpayGuaranteedPaymentsEnabledEffects } from '../../store/worldpay-guaranteed-payments/effects/worldpay-guaranteed-payments-enabled.effects';
import { WorldpayGuaranteedPaymentsSessionIdEffects } from '../../store/worldpay-guaranteed-payments/effects/worldpay-guaranteed-payments-session-id.effects';

class MockLoadScriptService {
  loadScript({
    idScript,
    src,
    onloadCallback,
    async,
    defer,
    attributes
  }) {

  }
}

describe('WorldpayGuaranteedPaymentsService', () => {
  let service: WorldpayGuaranteedPaymentsService;
  let store: Store;
  let loadScriptService: LoadScriptService;
  let winRef: WindowRef;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({}),
        EffectsModule.forRoot([]),
      ],
      providers: [
        Store,
        LoadScriptService,
        WindowRef,
        WorldpayGuaranteedPaymentsEnabledEffects,
        WorldpayGuaranteedPaymentsSessionIdEffects,
      ]
    });
    service = TestBed.inject(WorldpayGuaranteedPaymentsService);
    store = TestBed.inject(Store);
    loadScriptService = TestBed.inject(LoadScriptService);
    winRef = TestBed.inject(WindowRef);

    spyOn(store, 'dispatch').and.callThrough();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should generate script', () => {
    loadScriptService.loadScript({
      idScript: 'test',
      src: 'https://google.com',
      async: false,
      defer: false,
      attributes: {
        'data-test': 'Test Data'
      }
    });

    let isFound = false;
    const scripts = winRef.nativeWindow.document.getElementsByTagName('script');
    for (let i = 0; i < scripts.length; ++i) {
      if (
        scripts[i].getAttribute('id') != null &&
        scripts[i].getAttribute('id') === 'test'
      ) {
        isFound = true;
        break;
      }
    }

    expect(isFound).toBeTrue();
  });
});

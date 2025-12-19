import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoggerService, QueryState, WindowRef } from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { WorldpayFraudsightRiskComponent } from 'worldpay-sap-composable-components';
import { WorldpayFraudsightService } from 'worldpay-sap-composable-services';

class MockWorldpayFraudsightService implements Partial<WorldpayFraudsightService> {
  isFraudSightEnabledFromState(): Observable<boolean> {
    return of(true);
  }

  setFraudSightEnabled = () => {
  };

  isFraudSightEnabled(): Observable<QueryState<boolean>> {
    return of({
      data: true,
      loading: false,
      error: false
    });
  }

  setFraudSightId(): void {

  }
}

describe('WorldpayFraudsightRiskComponent', () => {
  let component: WorldpayFraudsightRiskComponent;
  let fixture: ComponentFixture<WorldpayFraudsightRiskComponent>;
  let windowRef: WindowRef;
  let service: WorldpayFraudsightService;
  let spyWinRef: jasmine.Spy;
  let enabledSpy: jasmine.Spy;
  let logger: LoggerService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        WorldpayFraudsightRiskComponent,
      ],
      imports: [],
      providers: [
        WindowRef,
        {
          provide: WorldpayFraudsightService,
          useClass: MockWorldpayFraudsightService,
        },
        LoggerService
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayFraudsightRiskComponent);
    component = fixture.componentInstance;
    service = TestBed.inject(WorldpayFraudsightService);
    windowRef = TestBed.inject(WindowRef);
    logger = TestBed.inject(LoggerService);
    spyWinRef = spyOn(windowRef, 'isBrowser');
    enabledSpy = spyOn(service, 'isFraudSightEnabledFromState');
    enabledSpy.and.callThrough();
    spyOn(service, 'setFraudSightId').and.callThrough();
    spyOnProperty(windowRef, 'nativeWindow', 'get').and.returnValue({
      // @ts-ignore
      wprofile: {
        profile: () => true
      },
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not be browser', () => {
    spyWinRef.and.returnValue(false);
    enabledSpy.and.returnValue(of(false));
    spyOn(logger, 'log');
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(service.setFraudSightId).not.toHaveBeenCalled();
    expect(logger.log).toHaveBeenCalledWith('SSR - skipping FraudSight initialization');
  });

  describe('should be browser', () => {
    beforeEach(() => {
      spyWinRef.and.returnValue(true);
      fixture.detectChanges();
    });

    it('and isFraudSightEnabled should be called and enabled', () => {
      let enabled = false;
      enabledSpy.and.returnValue(of(true));
      service.isFraudSightEnabledFromState().subscribe(
        isEnabled => {
          enabled = isEnabled;
        }
      );

      expect(enabled).toBeTrue();
    });

    it('isFraudSightEnabled should be called and disabled', () => {
      let enabled = false;
      fixture.detectChanges();
      service.isFraudSightEnabledFromState().subscribe(
        isEnabled => {
          enabled = isEnabled;
        }
      );

      expect(enabled).toBeTrue();
    });
  });
});

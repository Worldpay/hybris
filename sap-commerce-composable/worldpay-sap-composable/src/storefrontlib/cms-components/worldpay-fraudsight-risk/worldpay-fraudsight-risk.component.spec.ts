import { ComponentFixture, TestBed } from '@angular/core/testing';
import { WorldpayFraudsightRiskComponent } from './worldpay-fraudsight-risk.component';
import { WorldpayFraudsightService } from '../../../core/services/worldpay-fraudsight/worldpay-fraudsight.service';
import { Observable, of } from 'rxjs';
import { QueryState, WindowRef } from '@spartacus/core';
import { NgZone } from '@angular/core';

class MockWorldpayFraudsightService implements Partial<WorldpayFraudsightService> {
  isFraudSightEnabledFromState(): Observable<boolean> {
    return of(true);
  };

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

  };
}

describe('WorldpayFraudsightRiskComponent', () => {
  let component: WorldpayFraudsightRiskComponent;
  let fixture: ComponentFixture<WorldpayFraudsightRiskComponent>;
  let ngZone: NgZone;
  let windowRef: WindowRef;
  let service: WorldpayFraudsightService;
  let spyWinRef: jasmine.Spy;
  let enabledSpy: jasmine.Spy;

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
          }
        ]
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayFraudsightRiskComponent);
    component = fixture.componentInstance;
    service = TestBed.inject(WorldpayFraudsightService);
    ngZone = TestBed.inject(NgZone);
    windowRef = TestBed.inject(WindowRef);
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
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(service.setFraudSightId).not.toHaveBeenCalled();
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

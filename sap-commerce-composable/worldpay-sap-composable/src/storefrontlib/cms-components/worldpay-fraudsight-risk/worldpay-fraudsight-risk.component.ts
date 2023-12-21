import { AfterViewInit, Component, Input, NgZone, OnDestroy, OnInit } from '@angular/core';
import { EventService, QueryState, WindowRef } from '@spartacus/core';
import { WorldpayFraudsightService } from '../../../core/services/worldpay-fraudsight/worldpay-fraudsight.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'worldpay-fraudsight-risk',
  template: ''
})
export class WorldpayFraudsightRiskComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input() threatMetrix: string = 'wprofile';
  @Input() profilingDomain: string;
  @Input() organisationId: string;
  @Input() pageId: string;
  @Input() randomIdLength: number = 128;

  private drop = new Subject<void>();

  constructor(
    protected ngZone: NgZone,
    protected windowRef: WindowRef,
    protected eventService: EventService,
    protected worldpayFraudsightService: WorldpayFraudsightService,
  ) {
  }

  ngOnInit(): void {
    if (!this.windowRef.isBrowser()) {
      console.log('SSR - skipping FraudSight initialization');
      return;
    }

    this.worldpayFraudsightService.isFraudSightEnabled().pipe(
      takeUntil(this.drop)
    ).subscribe({
      next: (response: QueryState<boolean>) => {
        this.worldpayFraudsightService.setFraudSightEnabled(response);
      },
      error: (error: unknown) => {
        console.error('Failed to initialize FraudSight, check component configuration', error);
      }
    });
  }

  ngAfterViewInit(): void {
    this.worldpayFraudsightService.isFraudSightEnabledFromState()
      .pipe(
        takeUntil(this.drop)
      ).subscribe({
        next: (isEnabled: boolean): void => {
          if (isEnabled) {
            this.doProfile();
          }
        }
      }
      );
  }

  protected doProfile(): void {
    this.ngZone.run(() => {
      // @ts-ignore
      const tm = this.windowRef.nativeWindow[this.threatMetrix];
      // eslint-disable-next-line no-prototype-builtins
      if (tm && tm.hasOwnProperty('profile') && typeof tm.profile === 'function') {
        const uniqueId = this.random(this.randomIdLength);
        tm.profile(this.profilingDomain, this.organisationId, uniqueId, this.pageId);

        this.worldpayFraudsightService.setFraudSightId(uniqueId);
      } else {
        console.error('Failed to initialize Fraudsight, check component configuration');
      }
    });
  }

  protected random(length: number): string {
    const randomChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_';
    let result = '';
    for (let i = 0; i < length; i++) {
      result += randomChars.charAt(Math.floor(Math.random() * randomChars.length));
    }
    return result;
  }

  ngOnDestroy(): void {
    this.drop.next();
    this.drop.complete();
  }
}

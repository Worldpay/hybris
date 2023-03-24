import { AfterViewInit, Component, Input, NgZone, OnDestroy, OnInit } from '@angular/core';
import { WindowRef } from '@spartacus/core';
import { WorldpayFraudsightService } from '../../../core/services/worldpay-fraudsight/worldpay-fraudsight.service';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

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
    protected worldpayFraudsightService: WorldpayFraudsightService,
  ) {
  }

  ngOnInit(): void {
    if (!this.windowRef.isBrowser()) {
      console.log('SSR - skipping FraudSight initialization');
      return;
    }

    this.worldpayFraudsightService.isFraudSightEnabled();
  }

  ngAfterViewInit(): void {
    this.worldpayFraudsightService.isFraudSightEnabledFromState()
      .pipe(
        filter(Boolean),
        takeUntil(this.drop)
      ).subscribe((enabled) => {
        this.doProfile();
      });
  }

  protected doProfile(): void {
    this.ngZone.run(() => {
      const tm = this.windowRef.nativeWindow[this.threatMetrix];
      if (tm && tm.hasOwnProperty('profile') && typeof tm.profile === 'function') {
        const uniqueId = this.random(this.randomIdLength);
        tm.profile(this.profilingDomain, this.organisationId, uniqueId, this.pageId);

        this.worldpayFraudsightService.setFraudSightId(uniqueId);
      } else {
        console.error('Failed to initialize Fraudsight, check component configuration', {
          tm,
          component: this
        });
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

import { AfterViewInit, Component, DestroyRef, inject, Input, NgZone, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { EventService, LoggerService, QueryState, WindowRef } from '@spartacus/core';
import { WorldpayFraudsightService } from '@worldpay-services/worldpay-fraudsight/worldpay-fraudsight.service';

@Component({
  selector: 'worldpay-fraudsight-risk',
  template: ''
})
export class WorldpayFraudsightRiskComponent implements OnInit, AfterViewInit {
  @Input() threatMetrix: string = 'wprofile';
  @Input() profilingDomain: string;
  @Input() organisationId: string;
  @Input() pageId: string;
  @Input() randomIdLength: number = 128;
  protected logger: LoggerService = inject(LoggerService);
  private destroyRef: DestroyRef = inject(DestroyRef);

  constructor(
    protected ngZone: NgZone,
    protected windowRef: WindowRef,
    protected eventService: EventService,
    protected worldpayFraudsightService: WorldpayFraudsightService,
  ) {
  }

  ngOnInit(): void {
    if (!this.windowRef.isBrowser()) {
      this.logger.log('SSR - skipping FraudSight initialization');
      return;
    }

    this.worldpayFraudsightService.isFraudSightEnabled().pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (response: QueryState<boolean>) => {
        this.worldpayFraudsightService.setFraudSightEnabled(response);
      },
      error: (error: unknown) => {
        this.logger.error('Failed to initialize FraudSight, check component configuration', error);
      }
    });
  }

  ngAfterViewInit(): void {
    this.worldpayFraudsightService.isFraudSightEnabledFromState()
      .pipe(
        takeUntilDestroyed(this.destroyRef)
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
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const tm: any = (this.windowRef.nativeWindow as Record<string, any>)[this.threatMetrix as string];
      // eslint-disable-next-line no-prototype-builtins
      if (tm && tm.hasOwnProperty('profile') && typeof tm.profile === 'function') {
        const uniqueId: string = this.random(this.randomIdLength);
        tm.profile(this.profilingDomain, this.organisationId, uniqueId, this.pageId);

        this.worldpayFraudsightService.setFraudSightId(uniqueId);
      } else {
        this.logger.error('Failed to initialize Fraudsight, check component configuration');
      }
    });
  }

  protected random(length: number): string {
    const randomChars: string = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_';
    let result: string = '';
    for (let i: number = 0; i < length; i++) {
      result += randomChars.charAt(Math.floor(Math.random() * randomChars.length));
    }
    return result;
  }
}

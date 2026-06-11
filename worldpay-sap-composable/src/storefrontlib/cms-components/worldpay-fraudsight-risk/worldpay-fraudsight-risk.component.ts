import { AfterViewInit, Component, DestroyRef, inject, Input, NgZone, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { EventService, LoggerService, QueryState, WindowRef } from '@spartacus/core';
import { filter } from 'rxjs/operators';
import { WorldpayFraudsightFacade } from '../../../core';

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
  protected ngZone: NgZone = inject(NgZone);
  protected windowRef: WindowRef = inject(WindowRef);
  protected eventService: EventService = inject(EventService);
  protected worldpayFraudsightFacade: WorldpayFraudsightFacade = inject(WorldpayFraudsightFacade);
  private destroyRef: DestroyRef = inject(DestroyRef);

  ngOnInit(): void {
    if (!this.windowRef.isBrowser()) {
      this.logger.log('SSR - skipping FraudSight initialization');
      return;
    }

    this.worldpayFraudsightFacade.isFraudSightEnabled().pipe(
      filter((state: QueryState<boolean>): boolean => !state.loading),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (response: QueryState<boolean>): void => {
        this.worldpayFraudsightFacade.setFraudSightEnabled(response);
      },
      error: (error: unknown): void => {
        this.logger.error('Failed to initialize FraudSight, check component configuration', error);
      }
    });
  }

  ngAfterViewInit(): void {
    this.worldpayFraudsightFacade.isFraudSightEnabledFromState().pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (isEnabled: boolean): void => {
        if (isEnabled) {
          this.doProfile();
        }
      }
    });
  }

  protected doProfile(): void {
    this.ngZone.run((): void => {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const tm: any = (this.windowRef.nativeWindow as any)[this.threatMetrix];
      // eslint-disable-next-line no-prototype-builtins
      if (tm && tm.hasOwnProperty('profile') && typeof tm.profile === 'function') {
        const uniqueId: string = this.random(this.randomIdLength);
        tm.profile(this.profilingDomain, this.organisationId, uniqueId, this.pageId);

        this.worldpayFraudsightFacade.setFraudSightId(uniqueId);
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

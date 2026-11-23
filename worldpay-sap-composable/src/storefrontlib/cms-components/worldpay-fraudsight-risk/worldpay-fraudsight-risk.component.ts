import { AfterViewInit, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { EventService, LoggerService, QueryState, WindowRef } from '@spartacus/core';
import Ravelin from 'ravelinjs/core+track+encrypt';
import { Observable } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
import { WorldpayFraudsightFacade } from '../../../core';

@Component({
  selector: 'worldpay-fraudsight-risk',
  template: ''
})
export class WorldpayFraudsightRiskComponent implements OnInit, AfterViewInit {
  protected logger: LoggerService = inject(LoggerService);
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
      filter((isEnabled: boolean): boolean => isEnabled),
      switchMap((): Observable<string> => this.worldpayFraudsightFacade.getFraudPublicKeyFromState()),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (publishableKey: string): void => {
        this.doProfile(publishableKey).catch((error: unknown): void => {
          this.logger.error('Failed to run FraudSight profiling', error);
        });
      }
    });
  }

  protected async doProfile(publishableKey: string): Promise<void> {
    const ravelin: Ravelin = new Ravelin({
      key: publishableKey
    });
    const deviceId: string = await ravelin.core.id();
    this.worldpayFraudsightFacade.setFraudSightId(deviceId);
  }
}

import { ChangeDetectionStrategy, Component, NgZone, OnDestroy, OnInit, Renderer2, ViewContainerRef, ViewEncapsulation } from '@angular/core';
import { GlobalMessageService, GlobalMessageType, HttpErrorModel, QueryState, RoutingService, WindowRef } from '@spartacus/core';
import { LaunchDialogService, } from '@spartacus/storefront';
import { BehaviorSubject, combineLatest, Observable, ObservedValueOf, Subject, Subscription } from 'rxjs';
import { filter, take, takeUntil } from 'rxjs/operators';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { CheckoutPlaceOrderComponent, CheckoutStepService } from '@spartacus/checkout/base/components';
import { Order } from '@spartacus/order/root';
import { WorldpayApmService } from '../../../core/services/worldpay-apm/worldpay-apm.service';
import { WorldpayCheckoutPaymentService } from '../../../core/services/worldpay-checkout/worldpay-checkout-payment.service';
import { WorldpayFraudsightService } from '../../../core/services/worldpay-fraudsight/worldpay-fraudsight.service';
import {
  ApmPaymentDetails,
  APMRedirectResponse,
  PlaceOrderResponse,
  ThreeDsDDCInfo,
  ThreeDsInfo,
  WorldpayApmPaymentInfo,
  WorldpayChallengeResponse
} from '../../../core/interfaces';
import { WorldpayOrderService } from '../../../core/services';
import { ApmNormalizer } from '../../../core/normalizers';
import { PaymentDetails } from '@spartacus/cart/base/root';

@Component({
  selector: 'y-worldpay-checkout-place-order',
  templateUrl: './worldpay-checkout-place-order.component.html',
  styleUrls: ['worldpay-checkout-place-order.component.scss'],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorldpayCheckoutPlaceOrderComponent extends CheckoutPlaceOrderComponent implements OnInit, OnDestroy {

  paymentDetails: WorldpayApmPaymentInfo;
  cseToken: string;
  override checkoutSubmitForm: UntypedFormGroup = this.fb.group({
    termsAndConditions: [false, Validators.requiredTrue]
  });
  paymentAuthorized: boolean = false;
  ddcIframeUrl$: Observable<SafeResourceUrl>;
  challengeIframeUrl$: Observable<SafeResourceUrl>;
  challengeIframeHeight: number;
  challengeIframeWidth: number;
  redirectData$: BehaviorSubject<APMRedirectResponse> = new BehaviorSubject<APMRedirectResponse>(null);
  isLoading$: Observable<boolean> = this.worldpayApmService.getLoading();
  nativeWindow: Window = this.winRef.nativeWindow;
  listenerFn: () => void;
  queryParams: { key: string; value: string }[] = [];
  protected save: boolean = false;
  private ddcInfoFromState$: Subscription;
  private challengeInfo$: Subscription;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private ddcHandler: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private challengeHandler: any;
  private drop: Subject<void> = new Subject<void>();
  private fraudSightId: string;

  /**
   * Constructor
   * @param orderFacade WorldpayOrderService
   * @param routingService RoutingService
   * @param fb UntypedFormBuilder
   * @param launchDialogService LaunchDialogService
   * @param vcr ViewContainerRef
   * @param globalMessageService GlobalMessageService
   * @param checkoutStepService CheckoutStepService
   * @param activatedRoute ActivatedRoute
   * @param ngZone NgZone
   * @param winRef WindowRef
   * @param renderer Renderer2
   * @param worldpayCheckoutPaymentService WorldpayCheckoutPaymentService
   * @param worldpayApmService WorldpayApmService
   * @param worldpayFraudsightService WorldpayFraudsightService
   * @param apmNormalizer
   */
  constructor(
    protected override orderFacade: WorldpayOrderService,
    protected override routingService: RoutingService,
    protected override fb: UntypedFormBuilder,
    protected override launchDialogService: LaunchDialogService,
    protected override vcr: ViewContainerRef,
    protected globalMessageService: GlobalMessageService,
    protected checkoutStepService: CheckoutStepService,
    protected activatedRoute: ActivatedRoute,
    protected ngZone: NgZone,
    protected winRef: WindowRef,
    private renderer: Renderer2,
    protected worldpayCheckoutPaymentService: WorldpayCheckoutPaymentService,
    protected worldpayApmService: WorldpayApmService,
    protected worldpayFraudsightService: WorldpayFraudsightService,
    protected apmNormalizer: ApmNormalizer
  ) {
    super(
      orderFacade,
      routingService,
      fb,
      launchDialogService,
      vcr
    );
  }

  /**
   * Submit the checkout form
   * @since 4.3.6
   */
  override submitForm(): void {
    if (this.checkoutSubmitForm.valid) {
      this.orderFacade.startLoading(this.vcr);

      this.ddcIframeHandler();
      this.challengeIframeHandler();

      this.globalMessageService.add({ key: 'checkoutReview.placingOrder' }, GlobalMessageType.MSG_TYPE_INFO);

      this.placeWorldpayOrder().pipe(
        take(1),
        takeUntil(this.drop)
      ).subscribe({
        next: ([apm, paymentDetails]): void => {
          if (!apm || apm?.cardType) {
            this.executeDDC3dsJwtCommand();
          } else {
            const apmPaymentDetails: ApmPaymentDetails = apm ?? { ...paymentDetails?.data?.worldpayAPMPaymentInfo };
            this.worldpayApmService.getAPMRedirectUrl(apmPaymentDetails, this.save);
          }
        },
        error: (error: unknown): void => {
          this.orderFacade.clearLoading();
          this.worldpayApmService.showErrorMessage(error as HttpErrorModel);
        }
      });

    } else {
      this.checkoutSubmitForm.markAllAsTouched();
    }
  }

  /**
   * Execute DDC3dsJwt Command
   * @since 6.4.0
   */
  executeDDC3dsJwtCommand(): void {
    this.orderFacade.executeDDC3dsJwtCommand().pipe(takeUntil(this.drop)).subscribe();
  }

  /**
   * Trigger Place order for Worldpay APM
   * @since 6.4.0
   */
  placeWorldpayOrder(): Observable<[ObservedValueOf<Observable<WorldpayApmPaymentInfo>>, ObservedValueOf<Observable<QueryState<PaymentDetails | undefined>>>]> {
    return combineLatest([
      this.worldpayApmService.getSelectedAPMFromState(),
      this.worldpayCheckoutPaymentService.getPaymentDetailsState(),
    ]);
  }

  /**
   * On Init
   * @since 4.3.6
   */
  ngOnInit(): void {
    this.worldpayCheckoutPaymentService.getPaymentDetailsState()
      .pipe(
        filter((paymentDetails: QueryState<PaymentDetails>) => paymentDetails && Object.keys(paymentDetails).length > 0),
        takeUntil(this.drop)
      )
      .subscribe({
        next: (paymentDetails: QueryState<PaymentDetails>): void => {
          this.paymentDetails = this.apmNormalizer.normalizeApmData(paymentDetails.data);
          this.worldpayApmService.selectAPM(this.paymentDetails);
        }
      })
      .unsubscribe();

    this.worldpayCheckoutPaymentService.getCseTokenFromState()
      .pipe(takeUntil(this.drop))
      .subscribe({
        next: (cseToken: string): void => {
          this.cseToken = cseToken;
        }
      }).unsubscribe();

    this.orderFacade.getOrderDetails()
      .pipe(
        filter((order: Order): boolean => order && Object.keys(order).length !== 0),
        takeUntil(this.drop)
      )
      .subscribe({
        next: (): void => {
          this.onSuccess();
        }
      });

    if (!this.paymentDetails || Object.keys(this.paymentDetails).length === 0) {
      this.routingService.go(
        this.checkoutStepService.getPreviousCheckoutStepUrl(this.activatedRoute)
      );
    }

    this.ddcIframeUrl$ = this.worldpayCheckoutPaymentService.getThreeDsDDCIframeUrlFromState();
    this.challengeIframeUrl$ = this.worldpayCheckoutPaymentService.getThreeDsChallengeIframeUrlFromState();

    combineLatest([
      this.checkoutSubmitForm.controls.termsAndConditions.valueChanges,
      this.worldpayCheckoutPaymentService.getPaymentDetailsState(),
      this.worldpayCheckoutPaymentService.getCseTokenFromState()
    ])
      .pipe(takeUntil(this.drop))
      .subscribe({
        next: ([termsAndConditions, paymentDetails]): void => {
          this.paymentAuthorized =
            termsAndConditions &&
            paymentDetails?.data &&
            Object.keys(paymentDetails?.data).length > 0;
        }
      });

    this.worldpayApmService.getWorldpayAPMRedirectUrlFromState()
      .pipe(
        filter((response: APMRedirectResponse) => !!response),
        takeUntil(this.drop)
      )
      .subscribe({
        next: (redirectData: APMRedirectResponse): void => {
          this.redirectData$.next(redirectData);
          if (redirectData.postUrl) {
            new URLSearchParams(redirectData.postUrl.split('?')[1]).forEach((value, key) => {
              const found = redirectData.parameters?.entry?.filter((entry) => entry.key === key);
              if (!found) {
                this.queryParams.push({
                  key,
                  value
                });
              }
            });
          }
          this.ngZone.run(() => {
            setTimeout(() => {
              const form: HTMLFormElement = this.winRef.document.querySelector('#redirect-form');
              if (form) {
                form.submit();
              }
            }, 250);
          });
        }
      });

    this.worldpayFraudsightService.getFraudSightIdFromState()
      .pipe(
        take(1),
        takeUntil(this.drop)
      )
      .subscribe({
        next: (res: string) => this.fraudSightId = res
      });
  }

  /**
   * Populate this.ddcIframeUrl based on the available JWT for Device Data Collection
   */
  private ddcIframeHandler(): void {
    if (!this.ddcHandler) {
      this.ddcHandler = this.processDdc.bind(this);
      this.listenerFn = this.renderer.listen(this.nativeWindow, 'message', this.ddcHandler);
    }

    this.ddcInfoFromState$ = this.worldpayCheckoutPaymentService.getDDCInfoFromState()
      .pipe(
        filter((threeDsDDCInfo: ThreeDsDDCInfo): boolean => (!!threeDsDDCInfo && threeDsDDCInfo?.jwt?.length !== 0 && !!this.paymentDetails?.cardNumber)),
        takeUntil(this.drop)
      )
      .subscribe({
        next: ({
          ddcUrl,
          jwt
        }): void => {
          this.worldpayCheckoutPaymentService.setThreeDsDDCIframeUrl(
            ddcUrl,
            this.paymentDetails.cardNumber,
            jwt
          );
        }
      });
  }

  /**
   * Event handler for the Device Data Collection
   *
   * @param event event from the DDT iframe
   */
  private processDdc(event: MessageEvent): void {
    if (event.origin.includes('https://centinelapistag.cardinalcommerce.com')) {
      try {
        const deviceData: { SessionId: string; Status: boolean } = JSON.parse(
          event.data
        );
        if (deviceData && deviceData.Status) {
          this.orderFacade.initialPaymentRequest(
            this.paymentDetails,
            deviceData.SessionId,
            this.cseToken,
            this.checkoutSubmitForm.get('termsAndConditions').value,
            this.fraudSightId
          ).pipe(
            takeUntil(this.drop)
          ).subscribe({
            next: (response: PlaceOrderResponse): void => {
              this.orderFacade.setPlacedOrder(response.order);
            },
            error: (): void => {
              this.globalMessageService.remove(GlobalMessageType.MSG_TYPE_INFO);
              this.globalMessageService.add({ key: 'checkoutReview.applicationError' }, GlobalMessageType.MSG_TYPE_ERROR);
              this.orderFacade.clearLoading();
            }
          });
          return;
        }
      } catch (err) {
        console.log('failed to process ddc response', err);
      }

      this.globalMessageService.add({ key: 'checkoutReview.threeDsChallengeFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
      this.orderFacade.clearLoading();
    }
  }

  /**
   * Show the 3ds Challenge when challenge is given during the initial payment request.
   * Initial payment request is kicked of after DDC - see this.processDdc
   */
  private challengeIframeHandler(): void {
    this.challengeInfo$ = this.worldpayCheckoutPaymentService.getThreeDsChallengeInfoFromState()
      .pipe(
        filter((challengeInfo: ThreeDsInfo): boolean => !!challengeInfo),
        takeUntil(this.drop)
      )
      .subscribe({
        next: ({
          threeDSFlexData,
          merchantData
        }): void => {
          if (!threeDSFlexData || !threeDSFlexData.jwt) {
            console.log('3ds flow not implemented');
            return;
          }
          this.challengeIframeHeight = 400;
          this.challengeIframeWidth = this.nativeWindow.innerWidth >= 620 ? 600 : 390;

          this.worldpayCheckoutPaymentService.setThreeDsChallengeIframeUrl(
            threeDSFlexData.challengeUrl,
            threeDSFlexData.jwt,
            merchantData
          );

          // unregister message handler for device detection (previous step in process)
          if (this.ddcHandler) {
            this.nativeWindow.removeEventListener('message', this.ddcHandler, true);
          }

          if (!this.challengeHandler) {
            this.challengeHandler = this.processChallenge.bind(this);
            this.listenerFn = this.renderer.listen(this.nativeWindow, 'message', this.challengeHandler);
          }
        }
      });
  }

  private processChallenge(event: MessageEvent): void {
    if (this.challengeHandler) {
      this.nativeWindow.removeEventListener('message', this.challengeHandler, true);
    }

    try {
      const response: WorldpayChallengeResponse = event.data;
      // eslint-disable-next-line no-prototype-builtins
      if (!response.hasOwnProperty('accepted')) {
        // don't trigger on other message events
        return;
      }

      if (response.accepted) {
        this.orderFacade.challengeAccepted(response);
        return;
      }
    } catch (err) {
      console.log('Failed to process challenge event data', err);
    }
    this.orderFacade.challengeFailed();
  }

  override ngOnDestroy(): void {
    super.ngOnDestroy();
    if (this.ddcHandler) {
      this.nativeWindow.removeEventListener('message', this.ddcHandler, true);
    }
    if (this.challengeHandler) {
      this.nativeWindow.removeEventListener('message', this.challengeHandler, true);
    }

    if (this.listenerFn) {
      this.listenerFn();
    }

    this.drop.next();
    this.drop.complete();
  }
}

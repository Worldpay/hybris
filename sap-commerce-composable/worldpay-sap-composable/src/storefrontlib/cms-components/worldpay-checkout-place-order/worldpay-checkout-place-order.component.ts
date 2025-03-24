import { ChangeDetectionStrategy, Component, DestroyRef, inject, NgZone, OnDestroy, OnInit, Renderer2, ViewContainerRef, ViewEncapsulation } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { CheckoutPlaceOrderComponent, CheckoutStepService } from '@spartacus/checkout/base/components';
import { GlobalMessageService, GlobalMessageType, HttpErrorModel, LoggerService, PaymentDetails, QueryState, RoutingService, WindowRef } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { LaunchDialogService } from '@spartacus/storefront';
import { WorldpayACHFacade } from '@worldpay-facade/worldpay-ach.facade';
import { WorldpayApmService } from '@worldpay-services/worldpay-apm/worldpay-apm.service';
import { WorldpayCheckoutPaymentService } from '@worldpay-services/worldpay-checkout/worldpay-checkout-payment.service';
import { WorldpayFraudsightService } from '@worldpay-services/worldpay-fraudsight/worldpay-fraudsight.service';
import { BehaviorSubject, combineLatest, Observable, Subscription } from 'rxjs';
import { filter, take } from 'rxjs/operators';
import {
  ACHPaymentForm,
  ApmPaymentDetails,
  APMRedirectResponse,
  BrowserInfo,
  PlaceOrderResponse,
  ThreeDsDDCInfo,
  ThreeDsInfo,
  WorldpayApmPaymentInfo,
  WorldpayChallengeResponse
} from '../../../core/interfaces';
import { ApmNormalizer } from '../../../core/normalizers';
import { WorldpayOrderService } from '../../../core/services';

@Component({
  selector: 'y-worldpay-checkout-place-order',
  templateUrl: './worldpay-checkout-place-order.component.html',
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorldpayCheckoutPlaceOrderComponent extends CheckoutPlaceOrderComponent implements OnInit, OnDestroy {

  paymentDetails: ApmPaymentDetails;
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
  browserInfo: BrowserInfo;
  protected save: boolean = false;
  protected logger: LoggerService = inject(LoggerService);
  private ddcInfoFromState$: Subscription;
  private challengeInfo$: Subscription;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private ddcHandler: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private challengeHandler: any;
  private destroyRef: DestroyRef = inject(DestroyRef);
  private fraudSightId: string;

  /**
   * Constructor for the WorldpayCheckoutPlaceOrderComponent.
   *
   * @param {WorldpayOrderService} orderFacade - The Worldpay order service.
   * @param {RoutingService} routingService - The routing service.
   * @param {UntypedFormBuilder} fb - The form builder.
   * @param {LaunchDialogService} launchDialogService - The launch dialog service.
   * @param {ViewContainerRef} vcr - The view container reference.
   * @param {GlobalMessageService} globalMessageService - The global message service.
   * @param {CheckoutStepService} checkoutStepService - The checkout step service.
   * @param {ActivatedRoute} activatedRoute - The activated route.
   * @param {NgZone} ngZone - The Angular zone service.
   * @param {WindowRef} winRef - The window reference.
   * @param {Renderer2} renderer - The renderer.
   * @param {WorldpayCheckoutPaymentService} worldpayCheckoutPaymentService - The Worldpay checkout payment service.
   * @param {WorldpayApmService} worldpayApmService - The Worldpay APM service.
   * @param {WorldpayFraudsightService} worldpayFraudsightService - The Worldpay fraudsight service.
   * @param {WorldpayACHFacade} worldpayACHFacade - The Worldpay ACH facade.
   * @param {ApmNormalizer} apmNormalizer - The APM normalizer.
   * @since 6.4.0
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
    protected worldpayACHFacade: WorldpayACHFacade,
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
   * Submits the checkout form. If the form is valid, it starts the loading process,
   * handles the DDC iframe and challenge iframe, and places the Worldpay order.
   * If the form is invalid, it marks all form controls as touched.
   *
   * @override
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
        takeUntilDestroyed(this.destroyRef)
      ).subscribe({
        next: ([apm, paymentDetails, achPaymentFormValue]: [ApmPaymentDetails, QueryState<PaymentDetails>, ACHPaymentForm]): void => {
          if (!apm || apm?.cardType) {
            if (apm.cardType) {
              this.browserInfo = {
                javaEnabled: navigator.javaEnabled(),
                javascriptEnabled: true,
                language: navigator.language,
                colorDepth: screen.colorDepth,
                screenHeight: screen.height,
                screenWidth: screen.width,
                timeZone: new Date().getTimezoneOffset().toString(),
              };
            }
            this.executeDDC3dsJwtCommand();
          } else {
            const apmPaymentDetails: ApmPaymentDetails = apm ?? { ...paymentDetails?.data?.worldpayAPMPaymentInfo };
            if (achPaymentFormValue) {
              this.placeACHOrder(achPaymentFormValue);
            } else {
              this.worldpayApmService.getAPMRedirectUrl(apmPaymentDetails, this.save);
            }
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
   * Executes the DDC3dsJwt command and subscribes to its observable.
   * The subscription is automatically unsubscribed when the component is destroyed.
   *
   * @since 6.4.0
   */
  executeDDC3dsJwtCommand(): void {
    this.orderFacade.executeDDC3dsJwtCommand().pipe(takeUntilDestroyed(this.destroyRef)).subscribe();
  }

  /**
   * Combines the latest values from the selected APM, payment details, and ACH payment form value observables.
   *
   * @returns {Observable<[ApmPaymentDetails, QueryState<PaymentDetails>, ACHPaymentForm]>} An observable emitting the combined values.
   * @since 6.4.2
   */
  placeWorldpayOrder(): Observable<[ApmPaymentDetails, QueryState<PaymentDetails>, ACHPaymentForm]> {
    return combineLatest([
      this.worldpayApmService.getSelectedAPMFromState(),
      this.worldpayCheckoutPaymentService.getPaymentDetailsState(),
      this.worldpayACHFacade.getACHPaymentFormValue()
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
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (paymentDetails: QueryState<PaymentDetails>): void => {
          this.paymentDetails = this.apmNormalizer.normalizeApmData(paymentDetails.data);
          this.worldpayApmService.selectAPM(this.paymentDetails);
        }
      })
      .unsubscribe();

    this.worldpayCheckoutPaymentService.getCseTokenFromState()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (cseToken: string): void => {
          this.cseToken = cseToken;
        }
      }).unsubscribe();

    this.orderFacade.getOrderDetails()
      .pipe(
        filter((order: Order): boolean => order && Object.keys(order).length !== 0),
        takeUntilDestroyed(this.destroyRef)
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
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        next: ([termsAndConditions, paymentDetails]: [any, QueryState<WorldpayApmPaymentInfo>, string]): void => {
          this.paymentAuthorized =
            termsAndConditions &&
            paymentDetails?.data &&
            Object.keys(paymentDetails?.data).length > 0;
        }
      });

    this.worldpayApmService.getWorldpayAPMRedirectUrlFromState()
      .pipe(
        filter((response: APMRedirectResponse) => !!response),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (redirectData: APMRedirectResponse): void => {
          this.redirectData$.next(redirectData);
          if (redirectData.postUrl) {
            new URLSearchParams(redirectData.postUrl.split('?')[1]).forEach((value: string, key: string): void => {
              // eslint-disable-next-line @typescript-eslint/typedef
              const found = redirectData.parameters?.entry?.filter((entry): boolean => entry.key === key);
              if (!found) {
                this.queryParams.push({
                  key,
                  value
                });
              }
            });
          }
          this.ngZone.run((): void => {
            setTimeout((): void => {
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
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (res: string) => this.fraudSightId = res
      });
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
  }

  /**
   * Place ACH Order
   * @since 6.4.2
   * @param achPaymentFormValue
   */
  protected placeACHOrder(achPaymentFormValue: ACHPaymentForm): void {
    this.orderFacade.placeACHOrder(achPaymentFormValue)
      .pipe(
        take(1),
        takeUntilDestroyed(this.destroyRef)
      ).subscribe({
        error: (error: unknown): void => {
          this.logger.error('Failed to place ACH order', error);
          this.orderFacade.clearLoading();
        },
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
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: ({
          ddcUrl,
          jwt
        }: ThreeDsDDCInfo): void => {
          this.worldpayCheckoutPaymentService.setThreeDsDDCIframeUrl(
            ddcUrl,
            this.paymentDetails.cardNumber,
            jwt
          );
        }
      });
  }

  /**
   * Processes the Device Data Collection (DDC) response event.
   * If the event origin matches the expected URL, it parses the event data and
   * initiates the initial payment request with the parsed device data.
   * If the DDC response is valid, it sets the placed order on the order facade.
   * If an error occurs during the process, it logs the error and shows an error message.
   *
   * @param {MessageEvent} event - The message event from the DDC iframe.
   * @since 6.4.0
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
            this.fraudSightId,
            this.browserInfo
          ).pipe(
            takeUntilDestroyed(this.destroyRef)
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
        this.logger.error('failed to process ddc response', err);
      }

      this.globalMessageService.add({ key: 'checkoutReview.threeDsChallengeFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
      this.orderFacade.clearLoading();
    }
  }

  /**
   * Handles the 3DS challenge iframe by subscribing to the ThreeDsChallengeInfo observable.
   * If the challenge info is available, it sets the iframe URL and dimensions.
   * Unregisters the message handler for device detection and registers a new handler for the challenge response.
   *
   * @private
   * @since 6.4.0
   */
  private challengeIframeHandler(): void {
    this.challengeInfo$ = this.worldpayCheckoutPaymentService.getThreeDsChallengeInfoFromState()
      .pipe(
        filter((challengeInfo: ThreeDsInfo): boolean => !!challengeInfo),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: ({
          threeDSFlexData,
          merchantData
        }: ThreeDsInfo): void => {
          if (!threeDSFlexData || !threeDSFlexData.jwt) {
            this.logger.log('3ds flow not implemented');
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

  /**
   * Processes the challenge response event.
   * If the challenge handler exists, it removes the event listener for the challenge handler.
   * Attempts to parse the event data as a WorldpayChallengeResponse.
   * If the response contains an 'accepted' property, it either calls challengeAccepted or challengeFailed on the order facade.
   * Logs an error if parsing the event data fails.
   *
   * @param {MessageEvent} event - The message event containing the challenge response.
   * @since 6.4.0
   */
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
      this.logger.error('Failed to process challenge event data', err);
    }
    this.orderFacade.challengeFailed();
  }
}

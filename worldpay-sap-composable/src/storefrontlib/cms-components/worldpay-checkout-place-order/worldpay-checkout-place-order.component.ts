import { KeyValue } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, NgZone, OnDestroy, OnInit, Renderer2, ViewEncapsulation } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormGroup, Validators } from '@angular/forms';
import { SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { CheckoutPlaceOrderComponent, CheckoutStepService } from '@spartacus/checkout/base/components';
import { GlobalMessageService, GlobalMessageType, HttpErrorModel, LoggerService, PaymentDetails, QueryState, WindowRef } from '@spartacus/core';
import { Order, ScheduleReplenishmentForm } from '@spartacus/order/root';
import { BehaviorSubject, combineLatest, Observable, Subscription } from 'rxjs';
import { filter, take } from 'rxjs/operators';
import {
  ACHPaymentForm,
  ApmNormalizer,
  ApmPaymentDetails,
  APMRedirectResponse,
  BrowserInfo,
  PlaceOrderResponse,
  ThreeDsDDCInfo,
  ThreeDsInfo,
  WorldpayACHFacade,
  WorldpayApmFacade,
  WorldpayApmPaymentInfo,
  WorldpayChallengeResponse,
  WorldpayCheckoutPaymentFacade,
  WorldpayFraudsightFacade,
  WorldpayOrderFacade,
} from '../../../core';

@Component({
  selector: 'y-worldpay-checkout-place-order',
  templateUrl: './worldpay-checkout-place-order.component.html',
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false
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
  listenerFn: () => void;
  queryParams: { key: string; value: string }[] = [];
  browserInfo: BrowserInfo;
  scheduleReplenishmentFormData: ScheduleReplenishmentForm;
  protected override orderFacade: WorldpayOrderFacade = inject(WorldpayOrderFacade);
  protected logger: LoggerService = inject(LoggerService);
  protected destroyRef: DestroyRef = inject(DestroyRef);
  protected globalMessageService: GlobalMessageService = inject(GlobalMessageService);
  protected checkoutStepService: CheckoutStepService = inject(CheckoutStepService);
  protected activatedRoute: ActivatedRoute = inject(ActivatedRoute);
  protected ngZone: NgZone = inject(NgZone);
  protected winRef: WindowRef = inject(WindowRef);
  nativeWindow: Window = this.winRef.nativeWindow;
  protected worldpayCheckoutPaymentFacade: WorldpayCheckoutPaymentFacade = inject(WorldpayCheckoutPaymentFacade);
  protected worldpayApmFacade: WorldpayApmFacade = inject(WorldpayApmFacade);
  isLoading$: Observable<boolean> = this.worldpayApmFacade.getLoading();
  protected worldpayFraudsightFacade: WorldpayFraudsightFacade = inject(WorldpayFraudsightFacade);
  protected worldpayACHFacade: WorldpayACHFacade = inject(WorldpayACHFacade);
  protected apmNormalizer: ApmNormalizer = inject(ApmNormalizer);
  private ddcInfoFromState$: Subscription;
  private challengeInfo$: Subscription;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private ddcHandler: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private challengeHandler: any;
  private fraudSightId: string;
  private renderer: Renderer2 = inject(Renderer2);

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

      this.placeOrder();

    } else {
      this.checkoutSubmitForm.markAllAsTouched();
    }
  }

  placeOrder(): void {
    this.placeWorldpayOrder().pipe(
      take(1),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: ([apm, paymentDetails, achPaymentFormValue]: [ApmPaymentDetails, QueryState<PaymentDetails>, ACHPaymentForm]): void => {
        switch (true) {
          case (!apm || Boolean(apm?.cardType)): {
            if (apm.cardType) {
              this.browserInfo = {
              // eslint-disable-next-line deprecation/deprecation
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
            break;
          }

          case(Boolean(apm?.subscriptionId)): {
            this.processPayment(apm.subscriptionId);
            break;
          }

          default: {
            const apmPaymentDetails: ApmPaymentDetails = apm ?? { ...paymentDetails?.data?.worldpayAPMPaymentInfo };
            if (achPaymentFormValue) {
              this.placeACHOrder(achPaymentFormValue);
            } else {
              this.worldpayApmFacade.getAPMRedirectUrl(apmPaymentDetails);
            }
          }
        }
      },
      error: (error: unknown): void => {
        this.orderFacade.clearLoading();
        this.worldpayApmFacade.showErrorMessage(error as HttpErrorModel);
      }
    });
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
      this.worldpayApmFacade.getSelectedAPMFromState(),
      this.worldpayCheckoutPaymentFacade.getPaymentDetailsState(),
      this.worldpayACHFacade.getACHPaymentFormValue()
    ]);
  }

  /**
   * On Init
   * @since 4.3.6
   */
  override ngOnInit(): void {
    this.initializePaymentDetails();
    this.initializeCseToken();
    this.handleOrderCompletion();
    this.validateCheckoutStep();
    this.initializeIframeUrls();
    this.bindPaymentAuthorizationChanges();
    this.handleApmRedirect();
    this.initializeFraudSightId();
  }

  /**
   * Cleans up resources and event listeners when the component is destroyed.
   * Calls the parent class's `ngOnDestroy` method, removes event listeners for
   * the DDC and challenge handlers, and invokes the `listenerFn` if it exists.
   *
   * @override
   * @since 4.3.6
   */
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
   * Initializes the payment details by retrieving them from the state.
   * Subscribes to the payment details observable, normalizes the data,
   * and selects the APM (Alternative Payment Method) using the normalized details.
   *
   * @protected
   * @since 2211.43.0
   */
  protected initializePaymentDetails(): void {
    this.worldpayCheckoutPaymentFacade.getPaymentDetailsState().pipe(
      filter((paymentDetails: QueryState<PaymentDetails>): boolean => paymentDetails && Object.keys(paymentDetails).length > 0),
      take(1),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (paymentDetails: QueryState<PaymentDetails>): void => {
        this.paymentDetails = this.apmNormalizer.normalizeApmData(paymentDetails.data);
        this.worldpayApmFacade.selectAPM(this.paymentDetails);
      }
    });
  }

  /**
   * Initializes the CSE (Client-Side Encryption) token by retrieving it from the state.
   * Subscribes to the observable and assigns the retrieved token to the `cseToken` property.
   *
   * @protected
   * @since 2211.43.0
   */
  protected initializeCseToken(): void {
    this.worldpayCheckoutPaymentFacade.getCseTokenFromState().pipe(
      take(1),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (cseToken: string): void => {
        this.cseToken = cseToken;
      }
    });
  }

  /**
   * Handles the completion of an order by subscribing to the order details observable.
   * Once the order details are available and valid, it triggers the `onSuccess` method.
   *
   * @protected
   * @since 2211.43.0
   */
  protected handleOrderCompletion(): void {
    this.orderFacade.getOrderDetails().pipe(
      filter((order: Order): boolean => order && Object.keys(order).length !== 0),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (): void => {
        this.onSuccess();
      }
    });
  }

  /**
   * Validate checkout step and redirect if necessary
   *
   */
  protected validateCheckoutStep(): void {
    if (!this.paymentDetails || Object.keys(this.paymentDetails).length === 0) {
      this.routingService.go(
        this.checkoutStepService.getPreviousCheckoutStepUrl(this.activatedRoute)
      );
    }
  }

  /**
   * Initializes the URLs for the 3DS (Three-Domain Secure) iframe components.
   * Retrieves the Device Data Collection (DDC) iframe URL and the challenge iframe URL
   * from the Worldpay checkout payment service state.
   *
   * @protected
   * @since 6.4.0
   */
  protected initializeIframeUrls(): void {
    this.ddcIframeUrl$ = this.worldpayCheckoutPaymentFacade.getThreeDsDDCIframeUrlFromState();
    this.challengeIframeUrl$ = this.worldpayCheckoutPaymentFacade.getThreeDsChallengeIframeUrlFromState();
  }

  /**
   * Watches for changes in payment authorization status by combining the latest values
   * from the terms and conditions checkbox, payment details state, and CSE token state.
   * Updates the `paymentAuthorized` property based on the validity of these values.
   *
   * @protected
   * @since 2211.43.0
   */
  protected bindPaymentAuthorizationChanges(): void {
    combineLatest([
      // @ts-ignore: TS4111
      this.checkoutSubmitForm.controls.termsAndConditions.valueChanges,
      this.worldpayCheckoutPaymentFacade.getPaymentDetailsState(),
      this.worldpayCheckoutPaymentFacade.getCseTokenFromState()
    ])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        next: ([termsAndConditions, paymentDetails, _cseToken]: [boolean, QueryState<WorldpayApmPaymentInfo>, string]): void => {
          this.paymentAuthorized =
            termsAndConditions &&
            !!paymentDetails?.data &&
            Object.keys(paymentDetails.data).length > 0;
        }
      });
  }

  /**
   * Handles the APM (Alternative Payment Method) redirect flow.
   * Subscribes to the observable that emits the APM redirect URL from the state.
   * When a valid redirect response is received, it updates the `redirectData$` observable,
   * processes the redirect query parameters, and submits the redirect form.
   *
   * @protected
   * @since 2211.43.0
   */
  protected handleApmRedirect(): void {
    this.worldpayApmFacade.getWorldpayAPMRedirectUrlFromState()
      .pipe(
        filter((response: APMRedirectResponse): boolean => !!response),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (redirectData: APMRedirectResponse): void => {
          this.redirectData$.next(redirectData);
          this.processRedirectQueryParams(redirectData);
          this.submitRedirectForm();
        }
      });
  }

  /**
   * Processes the query parameters from the APM (Alternative Payment Method) redirect URL.
   * Extracts the query parameters from the `postUrl` and checks if they already exist
   * in the `parameters` property of the `redirectData`. If not, adds them to the `queryParams` array.
   *
   * @protected
   * @param {APMRedirectResponse} redirectData - The redirect data containing the `postUrl` and optional parameters.
   * @since 2211.43.0
   */
  protected processRedirectQueryParams(redirectData: APMRedirectResponse): void {
    if (!redirectData.postUrl) {
      return;
    }

    const urlParams: URLSearchParams = new URLSearchParams(redirectData.postUrl.split('?')[1]);

    urlParams.forEach((value: string, key: string): void => {
      const existsInParameters: boolean = redirectData.parameters?.entry?.some(
        (entry: KeyValue<string, string>): boolean => entry.key === key
      );

      if (!existsInParameters) {
        this.queryParams.push({
          key,
          value
        });
      }
    });
  }

  /**
   * Submits the redirect form after a delay.
   * Runs the form submission logic inside Angular's `NgZone` to ensure proper change detection.
   * The form is selected using its `#redirect-form` ID and submitted after a 250ms delay.
   *
   * @protected
   * @since 2211.43.0
   */
  protected submitRedirectForm(): void {
    this.ngZone.run((): void => {
      setTimeout((): void => {
        const form: HTMLFormElement = this.winRef.document.querySelector('#redirect-form');
        if (form) {
          form.submit();
        }
      }, 250);
    });
  }

  /**
   * Place ACH Order
   * @since 6.4.2
   * @param achPaymentFormValue
   */
  protected placeACHOrder(achPaymentFormValue: ACHPaymentForm): void {
    this.orderFacade.placeACHOrder(achPaymentFormValue).pipe(
      take(1),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      error: (): void => {
        this.orderFacade.clearLoading();
      },
    });
  }

  /**
   * Handles the 3DS challenge iframe by subscribing to the ThreeDsChallengeInfo observable.
   * If the challenge info is available, it sets the iframe URL and dimensions.
   * Unregisters the message handler for device detection and registers a new handler for the challenge response.
   *
   * @private
   * @since 6.4.0
   */
  protected challengeIframeHandler(): void {
    this.challengeInfo$ = this.worldpayCheckoutPaymentFacade.getThreeDsChallengeInfoFromState()
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

          this.worldpayCheckoutPaymentFacade.setThreeDsChallengeIframeUrl(
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
   * Initializes the FraudSight ID by retrieving it from the state.
   * Subscribes to the observable that emits the FraudSight ID and assigns it to the `fraudSightId` property.
   *
   * @protected
   * @since 2211.43.0
   */
  protected initializeFraudSightId(): void {
    this.worldpayFraudsightFacade.getFraudSightIdFromState().pipe(
      take(1),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (fraudSightId: string): void => {
        this.fraudSightId = fraudSightId;
      }
    });
  }

  protected processPayment(sessionId: string): void {
    this.orderFacade.initialPaymentRequest(
      this.paymentDetails,
      sessionId,
      this.cseToken,
      this.checkoutSubmitForm.get('termsAndConditions').value,
      this.fraudSightId,
      this.browserInfo,
      this.scheduleReplenishmentFormData
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
  }

  /**
   * Populate this.ddcIframeUrl based on the available JWT for Device Data Collection
   */
  private ddcIframeHandler(): void {
    if (!this.ddcHandler) {
      this.ddcHandler = this.processDdc.bind(this);
      this.listenerFn = this.renderer.listen(this.nativeWindow, 'message', this.ddcHandler);
    }

    this.ddcInfoFromState$ = this.worldpayCheckoutPaymentFacade.getDDCInfoFromState()
      .pipe(
        filter((threeDsDDCInfo: ThreeDsDDCInfo): boolean => (!!threeDsDDCInfo && threeDsDDCInfo?.jwt?.length !== 0 && !!this.paymentDetails?.cardNumber)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: ({
          ddcUrl,
          jwt
        }: ThreeDsDDCInfo): void => {
          this.worldpayCheckoutPaymentFacade.setThreeDsDDCIframeUrl(
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
          this.processPayment(deviceData.SessionId);
          return;
        }
      } catch (err) {
        this.logger.error('failed to process ddc response', err);
      }

      this.globalMessageService.add({ key: 'checkoutReview.threeDsChallengeFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
      this.orderFacade.clearLoading();
      this.orderFacade.challengeFailed('checkoutReview.threeDsChallengeFailed');
    }
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
    this.orderFacade.challengeFailed('checkoutReview.challengeFailed');
  }
}
